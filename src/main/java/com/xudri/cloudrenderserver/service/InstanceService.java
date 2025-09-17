package com.xudri.cloudrenderserver.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xudri.cloudrenderserver.entity.Instance;


import java.util.List;
import java.util.Map;

/**
 * (Instance)表服务接口
 *
 * @author maxyun
 * @since 2024-05-13 11:15:47
 */
public interface InstanceService extends IService<Instance> {

    boolean addOrUpdateInstance(JSONObject instance);

    Map runInstance(String id) throws InterruptedException;

    Map killInstance(String id) throws InterruptedException;

    List<Instance>  getAllInstance();
}
