package com.xudri.cloudrenderserver.infrastructure.monitor;

import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import com.xudri.cloudrenderserver.core.client.ClientManager;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncher;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * @ClassName PixelStreamingInstanceChecker
 * @Description 实例运行状态检查
 * @Author MaxYun
 * @Since 2025/9/17 14:20
 * @Version 1.0
 */
@Component
public class InstanceCheckJob {

    @Resource
    private PixelStreamingLauncherManager pixelStreamingLauncherManager;

    @Resource
    private ClientManager clientManager;

    @Scheduled(fixedRate = 10000)
    public void check() {
        Collection<PixelStreamingLauncher> launchers = pixelStreamingLauncherManager.getAllLaunchers();
        LoggerUtil.logBusiness("InstanceCheckJob", "Checking %s instances", launchers.size());
        for (PixelStreamingLauncher launcher : launchers) {
            if (!launcher.isRunning()) {
                Optional<Channel> streamerOpt = clientManager.getStreamerByInsId(launcher.getId());
                if (streamerOpt.isEmpty()) {
                    pixelStreamingLauncherManager.stop(launcher.getId());
                }
            }
        }
    }
}
