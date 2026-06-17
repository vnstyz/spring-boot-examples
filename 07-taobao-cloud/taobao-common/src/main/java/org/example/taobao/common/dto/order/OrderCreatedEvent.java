package org.example.taobao.common.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单创建事件，用于通过MQ做异步处理。
 */
public record OrderCreatedEvent(
        String orderNo,
        Long userId,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
