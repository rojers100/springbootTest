package com.luojie.common;

import org.springframework.http.HttpStatus;

public class ResponseUtil {

    public static ResponseCommonImpl success(String msg, Object data) {
        ResponseCommonImpl common = new ResponseCommonImpl();
        common.setCode(HttpStatus.OK.value());
        common.setMsg(msg);
        common.setEntity(data);
        return common;
    }

    public static ResponseCommonImpl failCommon(String msg, Object data) {
        ResponseCommonImpl common = new ResponseCommonImpl();
        common.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        common.setMsg(msg);
        common.setEntity(data);
        return common;
    }

    public static ResponseCommonImpl failWithNoPermission(String msg) {
        ResponseCommonImpl common = new ResponseCommonImpl();
        common.setCode(HttpStatus.UNAUTHORIZED.value());
        common.setMsg(msg);
        return common;
    }
}
