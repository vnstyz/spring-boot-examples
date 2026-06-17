package org.example.taobao.common.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 库存变更请求参数（扣减或回补共用）。
 */
public record StockOperateRequest(
        @NotNull(message = "商品ID不能为空")
        Long productId,
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        Integer quantity
) {
}
