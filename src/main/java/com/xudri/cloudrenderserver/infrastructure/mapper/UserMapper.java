package com.xudri.cloudrenderserver.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xudri.cloudrenderserver.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 *
 * @author maxyun
 * @since 2024-09-18 15:48:44
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户及其角色信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT u.id, u.username, u.password, u.email, u.status, u.created_at, u.updated_at, u.name " +
            "FROM user u WHERE u.username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT u.id, u.username, u.password, u.email, u.status, u.created_at, u.updated_at, u.name " +
            "FROM user u WHERE u.email = #{email}")
    User findByEmail(@Param("email") String email);
}