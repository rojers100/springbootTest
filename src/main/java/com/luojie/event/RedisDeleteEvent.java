package com.luojie.event;

import com.luojie.moudle.RedisEventModule;

public class RedisDeleteEvent extends BaseRedisEvent{

    public RedisEventModule eventModule;

    public RedisDeleteEvent(RedisEventModule source) {
        super(source);
        this.eventModule = source;
    }
}
