package com.luojie.config.trace;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 支持TraceId传递的CompletableFuture
 * 用于在CompletableFuture异步任务中传递TraceId，确保日志链路完整
 */
public class TraceIdCompletableFuture {

    /**
     * 创建一个支持TraceId传递的CompletableFuture
     * @param <T> 返回值类型
     * @return 包装后的CompletableFuture
     */
    public static <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> supplier) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                return supplier.get();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 创建一个支持TraceId传递的CompletableFuture，使用指定的线程池
     * @param <T> 返回值类型
     * @param executor 线程池
     * @return 包装后的CompletableFuture
     */
    public static <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> supplier, Executor executor) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                return supplier.get();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        }, executor);
    }

    /**
     * 创建一个支持TraceId传递的CompletableFuture，无返回值
     * @return 包装后的CompletableFuture
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return CompletableFuture.runAsync(() -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                runnable.run();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 创建一个支持TraceId传递的CompletableFuture，无返回值，使用指定的线程池
     * @param executor 线程池
     * @return 包装后的CompletableFuture
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return CompletableFuture.runAsync(() -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                runnable.run();
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        }, executor);
    }

    /**
     * 包装thenApply方法，使其支持TraceId传递
     * @param future 原始CompletableFuture
     * @param fn 转换函数
     * @param <T> 输入类型
     * @param <U> 输出类型
     * @return 包装后的CompletableFuture
     */
    public static <T, U> CompletableFuture<U> thenApply(CompletableFuture<T> future, Function<? super T, ? extends U> fn) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return future.thenApply(t -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                return fn.apply(t);
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 包装thenAccept方法，使其支持TraceId传递
     * @param future 原始CompletableFuture
     * @param action 消费函数
     * @param <T> 输入类型
     * @return 包装后的CompletableFuture
     */
    public static <T> CompletableFuture<Void> thenAccept(CompletableFuture<T> future, Consumer<? super T> action) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return future.thenAccept(t -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                action.accept(t);
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 包装exceptionally方法，使其支持TraceId传递
     * @param future 原始CompletableFuture
     * @param fn 异常处理函数
     * @param <T> 返回类型
     * @return 包装后的CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionally(CompletableFuture<T> future, Function<Throwable, ? extends T> fn) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return future.exceptionally(ex -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                return fn.apply(ex);
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 包装handle方法，使其支持TraceId传递
     * @param future 原始CompletableFuture
     * @param fn 处理函数
     * @param <T> 输入类型
     * @param <U> 输出类型
     * @return 包装后的CompletableFuture
     */
    public static <T, U> CompletableFuture<U> handle(CompletableFuture<T> future, BiFunction<? super T, Throwable, ? extends U> fn) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return future.handle((t, ex) -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                return fn.apply(t, ex);
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }

    /**
     * 包装whenComplete方法，使其支持TraceId传递
     * @param future 原始CompletableFuture
     * @param action 完成动作
     * @param <T> 输入类型
     * @return 包装后的CompletableFuture
     */
    public static <T> CompletableFuture<T> whenComplete(CompletableFuture<T> future, BiConsumer<? super T, ? super Throwable> action) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return future.whenComplete((t, ex) -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (context != null) {
                    MDC.setContextMap(context);
                }
                action.accept(t, ex);
            } finally {
                if (previous == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(previous);
                }
            }
        });
    }
}