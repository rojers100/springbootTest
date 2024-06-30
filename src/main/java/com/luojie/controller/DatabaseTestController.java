package com.luojie.controller;

import com.luojie.controImpl.DatabaseTestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseTestController {

    @Autowired
    DatabaseTestImpl test;

    @GetMapping("/database/test")
    public void test1() {
        test.add();
    }
}
