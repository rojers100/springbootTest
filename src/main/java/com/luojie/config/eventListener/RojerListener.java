package com.luojie.config.eventListener;

import com.luojie.event.RojerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RojerListener implements ApplicationListener<RojerEvent> {

    @Override
    @Async("asyncExecutor") // 使用异步线程池进行处理
    public void onApplicationEvent(RojerEvent event1) {
        if (event1.model.getSex()) {
            a();
        } else {
            b();
        }
    }

    public void a() {
        log.info("this is a boy");
    }

    public void  b() {
        log.info("this is a girl");
    }

}
