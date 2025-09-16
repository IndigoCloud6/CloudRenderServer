package com.xudri.cloudrenderserver.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.service.ClientManagerService;
import com.xudri.cloudrenderserver.util.ClientManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @ClassName ClientDetailServiceImpl
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/3/19 10:49
 * @Version 1.0
 */
@Service
public class ClientManagerServiceImpl implements ClientManagerService {

    @Resource
    private ClientManager clientManager;

    @Override
    public JSONObject getDetails() {
        return  clientManager.getDetails();
    }
}
