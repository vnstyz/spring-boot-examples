package org.example.taobao.common.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单后返回的核心信息。
 */
public record OrderCreateResponse(
        String orderNo,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
