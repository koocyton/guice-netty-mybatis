package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.common.dao.UserDao;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.exception.GaussException;
import com.doopp.gauss.common.util.EncryptHelper;
import com.doopp.gauss.common.util.IdWorker;
import com.doopp.gauss.server.redis.CustomShadedJedis;
import com.google.inject.Inject;
import com.doopp.gauss.common.exception.ExceptionCode;

import java.util.UUID;

public class AccountServiceImpl implements AccountService {

    @Inject
    private UserDao userDao;

    @Inject
    private CustomShadedJedis sessionRedis;

    @Inject
    private IdWorker userIdWorker;

    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    @Override
    public User userLogin(String account, String password) throws GaussException {
        User user = userDao.fetchByAccount(account);
        if (user==null) {
            throw new GaussException(ExceptionCode.DATA_NOT_FOUND, "not found account");
        }
        String hasPassword = user.encryptPassword(password);
        if (!user.getPassword().equals(hasPassword)) {
            throw new GaussException(ExceptionCode.PASSWORD_ERROR, "password is error");
        }
        return user;
    }

    /**
     * 通过账号，密码获取用户信息
     *
     * @param account 用户账号
     * @param password 用户密码
     * @return 用户信息
     * @throws Exception 登录异常
     */
    @Override
    public User userRegister(String account, String password) throws GaussException {
        Long newUserId = userIdWorker.nextId();
        User user = new User();
        user.setId(newUserId);
        user.setAccount(account);
        user.setPassword_salt(EncryptHelper.md5(UUID.randomUUID().toString()));
        user.setPassword(user.encryptPassword(password));
        user.setCreated_at("CURRENT_TIMESTAMP");
        try {
            userDao.create(user);
        }
        catch(Exception e) {
            throw new GaussException(ExceptionCode.ACCOUNT_REGISTERED, "Account has been registered");
        }
        return user;
    }

    @Override
    public String setSession(User user) {
        String userId = String.valueOf(user.getId());
        String token = UUID.randomUUID().toString();
        sessionRedis.set(userId.getBytes(), user);
        sessionRedis.set(token, userId);
        return token;
    }

    @Override
    public User userByToken(String token) throws GaussException {
        String userId = sessionRedis.get(token);
        if (userId==null) {
            throw new GaussException(ExceptionCode.DATA_NOT_FOUND, "not found account");
        }
        return (User) sessionRedis.get(userId.getBytes());
    }
}
