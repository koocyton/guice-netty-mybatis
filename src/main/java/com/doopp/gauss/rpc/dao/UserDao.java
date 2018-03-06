package com.doopp.gauss.rpc.dao;

import com.doopp.gauss.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("SELECT * FROM `user` WHERE id=#{userId} LIMIT 1")
    User fetchById(@Param("userId") Long userId);
}
