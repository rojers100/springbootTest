package com.luojie.config.filter;

import com.luojie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private UserService userService;

    @Bean
    public FilterRegistrationBean<UserAuthFilter> userAuthFilterRegistration() {
        FilterRegistrationBean<UserAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserAuthFilter(userService));
        registration.addUrlPatterns("/*"); // 根据需要调整URL模式
        registration.setName("userAuthFilter");
        registration.setOrder(1); // 设置过滤器顺序
        return registration;
    }
}
