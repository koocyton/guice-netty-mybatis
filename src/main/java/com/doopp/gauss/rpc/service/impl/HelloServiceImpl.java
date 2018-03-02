package com.doopp.gauss.rpc.service.impl;

import com.doopp.gauss.rpc.service.HelloService;

import javax.inject.Singleton;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello() {
        return "hello word";
    }
}
