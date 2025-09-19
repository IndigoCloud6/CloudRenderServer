package com.xudri.cloudrenderserver.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体类
 *
 * @author maxyun
 * @since 2024-09-18 15:48:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_roles")
public class UserRole extends Model<UserRole> {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer roleId;
}