package org.example.taobao.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置：声明订单创建事件交换机、队列和绑定关系。
 */
@Configuration
public class RabbitOrderConfig {

    public static final String ORDER_EXCHANGE = "taobao.order.exchange";
    public static final String ORDER_CREATED_QUEUE = "taobao.order.created.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    /**
     * 订单事件交换机。
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 订单创建队列。
     */
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_CREATED_QUEUE, true);
    }

    /**
     * 将订单创建路由键绑定到队列。
     */
    @Bean
    public Binding orderCreatedBinding(DirectExchange orderExchange, Queue orderCreatedQueue) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(orderExchange)
                .with(ORDER_CREATED_ROUTING_KEY);
    }
}
