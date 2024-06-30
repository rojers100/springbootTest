package com.luojie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AOPTestController {

    @GetMapping("/aop/test")
    public Object testAOP(@RequestParam("ab") int a, @RequestParam(value = "sr", required = false) String b) {
        int i = b.compareTo(String.valueOf(a));
        return i > 0 ? b : a;
    }
}
