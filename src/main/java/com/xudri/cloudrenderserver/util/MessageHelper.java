package com.xudri.cloudrenderserver.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.entity.ClientType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName MessageHelper
 * @Description 消息发送工具类
 * @Author MaxYun
 * @Since 2025/9/15 17:28
 * @Version 1.0
 */
@Component
@Log4j2
public class MessageHelper {
    private static final String MESSAGE_SENT_SUCCESS = "消息发送成功 - 通道: {}, 消息类型: {}, 内容: {}";
    private static final String MESSAGE_SENT_FAIL = "消息发送失败 - 通道: {}, 消息类型: {}, 原因: {}";
    private static final String CHANNEL_INACTIVE = "通道未激活或为空";
    private static final String UNKNOWN_ERROR = "未知错误";

    public boolean sendMessage(Channel channel, String message) {
        return sendMessage(channel, message, "Text");
    }

    public boolean sendBinaryMessage(Channel channel, String message) {
        if (channel == null || !channel.isActive()) {
            log.warn(MESSAGE_SENT_FAIL, channel, "Binary", CHANNEL_INACTIVE);
            return false;
        }

        ByteBuf byteBuf = null;
        try {
            byteBuf = Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
            channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf))
                    .addListener(future -> {
                        if (!future.isSuccess()) {
                            log.warn(MESSAGE_SENT_FAIL, channel, "Binary", future.cause().getMessage());
                        }
                    });
            log.debug(MESSAGE_SENT_SUCCESS, channel, "Binary", message);
            return true;
        } catch (Exception e) {
            log.error(MESSAGE_SENT_FAIL, channel, "Binary", e.getMessage());
            if (byteBuf != null) {
                ReferenceCountUtil.safeRelease(byteBuf);
            }
            return false;
        }
    }

    private boolean sendMessage(Channel channel, String message, String messageType) {
        if (channel == null || !channel.isActive()) {
            log.warn(MESSAGE_SENT_FAIL, channel, messageType, CHANNEL_INACTIVE);
            return false;
        }

        try {
            channel.writeAndFlush(new TextWebSocketFrame(message))
                    .addListener(future -> {
                        if (!future.isSuccess()) {
                            log.warn(MESSAGE_SENT_FAIL, channel, messageType, future.cause().getMessage());
                        }
                    });
            log.debug(MESSAGE_SENT_SUCCESS, channel, messageType, message);
            return true;
        } catch (Exception e) {
            log.error(MESSAGE_SENT_FAIL, channel, messageType, e.getMessage());
            return false;
        }
    }

    public void forwardMessage(Channel from, Channel to, JSONObject message) {
        String fromClientType = getClientType(from);
        String toClientType = getClientType(to);

        if (fromClientType == null || toClientType == null) {
            log.warn("消息转发失败 - 缺失客户端类型信息. 来源: {}, 目标: {}", from, to);
            return;
        }

        enhanceMessageForStreamer(from, to, message, fromClientType, toClientType);

        boolean sent = sendMessage(to, message.toString(), "Forwarded");
        if (sent) {
            log.info("消息转发成功 - 从 {} 到 {}: {}", fromClientType, toClientType, message);
        } else {
            log.warn("消息转发失败 - 从 {} 到 {}: {}", fromClientType, toClientType, message);
        }
    }

    private String getClientType(Channel channel) {
        if (channel == null) return null;
        Attribute<String> attr = channel.attr(ClientManager.TYPE);
        return attr != null ? attr.get() : null;
    }

    private void enhanceMessageForStreamer(Channel from, Channel to, JSONObject message,
                                           String fromClientType, String toClientType) {
        if (ClientType.STREAMER.getValue().equals(toClientType) &&
                ClientType.PLAYER.getValue().equals(fromClientType)) {
            String playerId = getPlayerId(from);
            if (playerId != null) {
                message.put("playerId", playerId);
            } else {
                log.warn("Player客户端缺失playerId: {}", from);
            }
        }
    }

    private String getPlayerId(Channel channel) {
        if (channel == null) return null;
        Attribute<String> attr = channel.attr(ClientManager.PLAYERID);
        return attr != null ? attr.get() : null;
    }

    public void sendConfigMessage(Channel channel) {
        JSONObject clientConfig = new JSONObject();
        clientConfig.put("type", "config");
        JSONObject peerConnectionOptions = new JSONObject();
        peerConnectionOptions.put("iceServers", new JSONArray());
        clientConfig.put("peerConnectionOptions", peerConnectionOptions);
        sendMessage(channel, clientConfig.toString(), "Config");
    }

    public void subscribeToStreamer(String playerId, Channel streamer) {
        JSONObject message = new JSONObject();
        message.put("type", "playerDisconnected");
        message.put("playerId", String.valueOf(playerId));
        sendMessage(streamer, message.toString(), "Subscription");
    }

    public void sendIdentifyMessage(Channel channel) {
        JSONObject identify = new JSONObject();
        identify.put("type", "identify");
        sendMessage(channel, identify.toString(), "Identify");
    }

    public void sendPong(Channel channel) {
        JSONObject msg = new JSONObject();
        msg.put("type", "pong");
        msg.put("time", System.currentTimeMillis());
        sendMessage(channel, msg.toString(), "Pong");
    }

    public void sendPing(Channel channel) {
        JSONObject msg = new JSONObject();
        msg.put("type", "ping");
        msg.put("time", System.currentTimeMillis());
        sendMessage(channel, msg.toString(), "Ping");
    }

    public void unsubscribeStreamer(Channel streamer, String playerId) {
        JSONObject message = new JSONObject();
        message.put("type", "playerDisconnected");
        message.put("playerId", playerId);
        sendMessage(streamer, message.toString(), "Unsubscribe");
    }

    public void disconnectPlayer(Channel player, JSONObject message) {
        if (player == null || !player.isActive()) {
            log.warn("无法断开玩家连接 - 通道未激活: {}", player);
            return;
        }

        try {
            String reason = message != null ? message.getString("reason") : UNKNOWN_ERROR;
            player.writeAndFlush(new CloseWebSocketFrame(1011, reason))
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            log.info("玩家连接已断开 - 通道: {}, 原因: {}", player, reason);
                        } else {
                            log.warn("断开玩家连接失败 - 通道: {}, 原因: {}", player, future.cause().getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("断开玩家连接时发生异常 - 通道: {}, 错误: {}", player, e.getMessage());
        }
    }
}