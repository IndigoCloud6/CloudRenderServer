package com.xudri.cloudrenderserver.service.impl;

import cn.hutool.core.lang.ObjectId;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xudri.cloudrenderserver.dao.InstanceDao;
import com.xudri.cloudrenderserver.entity.Instance;
import com.xudri.cloudrenderserver.entity.PixelStreamingConfig;
import com.xudri.cloudrenderserver.entity.Project;
import com.xudri.cloudrenderserver.entity.SystemConfig;
import com.xudri.cloudrenderserver.server.SignallingServer;
import com.xudri.cloudrenderserver.service.InstanceService;
import com.xudri.cloudrenderserver.service.ProjectService;
import com.xudri.cloudrenderserver.service.SystemConfigService;
import com.xudri.cloudrenderserver.util.ClientManager;
import com.xudri.cloudrenderserver.util.PixelStreamingLauncherManager;
import com.xudri.cloudrenderserver.util.ProcessManagerByPowerShell;
import io.netty.channel.Channel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * (Instance)表服务实现类
 *
 * @author maxyun
 * @since 2024-05-13 11:15:47
 */
@Service("instanceService")
public class InstanceServiceImpl extends ServiceImpl<InstanceDao, Instance> implements InstanceService {

    private static final String RESULT_KEY = "result";
    private static final String RESULT_MSG_KEY = "resultMsg";
    private static final String PROJECT_ID_FIELD = "project_id";
    private static final int MAX_CHECK_ATTEMPTS = 5;
    private static final int CHECK_INTERVAL_MS = 1000;
    private static final int DEFAULT_SYSTEM_CONFIG_ID = 1;
    private static final int DEFAULT_MAX_BITRATE = 50000000;

    @Resource
    private ProjectService projectService;

    @Resource
    private SignallingServer signallingServer;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private ClientManager clientManager;

    @Resource
    private PixelStreamingLauncherManager pixelStreamingLauncherManager;

    @Override
    public boolean addOrUpdateInstance(JSONObject instance) {
        String instanceId = ObjectId.next();
        String maxFps = instance.getString("maxFps");
        String projectId = instance.getString("ProjectID");
        String encoderMaxQP = instance.getString("encoderMaxQP");

        PixelStreamingConfig pixelStreamingConfig = createPixelStreamingConfig(maxFps, encoderMaxQP);
        Instance newInstance = createInstance(instanceId, projectId, pixelStreamingConfig);

        return this.saveOrUpdate(newInstance);
    }

    private PixelStreamingConfig createPixelStreamingConfig(String maxFps, String encoderMaxQP) {
        PixelStreamingConfig config = new PixelStreamingConfig();
        config.setRenderOffscreen(true);
        config.setForceRes(true);
        config.setUnattended(true);
        config.setPixelStreamingEncoderMaxQuality(Integer.valueOf(encoderMaxQP));
        config.setPixelStreamingWebRTCMaxBitrate(DEFAULT_MAX_BITRATE);
        config.setPixelStreamingWebRTCMaxFps(Integer.valueOf(maxFps));
        return config;
    }

    private Instance createInstance(String id, String projectId, PixelStreamingConfig config) {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setRenderconfig(JSON.toJSONString(config));
        instance.setProjectid(projectId);
        instance.setState(0);
        return instance;
    }

