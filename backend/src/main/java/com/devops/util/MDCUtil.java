package com.devops.util;

import org.slf4j.MDC;

/**
 * MDC工具类，用于管理traceId
 */
public class MDCUtil {
    public static final String TRACE_ID = "traceId";

    /**
     * 设置traceId
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 清除traceId
     */
    public static void clear() {
        MDC.clear();
    }
} 