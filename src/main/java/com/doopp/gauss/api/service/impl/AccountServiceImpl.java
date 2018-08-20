package com.doopp.gauss.api.service.impl;

import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.exception.GaussException;
import com.doopp.gauss.common.util.IdWorker;
import com.google.inject.Inject;

public class AccountServiceImpl implements AccountService {


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
        return new User();
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
        return new User();
    }

    @Override
    public String setSession(User user) {
        return "test";
    }

    @Override
    public User userByToken(String token) throws GaussException {
        return new User();
    }
}
