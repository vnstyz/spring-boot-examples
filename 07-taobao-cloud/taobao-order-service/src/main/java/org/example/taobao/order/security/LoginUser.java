package org.example.taobao.order.security;

/**
 * 当前登录用户上下文模型。
 */
public record LoginUser(Long userId, String username, String nickname) {
}
