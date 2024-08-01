package com.luojie.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class A {
    private final B b;

    @Autowired
    public A(B b) {
        this.b = b;
    }
}