package com.doopp.gauss.api.controller;

import com.doopp.gauss.api.service.AccountService;
import com.doopp.gauss.common.entity.User;
import com.doopp.gauss.common.exception.GaussException;
import com.doopp.gauss.server.annotation.RequestParam;
import com.doopp.gauss.server.annotation.ResponseBody;
import com.doopp.gauss.server.freemarker.ModelMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AccountController {

    // private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Inject
    private AccountService accountService;


    public String loginPage(ModelMap modelMap) {
        modelMap.addAttribute("hello", "hello");
        return "hello";
    }

    @ResponseBody
    public String login(@RequestParam("account") String account, @RequestParam("password") String password) throws GaussException {
        User user = accountService.userLogin(account, password);
        return accountService.setSession(user);
    }

    @ResponseBody
    public String register(@RequestParam("account") String account, @RequestParam("password") String password) throws GaussException {
        User user = accountService.userRegister(account, password);
        return accountService.setSession(user);
    }

    @ResponseBody
    public User user(@RequestParam("token") String token) throws GaussException {
        return accountService.userByToken(token);
    }
}
