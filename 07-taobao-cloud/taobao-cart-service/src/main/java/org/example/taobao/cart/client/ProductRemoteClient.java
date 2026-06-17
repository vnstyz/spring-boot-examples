package org.example.taobao.cart.client;

import org.example.taobao.cart.config.RemoteServiceProperties;
import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.product.ProductDetailDTO;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务远程客户端，购物车渲染和结算时查询商品快照。
 */
@Component
public class ProductRemoteClient {

    private final RestClient restClient;
    private final RemoteServiceProperties remoteServiceProperties;

    public ProductRemoteClient(RestClient.Builder restClientBuilder, RemoteServiceProperties remoteServiceProperties) {
        this.restClient = restClientBuilder.build();
        this.remoteServiceProperties = remoteServiceProperties;
    }

    /**
     * 批量查询商品详情。
     */
    public List<ProductDetailDTO> batchQuery(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        String ids = productIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            ApiResponse<List<ProductDetailDTO>> response = restClient.get()
                    .uri(remoteServiceProperties.getProductBaseUrl() + "/api/products/internal/batch?ids=" + ids)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.getCode() != 0 || response.getData() == null) {
                throw new BusinessException(50010, response == null ? "商品服务返回为空" : response.getMessage());
            }
            return response.getData();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(50010, "调用商品服务失败");
        }
    }
}
