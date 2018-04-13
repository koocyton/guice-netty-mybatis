package com.doopp.gauss.backend.controller;

import com.doopp.gauss.backend.service.AccountService;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.server.annotation.RequestParam;
import com.doopp.gauss.server.annotation.ResponseBody;
import com.doopp.gauss.server.dispatcher.RequestDispatcher;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class AccountController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Inject
    private AccountService accountService;

    @ResponseBody
    public String register(@RequestParam("account") String account,
                      @RequestParam("password") String password,
                      @RequestParam("client_key") String clientKey,
                      @RequestParam("client_token") String clientToken) {
        logger.info(account + " " + password + " " + clientKey + " " + clientToken);
        return "aaaa";//accountService.getTokenOnLogin(account, password, clientKey, clientToken);
    }
}
