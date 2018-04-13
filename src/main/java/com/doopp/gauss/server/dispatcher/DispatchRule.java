package com.doopp.gauss.server.dispatcher;

import java.util.HashMap;

public class DispatchRule {

    public static final HashMap<String, String> rules = new HashMap<>(256);

    static {
        rules.put("", "account.hello");
        rules.put("/", "account.hello");
        rules.put("POST /api/account/hello", "account.hello");
        rules.put("GET /api/account/hello", "account.hello");
        rules.put("GET /api/user", "account.user");
        rules.put("POST /api/register", "account.register");
    }
}
