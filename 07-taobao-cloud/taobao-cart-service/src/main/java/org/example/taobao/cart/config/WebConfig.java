package org.example.taobao.cart.config;

import org.example.taobao.cart.security.AuthTokenInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置：注册登录拦截器并启用远程服务配置。
 */
@Configuration
@EnableConfigurationProperties(RemoteServiceProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final AuthTokenInterceptor authTokenInterceptor;

    public WebConfig(AuthTokenInterceptor authTokenInterceptor) {
        this.authTokenInterceptor = authTokenInterceptor;
    }

    /**
     * 拦截购物车外部接口，内部接口放行给订单服务调用。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authTokenInterceptor)
                .addPathPatterns("/api/cart/**")
                .excludePathPatterns("/api/cart/internal/**");
    }
}
