package com.xudri.cloudrenderserver.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xudri.cloudrenderserver.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联数据访问层
 *
 * @author maxyun
 * @since 2024-09-18 15:48:44
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}