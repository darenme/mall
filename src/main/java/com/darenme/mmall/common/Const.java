package com.darenme.mmall.common;

/**
 * Created by darenme
 * date: 2018/8/8
 * time: 14:16
 */

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    // 为什么不用枚举？
    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
}
