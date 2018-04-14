package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {

    @Select("SELECT * FROM `user` WHERE account=#{account,jdbcType=VARCHAR} LIMIT 1")
    User fetchByAccount(String account);

    @Select("SELECT * FROM `user` WHERE id=#{id,jdbcType=BIGINT} LIMIT 1")
    User fetchById(long id);

    @Select("SELECT count(*) FROM `user` LIMIT 1")
    Long count();

    @Select("SELECT * FROM `user` ORDER BY id DESC LIMIT #{offset,jdbcType=BIGINT}, #{limit,jdbcType=INTEGER}")
    List<User> fetchList(@Param("offset") Long offset, @Param("limit") int limit);

    @Insert("INSERT INTO `user` (`id`, `account`, `password`, `password_salt`, `create_at`) VALUES (${id}, #{account}, #{password}, #{password_salt}, ${created_at})")
    void create(User user);

    @Delete("DELETE FROM `user` WHERE id=${id,jdbcType=BIGINT}")
    void delete(int id);

    @Update("UPDATE `user` SET `account`=#{account}, `password`=#{password}, `password_salt`=${password_salt} WHERE `id`=#{id,jdbcType=BIGINT}")
    void update(User user);
}
