package com.luojie.controller;

import com.luojie.controImpl.EventTestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventTestController {

    @Autowired
    EventTestImpl  impl;

    @GetMapping(value = "/event/test")
    public void testEvent() {
        impl.eventTest();
    }
}
