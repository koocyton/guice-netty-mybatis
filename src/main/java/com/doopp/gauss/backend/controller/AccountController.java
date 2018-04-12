package com.doopp.gauss.backend.controller;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.backend.service.HelloService;
import com.doopp.gauss.server.annotation.JsonResponse;
import com.doopp.gauss.server.ui.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


@Singleton
public class AccountController {

    @Inject
    private HelloService helloService;

    @JsonResponse
    @Inject
    public ModelMap user(@Named("modelMap") ModelMap modelMap) {
        User user = helloService.hello();
        modelMap.addAttribute("user", user);
        return modelMap;
    }

    @Inject
    public String hello(@Named("modelMap") ModelMap modelMap) {
        User user = helloService.hello();
        modelMap.addAttribute("user", user);
        return "hello";
    }
}
