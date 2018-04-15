package com.doopp.gauss.common.entity;

import com.doopp.gauss.common.util.EncryptHelper;
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

    // password salt
    private String password_salt;

    // 时间
    private String created_at;

    // 加密密码
    public String encryptPassword(String password) {
        return EncryptHelper.md5(this.account + " " + password + " " + this.password_salt);
    }
}
