package com.doopp.gauss.backend.service;

import com.doopp.gauss.common.entity.User;

public interface AccountService {

    User getUserByToken(String token);
}
