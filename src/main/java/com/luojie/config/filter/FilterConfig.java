package com.luojie.config.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    // 禁用自动注册
    @Bean
    public FilterRegistrationBean<UserAuthFilter> userAuthFilter(UserAuthFilter filter) {
        FilterRegistrationBean<UserAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 明确指定顺序
        registration.addUrlPatterns("/*"); // 指定过滤路径
        registration.setEnabled(true);
        return registration;
    }
}