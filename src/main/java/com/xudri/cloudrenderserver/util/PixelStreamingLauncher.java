package com.xudri.cloudrenderserver.util;

import com.xudri.cloudrenderserver.entity.PixelStreamingConfig;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Thread outputThread;
    private Thread errorThread;
    private Thread monitorThread;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
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
            isRunning.set(true);

            // 启动输出和错误流读取线程
            startStreamReaders();

            // 启动监控线程
            startMonitorThread();

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
     * 启动输出和错误流读取线程
     */
    private void startStreamReaders() {
        // 标准输出流读取线程
        outputThread = new Thread(() -> {
            try (InputStream inputStream = process.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("实例 {} 输出: {}", id, line);

                    // 可以在这里添加特定输出的处理逻辑
                    if (line.contains("错误") || line.contains("error") || line.contains("ERROR")) {
                        log.warn("实例 {} 检测到错误输出: {}", id, line);
                    }
                }
            } catch (IOException e) {
                if (!"Stream closed".equals(e.getMessage()) && !isShuttingDown.get()) {
                    log.error("实例 {} 读取输出流失败", id, e);
                }
            } finally {
                log.debug("实例 {} 输出流读取线程结束", id);
            }
        });
        outputThread.setName("App-Output-Reader-" + id);
        outputThread.setDaemon(true);
        outputThread.start();

        // 错误输出流读取线程
        errorThread = new Thread(() -> {
            try (InputStream errorStream = process.getErrorStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    log.error("实例 {} 错误: {}", id, line);

                    // 可以在这里添加特定错误处理逻辑
                    if (line.contains("崩溃") || line.contains("crash") || line.contains("fatal")) {
                        log.error("实例 {} 检测到可能崩溃: {}", id, line);
                        // 可以在这里触发重启逻辑
                    }
                }
            } catch (IOException e) {
                if (!"Stream closed".equals(e.getMessage()) && !isShuttingDown.get()) {
                    log.error("实例 {} 读取错误流失败", id, e);
                }
            } finally {
                log.debug("实例 {} 错误流读取线程结束", id);
            }
        });
        errorThread.setName("App-Error-Reader-" + id);
        errorThread.setDaemon(true);
        errorThread.start();
    }

    /**
     * 启动监控线程
     */
    private void startMonitorThread() {
        monitorThread = new Thread(() -> {
            log.info("开始监控实例 {}", id);

            while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // 每5秒检查一次进程状态
                    Thread.sleep(5000);

                    if (process != null) {
                        // 检查进程是否还在运行
                        if (!process.isAlive()) {
                            int exitCode = process.exitValue();
                            log.warn("实例 {} 已意外退出，退出代码: {}", id, exitCode);
                            isRunning.set(false);
                            break;
                        }

                        // 可以在这里添加其他监控逻辑，如资源使用情况等
                        log.debug("实例 {} 运行正常，进程ID: {}", id, getProcessId());
                    }
                } catch (InterruptedException e) {
                    log.info("实例 {} 监控线程被中断", id);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("实例 {} 监控线程发生异常", id, e);
                }
            }

            log.info("实例 {} 监控结束", id);
        });

        monitorThread.setName("App-Monitor-" + id);
        monitorThread.setDaemon(true);
        monitorThread.start();
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
        isShuttingDown.set(true);

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

            process = null;
            isRunning.set(false);
        }

        // 中断监控线程
        if (monitorThread != null && monitorThread.isAlive()) {
            monitorThread.interrupt();
        }

        isShuttingDown.set(false);
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