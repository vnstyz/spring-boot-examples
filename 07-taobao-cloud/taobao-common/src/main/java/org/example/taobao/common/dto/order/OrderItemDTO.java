package org.example.taobao.common.dto.order;

import java.math.BigDecimal;

/**
 * 订单项详情，用于订单详情页面展示。
 */
public record OrderItemDTO(
        Long productId,
        String productTitle,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal amount
) {
}
