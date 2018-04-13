package com.doopp.gauss.common.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class User implements Serializable{

    // 编号
    private Long id;

    // 账号
    private String account;

    // 密码
    private String password;

    // secret
    private int secret;

    // token
    private String token;

    // 时间
    private String created_at;
}
