package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserDao {

    @Select("SELECT * FROM `user` WHERE id=#{userId} LIMIT 1")
    User fetchById(@Param("userId") Long userId);
}
