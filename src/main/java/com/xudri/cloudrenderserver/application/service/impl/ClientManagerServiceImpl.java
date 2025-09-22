package com.xudri.cloudrenderserver.application.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.application.service.ClientManagerService;
import com.xudri.cloudrenderserver.core.client.ClientManager;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * @ClassName ClientDetailServiceImpl
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/3/19 10:49
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class ClientManagerServiceImpl implements ClientManagerService {

    private final ClientManager clientManager;

    @Override
    public JSONObject getDetails() {
        return  clientManager.getDetails();
    }
}
