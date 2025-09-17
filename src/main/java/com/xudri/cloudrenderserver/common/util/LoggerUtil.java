package com.xudri.cloudrenderserver.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 统一日志工具类
 * 提供中文日志记录方法，统一日志格式和输出
 * 
 * @author MaxYun
 * @version 1.0
 * @since 2024-01-01
 */
public class LoggerUtil {
    
    private static final Logger BUSINESS_LOGGER = LogManager.getLogger("BUSINESS");
    
    /**
     * 记录业务操作日志
     * 
     * @param operation 操作名称
     * @param message 详细信息
     */
    public static void logBusiness(String operation, String message) {
        BUSINESS_LOGGER.info("[{}] {}", operation, message);
    }
    
    /**
     * 记录业务操作日志（带参数）
     * 
     * @param operation 操作名称
     * @param message 详细信息模板
     * @param args 参数
     */
    public static void logBusiness(String operation, String message, Object... args) {
        BUSINESS_LOGGER.info("[{}] {}", operation, String.format(message, args));
    }
    
    /**
     * 记录用户连接相关日志
     * 
     * @param clientType 客户端类型
     * @param clientId 客户端ID
     * @param action 操作类型
     * @param details 详细信息
     */
    public static void logConnection(String clientType, String clientId, String action, String details) {
        logBusiness("客户端连接", "%s客户端[%s] %s - %s", clientType, clientId, action, details);
    }
    
    /**
     * 记录像素流实例相关日志
     * 
     * @param instanceId 实例ID
     * @param action 操作类型
     * @param details 详细信息
     */
    public static void logInstance(String instanceId, String action, String details) {
        logBusiness("像素流实例", "实例[%s] %s - %s", instanceId, action, details);
    }
    
    /**
     * 记录系统监控相关日志
     * 
     * @param component 组件名称
     * @param status 状态信息
     * @param details 详细信息
     */
    public static void logSystem(String component, String status, String details) {
        logBusiness("系统监控", "%s %s - %s", component, status, details);
    }
    
    /**
     * 记录网络通信相关日志
     * 
     * @param protocol 协议类型
     * @param action 操作类型
     * @param details 详细信息
     */
    public static void logNetwork(String protocol, String action, String details) {
        logBusiness("网络通信", "%s %s - %s", protocol, action, details);
    }
    
    /**
     * 记录服务启动相关日志
     * 
     * @param serviceName 服务名称
     * @param status 启动状态
     * @param config 配置信息
     */
    public static void logServiceStartup(String serviceName, String status, String config) {
        logBusiness("服务启动", "%s %s - 配置：%s", serviceName, status, config);
    }
    
    /**
     * 记录错误信息（带异常）
     * 
     * @param logger 日志记录器
     * @param operation 操作名称
     * @param message 错误信息
     * @param throwable 异常对象
     */
    public static void logError(Logger logger, String operation, String message, Throwable throwable) {
        logger.error("[{}] {} - 异常：{}", operation, message, throwable.getMessage(), throwable);
    }
    
    /**
     * 记录警告信息
     * 
     * @param logger 日志记录器
     * @param operation 操作名称
     * @param message 警告信息
     */
    public static void logWarning(Logger logger, String operation, String message) {
        logger.warn("[{}] {}", operation, message);
    }
    
    /**
     * 记录信息日志
     * 
     * @param logger 日志记录器
     * @param operation 操作名称
     * @param message 信息内容
     */
    public static void logInfo(Logger logger, String operation, String message) {
        logger.info("[{}] {}", operation, message);
    }
    
    /**
     * 记录调试信息
     * 
     * @param logger 日志记录器
     * @param operation 操作名称
     * @param message 调试信息
     */
    public static void logDebug(Logger logger, String operation, String message) {
        logger.debug("[{}] {}", operation, message);
    }
}