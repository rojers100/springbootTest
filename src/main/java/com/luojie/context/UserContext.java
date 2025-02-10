package com.luojie.context;


import com.luojie.moudle.UserModel;

public class UserContext {
    private static final ThreadLocal<UserModel> userHolder = new ThreadLocal<>();

    public static void setCurrentUser(UserModel user) {
        userHolder.set(user);
    }

    public static UserModel getCurrentUser() {
        return userHolder.get();
    }

    public static void clear() {
        userHolder.remove();
    }
} 