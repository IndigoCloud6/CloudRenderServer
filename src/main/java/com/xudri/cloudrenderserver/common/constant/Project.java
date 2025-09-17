package com.xudri.cloudrenderserver.common.constant;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (Project)表实体类
 *
 * @author maxyun
 * @since 2024-04-05 07:56:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Project extends Model<Project> {

    @TableId(value = "project_id", type = IdType.INPUT)
    private String projectId;

    private String projectName;

    private String createDate;

    private String projectContent;

    private String savePath;

}

