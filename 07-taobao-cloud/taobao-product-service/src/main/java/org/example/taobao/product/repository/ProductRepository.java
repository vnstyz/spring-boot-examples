package org.example.taobao.product.repository;

import org.example.taobao.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 商品数据访问层，包含商品查询与库存更新能力。
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 查询上架中的商品。
     */
    Optional<Product> findByIdAndEnabledTrue(Long id);

    /**
     * 批量查询上架中的商品。
     */
    List<Product> findAllByIdInAndEnabledTrue(Collection<Long> ids);

    /**
     * 原子扣减库存，确保并发下库存不被扣成负数。
     */
    @Modifying
    @Query("update Product p set p.stock = p.stock - :quantity where p.id = :productId and p.enabled = true and p.stock >= :quantity")
    int deductStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 回补库存，用于下单失败补偿。
     */
    @Modifying
    @Query("update Product p set p.stock = p.stock + :quantity where p.id = :productId and p.enabled = true")
    int restoreStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
