package com.devops.common;

import lombok.Data;

/**
 * 统一响应结果封装类
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> {
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（有数据）
     *
     * @param data 响应数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 错误响应（默认500错误码）
     *
     * @param message 错误信息
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 错误响应（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
} 