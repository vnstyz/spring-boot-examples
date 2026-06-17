package org.example.taobao.order.config;

import org.example.taobao.order.security.AuthTokenInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置：注册订单接口的登录拦截器。
 */
@Configuration
@EnableConfigurationProperties(RemoteServiceProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final AuthTokenInterceptor authTokenInterceptor;

    public WebConfig(AuthTokenInterceptor authTokenInterceptor) {
        this.authTokenInterceptor = authTokenInterceptor;
    }

    /**
     * 仅拦截外部订单接口，内部MQ和系统端点不参与。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authTokenInterceptor)
                .addPathPatterns("/api/orders/**");
    }
}
