package com.xudri.cloudrenderserver.scheduled;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.entity.HostInfo;
import com.xudri.cloudrenderserver.service.ProjectService;
import com.xudri.cloudrenderserver.service.SystemConfigService;
import com.xudri.cloudrenderserver.util.ClientManager;
import com.xudri.cloudrenderserver.util.MessageHelper;
import com.xudri.cloudrenderserver.util.SystemInfoUtil;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName UeClientDealJob
 * @Description TODO
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
        List<Channel> channels = clientManager.getChannelsByType("admin");
        if (!channels.isEmpty()) {
            long projectCount = projectService.count();
            int instanceCount = systemConfigService.getInsLimit();
            int runingCount = 0;
            HostInfo systemInfo = SystemInfoUtil.getSystemInfo();
            JSONObject returnData = JSON.parseObject(JSON.toJSONString(systemInfo));
            returnData.put("projectCount", projectCount);
            returnData.put("instanceCount", instanceCount);
            returnData.put("runningCount", runingCount);
            for (Channel channel : channels) {
                messageHelper.sendMessage(channel, JSON.toJSONString(returnData));
            }
        }
    }
}
