package com.luojie.test.trace;

import com.luojie.config.trace.TraceIdCallable;
import com.luojie.config.trace.TraceIdCompletableFuture;
import com.luojie.config.trace.TraceIdContext;
import com.luojie.config.trace.TraceIdInterceptor;
import com.luojie.config.trace.TraceIdRunnable;
import com.luojie.config.trace.TraceIdThreadPoolExecutor;
import com.luojie.config.trace.TraceIdThreadPoolTaskExecutor;
import com.luojie.util.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * TraceId Redis测试类
 * 用于测试在多线程、线程池环境下的traceId传递，结合Redis操作
 */
@Slf4j
@RestController
@RequestMapping("/trace/redis")
public class TraceIdRedisTest {

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    /**
     * 测试在普通线程中使用Redis操作并传递TraceId
     */
    @GetMapping("/thread")
    public String testThreadWithRedis() throws InterruptedException {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 生成唯一的Redis键
        String redisKey = "trace:thread:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 获取当前上下文
        final var context = TraceIdContext.getContext();
        
        // 创建新线程并使用TraceIdRunnable包装
        Thread thread = new Thread(TraceIdRunnable.wrap(() -> {
            log.info("子线程开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
            // 执行Redis写入操作
            redisServiceUtil.set(redisKey, value);
            log.info("子线程Redis写入完成，key={}, value={}", redisKey, value);
            
            // 执行Redis读取操作
            String result = redisServiceUtil.get(redisKey);
            log.info("子线程Redis读取完成，key={}, result={}", redisKey, result);
            
            // 执行Redis删除操作
            redisServiceUtil.delete(redisKey);
            log.info("子线程Redis删除完成，key={}", redisKey);
        }, context));
        
        thread.start();
        thread.join();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in thread with Redis: " + traceId;
    }

    /**
     * 测试在线程池中使用Redis操作并传递TraceId
     */
    @GetMapping("/threadpool")
    public String testThreadPoolWithRedis() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建支持TraceId传递的线程池
        TraceIdThreadPoolExecutor executor = TraceIdThreadPoolExecutor.create(
                5, 10, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        
        // 生成唯一的Redis键
        String redisKey = "trace:threadpool:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 提交任务到线程池
        Future<String> future = executor.submit(() -> {
            log.info("线程池任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
            // 执行Redis写入操作
            redisServiceUtil.set(redisKey, value);
            log.info("线程池任务Redis写入完成，key={}, value={}", redisKey, value);
            
            // 执行Redis读取操作
            String result = redisServiceUtil.get(redisKey);
            log.info("线程池任务Redis读取完成，key={}, result={}", redisKey, result);
            
            // 执行Redis删除操作
            redisServiceUtil.delete(redisKey);
            log.info("线程池任务Redis删除完成，key={}", redisKey);
            
            return "success";
        });
        
        String result = future.get();
        executor.shutdown();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in thread pool with Redis: " + traceId + ", result: " + result;
    }

    /**
     * 测试在CompletableFuture中使用Redis操作并传递TraceId
     */
    @GetMapping("/completable")
    public String testCompletableFutureWithRedis() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建支持TraceId传递的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 生成唯一的Redis键
        String redisKey = "trace:completable:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 获取当前上下文
        final var context = TraceIdContext.getContext();
        
        // 使用CompletableFuture并手动传递TraceId
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 设置上下文
            TraceIdContext.setContext(context);
            try {
                log.info("CompletableFuture任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
                // 执行Redis写入操作
                redisServiceUtil.set(redisKey, value);
                log.info("CompletableFuture任务Redis写入完成，key={}, value={}", redisKey, value);
                
                // 执行Redis读取操作
                String result = redisServiceUtil.get(redisKey);
                log.info("CompletableFuture任务Redis读取完成，key={}, result={}", redisKey, result);
                
                return result;
            } finally {
                // 清理上下文
                TraceIdContext.clearContext();
            }
        }, executor).thenApplyAsync(result -> {
            // 设置上下文
            TraceIdContext.setContext(context);
            try {
                log.info("CompletableFuture链式任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
                // 执行Redis删除操作
                redisServiceUtil.delete(redisKey);
                log.info("CompletableFuture链式任务Redis删除完成，key={}", redisKey);
                
                return "处理完成: " + result;
            } finally {
                // 清理上下文
                TraceIdContext.clearContext();
            }
        }, executor);
        
        String result = future.get();
        executor.shutdown();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in CompletableFuture with Redis: " + traceId + ", result: " + result;
    }

    /**
     * 测试使用TraceIdCompletableFuture工具类传递TraceId
     */
    @GetMapping("/trace-completable")
    public String testTraceIdCompletableFuture() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建支持TraceId传递的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 生成唯一的Redis键
        String redisKey = "trace:trace-completable:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 使用TraceIdCompletableFuture自动传递TraceId
        CompletableFuture<String> future = TraceIdCompletableFuture.supplyAsync(() -> {
            log.info("TraceIdCompletableFuture任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
            // 执行Redis写入操作
            redisServiceUtil.set(redisKey, value);
            log.info("TraceIdCompletableFuture任务Redis写入完成，key={}, value={}", redisKey, value);
            
            // 执行Redis读取操作
            String result = redisServiceUtil.get(redisKey);
            log.info("TraceIdCompletableFuture任务Redis读取完成，key={}, result={}", redisKey, result);
            
            return result;
        }, executor).thenApplyAsync(result -> {
            log.info("TraceIdCompletableFuture链式任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
            // 执行Redis删除操作
            redisServiceUtil.delete(redisKey);
            log.info("TraceIdCompletableFuture链式任务Redis删除完成，key={}", redisKey);
            
            return "处理完成: " + result;
        }, executor);
        
        String result = future.get();
        executor.shutdown();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in TraceIdCompletableFuture with Redis: " + traceId + ", result: " + result;
    }

    /**
     * 测试在Spring异步任务中传递TraceId
     */
    @GetMapping("/spring-async")
    public String testSpringAsync() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 生成唯一的Redis键
        String redisKey = "trace:spring-async:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 执行异步任务
        CompletableFuture<String> future = asyncRedisOperation(redisKey, value);
        String result = future.get();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in Spring async with Redis: " + traceId + ", result: " + result;
    }
    
    /**
     * Spring异步方法
     */
    @Async("traceIdTaskExecutor")
    public CompletableFuture<String> asyncRedisOperation(String redisKey, String value) {
        log.info("Spring异步任务开始执行Redis操作，TraceId: {}", TraceIdContext.getTraceId());
        // 执行Redis写入操作
        redisServiceUtil.set(redisKey, value);
        log.info("Spring异步任务Redis写入完成，key={}, value={}", redisKey, value);
        
        // 执行Redis读取操作
        String result = redisServiceUtil.get(redisKey);
        log.info("Spring异步任务Redis读取完成，key={}, result={}", redisKey, result);
        
        // 执行Redis删除操作
        redisServiceUtil.delete(redisKey);
        log.info("Spring异步任务Redis删除完成，key={}", redisKey);
        
        return CompletableFuture.completedFuture("处理完成: " + result);
    }

    /**
     * 测试在多线程并发环境下的TraceId传递
     */
    @GetMapping("/concurrent")
    public String testConcurrentTraceId() throws InterruptedException {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建支持TraceId传递的线程池
        TraceIdThreadPoolExecutor executor = TraceIdThreadPoolExecutor.create(
                10, 20, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        
        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<String> redisKeys = new ArrayList<>();
        
        // 提交多个并发任务
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            String redisKey = "trace:concurrent:" + index + ":" + UUID.randomUUID().toString();
            String value = "并发测试数据-" + index + "-" + System.currentTimeMillis();
            redisKeys.add(redisKey);
            
            executor.submit(() -> {
                try {
                    log.info("并发任务[{}]开始执行Redis操作，TraceId: {}", index, TraceIdContext.getTraceId());
                    // 执行Redis写入操作
                    redisServiceUtil.set(redisKey, value);
                    log.info("并发任务[{}]Redis写入完成，key={}, value={}", index, redisKey, value);
                    
                    // 执行Redis读取操作
                    String result = redisServiceUtil.get(redisKey);
                    log.info("并发任务[{}]Redis读取完成，key={}, result={}", index, redisKey, result);
                    
                    Thread.sleep(100); // 模拟业务处理
                    
                    // 执行Redis删除操作
                    redisServiceUtil.delete(redisKey);
                    log.info("并发任务[{}]Redis删除完成，key={}", index, redisKey);
                    
                    return "success";
                } catch (Exception e) {
                    log.error("并发任务[{}]执行异常", index, e);
                    return "error";
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in concurrent tasks with Redis: " + traceId + ", tasks: " + taskCount;
    }

    /**
     * 测试在嵌套线程中传递TraceId
     */
    @GetMapping("/nested")
    public String testNestedThreadWithRedis() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 生成唯一的Redis键
        String redisKey = "trace:nested:" + UUID.randomUUID().toString();
        String value = "测试数据-" + System.currentTimeMillis();
        
        // 获取当前上下文
        final var context = TraceIdContext.getContext();
        
        // 创建外层线程
        Thread outerThread = new Thread(TraceIdRunnable.wrap(() -> {
            log.info("外层线程开始执行，TraceId: {}", TraceIdContext.getTraceId());
            // 执行Redis写入操作
            redisServiceUtil.set(redisKey, value);
            log.info("外层线程Redis写入完成，key={}, value={}", redisKey, value);
            
            // 创建内层线程
            Thread innerThread = new Thread(TraceIdRunnable.wrap(() -> {
                log.info("内层线程开始执行，TraceId: {}", TraceIdContext.getTraceId());
                // 执行Redis读取操作
                String result = redisServiceUtil.get(redisKey);
                log.info("内层线程Redis读取完成，key={}, result={}", redisKey, result);
                
                // 执行Redis删除操作
                redisServiceUtil.delete(redisKey);
                log.info("内层线程Redis删除完成，key={}", redisKey);
            }));
            
            try {
                innerThread.start();
                innerThread.join();
            } catch (InterruptedException e) {
                log.error("内层线程执行异常", e);
            }
            
            log.info("外层线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        }, context));
        
        outerThread.start();
        outerThread.join();
        
        log.info("主线程执行完成，TraceId: {}", TraceIdContext.getTraceId());
        return "TraceId in nested threads with Redis: " + traceId;
    }
}