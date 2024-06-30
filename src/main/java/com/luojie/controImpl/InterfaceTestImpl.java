package com.luojie.controImpl;

import com.luojie.common.ResponseCommonImpl;
import com.luojie.config.myInterface.MyPermission;
import org.springframework.stereotype.Service;

@Service
public class InterfaceTestImpl {

    @MyPermission("admin")
    public ResponseCommonImpl test() {
        System.out.println("ok!");
        return null;
    }
}
