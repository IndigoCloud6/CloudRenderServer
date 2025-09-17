package com.xudri.cloudrenderserver.infrastructure.monitor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.domain.entity.HostInfo;
import com.xudri.cloudrenderserver.application.service.ProjectService;
import com.xudri.cloudrenderserver.application.service.SystemConfigService;
import com.xudri.cloudrenderserver.core.client.ClientManager;
import com.xudri.cloudrenderserver.core.client.MessageHelper;
import com.xudri.cloudrenderserver.infrastructure.monitor.SystemInfoUtil;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SystemInfoSendJob
 * @Description 定时向管理客户端发送系统信息
 * @Author MaxYun
 * @Date 2023/9/20 12:00
 * @Version 1.0
 */
@Log4j2
@Component
public class SystemInfoSendJob {

    @Resource
    private ProjectService projectService;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private ClientManager clientManager;

    @Resource
    private MessageHelper messageHelper;

    @Scheduled(fixedRate = 5000)
    public void sysInfo() {
        try {
            List<Channel> channels = clientManager.getChannelsByType("admin");
            if (channels.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("No admin channels available for system info broadcast");
                }
                return;
            }

            // 获取系统信息
            JSONObject systemInfo = getSystemInfo();
            String message = systemInfo.toJSONString();

            // 同步发送到所有通道
            for (Channel channel : channels) {
                try {
                    messageHelper.sendMessage(channel, message);
                    if (log.isDebugEnabled()) {
                        log.debug("System info sent to channel: {}", channel.id());
                    }
                } catch (Exception e) {
                    log.error("Failed to send system info to channel: {}", channel.id(), e);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("System info broadcast completed to {} channels", channels.size());
            }
        } catch (Exception e) {
            log.error("Error in system info scheduled task", e);
        }
    }

    /**
     * 获取系统信息
     */
    private JSONObject getSystemInfo() {
        try {
            long projectCount = projectService.count();
            int instanceCount = systemConfigService.getInsLimit();
            int runningCount = 0; // TODO: 需要实现获取实际运行实例数

            HostInfo systemInfo = SystemInfoUtil.getSystemInfo();
            JSONObject result = JSON.parseObject(JSON.toJSONString(systemInfo));
            result.put("projectCount", projectCount);
            result.put("instanceCount", instanceCount);
            result.put("runningCount", runningCount);

            return result;
        } catch (Exception e) {
            log.error("Failed to get system info", e);
            // 返回一个空的JSON对象而不是null，避免NPE
            return new JSONObject();
        }
    }
}