package org.example.taobao.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT配置参数，统一管理签名密钥和过期时间。
 */
@ConfigurationProperties(prefix = "taobao.jwt")
public class JwtProperties {

    private String secret;
    private long expireHours = 12;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpireHours() {
        return expireHours;
    }

    public void setExpireHours(long expireHours) {
        this.expireHours = expireHours;
    }
}
