package com.luojie.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect // 表示该类是一个切面类
@Component // 将该类注册为一个bean放入IOC容器
@Slf4j
public class LogAspect {
    /**
     * 通知类型
     *  前置通知：@Before()
     *  返回通知：@AfterReturning
     *  异常通知：@AfterThrowing
     *  后置通知：@After()
     *  环绕通知：@Around()
     */

    @Pointcut(value = "execution(* com.luojie.controller.*.*(..))")
    public void pointcut() {

    }

    // @Before(value = "切入点表达式") 也可以直接复用@Before("pointcut()")
    @Before(value = "execution(* com.luojie.controller.*.*(..))")
    public void before(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
        Object[] args = joinPoint.getArgs();// 获取参数列表
        log.info("前置通知： 开启调用，方法名：{}， 参数：{}", name, Arrays.toString(args));
    }

    @After(value = "pointcut()")
    public void after(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
        log.info("后置通知： 开启调用，方法名：{}", name);
    }

    @AfterReturning(value = "pointcut()", returning = "result")
    public void AfterReturning(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
        log.info("返回通知： 开启调用，方法名：{}. 返回结果：{}", name, result);
    }

    @AfterThrowing(value = "pointcut()", throwing = "e")
    public void AfterThrowing(JoinPoint joinPoint, Exception e) {
        String name = joinPoint.getSignature().getName(); // 获取方法名
        log.info("异常通知： 开启调用，方法名：{}. 异常：{}", name, e.getMessage());
    }

    @Around(value = "pointcut()")
    public Object Around(ProceedingJoinPoint pjp) {
        String name = null; // 获取方法名
        Object result = null;
        try {
            name = pjp.getSignature().getName();
            log.info("环绕通知--前置： 开启调用，方法名：{}", name);
            result = pjp.proceed();
            log.info("环绕通知--返回： 开启调用，方法名：{}", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            log.info("环绕通知--异常： 开启调用，方法名：{}", name);
        } finally {
            log.info("环绕通知--后置： 开启调用，方法名：{}", name);
        }
        return result;
    }

}
