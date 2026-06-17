package org.example.taobao.order.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.example.taobao.order.client.AuthRemoteClient;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 订单服务登录拦截器，统一校验令牌。
 */
@Component
public class AuthTokenInterceptor implements HandlerInterceptor {

    private final AuthRemoteClient authRemoteClient;

    public AuthTokenInterceptor(AuthRemoteClient authRemoteClient) {
        this.authRemoteClient = authRemoteClient;
    }

    /**
     * 请求进入控制器前校验登录态。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(40122, "请先登录");
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
     * 请求结束后清理线程变量。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}
