package com.luojie.config.trace;

import org.slf4j.MDC;

import java.util.Map;

/**
 * TraceId包装的Runnable
 * 用于在普通线程中传递TraceId，确保日志链路完整
 */
public class TraceIdRunnable implements Runnable {

    /**
     * 原始的Runnable任务
     */
    private final Runnable runnable;
    
    /**
     * 父线程的MDC上下文
     */
    private final Map<String, String> context;

    /**
     * 构造方法
     * @param runnable 原始的Runnable任务
     */
    public TraceIdRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.context = MDC.getCopyOfContextMap();
    }

    /**
     * 构造方法
     * @param runnable 原始的Runnable任务
     * @param context 指定的MDC上下文
     */
    public TraceIdRunnable(Runnable runnable, Map<String, String> context) {
        this.runnable = runnable;
        this.context = context;
    }

    @Override
    public void run() {
        // 获取当前MDC上下文
        Map<String, String> previous = MDC.getCopyOfContextMap();
        try {
            // 设置传递的上下文
            if (context != null) {
                MDC.setContextMap(context);
            }
            // 执行任务
            runnable.run();
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
     * 包装Runnable，使其支持TraceId传递
     * @param runnable 原始的Runnable任务
     * @return 包装后的TraceIdRunnable
     */
    public static TraceIdRunnable wrap(Runnable runnable) {
        return new TraceIdRunnable(runnable);
    }

    /**
     * 包装Runnable，使其支持TraceId传递，使用指定的上下文
     * @param runnable 原始的Runnable任务
     * @param context 指定的MDC上下文
     * @return 包装后的TraceIdRunnable
     */
    public static TraceIdRunnable wrap(Runnable runnable, Map<String, String> context) {
        return new TraceIdRunnable(runnable, context);
    }
}