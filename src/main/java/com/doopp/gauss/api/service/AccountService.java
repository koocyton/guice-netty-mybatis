package com.doopp.gauss.api.service;

import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.exception.GaussException;

public interface AccountService {

    User userLogin(String account, String password) throws GaussException;

    User userRegister(String account, String password) throws GaussException;

    User userByToken(String token) throws GaussException;

    String setSession(User user);
}
