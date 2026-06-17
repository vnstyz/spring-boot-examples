package org.example.taobao.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 订单服务远程调用配置。
 */
@ConfigurationProperties(prefix = "taobao.remote")
public class RemoteServiceProperties {

    private String authBaseUrl;
    private String cartBaseUrl;
    private String productBaseUrl;

    public String getAuthBaseUrl() {
        return authBaseUrl;
    }

    public void setAuthBaseUrl(String authBaseUrl) {
        this.authBaseUrl = authBaseUrl;
    }

    public String getCartBaseUrl() {
        return cartBaseUrl;
    }

    public void setCartBaseUrl(String cartBaseUrl) {
        this.cartBaseUrl = cartBaseUrl;
    }

    public String getProductBaseUrl() {
        return productBaseUrl;
    }

    public void setProductBaseUrl(String productBaseUrl) {
        this.productBaseUrl = productBaseUrl;
    }
}
