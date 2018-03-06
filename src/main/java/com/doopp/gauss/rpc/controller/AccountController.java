package com.doopp.gauss.rpc.controller;

import com.doopp.gauss.rpc.service.HelloService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AccountController {

    @Inject
    private HelloService helloService;

    public String hello() {
        return helloService.hello().toString();
    }
}
