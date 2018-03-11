package com.doopp.gauss.backend.controller;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.backend.service.HelloService;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

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
