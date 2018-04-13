package com.doopp.gauss.backend.service.impl;

import com.doopp.gauss.backend.service.AccountService;
import com.doopp.gauss.common.dao.ClientDao;
import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.Client;
import com.doopp.gauss.common.entity.User;
import com.google.inject.Inject;

public class AccountServiceImpl implements AccountService {

    @Inject
    private UserDao userDao;

    @Inject
    private ClientDao clientDao;

    @Override
    public String getTokenOnLogin(String account, String password, String clientKey, String clientToken) {

    }

    @Override
    public User getUserByToken(String token) {
        return userDao.fetchById(Long.valueOf("958001403853410304"));
    }

    private boolean checkClientToken(String clientKey, String clientToken) {
        Client client = clientDao.fetchById(clientKey);
        if (client==null) {
            return false;
        }
        client.getSecret
    }
}
