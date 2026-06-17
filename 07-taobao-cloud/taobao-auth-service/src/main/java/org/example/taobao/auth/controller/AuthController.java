package org.example.taobao.auth.controller;

import jakarta.validation.Valid;
import org.example.taobao.auth.service.AuthService;
import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.dto.auth.LoginRequest;
import org.example.taobao.common.dto.auth.LoginResponse;
import org.example.taobao.common.dto.auth.TokenValidationResponse;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口控制器，提供登录和令牌校验API。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口。
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authService.login(loginRequest));
    }

    /**
     * 令牌校验接口，供网关或其他服务调用。
     */
    @GetMapping("/validate")
    public ApiResponse<TokenValidationResponse> validate(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        return ApiResponse.success(authService.validateToken(token));
    }

    /**
     * 从Authorization头中提取Bearer令牌。
     */
    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(40103, "Authorization头缺失或格式错误");
        }
        return authorizationHeader.substring("Bearer ".length());
    }
}
