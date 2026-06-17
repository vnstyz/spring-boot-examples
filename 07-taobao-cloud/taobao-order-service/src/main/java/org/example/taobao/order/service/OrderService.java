package org.example.taobao.order.service;

import org.example.taobao.common.dto.cart.CheckedCartItemDTO;
import org.example.taobao.common.dto.order.OrderCreateResponse;
import org.example.taobao.common.dto.order.OrderDetailDTO;
import org.example.taobao.common.dto.order.OrderItemDTO;
import org.example.taobao.common.dto.product.StockOperateRequest;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.order.client.CartRemoteClient;
import org.example.taobao.order.client.ProductRemoteClient;
import org.example.taobao.order.entity.OrderItem;
import org.example.taobao.order.entity.OrderMain;
import org.example.taobao.order.mq.OrderEventPublisher;
import org.example.taobao.order.repository.OrderItemRepository;
import org.example.taobao.order.repository.OrderRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单业务服务，负责并发下单、库存处理、订单落库和消息通知。
 */
@Service
public class OrderService {

    private static final String LOCK_KEY_PREFIX = "order:lock:user:";
    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final CartRemoteClient cartRemoteClient;
    private final ProductRemoteClient productRemoteClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(CartRemoteClient cartRemoteClient,
                        ProductRemoteClient productRemoteClient,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        StringRedisTemplate stringRedisTemplate,
                        OrderEventPublisher orderEventPublisher) {
        this.cartRemoteClient = cartRemoteClient;
        this.productRemoteClient = productRemoteClient;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.orderEventPublisher = orderEventPublisher;
    }

    /**
     * 从购物车勾选项创建订单，支持并发保护和库存补偿。
     */
    public OrderCreateResponse createFromCart(Long userId) {
        String lockValue = UUID.randomUUID().toString();
        String lockKey = LOCK_KEY_PREFIX + userId;
        Boolean lockSuccess = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, Duration.ofSeconds(10));
        if (!Boolean.TRUE.equals(lockSuccess)) {
            throw new BusinessException(42900, "下单过于频繁，请稍后重试");
        }

        List<StockOperateRequest> deductedStockList = new ArrayList<>();
        boolean orderPersisted = false;
        try {
            List<CheckedCartItemDTO> checkedItems = cartRemoteClient.queryCheckedItems(userId);
            if (checkedItems.isEmpty()) {
                throw new BusinessException(40020, "购物车没有勾选商品，无法下单");
            }

            for (CheckedCartItemDTO item : checkedItems) {
                StockOperateRequest request = new StockOperateRequest(item.productId(), item.quantity());
                productRemoteClient.deductStock(request);
                deductedStockList.add(request);
            }

            BigDecimal totalAmount = checkedItems.stream()
                    .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            OrderMain orderMain = persistOrder(userId, checkedItems, totalAmount);
            orderPersisted = true;

            try {
                cartRemoteClient.clearCheckedItems(userId);
            } catch (Exception ignored) {
                // 购物车清理失败不影响订单主流程，可通过定时任务补偿。
            }

            orderEventPublisher.publishCreated(orderMain);
            return new OrderCreateResponse(
                    orderMain.getOrderNo(),
                    orderMain.getStatus(),
                    orderMain.getTotalAmount(),
                    orderMain.getCreatedAt()
            );
        } catch (BusinessException exception) {
            if (!orderPersisted) {
                compensateStock(deductedStockList);
            }
            throw exception;
        } catch (Exception exception) {
            if (!orderPersisted) {
                compensateStock(deductedStockList);
            }
            throw new BusinessException(50040, "下单失败，请稍后重试");
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 查询当前用户订单详情。
     */
    public OrderDetailDTO queryDetail(Long userId, String orderNo) {
        OrderMain orderMain = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new BusinessException(40420, "订单不存在"));

        List<OrderItemDTO> itemDTOList = orderItemRepository.findAllByOrderId(orderMain.getId()).stream()
                .map(item -> new OrderItemDTO(
                        item.getProductId(),
                        item.getProductTitle(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getAmount()
                ))
                .collect(Collectors.toList());

        return new OrderDetailDTO(
                orderMain.getOrderNo(),
                orderMain.getUserId(),
                orderMain.getStatus(),
                orderMain.getTotalAmount(),
                orderMain.getCreatedAt(),
                itemDTOList
        );
    }

    /**
     * MQ消费后更新订单状态，模拟异步流程推进。
     */
    @Transactional
    public void markWaitPay(String orderNo) {
        orderRepository.findByOrderNo(orderNo).ifPresent(orderMain -> {
            if ("CREATED".equals(orderMain.getStatus())) {
                orderMain.setStatus("WAIT_PAY");
                orderRepository.save(orderMain);
            }
        });
    }

    /**
     * 事务化保存订单主表和明细。
     */
    @Transactional
    protected OrderMain persistOrder(Long userId, List<CheckedCartItemDTO> checkedItems, BigDecimal totalAmount) {
        OrderMain orderMain = new OrderMain();
        orderMain.setOrderNo(generateOrderNo(userId));
        orderMain.setUserId(userId);
        orderMain.setTotalAmount(totalAmount);
        orderMain.setStatus("CREATED");
        OrderMain savedOrder = orderRepository.save(orderMain);

        List<OrderItem> orderItems = checkedItems.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(item.productId());
            orderItem.setProductTitle(item.title());
            orderItem.setUnitPrice(item.price());
            orderItem.setQuantity(item.quantity());
            orderItem.setAmount(item.price().multiply(BigDecimal.valueOf(item.quantity())));
            return orderItem;
        }).collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);
        return savedOrder;
    }

    /**
     * 回补已扣减库存，避免下单失败后库存丢失。
     */
    private void compensateStock(List<StockOperateRequest> deductedStockList) {
        for (StockOperateRequest request : deductedStockList) {
            try {
                productRemoteClient.restoreStock(request);
            } catch (Exception ignored) {
                // 库存补偿失败时记录日志可进一步接入告警系统。
            }
        }
    }

    private String generateOrderNo(Long userId) {
        String timePart = LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TB" + timePart + userId + randomPart;
    }

    private void releaseLock(String lockKey, String lockValue) {
        String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentValue)) {
            stringRedisTemplate.delete(lockKey);
        }
    }
}
