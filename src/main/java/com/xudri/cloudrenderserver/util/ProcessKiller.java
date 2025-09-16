package com.xudri.cloudrenderserver.util;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @ClassName ProcessKiller
 * @Description 进程管理
 * @Author MaxYun
 * @Since 2025/9/15 11:57
 * @Version 1.0
 */
@Log4j2
public class ProcessKiller {

    /**
     * 根据端口号杀死占用该端口的进程
     * @param port 端口号
     * @return true 如果成功杀死进程或未找到占用进程，false 如果发生错误
     */
    public static boolean killProcessByPort(int port) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            String pid;

            if (os.contains("win")) {
                // Windows 系统
                command = "netstat -aon | findstr :" + port;
                pid = findPidWindows(command);
                if (pid != null) {
                    Runtime.getRuntime().exec("taskkill /PID " + pid + " /F");
                    log.info("成功杀死占用端口 {} 的进程，PID: {}", port, pid);
                    return true;
                } else {
                    log.info("未找到占用端口 {} 的进程", port);
                    return true;
                }
            } else {
                // 类 Unix 系统 (Linux, macOS)
                command = "lsof -i :" + port + " | grep LISTEN";
                pid = findPidUnix(command);
                if (pid != null) {
                    Runtime.getRuntime().exec("kill -9 " + pid);
                    log.info("成功杀死占用端口 {} 的进程，PID: {}", port, pid);
                    return true;
                } else {
                    log.info("未找到占用端口 {} 的进程", port);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("杀死端口 {} 的进程失败", port, e);
            return false;
        }
    }

    /**
     * 在 Windows 系统上查找占用端口的进程 PID
     * @param command netstat 命令
     * @return 进程 PID 或 null（未找到）
     */
    private static String findPidWindows(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 示例输出: TCP  0.0.0.0:8080  0.0.0.0:0  LISTENING  1234
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 5) {
                    return parts[parts.length - 1]; // PID 在最后
                }
            }
        }
        return null;
    }

    /**
     * 在类 Unix 系统上查找占用端口的进程 PID
     * @param command lsof 命令
     * @return 进程 PID 或 null（未找到）
     */
    private static String findPidUnix(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 示例输出: java  1234  user  ...  TCP *:8080 (LISTEN)
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    return parts[1]; // PID 在第二列
                }
            }
        }
        return null;
    }
}