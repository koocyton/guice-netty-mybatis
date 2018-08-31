package com.doopp.gauss.common.dao;

import com.doopp.gauss.common.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

public interface BaseDao {

    @Select("SELECT count(*) FROM `user` WHERE 1 LIMIT 1")
    Long count();

    @Insert("INSERT INTO `user` (`id`, `account`, `password`, `password_salt`, `create_at`) VALUES (${id}, #{account}, #{password}, #{password_salt}, ${created_at})")
    void create(User user);

    @Delete("DELETE FROM `user` WHERE id=${id,jdbcType=BIGINT} LIMIT 1")
    void delete(int id);

    @Update("UPDATE `user` SET `account`=#{account}, `password`=#{password}, `password_salt`=${password_salt} WHERE `id`=#{id,jdbcType=BIGINT} LIMIT 1")
    void update(User user);

    //    @SelectProvider(type = TestSqlProvider.class, method = "getSql")
    //    @Options(useCache = true, flushCache = false, timeout = 10000)
    //    @Results(value = {
    //        @Result(id = true, property = "id", column = "test_id", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    //        @Result(property = "testText", column = "test_text", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    //    })
    //    public TestBean get(@Param("id") String id);
}
