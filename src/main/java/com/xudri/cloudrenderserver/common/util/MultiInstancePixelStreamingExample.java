package com.xudri.cloudrenderserver.common.util;

import com.xudri.cloudrenderserver.config.PixelStreamingConfig;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncher;
import com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * @ClassName MultiInstancePixelStreamingExample
 * @Description MultiInstancePixelStreamingExample
 * @Author MaxYun
 * @Since 2025/9/15 15:03
 * @Version 1.0
 */
public class MultiInstancePixelStreamingExample {

    private static final Logger logger = LogManager.getLogger(MultiInstancePixelStreamingExample.class);

    public static void main(String[] args) {
        // 创建启动器管理器
        com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager manager = new com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager();

        // 添加关闭钩子，确保所有实例在JVM退出时被终止
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("JVM关闭中，正在终止所有实例...");
            manager.stopAll();
        }));

        PixelStreamingConfig config3 = new PixelStreamingConfig("127.0.0.1", 8890);
        config3.setRenderOffscreen(true);
        config3.setResX(1024);
        config3.setResY(768);

        // 启动多个实例
        String executablePath = "D:/xdtc_new/信令服务器/地灾程序/Windows/XDTC_Editor.exe";

        // 组1: 高分辨率实例
        manager.launch("instance-1", "group-high-res", executablePath, config3);
        manager.launch("instance-2", "group-high-res", executablePath, config3);

        // 监控运行状态
        monitorAndManage(manager);
    }

    /**
     * 监控和管理实例的简单控制台界面
     */
    private static void monitorAndManage(com.xudri.cloudrenderserver.core.streaming.PixelStreamingLauncherManager manager) {
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            System.out.println("像素流实例管理控制台");
            System.out.println("命令: status, status <id>, stop <id>, stop-group <groupId>, restart <id>, restart-group <groupId>, cleanup, exit");

            String command;
            while (!"exit".equalsIgnoreCase(command = scanner.nextLine())) {
                String[] parts = command.split(" ");

                switch (parts[0].toLowerCase()) {
                    case "status":
                        if (parts.length > 1) {
                            // 查看特定实例状态
                            PixelStreamingLauncher launcher = manager.getLauncher(parts[1]);
                            if (launcher != null) {
                                System.out.println("实例 " + parts[1] + " 状态: " +
                                        (launcher.isRunning() ? "运行中" : "已停止") +
                                        ", 进程ID: " + launcher.getProcessId());
                            } else {
                                System.out.println("实例 " + parts[1] + " 不存在");
                            }
                        } else {
                            // 查看所有实例状态
                            System.out.println("运行中实例: " + manager.getRunningCount() +
                                    "/" + manager.getTotalCount());
                            for (PixelStreamingLauncher launcher : manager.getAllLaunchers()) {
                                System.out.println("实例 " + launcher.getId() +
                                        " (组: " + launcher.getGroupId() +
                                        ") 状态: " + (launcher.isRunning() ? "运行中" : "已停止"));
                            }
                        }
                        break;

                    case "stop":
                        if (parts.length > 1) {
                            if (manager.stop(parts[1])) {
                                System.out.println("已停止实例 " + parts[1]);
                            } else {
                                System.out.println("停止实例 " + parts[1] + " 失败");
                            }
                        } else {
                            System.out.println("需要指定实例ID");
                        }
                        break;

                    case "stop-group":
                        if (parts.length > 1) {
                            int count = manager.stopGroup(parts[1]);
                            System.out.println("已停止组 " + parts[1] + " 的 " + count + " 个实例");
                        } else {
                            System.out.println("需要指定组ID");
                        }
                        break;

                    case "restart":
                        if (parts.length > 1) {
                            if (manager.restart(parts[1])) {
                                System.out.println("已重启实例 " + parts[1]);
                            } else {
                                System.out.println("重启实例 " + parts[1] + " 失败");
                            }
                        } else {
                            System.out.println("需要指定实例ID");
                        }
                        break;

                    case "restart-group":
                        if (parts.length > 1) {
                            int count = manager.restartGroup(parts[1]);
                            System.out.println("已重启组 " + parts[1] + " 的 " + count + " 个实例");
                        } else {
                            System.out.println("需要指定组ID");
                        }
                        break;

                    case "cleanup":
                        int count = manager.cleanup();
                        System.out.println("已清理 " + count + " 个已停止的实例");
                        break;

                    case "":
                        // 忽略空行
                        break;

                    default:
                        System.out.println("未知命令: " + command);
                        System.out.println("可用命令: status, status <id>, stop <id>, stop-group <groupId>, restart <id>, restart-group <groupId>, cleanup, exit");
                        break;
                }
            }

            // 退出前停止所有实例
            manager.stopAll();
            System.out.println("程序退出");
        }
    }
}