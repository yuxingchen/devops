package com.devops.util;

import java.util.UUID;

/**
 * 日志追踪工具类
 */
public class TraceIdUtil {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    /**
     * 设置traceId
     */
    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    /**
     * 获取当前线程的traceId
     */
    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        return traceId != null ? traceId : "";
    }

    /**
     * 清除traceId
     */
    public static void clear() {
        TRACE_ID.remove();
    }

    /**
     * 生成traceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
} 