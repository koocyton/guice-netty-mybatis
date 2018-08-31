package com.doopp.gauss.server.dispatcher;

import java.util.HashMap;

public class DispatchRule {

    public static final HashMap<String, String> rules = new HashMap<>(256);

    static {
        // 注册
        rules.put("POST /api/register", "account.register");
        // 登陆
        rules.put("POST /api/login", "account.login");
        // 登陆
        rules.put("GET /login", "account.loginPage");
        // 获取用户信息
        rules.put("GET /api/user", "account.user");
    }
}
