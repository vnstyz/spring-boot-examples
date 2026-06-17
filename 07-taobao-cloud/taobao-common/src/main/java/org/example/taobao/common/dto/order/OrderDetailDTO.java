package org.example.taobao.common.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情返回对象。
 */
public record OrderDetailDTO(
        String orderNo,
        Long userId,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemDTO> items
) {
}
