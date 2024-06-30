package com.luojie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@Slf4j
public class ProfilesTestController {

    @Value("${pro.a.value}")
    private String a;

    @Value("${pro.b.value}")
    private String b;

    @Autowired
    ConfigurableApplicationContext context;

    @RequestMapping(method = RequestMethod.GET, path = "/profiles/test2")
    public void test2() {
        boolean match = Arrays.stream(context.getBeanDefinitionNames()).anyMatch(a -> {
            return a.contains("testController");
        });
        if (match)  System.out.println("存在 TestController 的bean");;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/profiles/test")
    public void test1() {
        log.info("测试激活环境是否生效" + a);
        log.info("测试与application.properties中配置项冲突时，生效为哪个：" + b);
    }

}
