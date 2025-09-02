package com.luojie.config.logconfig;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect // 表示该类是一个切面类
@Component // 将该类注册为一个bean放入IOC容器
@Slf4j
public class LogAspectConfig {

    @Autowired
    private HttpServletRequest request;

    @Pointcut(value = "execution(* com.luojie.controller.*.*(..))")
    public void pointcut() {

    }

    @Before(value = "pointcut()")
    public void before(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
        Object[] args = joinPoint.getArgs();// 获取参数列表
//        log.info("前置通知： 开启调用，方法名：{}， 参数：{}", name, Arrays.toString(args));
        log.info(" From AOP Request In URL: {} - Method: {} -IP:{}", request.getRequestURL(), request.getMethod(), request.getRemoteAddr());
    }

    @After(value = "pointcut()")
    public void after(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
//        log.info("后置通知： 开启调用，方法名：{}", name);
        log.info(" From AOP Request Out URL: {} - Method: {} -IP:{}", request.getRequestURL(), request.getMethod(), request.getRemoteAddr());
    }

}
