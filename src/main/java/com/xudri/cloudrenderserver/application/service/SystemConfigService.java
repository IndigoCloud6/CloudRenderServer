package com.xudri.cloudrenderserver.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xudri.cloudrenderserver.domain.entity.SystemConfig;


/**
 * (SystemConfig)表服务接口
 *
 * @author maxyun
 * @since 2024-04-05 17:12:40
 */
public interface SystemConfigService extends IService<SystemConfig> {

    boolean updateConfig(String key, Object value);

    int getInsLimit();
}

