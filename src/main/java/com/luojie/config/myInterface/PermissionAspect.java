package com.luojie.config.myInterface;

import com.luojie.common.NoPermissionException;
import com.luojie.dao.mapper1.Mapper1;
import com.luojie.moudle.UserModel;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Aspect
public class PermissionAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Mapper1 mapper1;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(MyPermission requiresPermission) {
        String permission = requiresPermission.value();
        // 在这里进行权限校验逻辑
        // 检查用户是否拥有指定的权限，如果没有权限，可以抛出异常或者记录日志等
        if (!hasPermission(permission)) {
            throw new NoPermissionException(500, "没有权限");
        }
    }

    private boolean hasPermission(String permission) {
        // 一般我们会通过request拿token，解析token和数据库中数据比对，看用户是否有权限，这里我就简化为直接的值
        String userID = request.getHeader("userID");
        // 从数据库中拿到该用户的所有权限
        UserModel user = mapper1.getUser(userID);
        // 进行权限判断
        if (user == null) return false;
        if (user.getRoles().contains(permission)) {
            return true;
        }
        return false;
    }

}
