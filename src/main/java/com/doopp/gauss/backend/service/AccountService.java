package com.doopp.gauss.backend.service;

import com.doopp.gauss.common.entity.User;

public interface AccountService {

    String getTokenOnLogin(String account, String password, String clientKey, String clientToken);

    User getUserByToken(String token);
}
