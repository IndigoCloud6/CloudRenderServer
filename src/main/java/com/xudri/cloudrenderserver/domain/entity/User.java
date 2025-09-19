package com.xudri.cloudrenderserver.domain.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (User)表实体类
 *
 * @author maxyun
 * @since 2025-09-18 15:48:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Model<User> {

    private String name;

    private String password;
}

