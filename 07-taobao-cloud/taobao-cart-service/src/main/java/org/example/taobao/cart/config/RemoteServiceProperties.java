package org.example.taobao.cart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 购物车服务远程调用地址配置。
 */
@ConfigurationProperties(prefix = "taobao.remote")
public class RemoteServiceProperties {

    private String authBaseUrl;
    private String productBaseUrl;

    public String getAuthBaseUrl() {
        return authBaseUrl;
    }

    public void setAuthBaseUrl(String authBaseUrl) {
        this.authBaseUrl = authBaseUrl;
    }

    public String getProductBaseUrl() {
        return productBaseUrl;
    }

    public void setProductBaseUrl(String productBaseUrl) {
        this.productBaseUrl = productBaseUrl;
    }
}
