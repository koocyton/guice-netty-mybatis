package com.doopp.gauss.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Client implements Serializable{

    // 编号
    private Long id;

    // 昵称
    private String secret;

}
