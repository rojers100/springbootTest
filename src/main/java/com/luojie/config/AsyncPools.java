package com.luojie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncPools {

    @Bean(name = "asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 设置核心线程数
        executor.setMaxPoolSize(20); // 设置最大线程数
        executor.setQueueCapacity(100); // 设置队列容量
        executor.setThreadNamePrefix("async-executor-"); // 设置线程名前缀
        executor.setKeepAliveSeconds(5000);// 设置线程最大等待时间
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());// 设置线程拒绝策略
        executor.initialize();
        return executor;
    }

}
