package com.luojie.config.eventListener;

import com.luojie.event.BaseRedisEvent;
import com.luojie.event.RedisAddEvent;
import com.luojie.event.RedisDeleteEvent;
import com.luojie.moudle.RedisEventModule;
import com.luojie.util.RedisServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisListener implements ApplicationListener<BaseRedisEvent> {

    @Autowired
    private RedisServiceUtil redisServiceUtil;

    @Override
    @Async("asyncExecutor") // 使用异步线程池进行处理
    public void onApplicationEvent(BaseRedisEvent redisEvent) {
        if (redisEvent instanceof RedisAddEvent) {
            handleAddEvent((RedisAddEvent) redisEvent);
        } else if (redisEvent instanceof RedisDeleteEvent) {
            handleDeleteEvent((RedisDeleteEvent) redisEvent);
        }
    }

    private void handleAddEvent(RedisAddEvent redisEvent) {
        log.info("RedisAddEvent:{}", redisEvent);
        RedisEventModule module = redisEvent.eventModule;
        if (StringUtils.isNotBlank(module.getField()) && module.getTimeout() != 0) {
            redisServiceUtil.setHashWithExpire(module.getKey(), module.getField(), module.getValue(),
                    module.getTimeout(), module.getTimeoutUnit());
        } else if (StringUtils.isNotBlank(module.getField())) {
            redisServiceUtil.setHash(module.getKey(), module.getField(), module.getValue());
        } else if (module.getTimeout() != 0){
            redisServiceUtil.setWithExpire(module.getKey(), module.getValue(),
                    module.getTimeout(), module.getTimeoutUnit());
        } else {
            redisServiceUtil.set(module.getKey(), module.getValue());
        }
    }

    private void handleDeleteEvent(RedisDeleteEvent redisEvent) {
        log.info("RedisDeleteEvent:{}", redisEvent);
        RedisEventModule module = redisEvent.eventModule;
        if (StringUtils.isNotBlank(module.getField()) ) {
            redisServiceUtil.deleteHash(module.getKey(), module.getField());
        } else {
            redisServiceUtil.delete(module.getKey());
        }
    }

}
