package org.example.taobao.order.repository;

import org.example.taobao.order.entity.OrderMain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 订单主表数据访问层。
 */
public interface OrderRepository extends JpaRepository<OrderMain, Long> {

    /**
     * 按订单号和用户ID查询订单。
     */
    Optional<OrderMain> findByOrderNoAndUserId(String orderNo, Long userId);

    /**
     * 按订单号查询订单。
     */
    Optional<OrderMain> findByOrderNo(String orderNo);
}
