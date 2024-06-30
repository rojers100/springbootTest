package com.luojie.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


//@ControllerAdvice
//@ResponseBody
public class CommonExceptionHandle {

    @Autowired
    private HttpServletResponse response;

    @ExceptionHandler(NoPermissionException.class)
    public ResponseCommonImpl NoPermission(NoPermissionException ex) {
        ResponseCommonImpl failedCommon = ResponseUtil.failWithNoPermission(ex.getErrorMsg());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return failedCommon;
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseCommonImpl handleException(Exception ex) {
        ResponseCommonImpl failedCommon = ResponseUtil.failCommon(ex.getMessage(), null);
        return failedCommon;
    }
}
