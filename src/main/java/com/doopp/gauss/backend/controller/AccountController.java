package com.doopp.gauss.backend.controller;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.backend.service.HelloService;
import com.doopp.gauss.server.annotation.PathVariable;
import com.doopp.gauss.server.annotation.RequestParam;
import com.doopp.gauss.server.annotation.ResponseBody;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.handler.codec.http.FullHttpRequest;


@Singleton
public class AccountController {

    @Inject
    private HelloService helloService;

    @ResponseBody
    public ModelMap user(ModelMap modelMap,
                         FullHttpRequest httpRequest,
                         @RequestParam("userId") Long userId) {
        User user = helloService.hello();
        modelMap.addAttribute("user", user);
        return modelMap;
    }

    public String hello(ModelMap modelMap, FullHttpRequest httpRequest) {
        User user = helloService.hello();
        modelMap.addAttribute("user", user);
        return "hello";
    }
}
