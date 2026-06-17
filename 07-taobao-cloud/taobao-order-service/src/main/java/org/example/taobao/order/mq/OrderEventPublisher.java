package org.example.taobao.order.mq;

import org.example.taobao.common.dto.order.OrderCreatedEvent;
import org.example.taobao.order.config.RabbitOrderConfig;
import org.example.taobao.order.entity.OrderMain;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单事件发布器，下单后发送订单创建消息。
 */
@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送订单创建事件。
     */
    public void publishCreated(OrderMain orderMain) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderMain.getOrderNo(),
                orderMain.getUserId(),
                orderMain.getTotalAmount(),
                orderMain.getCreatedAt()
        );
        rabbitTemplate.convertAndSend(
                RabbitOrderConfig.ORDER_EXCHANGE,
                RabbitOrderConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );
    }
}
