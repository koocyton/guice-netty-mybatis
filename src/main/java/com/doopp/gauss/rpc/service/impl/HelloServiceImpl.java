package com.doopp.gauss.rpc.service.impl;

import com.doopp.gauss.entity.User;
import com.doopp.gauss.rpc.dao.UserDao;
import com.doopp.gauss.rpc.service.HelloService;
import com.google.inject.Inject;

public class HelloServiceImpl implements HelloService {

    @Inject
    private UserDao userDao;

    @Override
    public User hello() {
        return userDao.fetchById(Long.valueOf("958001403853410304"));
    }
}
