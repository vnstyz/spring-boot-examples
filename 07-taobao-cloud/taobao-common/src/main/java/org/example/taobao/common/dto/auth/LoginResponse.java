package org.example.taobao.common.dto.auth;

import java.time.LocalDateTime;

/**
 * 登录成功后返回给前端的令牌信息。
 */
public record LoginResponse(
        Long userId,
        String nickname,
        String token,
        LocalDateTime expiresAt
) {
}
