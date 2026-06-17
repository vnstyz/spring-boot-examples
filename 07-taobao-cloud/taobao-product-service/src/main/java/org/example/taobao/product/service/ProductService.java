package org.example.taobao.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taobao.common.dto.product.ProductDetailDTO;
import org.example.taobao.common.dto.product.StockOperateRequest;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.product.entity.Product;
import org.example.taobao.product.repository.ProductRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品业务服务，负责详情查询缓存和库存原子更新。
 */
@Service
public class ProductService {

    private static final String PRODUCT_CACHE_PREFIX = "product:detail:";

    private final ProductRepository productRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository productRepository,
                          StringRedisTemplate stringRedisTemplate,
                          ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 查询商品详情，优先读Redis缓存，未命中回源MySQL。
     */
    public ProductDetailDTO getProductDetail(Long productId) {
        String cacheKey = PRODUCT_CACHE_PREFIX + productId;
        String cacheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheJson != null && !cacheJson.isBlank()) {
            try {
                return objectMapper.readValue(cacheJson, ProductDetailDTO.class);
            } catch (Exception ignored) {
                // 缓存反序列化失败时继续走数据库查询，避免影响主链路。
            }
        }

        Product product = productRepository.findByIdAndEnabledTrue(productId)
                .orElseThrow(() -> new BusinessException(40401, "商品不存在或已下架"));

        ProductDetailDTO dto = toDetailDTO(product);
        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(dto), Duration.ofMinutes(30));
        } catch (Exception ignored) {
            // 缓存写入失败不影响主流程。
        }
        return dto;
    }

    /**
     * 批量查询商品详情，购物车渲染时使用。
     */
    public List<ProductDetailDTO> batchQuery(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findAllByIdInAndEnabledTrue(productIds)
                .stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

    /**
     * 原子扣减库存，并清理详情缓存。
     */
    @Transactional
    public void deductStock(StockOperateRequest request) {
        int updated = productRepository.deductStock(request.productId(), request.quantity());
        if (updated <= 0) {
            throw new BusinessException(41001, "库存不足，无法下单");
        }
        stringRedisTemplate.delete(PRODUCT_CACHE_PREFIX + request.productId());
    }

    /**
     * 回补库存，并清理详情缓存。
     */
    @Transactional
    public void restoreStock(StockOperateRequest request) {
        int updated = productRepository.restoreStock(request.productId(), request.quantity());
        if (updated <= 0) {
            throw new BusinessException(40402, "商品不存在，无法回补库存");
        }
        stringRedisTemplate.delete(PRODUCT_CACHE_PREFIX + request.productId());
    }

    private ProductDetailDTO toDetailDTO(Product product) {
        return new ProductDetailDTO(
                product.getId(),
                product.getTitle(),
                product.getSubTitle(),
                product.getCoverUrl(),
                product.getPrice(),
                product.getStock(),
                product.getEnabled()
        );
    }
}
