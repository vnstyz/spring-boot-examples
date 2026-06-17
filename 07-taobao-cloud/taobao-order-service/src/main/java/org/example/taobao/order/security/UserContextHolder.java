package org.example.taobao.order.security;

import org.example.taobao.common.exception.BusinessException;

/**
 * 用户上下文持有器，存储当前请求已认证用户。
 */
public final class UserContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * 写入登录用户。
     */
    public static void set(LoginUser loginUser) {
        CONTEXT.set(loginUser);
    }

    /**
     * 获取当前用户ID，不存在则抛错。
     */
    public static Long requireUserId() {
        LoginUser loginUser = CONTEXT.get();
        if (loginUser == null) {
            throw new BusinessException(40120, "未获取到登录用户");
        }
        return loginUser.userId();
    }

    /**
     * 清理上下文，避免线程复用污染。
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
