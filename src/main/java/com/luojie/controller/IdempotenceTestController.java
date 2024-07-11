package com.luojie.controller;

import com.luojie.controImpl.IdempotenceTestImpl;
import com.luojie.moudle.IdempotenceTestModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdempotenceTestController {

    @Autowired
    private IdempotenceTestImpl idempotenceTest;

    @PostMapping("/idempotence/test")
    public void idempotenceTest(@RequestHeader("x-uuid") String uuid, @RequestBody IdempotenceTestModule module) {
        idempotenceTest.setup(uuid, module);
    }
}
