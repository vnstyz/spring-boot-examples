package org.example.taobao.common.dto.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车结算结果。
 */
public record CartSettleDTO(
        List<CartItemDTO> items,
        BigDecimal totalAmount
) {
}
