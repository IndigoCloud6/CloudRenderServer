package com.xudri.cloudrenderserver.runner;

import cn.hutool.core.net.NetUtil;
import com.xudri.cloudrenderserver.entity.SystemConfig;
import com.xudri.cloudrenderserver.server.SignallingServer;
import com.xudri.cloudrenderserver.service.SystemConfigService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * @ClassName ApplicationRunner
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/3/18 18:22
 * @Version 1.0
 */

@Component
public class CloudRenderRunner implements ApplicationRunner {

    @Resource
    private SignallingServer signallingServer;

    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public void run(ApplicationArguments args) {
        SystemConfig systemConfig = systemConfigService.getById(1);
        int signallingServerPort = systemConfig.getSignallingserverport();
        int autoRunSignallingServer = systemConfig.getAutorunsignallingserver();
        signallingServer.setServicePort(signallingServerPort);
        if (autoRunSignallingServer == 1 && NetUtil.isUsableLocalPort(signallingServerPort)) {
            signallingServer.run();
        }
    }
}
