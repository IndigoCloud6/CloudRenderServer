package com.xudri.cloudrenderserver.core.streaming;
import com.xudri.cloudrenderserver.config.PixelStreamingConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager
 * @Description 像素流启动器管理类
 * @Author MaxYun
 * @Since 2025/9/15 14:59
 * @Version 1.0
 */

@Component
@Log4j2
public class PixelStreamingLauncherManager {

    private final Map<String, PixelStreamingLauncher> launchers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> groupLaunchers = new ConcurrentHashMap<>();

    /**
     * 启动新的像素流实例
     * @param id 实例唯一ID
     * @param groupId 实例组ID
     * @param executablePath 可执行文件路径
     * @param config 像素流配置
     * @return 是否成功启动
     */
    public boolean launch(String id, String groupId, String executablePath, PixelStreamingConfig config) {
        if (launchers.containsKey(id)) {
            log.warn("实例ID {} 已存在", id);
            return false;
        }

        PixelStreamingLauncher launcher = new PixelStreamingLauncher(id, groupId);
        boolean success = launcher.launch(executablePath, config);

        if (success) {
            launchers.put(id, launcher);

            // 添加到组映射
            groupLaunchers.computeIfAbsent(groupId, k -> new HashSet<>()).add(id);

            log.info("成功启动实例 {}，组ID: {}", id, groupId);
        }

        return success;
    }

    /**
     * 停止指定实例
     * @param id 实例ID
     * @return 是否成功停止
     */
    public boolean stop(String id) {
        PixelStreamingLauncher launcher = launchers.get(id);
        if (launcher == null) {
            log.warn("实例ID {} 不存在", id);
            return false;
        }

        launcher.shutdown();
        launchers.remove(id);

        // 从组映射中移除
        String groupId = launcher.getGroupId();
        if (groupLaunchers.containsKey(groupId)) {
            groupLaunchers.get(groupId).remove(id);
            if (groupLaunchers.get(groupId).isEmpty()) {
                groupLaunchers.remove(groupId);
            }
        }

        log.info("成功停止实例 {}", id);
        return true;
    }

    /**
     * 停止指定组的所有实例
     * @param groupId 组ID
     * @return 成功停止的实例数量
     */
    public int stopGroup(String groupId) {
        Set<String> groupInstanceIds = groupLaunchers.get(groupId);
        if (groupInstanceIds == null || groupInstanceIds.isEmpty()) {
            log.warn("组ID {} 不存在或没有实例", groupId);
            return 0;
        }

        // 复制集合以避免并发修改异常
        Set<String> instanceIds = new HashSet<>(groupInstanceIds);
        int count = 0;

        for (String id : instanceIds) {
            if (stop(id)) {
                count++;
            }
        }

        log.info("成功停止组 {} 的 {} 个实例", groupId, count);
        return count;
    }

    /**
     * 停止所有实例
     * @return 成功停止的实例数量
     */
    public int stopAll() {
        int count = 0;

        // 复制键集合以避免并发修改异常
        Set<String> instanceIds = new HashSet<>(launchers.keySet());

        for (String id : instanceIds) {
            if (stop(id)) {
                count++;
            }
        }

        log.info("成功停止所有 {} 个实例", count);
        return count;
    }

    /**
     * 重启指定实例
     * @param id 实例ID
     * @return 是否成功重启
     */
    public boolean restart(String id) {
        PixelStreamingLauncher launcher = launchers.get(id);
        if (launcher == null) {
            log.warn("实例ID {} 不存在", id);
            return false;
        }

        return launcher.restart();
    }

    /**
     * 重启指定组的所有实例
     * @param groupId 组ID
     * @return 成功重启的实例数量
     */
    public int restartGroup(String groupId) {
        Set<String> groupInstanceIds = groupLaunchers.get(groupId);
        if (groupInstanceIds == null || groupInstanceIds.isEmpty()) {
            log.warn("组ID {} 不存在或没有实例", groupId);
            return 0;
        }

        int count = 0;
        for (String id : groupInstanceIds) {
            if (restart(id)) {
                count++;
            }
        }

        log.info("成功重启组 {} 的 {} 个实例", groupId, count);
        return count;
    }

    /**
     * 获取指定实例
     * @param id 实例ID
     * @return 实例对象，如果不存在则返回null
     */
    public PixelStreamingLauncher getLauncher(String id) {
        return launchers.get(id);
    }

    /**
     * 获取指定组的所有实例
     * @param groupId 组ID
     * @return 实例集合，如果组不存在则返回空集合
     */
    public Set<PixelStreamingLauncher> getLaunchersByGroup(String groupId) {
        Set<String> instanceIds = groupLaunchers.get(groupId);
        if (instanceIds == null) {
            return Collections.emptySet();
        }

        Set<PixelStreamingLauncher> result = new HashSet<>();
        for (String id : instanceIds) {
            PixelStreamingLauncher launcher = launchers.get(id);
            if (launcher != null) {
                result.add(launcher);
            }
        }

        return result;
    }

    /**
     * 获取所有实例
     * @return 所有实例的集合
     */
    public Collection<PixelStreamingLauncher> getAllLaunchers() {
        return launchers.values();
    }

    /**
     * 获取所有组ID
     * @return 所有组ID的集合
     */
    public Set<String> getAllGroupIds() {
        return groupLaunchers.keySet();
    }

    /**
     * 检查指定实例是否正在运行
     * @param id 实例ID
     * @return 是否正在运行
     */
    public boolean isRunning(String id) {
        PixelStreamingLauncher launcher = launchers.get(id);
        return launcher != null && launcher.isRunning();
    }

    /**
     * 获取运行中的实例数量
     * @return 运行中的实例数量
     */
    public int getRunningCount() {
        int count = 0;
        for (PixelStreamingLauncher launcher : launchers.values()) {
            if (launcher.isRunning()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取实例总数
     * @return 实例总数
     */
    public int getTotalCount() {
        return launchers.size();
    }

    /**
     * 清理已停止的实例
     * @return 清理的实例数量
     */
    public int cleanup() {
        int count = 0;

        // 复制键集合以避免并发修改异常
        Set<String> instanceIds = new HashSet<>(launchers.keySet());

        for (String id : instanceIds) {
            PixelStreamingLauncher launcher = launchers.get(id);
            if (launcher != null && !launcher.isRunning()) {
                // 从主映射中移除
                launchers.remove(id);

                // 从组映射中移除
                String groupId = launcher.getGroupId();
                if (groupLaunchers.containsKey(groupId)) {
                    groupLaunchers.get(groupId).remove(id);
                    if (groupLaunchers.get(groupId).isEmpty()) {
                        groupLaunchers.remove(groupId);
                    }
                }

                count++;
                log.info("清理已停止的实例 {}", id);
            }
        }

        return count;
    }
}