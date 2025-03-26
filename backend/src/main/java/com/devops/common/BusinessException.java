package com.devops.common;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑异常，可以指定错误码和错误信息
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 创建业务异常（使用默认500错误码）
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 创建业务异常（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}