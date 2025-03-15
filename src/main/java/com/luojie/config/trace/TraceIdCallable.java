package com.luojie.config.trace;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * TraceId包装的Callable
 * 用于在使用Callable接口时传递TraceId，确保日志链路完整
 */
public class TraceIdCallable<V> implements Callable<V> {

    /**
     * 原始的Callable任务
     */
    private final Callable<V> callable;
    
    /**
     * 父线程的MDC上下文
     */
    private final Map<String, String> context;

    /**
     * 构造方法
     * @param callable 原始的Callable任务
     */
    public TraceIdCallable(Callable<V> callable) {
        this.callable = callable;
        this.context = MDC.getCopyOfContextMap();
    }

    /**
     * 构造方法
     * @param callable 原始的Callable任务
     * @param context 指定的MDC上下文
     */
    public TraceIdCallable(Callable<V> callable, Map<String, String> context) {
        this.callable = callable;
        this.context = context;
    }

    @Override
    public V call() throws Exception {
        // 获取当前MDC上下文
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
            // 设置传递的上下文
            if (context != null) {
                MDC.setContextMap(context);
            }
            // 执行任务
            return callable.call();
        } finally {
            // 恢复上下文
            if (previous == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(previous);
            }
        }
    }

    /**
     * 包装Callable，使其支持TraceId传递
     * @param callable 原始的Callable任务
     * @param <V> 返回值类型
     * @return 包装后的TraceIdCallable
     */
    public static <V> TraceIdCallable<V> wrap(Callable<V> callable) {
        return new TraceIdCallable<>(callable);
    }

    /**
     * 包装Callable，使其支持TraceId传递，使用指定的上下文
     * @param callable 原始的Callable任务
     * @param context 指定的MDC上下文
     * @param <V> 返回值类型
     * @return 包装后的TraceIdCallable
     */
    public static <V> TraceIdCallable<V> wrap(Callable<V> callable, Map<String, String> context) {
        return new TraceIdCallable<>(callable, context);
    }
}