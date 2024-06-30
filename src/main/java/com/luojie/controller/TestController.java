package com.luojie.controller;

import com.luojie.common.ResponseCommonImpl;
import com.luojie.common.ResponseUtil;
import com.luojie.util.ResourceBundleUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.ResourceBundle;

@RestController
public class TestController {

    @Value("${test.a.value}")
    private String a;

    @Value("${test.b.value}")
    private String b;

    @Value("${test.c.value}")
    private String c;

    @GetMapping("/test/geta")
    public void test1() {
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }


    @GetMapping("/test/resource")
    public ResponseCommonImpl test2(@RequestHeader String language) {
        ResourceBundle instance = ResourceBundleUtil.getInstance(Locale.CHINA);
        System.out.println(instance.getString("error.push"));
        ResourceBundle instance2 = ResourceBundleUtil.getInstance(Locale.ENGLISH);
        System.out.println(instance2.getString("error.push"));
        ResourceBundle instance3 = ResourceBundleUtil.getInstance(Locale.JAPAN);
        System.out.println(instance3.getString("error.push"));
        return ResponseUtil.success("success", null);
    }

}
