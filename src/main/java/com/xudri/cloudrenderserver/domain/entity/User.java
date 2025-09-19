package com.xudri.cloudrenderserver.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * (User)表实体类
 *
 * @author maxyun
 * @since 2025-09-18 15:48:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class User extends Model<User> {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private Integer status; // 1:active, 0:inactive

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Deprecated
    private String name; // 保持向后兼容

    @TableField(exist = false)
    private List<Role> roles;
}

