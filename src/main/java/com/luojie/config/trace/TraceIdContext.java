package com.luojie.config.trace;

import org.slf4j.MDC;

import java.util.Map;

/**
 * TraceId上下文工具类
 * 用于在多线程环境中传递traceId
 */
public class TraceIdContext {

    /**
     * 获取当前线程的MDC上下文
     * @return MDC上下文Map
     */
    public static Map<String, String> getContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 设置MDC上下文
     * @param context MDC上下文Map
     */
    public static void setContext(Map<String, String> context) {
        if (context == null) {
            MDC.clear();
        } else {
            MDC.setContextMap(context);
        }
    }

    /**
     * 清除MDC上下文
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * 获取当前的traceId
     * @return traceId
     */
    public static String getTraceId() {
        return MDC.get(TraceIdInterceptor.TRACE_ID);
    }
}