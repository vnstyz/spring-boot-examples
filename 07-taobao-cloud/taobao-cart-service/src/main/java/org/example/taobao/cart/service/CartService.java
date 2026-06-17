package org.example.taobao.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taobao.cart.client.ProductRemoteClient;
import org.example.taobao.cart.entity.CartItem;
import org.example.taobao.cart.repository.CartItemRepository;
import org.example.taobao.common.dto.cart.AddCartItemRequest;
import org.example.taobao.common.dto.cart.CartItemDTO;
import org.example.taobao.common.dto.cart.CartSettleDTO;
import org.example.taobao.common.dto.cart.CheckedCartItemDTO;
import org.example.taobao.common.dto.product.ProductDetailDTO;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 购物车业务服务，负责购物车维护和结算。
 */
@Service
public class CartService {

    private static final String CART_CACHE_PREFIX = "cart:snapshot:";

    private final CartItemRepository cartItemRepository;
    private final ProductRemoteClient productRemoteClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRemoteClient productRemoteClient,
                       StringRedisTemplate stringRedisTemplate,
                       ObjectMapper objectMapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRemoteClient = productRemoteClient;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 新增商品到购物车：若已存在则累加数量。
     */
    @Transactional
    public void addItem(Long userId, AddCartItemRequest request) {
        ensureProductExists(request.productId());

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.productId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(userId);
                    newItem.setProductId(request.productId());
                    newItem.setQuantity(0);
                    newItem.setChecked(true);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
        cartItemRepository.save(cartItem);
        evictCartCache(userId);
    }

    /**
     * 查询购物车列表，优先读取Redis快照。
     */
    public List<CartItemDTO> listCart(Long userId) {
        String cacheKey = cacheKey(userId);
        String cacheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheJson != null && !cacheJson.isBlank()) {
            try {
                return objectMapper.readValue(cacheJson, new TypeReference<>() {
                });
            } catch (Exception ignored) {
                // 缓存解析失败时回源数据库。
            }
        }

        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByUpdatedAtDesc(userId);
        List<CartItemDTO> dtoList = buildCartItemDTOs(cartItems);
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(dtoList), Duration.ofMinutes(10));
        } catch (Exception ignored) {
            // 缓存写入失败不影响主链路。
        }
        return dtoList;
    }

    /**
     * 修改购物车项勾选状态。
     */
    @Transactional
    public void changeChecked(Long userId, Long cartItemId, boolean checked) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new BusinessException(40410, "购物车项不存在"));
        cartItem.setChecked(checked);
        cartItemRepository.save(cartItem);
        evictCartCache(userId);
    }

    /**
     * 结算购物车勾选项并计算总金额。
     */
    public CartSettleDTO settle(Long userId) {
        List<CartItem> checkedItems = cartItemRepository.findAllByUserIdAndCheckedTrue(userId);
        List<CartItemDTO> items = buildCartItemDTOs(checkedItems);
        BigDecimal totalAmount = items.stream()
                .map(CartItemDTO::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartSettleDTO(items, totalAmount);
    }

    /**
     * 查询用户勾选购物车项（内部接口给订单服务调用）。
     */
    public List<CheckedCartItemDTO> queryCheckedForOrder(Long userId) {
        List<CartItem> checkedItems = cartItemRepository.findAllByUserIdAndCheckedTrue(userId);
        if (checkedItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ProductDetailDTO> productMap = queryProductMap(checkedItems);
        return checkedItems.stream()
                .map(item -> {
                    ProductDetailDTO detailDTO = productMap.get(item.getProductId());
                    if (detailDTO == null) {
                        throw new BusinessException(40411, "商品不存在，无法结算");
                    }
                    return new CheckedCartItemDTO(
                            item.getProductId(),
                            item.getQuantity(),
                            detailDTO.title(),
                            detailDTO.price()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 清空用户勾选购物车项，结算后调用。
     */
    @Transactional
    public void clearChecked(Long userId) {
        cartItemRepository.deleteAllByUserIdAndCheckedTrue(userId);
        evictCartCache(userId);
    }

    private List<CartItemDTO> buildCartItemDTOs(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ProductDetailDTO> productMap = queryProductMap(cartItems);
        return cartItems.stream().map(item -> {
            ProductDetailDTO detailDTO = productMap.get(item.getProductId());
            if (detailDTO == null) {
                return new CartItemDTO(
                        item.getId(),
                        item.getProductId(),
                        "商品已失效",
                        "",
                        BigDecimal.ZERO,
                        item.getQuantity(),
                        item.getChecked(),
                        BigDecimal.ZERO
                );
            }

            BigDecimal amount = detailDTO.price().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemDTO(
                    item.getId(),
                    item.getProductId(),
                    detailDTO.title(),
                    detailDTO.coverUrl(),
                    detailDTO.price(),
                    item.getQuantity(),
                    item.getChecked(),
                    amount
            );
        }).collect(Collectors.toList());
    }

    private Map<Long, ProductDetailDTO> queryProductMap(List<CartItem> cartItems) {
        Set<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toSet());
        List<ProductDetailDTO> productDetails = productRemoteClient.batchQuery(productIds);
        return productDetails.stream().collect(Collectors.toMap(ProductDetailDTO::productId, Function.identity()));
    }

    private void ensureProductExists(Long productId) {
        List<ProductDetailDTO> productDetails = productRemoteClient.batchQuery(Set.of(productId));
        if (productDetails.isEmpty()) {
            throw new BusinessException(40412, "商品不存在，不能加入购物车");
        }
    }

    private String cacheKey(Long userId) {
        return CART_CACHE_PREFIX + userId;
    }

    private void evictCartCache(Long userId) {
        stringRedisTemplate.delete(cacheKey(userId));
    }
}
