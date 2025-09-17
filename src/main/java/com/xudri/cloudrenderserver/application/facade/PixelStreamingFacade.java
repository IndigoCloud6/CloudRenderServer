package com.xudri.cloudrenderserver.application.facade;

import com.xudri.cloudrenderserver.application.service.InstanceService;
import com.xudri.cloudrenderserver.application.service.ClientManagerService;
import com.xudri.cloudrenderserver.domain.entity.Instance;
import com.xudri.cloudrenderserver.common.exception.Result;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager;
import com.xudri.cloudrenderserver.infrastructure.network.SignallingServer;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 像素流服务外观类
 * 提供统一的像素流服务接口，隐藏内部复杂性
 * 实现外观模式，简化客户端调用
 * 
 * @author MaxYun
 * @version 1.0
 * @since 2024-01-01
 */
@Component
@Log4j2
public class PixelStreamingFacade {

    @Resource
    private InstanceService instanceService;
    
    @Resource
    private ClientManagerService clientManagerService;
    
    @Resource
    private PixelStreamingLauncherManager launcherManager;
    
    @Resource
    private SignallingServer signallingServer;

    /**
     * 启动像素流服务
     * 包括启动信令服务器和相关组件
     * 
     * @return 启动结果
     */
    public Result<String> startPixelStreamingService() {
        try {
            LoggerUtil.logBusiness("服务管理", "开始启动像素流服务");
            
            // 启动信令服务器
            if (!signallingServer.isRunning()) {
                signallingServer.run();
                LoggerUtil.logBusiness("服务管理", "信令服务器启动成功");
            }
            
            LoggerUtil.logBusiness("服务管理", "像素流服务启动完成");
            return Result.success("像素流服务启动成功");
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "服务管理", "启动像素流服务失败", e);
            return Result.error("启动像素流服务失败：" + e.getMessage());
        }
    }

    /**
     * 停止像素流服务
     * 
     * @return 停止结果
     */
    public Result<String> stopPixelStreamingService() {
        try {
            LoggerUtil.logBusiness("服务管理", "开始停止像素流服务");
            
            // 停止所有实例
            List<Instance> instances = instanceService.getAllInstance();
            for (Instance instance : instances) {
                stopInstance(instance.getId());
            }
            
            // 停止信令服务器
            if (signallingServer.isRunning()) {
                signallingServer.stop();
                LoggerUtil.logBusiness("服务管理", "信令服务器已停止");
            }
            
            LoggerUtil.logBusiness("服务管理", "像素流服务停止完成");
            return Result.success("像素流服务停止成功");
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "服务管理", "停止像素流服务失败", e);
            return Result.error("停止像素流服务失败：" + e.getMessage());
        }
    }

    /**
     * 创建并启动像素流实例
     * 
     * @param instanceId 实例ID
     * @param projectId 项目ID
     * @param config 额外配置参数
     * @return 操作结果
     */
    public Result<Map<String, Object>> createAndStartInstance(String instanceId, String projectId, Map<String, String> config) {
        try {
            LoggerUtil.logInstance(instanceId, "创建实例", "项目ID：" + projectId);
            
            // 创建实例配置
            com.alibaba.fastjson2.JSONObject instanceConfig = new com.alibaba.fastjson2.JSONObject();
            instanceConfig.put("instanceid", instanceId);
            instanceConfig.put("projectid", projectId);
            if (config != null) {
                config.forEach(instanceConfig::put);
            }
            
            // 添加实例到数据库
            boolean created = instanceService.addOrUpdateInstance(instanceConfig);
            if (!created) {
                return Result.error("创建实例失败");
            }
            
            // 启动实例
            Map<String, Object> result = instanceService.runInstance(instanceId);
            
            LoggerUtil.logInstance(instanceId, "创建并启动", "操作完成");
            return Result.success(result);
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "实例管理", "创建并启动实例失败：" + instanceId, e);
            return Result.error("创建并启动实例失败：" + e.getMessage());
        }
    }

    /**
     * 停止像素流实例
     * 
     * @param instanceId 实例ID
     * @return 停止结果
     */
    public Result<Map<String, Object>> stopInstance(String instanceId) {
        try {
            LoggerUtil.logInstance(instanceId, "停止实例", "开始停止操作");
            
            Map<String, Object> result = instanceService.killInstance(instanceId);
            
            LoggerUtil.logInstance(instanceId, "停止实例", "操作完成");
            return Result.success(result);
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "实例管理", "停止实例失败：" + instanceId, e);
            return Result.error("停止实例失败：" + e.getMessage());
        }
    }

    /**
     * 重启像素流实例
     * 
     * @param instanceId 实例ID
     * @return 重启结果
     */
    public Result<Map<String, Object>> restartInstance(String instanceId) {
        try {
            LoggerUtil.logInstance(instanceId, "重启实例", "开始重启操作");
            
            // 先停止实例
            Result<Map<String, Object>> stopResult = stopInstance(instanceId);
            if (stopResult.getCode() != 200) {
                return stopResult;
            }
            
            // 等待一段时间确保实例完全停止
            Thread.sleep(2000);
            
            // 重新启动实例
            Map<String, Object> result = instanceService.runInstance(instanceId);
            
            LoggerUtil.logInstance(instanceId, "重启实例", "操作完成");
            return Result.success(result);
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "实例管理", "重启实例失败：" + instanceId, e);
            return Result.error("重启实例失败：" + e.getMessage());
        }
    }

    /**
     * 获取服务运行状态
     * 
     * @return 服务状态信息
     */
    public Result<Map<String, Object>> getServiceStatus() {
        try {
            Map<String, Object> status = new java.util.HashMap<>();
            
            // 信令服务器状态
            status.put("信令服务器运行状态", signallingServer.isRunning() ? "运行中" : "已停止");
            status.put("信令服务器端口", signallingServer.getServicePort());
            
            // 实例统计
            List<Instance> instances = instanceService.getAllInstance();
            status.put("实例总数", instances.size());
            
            // 在线客户端统计
            Map<String, Object> clientStats = new java.util.HashMap<>();
            clientStats.put("说明", "客户端统计功能待实现");
            status.put("客户端连接统计", clientStats);
            
            return Result.success(status);
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "服务管理", "获取服务状态失败", e);
            return Result.error("获取服务状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有实例状态
     * 
     * @return 实例状态列表
     */
    public Result<List<Instance>> getAllInstancesStatus() {
        try {
            List<Instance> instances = instanceService.getAllInstance();
            
            LoggerUtil.logBusiness("实例管理", "查询所有实例状态，共 " + instances.size() + " 个实例");
            return Result.success(instances);
            
        } catch (Exception e) {
            LoggerUtil.logError(log, "实例管理", "获取实例状态失败", e);
            return Result.error("获取实例状态失败：" + e.getMessage());
        }
    }
}