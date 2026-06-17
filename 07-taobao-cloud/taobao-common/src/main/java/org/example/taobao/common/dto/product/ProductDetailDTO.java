package org.example.taobao.common.dto.product;

import java.math.BigDecimal;

/**
 * 商品详情数据传输对象。
 */
public record ProductDetailDTO(
        Long productId,
        String title,
        String subTitle,
        String coverUrl,
        BigDecimal price,
        Integer stock,
        Boolean available
) {
}
