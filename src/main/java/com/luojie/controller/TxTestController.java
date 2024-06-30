package com.luojie.controller;

import com.luojie.common.ResponseCommonImpl;
import com.luojie.common.ResponseUtil;
import com.luojie.controImpl.TxTestImpl;
import com.luojie.moudle.UserBuyBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TxTestController {

    @Autowired
    private TxTestImpl txTest;

    @PostMapping("/tx/test")
    public ResponseCommonImpl testTs(@RequestBody UserBuyBook userBuyBook) {
        txTest.testTs(userBuyBook.getUserid(), userBuyBook.getBookName(), userBuyBook.getAmount());
        return ResponseUtil.success("ok", null);
    }
}
