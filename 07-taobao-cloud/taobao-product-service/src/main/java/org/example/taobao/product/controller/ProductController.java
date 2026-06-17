package org.example.taobao.product.controller;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.product.ProductDetailDTO;
import org.example.taobao.common.dto.product.StockOperateRequest;
import org.example.taobao.product.service.ProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品接口控制器。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 查询商品详情。
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailDTO> detail(@PathVariable Long productId) {
        return ApiResponse.success(productService.getProductDetail(productId));
    }

    /**
     * 商品服务内部批量查询接口。
     */
    @GetMapping("/internal/batch")
    public ApiResponse<List<ProductDetailDTO>> batch(@RequestParam("ids") String ids) {
        Set<Long> productIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        return ApiResponse.success(productService.batchQuery(productIds));
    }

    /**
     * 商品服务内部扣库存接口。
     */
    @PostMapping("/internal/deduct")
    public ApiResponse<Void> deduct(@Validated @RequestBody StockOperateRequest request) {
        productService.deductStock(request);
        return ApiResponse.success(null);
    }

    /**
     * 商品服务内部回补库存接口。
     */
    @PostMapping("/internal/restore")
    public ApiResponse<Void> restore(@Validated @RequestBody StockOperateRequest request) {
        productService.restoreStock(request);
        return ApiResponse.success(null);
    }
}
