package com.doopp.gauss.backend.service.impl;

import com.doopp.gauss.backend.service.AccountService;
import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.User;
import com.google.inject.Inject;

public class AccountServiceImpl implements AccountService {

    @Inject
    private UserDao userDao;

    @Override
    public User getUserByToken(String token) {
        return userDao.fetchById(Long.valueOf("958001403853410304"));
    }
}
