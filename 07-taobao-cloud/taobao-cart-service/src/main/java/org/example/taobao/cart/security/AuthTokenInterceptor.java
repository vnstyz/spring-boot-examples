package org.example.taobao.cart.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.taobao.cart.client.AuthRemoteClient;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录令牌拦截器，拦截并校验购物车外部接口的登录态。
 */
@Component
public class AuthTokenInterceptor implements HandlerInterceptor {

    private final AuthRemoteClient authRemoteClient;

    public AuthTokenInterceptor(AuthRemoteClient authRemoteClient) {
        this.authRemoteClient = authRemoteClient;
    }

    /**
     * 请求进入控制器前校验Authorization并写入用户上下文。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(40112, "请先登录");
        }

        TokenValidationResponse tokenValidationResponse = authRemoteClient.validate(authorizationHeader);
        UserContextHolder.set(new LoginUser(
                tokenValidationResponse.userId(),
                tokenValidationResponse.username(),
                tokenValidationResponse.nickname()
        ));
        return true;
    }

    /**
     * 请求结束后清理上下文。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
