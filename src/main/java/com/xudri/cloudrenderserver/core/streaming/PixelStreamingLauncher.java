package com.xudri.cloudrenderserver.core.streaming;

import com.xudri.cloudrenderserver.config.PixelStreamingConfig;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xudri.cloudrenderserver.infrastructure.monitor.ProcessManagerByPowerShell.killProcess;

/**
 * @ClassName PixelStreamingLauncher
 * @Description 像素流应用程序启动器
 * @Author MaxYun
 * @Since 2025/9/15 14:21
 * @Version 1.0
 */

@Log4j2
@Data
public class PixelStreamingLauncher {

    private final String id;
    private final String groupId;
    private Process process;
    private LocalDateTime launchTime;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private PixelStreamingConfig config;
    private String executablePath;

    /**
     * 构造函数
     *
     * @param id      实例唯一ID
     * @param groupId 实例组ID
     */
    public PixelStreamingLauncher(String id, String groupId) {
        this.id = id;
        this.groupId = groupId;
    }

    /**
     * 启动像素流应用程序
     *
     * @param executablePath 可执行文件路径
     * @param config         像素流配置
     * @return 是否成功启动
     */
    public boolean launch(String executablePath, PixelStreamingConfig config) {
        this.executablePath = executablePath;
        this.config = config;

        if (isRunning.get()) {
            log.warn("实例 {} 已在运行中", id);
            return false;
        }

        try {
            // 构建命令行
            List<String> command = buildCommand(executablePath, config);
            log.info("实例 {} 启动命令: {}", id, String.join(" ", command));

            // 创建进程构建器
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 启动进程
            process = processBuilder.start();
            this.launchTime = LocalDateTime.now();
            isRunning.set(true);

            log.info("实例 {} 已启动，进程ID: {}", id, getProcessId());
            return true;

        } catch (IOException e) {
            log.error("实例 {} 启动失败", id, e);
            isRunning.set(false);
            return false;
        }
    }

    /**
     * 构建命令行
     *
     * @param executablePath 可执行文件路径
     * @param config         像素流配置
     * @return 命令行列表
     */
    private List<String> buildCommand(String executablePath, PixelStreamingConfig config) {
        List<String> command = new java.util.ArrayList<>();
        command.add(executablePath);
        command.addAll(config.toCommandLineArgs());
        return command;
    }

    /**
     * 获取进程ID
     *
     * @return 进程ID，如果无法获取则返回-1
     */
    public long getProcessId() {
        if (process == null) {
            return -1;
        }

        try {
            // Java 9+ 的方式
            return process.pid();
        } catch (NoSuchMethodError e) {
            // Java 8 及以下版本，尝试通过反射获取
            try {
                java.lang.reflect.Field field = process.getClass().getDeclaredField("pid");
                field.setAccessible(true);
                return field.getLong(process);
            } catch (Exception ex) {
                log.warn("实例 {} 无法获取进程ID", id, ex);
                return -1;
            }
        }
    }

    /**
     * 检查应用程序是否正在运行
     *
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return isRunning.get() && process != null && process.isAlive();
    }

    /**
     * 等待应用程序退出
     *
     * @return 退出代码
     */
    public int waitFor() {
        if (process == null) {
            return -1;
        }

        try {
            int exitCode = process.waitFor();
            isRunning.set(false);
            return exitCode;
        } catch (InterruptedException e) {
            log.error("实例 {} 等待退出时被中断", id, e);
            Thread.currentThread().interrupt();
            return -1;
        }
    }

    /**
     * 终止应用程序
     */
    public void destroy() {

        if (process != null) {
            log.info("正在终止实例 {}...", id);

            // 先尝试正常关闭
            process.destroy();

            // 等待一段时间让进程正常退出
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 如果进程还在运行，强制终止
            if (process.isAlive()) {
                log.warn("实例 {} 未正常退出，强制终止...", id);
                process.destroyForcibly();
            }

            try {
                int exitCode = process.waitFor();
                log.info("实例 {} 已终止，退出代码: {}", id, exitCode);
            } catch (InterruptedException e) {
                log.warn("实例 {} 等待终止时被中断", id, e);
                Thread.currentThread().interrupt();
            }

            killProcess(Paths.get(executablePath).getFileName().toString(), id);

            process = null;
            isRunning.set(false);
        }
        isRunning.set(false);
    }

    /**
     * 优雅关闭应用程序
     */
    public void shutdown() {
        destroy();
    }

    /**
     * 重启应用程序
     *
     * @return 是否成功重启
     */
    public boolean restart() {
        log.info("正在重启实例 {}...", id);
        destroy();

        // 等待一段时间确保进程完全终止
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        return launch(executablePath, config);
    }

    @Override
    public String toString() {
        return "PixelStreamingLauncher{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", isRunning=" + isRunning +
                ", processId=" + getProcessId() +
                '}';
    }
}