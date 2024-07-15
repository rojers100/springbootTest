package com.luojie.event;

import org.springframework.context.ApplicationEvent;

public abstract class BaseRedisEvent extends ApplicationEvent {
    public BaseRedisEvent(Object source) {
        super(source);
    }
}
