package org.example.taobao.common.dto.cart;

import java.math.BigDecimal;

/**
 * 订单服务读取购物车勾选项时使用的内部DTO。
 */
public record CheckedCartItemDTO(
        Long productId,
        Integer quantity,
        String title,
        BigDecimal price
) {
}
