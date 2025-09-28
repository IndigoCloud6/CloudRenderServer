package com.xudri.cloudrenderserver.infrastructure.monitor;

import com.xudri.cloudrenderserver.application.service.InstanceService;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import com.xudri.cloudrenderserver.core.client.ClientManager;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncher;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager;
import com.xudri.cloudrenderserver.domain.entity.Instance;
import com.xudri.cloudrenderserver.domain.entity.SystemConfig;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Collection;

import static com.xudri.cloudrenderserver.infrastructure.monitor.SystemInfoUtil.getFileNameWithoutExtension;

/**
 * @ClassName PixelStreamingInstanceChecker
 * @Description 实例运行状态检查
 * @Author MaxYun
 * @Since 2025/9/17 14:20
 * @Version 1.0
 */
@Component
@AllArgsConstructor
public class InstanceControlJob {

    private final PixelStreamingLauncherManager pixelStreamingLauncherManager;

    private final ClientManager clientManager;

    private final InstanceService instanceService;

    private final SystemConfigService systemConfigService;

    @Scheduled(fixedRate = 10000)
    public void check() {
        Collection<PixelStreamingLauncher> launchers = pixelStreamingLauncherManager.getAllLaunchers();
        // 第一轮循环：清理无效的Launcher
        launchers.stream()
                .filter(launcher -> !launcher.isRunning())
                .forEach(launcher -> {
                    if (clientManager.getStreamerByInsId(launcher.getId()).isEmpty()) {
                        pixelStreamingLauncherManager.stop(launcher.getId());
                        LoggerUtil.logBusiness("InstanceControlJob", "Stopped inactive launcher for instance %s", launcher.getId());
                    }
                });

        SystemConfig systemConfig = systemConfigService.getById(1);
        String processName = getFileNameWithoutExtension(systemConfig.getRenderclientpath());
        // 第二轮循环：检查Streamer状态
        clientManager.getAllStreamers().forEach(streamer -> {
            String insId = streamer.attr(ClientManager.INS_ID).get();
            String projectId = streamer.attr(ClientManager.PROJECT_ID).get();

            if (StringUtils.isNotEmpty(insId)) {
                PixelStreamingLauncher launcher = pixelStreamingLauncherManager.getLauncher(insId);
                if (launcher == null) {
                    Instance instance = instanceService.getById(insId);
                    if (instance == null) {
                        LoggerUtil.logBusiness("InstanceControlJob", "Stopping streamer for non-existing instance %s", insId);
                        streamer.close();
                        ProcessManagerByPowerShell.killProcess(processName, insId);
                    } else {
                        pixelStreamingLauncherManager.addLauncher(new PixelStreamingLauncher(insId, projectId));
                    }
                }
            }
        });
    }

    @PreDestroy
    public void destroy() {
        LoggerUtil.logBusiness("InstanceControlJob", "Stopping all instances due to application shutdown");
        Collection<PixelStreamingLauncher> launchers = pixelStreamingLauncherManager.getAllLaunchers();
        for (PixelStreamingLauncher launcher : launchers) {
            pixelStreamingLauncherManager.stop(launcher.getId());
        }
    }
}
