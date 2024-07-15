package com.luojie.controller;

import com.luojie.event.RedisAddEvent;
import com.luojie.event.RedisDeleteEvent;
import com.luojie.event.RojerEvent;
import com.luojie.moudle.RedisEventModule;
import com.luojie.util.RedisServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    @PostMapping("/redis/test1")
    public Object redisTest1(@RequestBody RedisEventModule module) throws InterruptedException {
        // 测试普通string
        eventPublisher.publishEvent(new RedisAddEvent(module));
        // 获取刚存入的value
        Thread.sleep(3000);
        String re = redisServiceUtil.get(module.getKey());
        System.out.println(re);
        // 测试删除
        eventPublisher.publishEvent(new RedisDeleteEvent(module));
        Thread.sleep(3000);
        System.out.println("删除之后re的值: " + redisServiceUtil.get(module.getKey()));
        return re;
    }

    @PostMapping("/redis/test2")
    public Object redisTest2(@RequestBody RedisEventModule module) throws InterruptedException {
        // 测试普通string
        eventPublisher.publishEvent(new RedisAddEvent(module));
        // 获取刚存入的value
        Thread.sleep(3000);
        String hash = redisServiceUtil.getHash(module.getKey(), module.getField());
        System.out.println(hash);
        eventPublisher.publishEvent(new RedisDeleteEvent(module));
        Thread.sleep(3000);
        System.out.println("删除之后re的值: " + redisServiceUtil.getHash(module.getKey(),
                module.getField()));
        return hash;
    }
}
