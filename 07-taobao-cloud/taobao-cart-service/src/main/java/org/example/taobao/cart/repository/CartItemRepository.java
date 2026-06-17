package org.example.taobao.cart.repository;

import org.example.taobao.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车数据访问层。
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 按用户+商品查询购物车记录。
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * 查询用户购物车全部项。
     */
    List<CartItem> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    /**
     * 查询用户勾选项，用于结算和下单。
     */
    List<CartItem> findAllByUserIdAndCheckedTrue(Long userId);

    /**
     * 查询某个用户下特定购物车项。
     */
    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    /**
     * 删除用户全部勾选项。
     */
    void deleteAllByUserIdAndCheckedTrue(Long userId);
}
