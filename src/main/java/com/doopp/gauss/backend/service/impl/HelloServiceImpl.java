package com.doopp.gauss.backend.service.impl;

import com.doopp.gauss.backend.service.HelloService;
import com.doopp.gauss.common.dao.UserDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.doopp.gauss.common.entity.User;

@Singleton
public class HelloServiceImpl implements HelloService {

    @Inject
    private UserDao userDao;

    @Override
    public User hello() {
        return userDao.fetchById(Long.valueOf("958001403853410304"));
    }
}
