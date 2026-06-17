package org.example.taobao.order.repository;

import org.example.taobao.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 订单明细数据访问层。
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 按订单主键查询明细列表。
     */
    List<OrderItem> findAllByOrderId(Long orderId);
}
