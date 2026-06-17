package org.example.taobao.common.dto.auth;

import java.time.LocalDateTime;

/**
 * 令牌校验成功后返回的用户身份信息。
 */
public record TokenValidationResponse(
        Long userId,
        String username,
        String nickname,
        LocalDateTime expiresAt
) {
}
