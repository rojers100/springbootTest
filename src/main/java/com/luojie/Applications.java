package com.luojie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

@SpringBootApplication
@EnableAsync // springboot 开启异步支持
@EnableTransactionManagement // 开启事务控制
public class Applications {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Applications.class, args);
//        boolean b = Arrays.stream(context.getBeanDefinitionNames()).anyMatch(a -> {
//            // 使用contains是因为，加载到BeanDefinition中的names有可能是全路径
//            return a.contains("testController");
//        });
//        if (b) {
//            System.out.println("存在 TestController 的bean");
//        }
    }
}
