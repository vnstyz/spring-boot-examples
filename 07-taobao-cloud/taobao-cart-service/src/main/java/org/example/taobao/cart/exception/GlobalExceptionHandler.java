package org.example.taobao.cart.exception;

import org.example.taobao.common.dto.ApiResponse;
import org.example.taobao.common.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 购物车服务全局异常处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

    /**
     * 处理参数校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValid(MethodArgumentNotValidException exception) {
        String message = "参数校验失败";
        if (!exception.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fieldError = exception.getBindingResult().getFieldErrors().getFirst();
            message = fieldError.getDefaultMessage();
        }
        return ApiResponse.fail(40000, message);
    }

    /**
     * 兜底异常处理。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.fail(50000, exception.getMessage());
    }
}
