package org.example.taobao.order.client;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.product.StockOperateRequest;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.order.config.RemoteServiceProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 商品服务远程客户端，用于库存扣减和回补。
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
     * 扣减库存。
     */
    public void deductStock(StockOperateRequest request) {
        callStockApi("/api/products/internal/deduct", request, 50030);
    }

    /**
     * 回补库存。
     */
    public void restoreStock(StockOperateRequest request) {
        callStockApi("/api/products/internal/restore", request, 50031);
    }

    private void callStockApi(String path, StockOperateRequest request, int errorCode) {
        try {
            ApiResponse<Void> response = restClient.post()
                    .uri(remoteServiceProperties.getProductBaseUrl() + path)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.getCode() != 0) {
                throw new BusinessException(errorCode, response == null ? "商品服务返回为空" : response.getMessage());
            }
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(errorCode, "调用商品服务失败");
        }
    }
}
