package com.xudri.cloudrenderserver.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * (Instance)表实体类
 *
 * @author maxyun
 * @since 2024-05-13 11:58:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Instance extends Model<Instance> {

    private String id;

    private String projectid;

    private String renderconfig;

    private Integer state;

    @TableField(exist = false)
    private boolean isRun ;

    @TableField(exist = false)
    private String previewUrl  ;

    @TableField(exist = false)
    private String projectName  ;

}