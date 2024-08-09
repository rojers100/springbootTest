package com.luojie.test.catchs;

import com.luojie.dao.mapper2.Mapper2;
import com.luojie.util.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 简单使用redis缓存
 */
@Component
@Slf4j
public class RedisCatch1 {

    @Autowired
    Mapper2 mapper2;

    @Autowired
    RedisServiceUtil redisUtil;
    public String test1() {
        // 先从redis中找数据，没有才去查数据库
        String value = redisUtil.get("uuid");
        if (StringUtils.isNotEmpty(value)) {
            return value;
        } else {
            String dbValue = mapper2.getuuid("uuid");
            log.info("get data form db dbValue:{}", dbValue);
            if (StringUtils.isNotEmpty(dbValue)) {
                // 缓存数据
                redisUtil.setWithExpire("uuid", dbValue, 3, TimeUnit.DAYS);
            }
            return dbValue;
        }
    }
}
