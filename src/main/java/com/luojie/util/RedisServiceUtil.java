package com.luojie.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
@Slf4j
public class RedisServiceUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 普通string类型，设置值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("fail set redis key:{},value:{}, errorMsg:{}", key, value, e.getMessage());
        }
    }

    /**
     * 普通string类型，设置没有就设置
     *
     * @param key
     * @param value
     */
    public boolean setIfAbsent(String key, String value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error("fail set redis key:{},value:{}, errorMsg:{}", key, value, e.getMessage());
            return false;
        }
    }

    /**
     * 普通string类型，没有就设置包括过期时间
     *
     * @param key
     * @param value
     */
    public boolean setIfAbsentWithExpire(String key, String value, int time, TimeUnit timeUnit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
        } catch (Exception e) {
            log.error("fail set redis key:{},value:{}, errorMsg:{}", key, value, e.getMessage());
            return false;
        }
    }

    /**
     * 普通string类型，设置值并设置超时时间
     *
     * @param key
     * @param value
     */
    public void setWithExpire(String key, String value, int time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } catch (Exception e) {
            log.error("fail set redis key with expire:{},value:{}, errorMsg:{}", key,
                    value, e.getMessage());
        }
    }

    /**
     * 普通string类型，获取值
     *
     * @param key
     */
    public String get(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("fail get redis key:{}, errorMsg:{}", key, e.getMessage());
        }
        return null;
    }

    /**
     * 普通string类型，删除key
     *
     * @param key
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("fail delete redis key:{}, errorMsg:{}", key, e.getMessage());
        }
    }

    /**
     * 普通string类型，判断key是否存在
     *
     * @param key
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("fail exists redis key:{}, errorMsg:{}", key, e.getMessage());
        }
        return false;
    }

    /**
     * 普通string类型，为某个key单独设置超时时间
     *
     * @param key
     */
    public void expire(String key, int seconds, TimeUnit timeUnit) {
        try {
            redisTemplate.expire(key, seconds, timeUnit);
        } catch (Exception e) {
            log.error("fail expire redis key:{}, errorMsg:{}", key, e.getMessage());
        }
    }

    /**
     * 普通string类型，获取key的超时时间
     *
     * @param key
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("fail getExpire redis key:{}, errorMsg:{}", key, e.getMessage());
        }
        return null;
    }

    /**
     * hash类型，设置值
     *
     * @param key
     * @param value
     */
    public void setHash(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("fail setHash redis key:{}, errorMsg:{}", key, e.getMessage());
        }
    }

    /**
     * hash类型，设置值
     *
     * @param key
     * @param value
     */
    public void setHashWithExpire(String key, String field, String value, int time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            redisTemplate.expire(key, time, timeUnit);
        } catch (Exception e) {
            log.error("fail setHash with expire redis key:{}, errorMsg:{}", key, e.getMessage());
        }
    }

    /**
     * hash类型，获取值
     *
     * @param key
     */
    public String getHash(String key, String field) {
        try {
            return (String) redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("fail getHash redis key:{}, errorMsg:{}", key, e.getMessage());
        }
        return null;
    }

    /**
     * hash类型，删除
     *
     * @param key
     */
    public void deleteHash(String key, String field) {
        try {
            redisTemplate.opsForHash().delete(key, field);
        } catch (Exception e) {
            log.error("fail deleteHash redis key:{}, errorMsg:{}", key, e.getMessage());
        }
    }
}
