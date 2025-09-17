package com.xudri.cloudrenderserver.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xudri.cloudrenderserver.infrastructure.repository.SystemConfigDao;
import com.xudri.cloudrenderserver.domain.entity.SystemConfig;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import org.springframework.stereotype.Service;

/**
 * (SystemConfig)表服务实现类
 *
 * @author maxyun
 * @since 2024-04-05 17:12:40
 */
@Service("systemConfigService")
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService {

    @Override
    public boolean updateConfig(String key, Object value){
        UpdateWrapper<SystemConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",1);
        updateWrapper.set(key,value);
        return this.update(updateWrapper);
    }

    @Override
    public int getInsLimit(){
        QueryWrapper<SystemConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",1);
        SystemConfig systemConfig= this.getOne(queryWrapper);
        return systemConfig.getMaximuminstancecount();
    }

}

