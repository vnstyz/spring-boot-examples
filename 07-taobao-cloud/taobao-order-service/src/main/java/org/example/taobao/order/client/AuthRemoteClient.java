package org.example.taobao.order.client;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.order.config.RemoteServiceProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 认证服务远程客户端，负责订单接口登录校验。
 */
@Component
public class AuthRemoteClient {

    private final RestClient restClient;
    private final RemoteServiceProperties remoteServiceProperties;

    public AuthRemoteClient(RestClient.Builder restClientBuilder, RemoteServiceProperties remoteServiceProperties) {
        this.restClient = restClientBuilder.build();
        this.remoteServiceProperties = remoteServiceProperties;
    }

    /**
     * 远程校验令牌。
     */
    public TokenValidationResponse validate(String authorizationHeader) {
        try {
            ApiResponse<TokenValidationResponse> response = restClient.get()
                    .uri(remoteServiceProperties.getAuthBaseUrl() + "/api/auth/validate")
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (response == null || response.getCode() != 0 || response.getData() == null) {
                throw new BusinessException(40121, response == null ? "登录校验失败" : response.getMessage());
            }
            return response.getData();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(40121, "登录校验失败，请重新登录");
        }
    }
}
