package org.example.taobao.common.exception;

/**
 * 业务异常，用于主动中断流程并返回可读错误信息。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(50001, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
