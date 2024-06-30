package com.luojie.controImpl;

import com.luojie.event.RojerEvent;
import com.luojie.moudle.RojerEventModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventTestImpl {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void eventTest() {
        RojerEventModel model = new RojerEventModel();
        model.setName("Rojer");
        model.setSex(true);
        model.setAge(12);
        eventPublisher.publishEvent(new RojerEvent(model));
        log.info("start listener!!!");
    }
}