    @Override
    public Map<String, Object> runInstance(String id) throws InterruptedException {

        if (isInstanceAlreadyRunning(id)) {
            return createResultMap(false, "重复启动!");
        }

        SystemConfig systemConfig = systemConfigService.getById(DEFAULT_SYSTEM_CONFIG_ID);
        if (!isRenderClientExists(systemConfig.getRenderclientpath())) {
            return createResultMap(false, "云渲染程序不存在!");
        }

        Instance instance = this.getById(id);
        PixelStreamingConfig pixelStreamingConfig = parsePixelStreamingConfig(instance);
        if (pixelStreamingConfig == null) {
            return createResultMap(false, "实例配置错误!");
        }

        Project project = getProject(instance.getProjectid());
        if (project == null || !isProjectExists(project.getSavePath())) {
            return createResultMap(false, "工程不存在!");
        }

        String signallingServerURL = createSignallingServerURL(id, instance.getProjectid());
        pixelStreamingConfig.setPixelStreamingURL(signallingServerURL);

        pixelStreamingLauncherManager.launch(
                id,
                instance.getProjectid(),
                project.getSavePath(),
                pixelStreamingConfig
        );

        boolean isRunning = checkInstanceStatus(id, true);
        return createResultMap(isRunning, isRunning ? "启动成功!" : "启动失败!");
    }

    @Override
    public Map<String, Object> killInstance(String id) throws InterruptedException {
        killProcessesByInstanceId(id);

        boolean isKilled = checkInstanceStatus(id, false);
        return createResultMap(isKilled, isKilled ? "停止成功!" : "停止失败!");
    }

    @Override
    public List<Instance> getAllInstance() {
        List<Instance> allInstances = this.list();

        for (Instance instance : allInstances) {
            enrichInstanceWithProjectInfo(instance);
            setInstanceRunningStatus(instance);
            setInstancePreviewUrl(instance);
        }

        return allInstances;
    }

    private boolean isInstanceAlreadyRunning(String id) {
        return clientManager.getStreamerByInsId(id).isPresent();
    }

    private boolean isRenderClientExists(String renderClientPath) {
        return Files.exists(Paths.get(renderClientPath));
    }

    private PixelStreamingConfig parsePixelStreamingConfig(Instance instance) {
        return JSON.parseObject(instance.getRenderconfig(), PixelStreamingConfig.class);
    }

    private Project getProject(String projectId) {
        return projectService.getOne(new QueryWrapper<Project>().eq(PROJECT_ID_FIELD, projectId));
    }

    private boolean isProjectExists(String projectPath) {
        return Files.exists(Paths.get(projectPath));
    }

    private String createSignallingServerURL(String instanceId, String projectId) {
        return "ws://127.0.0.1:" + signallingServer.getServicePort() +
                "/type=streamer&insid=" + instanceId + "&projectid=" + projectId;
    }

    private void killProcessesByInstanceId(String id) {
        JSONArray processList = ProcessManagerByPowerShell.queryProcessListByCmdKeyAndProcessName("XDTC", id);
        for (int i = 0; i < processList.size(); i++) {
            JSONObject process = processList.getJSONObject(i);
            String processId = process.getString("ProcessId");
            ProcessManagerByPowerShell.killProcessListByPid(processId);
        }
    }

    private boolean checkInstanceStatus(String id, boolean shouldBeRunning) throws InterruptedException {
        for (int attempt = 1; attempt <= MAX_CHECK_ATTEMPTS; attempt++) {
            Thread.sleep(CHECK_INTERVAL_MS);
            Optional<Channel> streamerOpt = clientManager.getStreamerByInsId(id);

            if (shouldBeRunning && streamerOpt.isPresent()) {
                return true;
            }
            if (!shouldBeRunning && streamerOpt.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void enrichInstanceWithProjectInfo(Instance instance) {
        Project project = getProject(instance.getProjectid());
        instance.setProjectName(project != null ? project.getProjectName() : "项目已删除");
    }

    private void setInstanceRunningStatus(Instance instance) {
        boolean isRunning = clientManager.getStreamerByInsId(instance.getId()).isPresent();
        instance.setRun(isRunning);
    }

    private void setInstancePreviewUrl(Instance instance) {
        instance.setPreviewUrl(String.valueOf(signallingServer.getServicePort()));
    }

    private Map<String, Object> createResultMap(boolean result, String message) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(RESULT_KEY, result);
        resultMap.put(RESULT_MSG_KEY, message);
        return resultMap;
    }
}