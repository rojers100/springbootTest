package com.luojie.test.catchs;

import com.luojie.dao.mapper2.Mapper2;
import com.luojie.util.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 简单使用redis缓存3
 */
@Component
@Slf4j
public class RedisCatch5 {

    @Autowired
    Mapper2 mapper2;

    @Autowired
    RedisServiceUtil redisUtil;

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 100;

    public String test1() throws InterruptedException {
        // 先从redis中找数据，没有才去查数据库
        String value = redisUtil.get("uuid");
        if (StringUtils.isNotEmpty(value)) {
            return value;
        }
        String uuid = UUID.randomUUID().toString();
        int retryCount = 0;
        boolean lockAcquired = false;
        // 循环代替迭代
        while (retryCount < MAX_RETRIES && !lockAcquired) {
            lockAcquired = redisUtil.setIfAbsentWithExpire("lock", uuid, 300, TimeUnit.SECONDS);
            if (!lockAcquired) {
                Thread.sleep(RETRY_DELAY_MS);
                ++retryCount;
            }
        }

        try {
            if (lockAcquired) {
                // 双重检查，以防止在获取锁之后，缓存已经被其他线程更新
                value = redisUtil.get("uuid");
                if (StringUtils.isNotEmpty(value)) {
                    return value;
                }
                // 缓存空结果的前提是该方法如果出错应抛出异常，而不是被catch后返回空
                String dbValue = mapper2.getuuid("uuid");
                log.info("get data form db dbValue:{}", dbValue);
                // 缓存数据
                redisUtil.setWithExpire("uuid", dbValue, 1 + (int) Math.ceil(Math.random() * 10), TimeUnit.DAYS);
                return dbValue;
            } else {
                // 如果未能获取到锁，重试从缓存获取数据
                return redisUtil.get("uuid");
            }
        } finally {
            // 获取锁值
            String lock = redisUtil.get("lock");
            if (StringUtils.isNotEmpty(lock) && lock.equals(uuid)) {
                // 删除锁
                redisUtil.delete("lock");
            }
        }
    }

}
