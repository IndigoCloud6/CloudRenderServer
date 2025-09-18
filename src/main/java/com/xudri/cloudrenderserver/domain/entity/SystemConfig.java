package com.xudri.cloudrenderserver.domain.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SystemConfig)表实体类
 * 系统配置表，存储应用程序的各种配置参数
 *
 * @author maxyun
 * @since 2024-04-08 10:04:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemConfig extends Model<SystemConfig> {

    /** 配置项主键ID */
    private Integer id;

    /** 信令服务器端口号 */
    private Integer signallingserverport;

    /** 渲染客户端程序路径 */
    private String renderclientpath;

    /** 最大实例数量（同时运行的渲染实例数） */
    private Integer maximuminstancecount;

    /** Coturn（TURN服务器）端口号 */
    private Integer coturnserverport;

    /** 文件保存路径 */
    private String filesavepath;

    /** 是否自动启动信令服务器（1:是, 0:否） */
    private Integer autorunsignallingserver;

    /** 是否自动启动Coturn服务器（1:是, 0:否） */
    private Integer autoruncoturnserver;

    /** Coturn服务器本地IP地址 */
    private String coturnlocalip;

    /** Coturn服务器公网IP地址 */
    private String coturnpublicip;
}