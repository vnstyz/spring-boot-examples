package org.example.taobao.order.client;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.cart.CheckedCartItemDTO;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.order.config.RemoteServiceProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 购物车服务远程客户端，订单创建时读取并清理勾选项。
 */
@Component
public class CartRemoteClient {

    private final RestClient restClient;
    private final RemoteServiceProperties remoteServiceProperties;

    public CartRemoteClient(RestClient.Builder restClientBuilder, RemoteServiceProperties remoteServiceProperties) {
        this.restClient = restClientBuilder.build();
        this.remoteServiceProperties = remoteServiceProperties;
    }

    /**
     * 查询用户勾选购物车项。
     */
    public List<CheckedCartItemDTO> queryCheckedItems(Long userId) {
        try {
            ApiResponse<List<CheckedCartItemDTO>> response = restClient.get()
                    .uri(remoteServiceProperties.getCartBaseUrl() + "/api/cart/internal/checked/" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.getCode() != 0 || response.getData() == null) {
                throw new BusinessException(50020, response == null ? "购物车服务返回为空" : response.getMessage());
            }
            return response.getData();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(50020, "调用购物车服务失败");
        }
    }

    /**
     * 下单成功后清空勾选购物车项。
     */
    public void clearCheckedItems(Long userId) {
        try {
            ApiResponse<Void> response = restClient.delete()
                    .uri(remoteServiceProperties.getCartBaseUrl() + "/api/cart/internal/checked/" + userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.getCode() != 0) {
                throw new BusinessException(50021, response == null ? "清空购物车失败" : response.getMessage());
            }
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(50021, "调用购物车服务失败");
        }
    }
}
