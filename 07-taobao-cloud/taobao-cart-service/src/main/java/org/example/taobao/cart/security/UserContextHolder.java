package org.example.taobao.cart.security;

import org.example.taobao.common.exception.BusinessException;

/**
 * 线程级用户上下文，便于控制层读取当前登录用户。
 */
public final class UserContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    /**
     * 写入当前请求用户。
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
            throw new BusinessException(40110, "未获取到登录用户信息");
        }
        return loginUser.userId();
    }

    /**
     * 清理当前线程上下文，防止线程复用污染。
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
