package com.luojie.context;


import com.luojie.moudle.UserModel;

public class UserContext {
    private static final ThreadLocal<UserModel> userHolder = new InheritableThreadLocal<>();

    public static void setCurrentUser(UserModel user) {
        userHolder.set(user);
    }

    public static UserModel getCurrentUser() {
        UserModel user = userHolder.get();
        if (user == null) {
            throw new IllegalStateException("用户上下文未初始化");
        }
        return user;
    }

    public static void clear() {
        userHolder.remove();
    }
} 