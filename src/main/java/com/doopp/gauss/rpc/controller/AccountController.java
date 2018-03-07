package com.doopp.gauss.rpc.controller;

import com.doopp.gauss.entity.User;
import com.doopp.gauss.rpc.service.HelloService;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AccountController {

    @Inject
    private HelloService helloService;

    @Inject
    public String hello(ModelMap modelMap) {
        User user = helloService.hello();
        modelMap.addAttribute("user", user);
        return "hello";
    }
}
