package com.doopp.gauss.server.dispatcher;

import java.util.HashMap;

public class DispatchRule {

    public static final HashMap<String, String> rules = new HashMap<>(256);

    static {
        // API 文档
        rules.put("/api", "helper.api");

        // 注册
        // clientId / clientToken / account / password
        rules.put("POST /api/register", "account.register");

        // 登陆
        // clientId / clientToken / account / password
        rules.put("POST /api/login", "account.login");

        // 获取用户信息
        rules.put("GET /api/user", "account.user");
    }
}
