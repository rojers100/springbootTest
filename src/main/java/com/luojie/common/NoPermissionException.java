package com.luojie.common;

import lombok.Getter;

@Getter
public class NoPermissionException extends RuntimeException {

    private String errorMsg;

    private Integer code;

    public NoPermissionException(Integer code, String msg) {
        this.errorMsg = msg;
        this.code = code;
    }
}
