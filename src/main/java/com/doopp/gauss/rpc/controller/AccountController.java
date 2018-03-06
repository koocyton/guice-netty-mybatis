package com.doopp.gauss.rpc.controller;

import com.doopp.gauss.rpc.service.HelloService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AccountController {

    @Inject
    private HelloService helloService;

    public String hello() {
        System.out.print("\nhelloService : " + helloService);
        return helloService.hello().toString();
    }
}
