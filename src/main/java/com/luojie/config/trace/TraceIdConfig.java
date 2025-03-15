package com.luojie.config.trace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TraceId配置类
 * 将TraceIdInterceptor添加到Spring MVC的拦截器链中
 */
@Configuration
public class TraceIdConfig implements WebMvcConfigurer {

    @Autowired
    private TraceIdInterceptor traceIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加TraceId拦截器，拦截所有请求
        registry.addInterceptor(traceIdInterceptor)
                .addPathPatterns("/**");
    }
}