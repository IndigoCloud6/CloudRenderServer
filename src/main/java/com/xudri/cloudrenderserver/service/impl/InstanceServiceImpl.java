//package com.xudri.cloudrenderserver.service.impl;
//
//import cn.hutool.core.convert.Convert;
//import cn.hutool.core.lang.ObjectId;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONArray;
//import com.alibaba.fastjson2.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.xudri.cloudrenderserver.dao.InstanceDao;
//import com.xudri.cloudrenderserver.entity.Instance;
//import com.xudri.cloudrenderserver.entity.Project;
//import com.xudri.cloudrenderserver.entity.SystemConfig;
//import com.xudri.cloudrenderserver.server.SignallingServer;
//import com.xudri.cloudrenderserver.service.InstanceService;
//import com.xudri.cloudrenderserver.service.ProjectService;
//import com.xudri.cloudrenderserver.service.SystemConfigService;
//import com.xudri.cloudrenderserver.util.ClientManager;
//import com.xudri.cloudrenderserver.util.ProcessManagerByPoweShell;
//import io.netty.channel.Channel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * (Instance)表服务实现类
// *
// * @author maxyun
// * @since 2024-05-13 11:15:47
// */
//@Service("instanceService")
//public class InstanceServiceImpl extends ServiceImpl<InstanceDao, Instance> implements InstanceService {
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private SignallingServer signallingServer;
//
//    @Autowired
//    private SystemConfigService SystemConfigService;
//
//    @Autowired
//    private ClientManager clientManager;
//
//    @Override
//    public boolean addInstance(JSONObject instance) {
//        String insid = ObjectId.next();
//        String maxFps = instance.getString("maxFps");
//        String projectID = instance.getString("ProjectID");
//        String encoderMaxQP = instance.getString("encoderMaxQP");
//        StreamerStartupParam streamer = new StreamerStartupParam();
//        streamer.setAllowCommands(true);
//        streamer.setMaxFps(Convert.toInt(maxFps));
//        streamer.setOffScreen(true);
//        streamer.setEncoderMaxQP(encoderMaxQP);
//        streamer.setUnattended(true);
//        streamer.setForceRes(true);
//        streamer.setResX("1920");
//        streamer.setResY("1080");
//        streamer.setEncoderMultipass("FULL");
//        streamer.setInstId(insid);
//        Instance ueins = new Instance();
//        ueins.setId(insid);
//        ueins.setRenderconfig(JSON.toJSONString(streamer));
//        ueins.setProjectid(projectID);
//        ueins.setState(0);
//        return this.save(ueins);
//    }
//
//    @Override
//    public boolean aupdateInstance(JSONObject instance) {
//        String insid = instance.getString("id");
//        String maxFps = instance.getString("maxFps");
//        String projectID = instance.getString("ProjectID");
//        String encoderMaxQP = instance.getString("encoderMaxQP");
//        StreamerStartupParam streamer = new StreamerStartupParam();
//        streamer.setAllowCommands(true);
//        streamer.setMaxFps(Convert.toInt(maxFps));
//        streamer.setOffScreen(true);
//        streamer.setEncoderMaxQP(encoderMaxQP);
//        streamer.setUnattended(true);
//        streamer.setForceRes(true);
//        streamer.setEncoderMultipass("FULL");
//        streamer.setInstId(insid);
//        streamer.setResX("1920");
//        streamer.setResY("1080");
//        Instance ueins = new Instance();
//        ueins.setId(insid);
//        ueins.setRenderconfig(JSON.toJSONString(streamer));
//        ueins.setProjectid(projectID);
//        ueins.setState(0);
//        return this.updateById(ueins);
//    }
//
//    @Override
//    public Map runInstance(String id) throws InterruptedException {
//        Map result = new HashMap();
//
//        Channel streamer = clientManager.getStreamerByInsId(id);
//        if (streamer != null) {
//            result.put("result", false);
//            result.put("resultMsg", "重复启动!");
//            return result;
//        }
//
//        SystemConfig systemConfig = SystemConfigService.getById(1);
//        String renderclientpath = systemConfig.getRenderclientpath();
//        java.nio.file.Path path = Paths.get(renderclientpath);
//        if (Files.notExists(path)) {
//            result.put("result", false);
//            result.put("resultMsg", "云渲染程序不存在!");
//            return result;
//        }
//
//        Instance instance = this.getById(id);
//        String renderconfig = instance.getRenderconfig();
//        StreamerStartupParam param = JSON.parseObject(renderconfig, StreamerStartupParam.class);
//        String projectid = instance.getProjectid();
//        String instanceId = instance.getId();
//        param.setInstId(instanceId);
//        Project project = projectService.getOne(new QueryWrapper<Project>().eq("project_id", projectid));
//        if (project == null) {
//            result.put("result", false);
//            result.put("resultMsg", "工程不存在!");
//            return result;
//        }
//        String projectPath = project.getSavePath();
//        java.nio.file.Path project_path = Paths.get(projectPath);
//        if (Files.notExists(project_path)) {
//            result.put("result", false);
//            result.put("resultMsg", "工程不存在!");
//            return result;
//        }
//        String signallingServerURL = "ws://127.0.0.1:" + signallingServer.getServicePort() + "/type=streamer&insid="
//                + instanceId + "&projectid=" + projectid;
//        param.setPixelStreamingURL(signallingServerURL);
//        param.setProjectPath(projectPath);
//        String cmdLines = ProcessManager.cmdStringBuilder(param);
//        ProcessManager.run(renderclientpath, cmdLines);
//        if (checkInstanceIsRunning(id)) {
//            result.put("result", true);
//            result.put("resultMsg", "启动成功!");
//        } else {
//            result.put("result", false);
//            result.put("resultMsg", "启动失败!");
//        }
//        return result;
//    }
//
//    @Override
//    public Map killInstance(String id) throws InterruptedException {
//        Map result = new HashMap();
//
//        /*
//        Channel streamer = ClientManager.getStreamerByInsId(id);
//        if (streamer != null) {
//            InetSocketAddress socketAddress = (InetSocketAddress) streamer.remoteAddress();
//            int clientPort = socketAddress.getPort();
//            ProcessManager.killProcessByPort(clientPort);
//        }*/
//        JSONArray processList = ProcessManagerByPoweShell.queryProcessListByCmdKeyAndProcessName("XDTC", id);
//        for (int i = 0; i < processList.size(); i++) {
//            JSONObject process = processList.getJSONObject(i);
//            String processId = process.getString("ProcessId");
//            ProcessManagerByPoweShell.killProcessListByPid(processId);
//        }
//        if (checkInstanceBeKilled(id)){
//            result.put("result", true);
//            result.put("resultMsg", "启动成功!");
//        } else {
//            result.put("result", false);
//            result.put("resultMsg", "启动失败!");
//        }
//        return result;
//    }
//
//    @Override
//    public List<Instance> getAllInstance() {
//        List<Instance> all = this.list();
//        for (Instance instance : all) {
//            String id = instance.getId();
//            Project project =
//                    projectService.getOne(new QueryWrapper<Project>().eq("project_id", instance.getProjectid()));
//            if (project != null) {
//                instance.setProjectName(project.getProjectName());
//            } else {
//                instance.setProjectName("项目已删除");
//            }
//            Channel streamer = clientManager.getStreamerByInsId(id);
//            if (streamer != null) {
//                instance.setRun(true);
//            } else instance.setRun(false);
//            int port = signallingServer.getServicePort();
//            String url = String.valueOf(port);
//            instance.setPreviewUrl(url);
//        }
//        return all;
//    }
//
//    private boolean checkInstanceIsRunning(String id) throws InterruptedException {
//        int maxAttempts = 5;
//        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
//            Thread.sleep(1000);
//            Channel streamer = clientManager.getStreamerByInsId(id);
//            if (streamer != null) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private boolean checkInstanceBeKilled(String id) throws InterruptedException {
//        int maxAttempts = 5;
//        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
//            Thread.sleep(1000);
//            Channel streamer = clientManager.getStreamerByInsId(id);
//            if (streamer == null) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
