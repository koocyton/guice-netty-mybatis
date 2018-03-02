package com.doopp.gauss.rpc.service.impl;

import com.doopp.gauss.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello() {
        return "hello word";
    }
}
