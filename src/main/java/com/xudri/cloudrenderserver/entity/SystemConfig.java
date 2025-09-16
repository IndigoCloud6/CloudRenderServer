package com.xudri.cloudrenderserver.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * (SystemConfig)表实体类
 *
 * @author maxyun
 * @since 2024-04-08 10:04:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemConfig extends Model<SystemConfig> {

    private Integer id;

    private Integer signallingserverport;

    private String renderclientpath;

    private Integer maximuminstancecount;

    private Integer coturnserverport;

    private String filesavepath;

    private Integer autorunsignallingserver;

    private Integer autoruncoturnserver;

    private String coturnlocalip;

    private String coturnpublicip;

}

