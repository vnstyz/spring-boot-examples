package org.example.taobao.order.mq;

import org.example.taobao.common.dto.order.OrderCreatedEvent;
import org.example.taobao.order.config.RabbitOrderConfig;
import org.example.taobao.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 订单消息监听器，消费订单创建事件并推进订单状态。
 */
@Component
public class OrderCreatedListener {

    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 消费订单创建消息，将订单状态从CREATED推进到WAIT_PAY。
     */
    @RabbitListener(queues = RabbitOrderConfig.ORDER_CREATED_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        orderService.markWaitPay(event.orderNo());
    }
}
