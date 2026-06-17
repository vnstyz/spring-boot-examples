package org.example.taobao.common.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 购物车新增商品请求参数。
 */
public record AddCartItemRequest(
        @NotNull(message = "商品ID不能为空")
        Long productId,
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "购买数量必须大于0")
        Integer quantity
) {
}
