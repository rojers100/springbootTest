package com.luojie.event;

import com.luojie.moudle.RedisEventModule;
import org.apache.commons.lang3.StringUtils;

public class RedisAddEvent extends BaseRedisEvent {

    public RedisEventModule eventModule;

    public RedisAddEvent(RedisEventModule source) {
        super(source);
        if (StringUtils.isBlank(source.getValue())) {
            throw new IllegalArgumentException("redis value can't be empty");
        }
        this.eventModule = source;
    }
}
