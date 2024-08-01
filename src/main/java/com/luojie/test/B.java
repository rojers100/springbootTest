package com.luojie.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class B {
    // 打开就循环依赖
//    private final A a;
//
//    @Autowired
//    public B(A a) {
//        this.a = a;
//    }
}