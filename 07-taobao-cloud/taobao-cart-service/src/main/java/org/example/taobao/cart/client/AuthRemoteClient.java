package org.example.taobao.cart.client;

import org.example.taobao.cart.config.RemoteServiceProperties;
import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 认证服务远程调用客户端，用于校验登录令牌。
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
     * 调用认证服务校验令牌并返回用户身份。
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
                throw new BusinessException(40111, response == null ? "登录校验失败" : response.getMessage());
            }
            return response.getData();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(40111, "登录校验失败，请重新登录");
        }
    }
}
