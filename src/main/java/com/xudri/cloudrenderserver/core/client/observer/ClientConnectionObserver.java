package com.xudri.cloudrenderserver.core.client.observer;

import com.xudri.cloudrenderserver.domain.enums.ClientType;
import io.netty.channel.Channel;

/**
 * 客户端连接观察者接口
 * 实现观察者模式，监听客户端连接状态变化
 * 
 * @author MaxYun
 * @version 1.0
 * @since 2024-01-01
 */
public interface ClientConnectionObserver {

    /**
     * 客户端连接建立时的回调
     * 
     * @param channel 客户端通道
     * @param clientType 客户端类型
     * @param clientId 客户端ID
     */
    void onClientConnected(Channel channel, ClientType clientType, String clientId);

    /**
     * 客户端连接断开时的回调
     * 
     * @param channel 客户端通道
     * @param clientType 客户端类型
     * @param clientId 客户端ID
     * @param reason 断开原因
     */
    void onClientDisconnected(Channel channel, ClientType clientType, String clientId, String reason);

    /**
     * 客户端状态发生变化时的回调
     * 
     * @param channel 客户端通道
     * @param clientType 客户端类型
     * @param clientId 客户端ID
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    void onClientStatusChanged(Channel channel, ClientType clientType, String clientId, String oldStatus, String newStatus);
}
