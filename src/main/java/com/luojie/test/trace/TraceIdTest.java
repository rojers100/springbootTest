package com.luojie.test.trace;

import com.luojie.config.trace.TraceIdContext;
import com.luojie.config.trace.TraceIdInterceptor;
import com.luojie.config.trace.TraceIdThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TraceId测试类
 * 用于测试TraceId在不同场景下的传递
 */
@Slf4j
@RestController
@RequestMapping("/trace")
public class TraceIdTest {

    /**
     * 测试普通请求中的TraceId
     * 由TraceIdInterceptor自动生成并注入
     */
    @GetMapping("/normal")
    public String testNormal() {
        String traceId = MDC.get(TraceIdInterceptor.TRACE_ID);
        log.info("当前请求的TraceId: {}", traceId);
        return "TraceId: " + traceId;
    }

    /**
     * 测试在普通线程中手动传递TraceId
     */
    @GetMapping("/thread")
    public String testThread() throws InterruptedException {
        String traceId = MDC.get(TraceIdInterceptor.TRACE_ID);
        log.info("主线程TraceId: {}", traceId);
        
        // 创建新线程并手动传递TraceId
        Thread thread = new Thread(() -> {
            try {
                // 设置上下文
                MDC.put(TraceIdInterceptor.TRACE_ID, traceId);
                log.info("子线程获取到的TraceId: {}", MDC.get(TraceIdInterceptor.TRACE_ID));
            } finally {
                // 清理上下文
                MDC.clear();
            }
        });
        
        thread.start();
        thread.join();
        
        return "TraceId in thread: " + traceId;
    }

    /**
     * 测试使用TraceIdContext工具类传递TraceId
     */
    @GetMapping("/context")
    public String testContext() throws InterruptedException {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 获取当前上下文
        final var context = TraceIdContext.getContext();
        
        // 创建新线程并使用TraceIdContext传递上下文
        Thread thread = new Thread(() -> {
            try {
                // 设置上下文
                TraceIdContext.setContext(context);
                log.info("通过TraceIdContext传递的TraceId: {}", TraceIdContext.getTraceId());
            } finally {
                // 清理上下文
                TraceIdContext.clearContext();
            }
        });
        
        thread.start();
        thread.join();
        
        return "TraceId with context: " + traceId;
    }

    /**
     * 测试使用TraceIdThreadPoolTaskExecutor传递TraceId
     */
    @GetMapping("/executor")
    public String testExecutor() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建支持TraceId传递的线程池
        TraceIdThreadPoolTaskExecutor executor = new TraceIdThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("trace-executor-");
        executor.initialize();
        
        // 提交任务到线程池
        var future = executor.submit(() -> {
            log.info("线程池中的TraceId: {}", MDC.get(TraceIdInterceptor.TRACE_ID));
            return "success";
        });
        
        String result = future.get();
        executor.shutdown();
        
        return "TraceId in executor: " + traceId + ", result: " + result;
    }

    /**
     * 测试在CompletableFuture中传递TraceId
     */
    @GetMapping("/completable")
    public String testCompletableFuture() throws Exception {
        String traceId = TraceIdContext.getTraceId();
        log.info("主线程TraceId: {}", traceId);
        
        // 创建普通线程池（不支持TraceId传递）
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 获取当前上下文
        final var context = TraceIdContext.getContext();
        
        // 使用CompletableFuture并手动传递TraceId
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 设置上下文
            TraceIdContext.setContext(context);
            try {
                log.info("CompletableFuture中的TraceId: {}", TraceIdContext.getTraceId());
                return "success";
            } finally {
                // 清理上下文
                TraceIdContext.clearContext();
            }
        }, executor);
        
        String result = future.get();
        executor.shutdown();
        
        return "TraceId in CompletableFuture: " + traceId + ", result: " + result;
    }

    /**
     * 测试手动设置TraceId
     */
    @GetMapping("/manual")
    public String testManualTraceId() {
        // 清除可能存在的TraceId
        MDC.remove(TraceIdInterceptor.TRACE_ID);
        
        // 手动设置TraceId
        String customTraceId = "manual-" + UUID.randomUUID().toString().replace("-", "");
        MDC.put(TraceIdInterceptor.TRACE_ID, customTraceId);
        
        log.info("手动设置的TraceId: {}", MDC.get(TraceIdInterceptor.TRACE_ID));
        
        return "Manual TraceId: " + customTraceId;
    }
}