package com.luojie.controller;

import com.luojie.common.ResponseCommonImpl;
import com.luojie.common.ResponseUtil;
import com.luojie.controImpl.InterfaceTestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InterfaceTestController {

    @Autowired
    private InterfaceTestImpl interfaceTest;

    @GetMapping("/in/test")
    public ResponseCommonImpl test() {
        System.out.println("111111111111111222222222");
        int a = 40/0;
        interfaceTest.test();
        return ResponseUtil.success("ok", null);
    }
}
