package com.luojie.test.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

@Slf4j
public class ThreadLifeCycleTest {

    public static void main(String[] args) throws InterruptedException {
        // 1. 测试线程生命周期
        testThreadLifeCycle();
        
        // 2. 测试线程优先级和守护线程
        testThreadPriorityAndDaemon();
        
        // 3. 测试线程池生命周期
        testThreadPoolLifeCycle();
        
        // 4. 测试不同的线程池拒绝策略
        testRejectionPolicies();
    }

    private static void testThreadLifeCycle() throws InterruptedException {
        log.info("========= 测试线程生命周期 =========");
        
        // 1. NEW状态：创建线程但未启动
        Thread thread = new Thread(() -> {
            try {
                // RUNNABLE状态：线程正在运行
                log.info("线程正在运行...");
                
                // TIMED_WAITING状态：调用sleep()方法
                Thread.sleep(1000);
                
                // 模拟一些工作
                for (int i = 0; i < 100000; i++) {
                    Math.sqrt(i);
                }
            } catch (InterruptedException e) {
                log.error("线程被中断", e);
            }
        });
        
        log.info("1. 线程状态 (NEW): {}", thread.getState());
        
        // 启动线程
        thread.start();
        log.info("2. 线程状态 (应该是RUNNABLE): {}", thread.getState());
        
        // 等待一会儿，让线程进入TIMED_WAITING状态
        Thread.sleep(100);
        log.info("3. 线程状态 (应该是TIMED_WAITING): {}", thread.getState());
        
        // 等待线程结束
        thread.join();
        log.info("4. 线程状态 (TERMINATED): {}", thread.getState());
        
        // 演示BLOCKED状态
        final Object lock = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error("线程被中断", e);
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                // 这个线程会被阻塞，因为thread1持有锁
            }
        });
        
        thread1.start();
        Thread.sleep(100); // 确保thread1先获得锁
        thread2.start();
        Thread.sleep(100);
        
        log.info("5. thread2的状态 (应该是BLOCKED): {}", thread2.getState());
        
        // 等待线程结束
        thread1.join();
        thread2.join();
        
        // 演示WAITING状态
        log.info("\n演示WAITING状态：");
        final Object waitObject = new Object();
        
        Thread waitingThread = new Thread(() -> {
            synchronized (waitObject) {
                try {
                    // 调用wait()方法会释放锁，并进入WAITING状态
                    log.info("waitingThread开始等待...");
                    waitObject.wait();
                    log.info("waitingThread被唤醒!");
                } catch (InterruptedException e) {
                    log.error("线程被中断", e);
                }
            }
        });
        
        Thread notifyThread = new Thread(() -> {
            synchronized (waitObject) {
                // 唤醒在waitObject上等待的线程
                log.info("notifyThread准备唤醒等待线程...");
                waitObject.notify();
                log.info("notifyThread已发出唤醒通知");
            }
        });
        
        waitingThread.start();
        Thread.sleep(500); // 确保waitingThread进入等待状态
        log.info("6. waitingThread的状态 (应该是WAITING): {}", waitingThread.getState());
        
        notifyThread.start();
        
        // 等待线程结束
        waitingThread.join();
        notifyThread.join();
    }
    
    /**
     * 测试线程优先级和守护线程
     */
    private static void testThreadPriorityAndDaemon() throws InterruptedException {
        log.info("\n========= 测试线程优先级和守护线程 =========");
        
        // 1. 线程优先级
        log.info("线程优先级范围: {} ~ {}", Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);
        log.info("默认优先级: {}", Thread.NORM_PRIORITY);
        
        Thread highPriorityThread = new Thread(() -> {
            String name = Thread.currentThread().getName();
            log.info("{}开始执行", name);
            long count = 0;
            for (int i = 0; i < 10_000_000; i++) {
                count += i;
            }
            log.info("{}执行完成，计算结果: {}", name, count);
        }, "高优先级线程");
        
        Thread lowPriorityThread = new Thread(() -> {
            String name = Thread.currentThread().getName();
            log.info("{}开始执行", name);
            long count = 0;
            for (int i = 0; i < 10_000_000; i++) {
                count += i;
            }
            log.info("{}执行完成，计算结果: {}", name, count);
        }, "低优先级线程");
        
        // 设置线程优先级
        highPriorityThread.setPriority(Thread.MAX_PRIORITY); // 10
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);  // 1
        
        log.info("高优先级线程的优先级: {}", highPriorityThread.getPriority());
        log.info("低优先级线程的优先级: {}", lowPriorityThread.getPriority());
        
        // 启动线程
        lowPriorityThread.start();
        highPriorityThread.start();
        
        // 等待线程结束
        lowPriorityThread.join();
        highPriorityThread.join();
        
        // 2. 守护线程
        log.info("\n测试守护线程:");
        Thread daemonThread = new Thread(() -> {
            try {
                log.info("守护线程开始执行，是否为守护线程: {}", Thread.currentThread().isDaemon());
                while (true) {
                    log.info("守护线程正在运行...");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                log.error("守护线程被中断", e);
            }
        });
        
        // 设置为守护线程 - 必须在启动前设置
        daemonThread.setDaemon(true);
        daemonThread.start();
        
        // 主线程休眠一段时间后结束
        log.info("主线程将在2秒后结束，守护线程也将随之结束");
        Thread.sleep(2000);
    }
    
    private static void testThreadPoolLifeCycle() throws InterruptedException {
        log.info("\n========= 测试线程池生命周期 =========");
        
        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,  // 核心线程数 - 线程池保持的最小线程数
            4,  // 最大线程数 - 线程池允许的最大线程数
            60L, // 空闲线程存活时间 - 超过核心线程数的线程空闲多久会被回收
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), // 工作队列 - 存放待执行任务的队列
            new ThreadFactory() {
                private int count = 1;
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "CustomThread-" + count++);
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略 - 当队列满且线程数达到最大时的处理策略
        );
        
        // 预启动所有核心线程
        log.info("预启动所有核心线程");
        executor.prestartAllCoreThreads();
        log.info("核心线程预启动后的活跃线程数: {}", executor.getActiveCount());
        
        log.info("1. 线程池初始化完成");
        log.info("   活跃线程数: {}", executor.getActiveCount());
        log.info("   核心线程数: {}", executor.getCorePoolSize());
        log.info("   最大线程数: {}", executor.getMaximumPoolSize());
        log.info("   线程池大小: {}", executor.getPoolSize());
        
        // 提交任务，观察线程池变化
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.execute(() -> {
                try {
                    log.info("任务{}开始执行，执行线程：{}", taskId, Thread.currentThread().getName());
                    Thread.sleep(2000); // 模拟任务执行
                    log.info("任务{}执行完成", taskId);
                } catch (InterruptedException e) {
                    log.error("任务被中断", e);
                }
            });
            
            log.info("\n提交任务{}后的线程池状态：", i);
            log.info("   活跃线程数: {}", executor.getActiveCount());
            log.info("   队列任务数: {}", executor.getQueue().size());
            log.info("   线程池大小: {}", executor.getPoolSize());
            log.info("   已完成任务数: {}", executor.getCompletedTaskCount());
        }
        
        // 等待一段时间，观察线程池状态
        Thread.sleep(1000);
        log.info("\n1秒后的线程池状态：");
        log.info("   活跃线程数: {}", executor.getActiveCount());
        log.info("   队列任务数: {}", executor.getQueue().size());
        log.info("   线程池大小: {}", executor.getPoolSize());
        
        // 关闭线程池
        log.info("\n开始关闭线程池...");
        executor.shutdown();
        
        // 等待线程池关闭
        boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
        log.info("线程池是否已终止: {}", terminated);
        log.info("   活跃线程数: {}", executor.getActiveCount());
        log.info("   队列任务数: {}", executor.getQueue().size());
        log.info("   线程池状态: {}", executor.isShutdown() ? "已关闭" : "未关闭");
        log.info("   线程池是否已终止: {}", executor.isTerminated());
    }
    
    /**
     * 测试不同的线程池拒绝策略
     */
    private static void testRejectionPolicies() {
        log.info("\n========= 测试线程池拒绝策略 =========");
        
        // 创建一个固定大小的任务列表
        Runnable task = () -> {
            try {
                log.info("任务开始执行，线程：{}", Thread.currentThread().getName());
                Thread.sleep(1000);
                log.info("任务执行完成，线程：{}", Thread.currentThread().getName());
            } catch (InterruptedException e) {
                log.error("任务被中断", e);
            }
        };
        
        // 1. AbortPolicy - 默认策略，抛出RejectedExecutionException
        testRejectionPolicy("AbortPolicy", new ThreadPoolExecutor.AbortPolicy(), task);
        
        // 2. CallerRunsPolicy - 在调用者线程中执行任务
        testRejectionPolicy("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy(), task);
        
        // 3. DiscardPolicy - 直接丢弃任务，不抛出异常
        testRejectionPolicy("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy(), task);
        
        // 4. DiscardOldestPolicy - 丢弃队列中最早的任务，然后尝试重新提交当前任务
        testRejectionPolicy("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy(), task);
        
        // 5. 自定义拒绝策略
        testRejectionPolicy("自定义拒绝策略", (r, executor) -> {
            log.info("自定义拒绝策略：记录被拒绝的任务，可以写入日志或者持久化");
        }, task);
    }
    
    /**
     * 测试特定的拒绝策略
     */
    private static void testRejectionPolicy(String policyName, RejectedExecutionHandler policy, Runnable task) {
        log.info("\n测试 {} 拒绝策略", policyName);
        
        // 创建一个容易触发拒绝策略的小线程池
        // 核心线程数=1，最大线程数=1，队列容量=1
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1),
            Executors.defaultThreadFactory(),
            policy
        );
        
        try {
            // 提交3个任务，应该会触发拒绝策略
            // 第1个任务会被线程执行
            // 第2个任务会进入队列
            // 第3个任务会触发拒绝策略
            for (int i = 1; i <= 3; i++) {
                final int taskId = i;
                log.info("提交任务 {}", taskId);
                try {
                    executor.execute(() -> {
                        try {
                            log.info("任务 {} 开始执行，线程：{}", taskId, Thread.currentThread().getName());
                            Thread.sleep(2000); // 确保任务执行时间足够长，以便观察拒绝策略
                            log.info("任务 {} 执行完成", taskId);
                        } catch (InterruptedException e) {
                            log.error("任务被中断", e);
                        }
                    });
                    log.info("任务 {} 提交成功", taskId);
                } catch (Exception e) {
                    log.error("任务 {} 提交失败: {}", taskId, e.getMessage());
                }
            }
        } finally {
            // 等待所有任务完成
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("等待线程池关闭被中断", e);
            }
        }
    }
}