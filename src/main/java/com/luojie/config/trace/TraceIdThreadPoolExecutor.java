package com.luojie.config.trace;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 支持TraceId传递的ThreadPoolExecutor
 * 用于在普通线程池中传递TraceId，确保日志链路完整
 */
public class TraceIdThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 构造方法
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 构造方法
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * 构造方法
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * 构造方法
     */
    public TraceIdThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                     RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(TraceIdRunnable.wrap(command));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(TraceIdRunnable.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(TraceIdRunnable.wrap(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(TraceIdCallable.wrap(task));
    }

    /**
     * 创建一个支持TraceId传递的ThreadPoolExecutor
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @return 包装后的TraceIdThreadPoolExecutor
     */
    public static TraceIdThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                  TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * 创建一个支持TraceId传递的ThreadPoolExecutor
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param threadFactory 线程工厂
     * @return 包装后的TraceIdThreadPoolExecutor
     */
    public static TraceIdThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                  ThreadFactory threadFactory) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * 创建一个支持TraceId传递的ThreadPoolExecutor
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param handler 拒绝策略
     * @return 包装后的TraceIdThreadPoolExecutor
     */
    public static TraceIdThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                  RejectedExecutionHandler handler) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
     * 创建一个支持TraceId传递的ThreadPoolExecutor
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 时间单位
     * @param workQueue 工作队列
     * @param threadFactory 线程工厂
     * @param handler 拒绝策略
     * @return 包装后的TraceIdThreadPoolExecutor
     */
    public static TraceIdThreadPoolExecutor create(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                  TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                                  ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new TraceIdThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                threadFactory, handler);
    }
}