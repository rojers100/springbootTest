package com.luojie.event;

import com.luojie.moudle.RojerEventModel;
import org.springframework.context.ApplicationEvent;

public class RojerEvent extends ApplicationEvent {

    public RojerEventModel model;

    public RojerEvent(RojerEventModel model) {
        super(model);
        this.model = model;
    }

}
