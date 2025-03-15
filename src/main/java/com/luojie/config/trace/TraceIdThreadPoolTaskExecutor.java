package com.luojie.config.trace;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 支持traceId传递的线程池
 * 用于在异步任务中传递traceId，确保日志链路完整
 */
public class TraceIdThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(Runnable task) {
        super.execute(wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrap(task, MDC.getCopyOfContextMap()));
    }

    /**
     * 包装Runnable，使其能够传递上下文信息
     */
    private Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
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
        };
    }

    /**
     * 包装Callable，使其能够传递上下文信息
     */
    private <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
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
        };
    }
}