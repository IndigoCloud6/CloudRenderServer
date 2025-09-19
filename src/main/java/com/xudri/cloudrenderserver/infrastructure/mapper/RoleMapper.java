package com.xudri.cloudrenderserver.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xudri.cloudrenderserver.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据访问层
 *
 * @author maxyun
 * @since 2024-09-18 15:48:44
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.id, r.name, r.description " +
            "FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(@Param("userId") Integer userId);

    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色信息
     */
    @Select("SELECT r.id, r.name, r.description FROM roles r WHERE r.name = #{name}")
    Role findByName(@Param("name") String name);
}