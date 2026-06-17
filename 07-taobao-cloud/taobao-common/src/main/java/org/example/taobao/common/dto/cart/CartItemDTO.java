package org.example.taobao.common.dto.cart;

import java.math.BigDecimal;

/**
 * 购物车展示项。
 */
public record CartItemDTO(
        Long cartItemId,
        Long productId,
        String title,
        String coverUrl,
        BigDecimal price,
        Integer quantity,
        Boolean checked,
        BigDecimal amount
) {
}
