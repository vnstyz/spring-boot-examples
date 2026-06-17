package org.example.taobao.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.taobao.auth.config.JwtProperties;
import org.example.taobao.auth.entity.UserAccount;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * JWT令牌服务，负责签发和解析令牌。
 */
@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成JWT，并返回令牌与过期时间。
     */
    public GeneratedToken generate(UserAccount userAccount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(jwtProperties.getExpireHours());
        String jti = UUID.randomUUID().toString().replace("-", "");

        String token = Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userAccount.getId()))
                .claim("username", userAccount.getUsername())
                .claim("nickname", userAccount.getNickname())
                .issuedAt(toDate(now))
                .expiration(toDate(expiresAt))
                .signWith(getSecretKey())
                .compact();

        return new GeneratedToken(token, jti, expiresAt);
    }

    /**
     * 解析并校验JWT，失败时抛出业务异常。
     */
    public ParsedToken parse(String token) {
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.parseLong(payload.getSubject());
            String username = payload.get("username", String.class);
            String nickname = payload.get("nickname", String.class);
            String jti = payload.getId();
            LocalDateTime expiresAt = toLocalDateTime(payload.getExpiration());
            return new ParsedToken(userId, username, nickname, jti, expiresAt);
        } catch (Exception ex) {
            throw new BusinessException(40101, "令牌无效或已过期");
        }
    }

    private SecretKey getSecretKey() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("taobao.jwt.secret 长度至少为32位");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 令牌签发结果。
     */
    public record GeneratedToken(String token, String jti, LocalDateTime expiresAt) {
    }

    /**
     * 令牌解析结果。
     */
    public record ParsedToken(Long userId, String username, String nickname, String jti, LocalDateTime expiresAt) {
    }
}
