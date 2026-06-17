package org.example.taobao.cart.security;

/**
 * 当前登录用户上下文对象。
 */
public record LoginUser(Long userId, String username, String nickname) {
}
