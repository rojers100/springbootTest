package com.luojie.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@ResponseBody
public class CommonExceptionHandle {

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
