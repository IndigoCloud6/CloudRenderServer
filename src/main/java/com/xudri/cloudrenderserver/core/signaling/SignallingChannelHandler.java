package com.xudri.cloudrenderserver.core.signaling;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.domain.enums.ClientType;
import com.xudri.cloudrenderserver.domain.enums.MessageType;
import com.xudri.cloudrenderserver.core.client.ClientManager;
import com.xudri.cloudrenderserver.core.client.MessageHelper;
import com.xudri.cloudrenderserver.core.client.PlayerIdPool;
import com.xudri.cloudrenderserver.common.util.LoggerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * WebSocket信令服务器处理器
 * 处理客户端连接、消息转发和连接管理
 *
 * @author MaxYun
 * @date 2024/3/6
 */
@Log4j2
@Component
@ChannelHandler.Sharable
public class SignallingChannelHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private static final String WEBSOCKET_PATH = "/websocket";
    private static final String WEBSOCKET_UPGRADE_HEADER = "websocket";

    @Resource
    private ClientManager clientManager;

    @Resource
    private PlayerIdPool playerIdPool;

    @Resource
    private MessageHelper messageHelper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else {
                handleWebSocketFrame(ctx, msg);
            }
        } catch (Exception e) {
            LoggerUtil.logError(log, "消息处理", "处理消息时发生错误，消息类型：" + msg.getClass().getSimpleName(), e);
            ctx.close();
        }
    }

    /**
     * 处理HTTP请求并完成WebSocket握手
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!isValidWebSocketRequest(request)) {
            sendBadRequestResponse(ctx, request);
            return;
        }

        try {
            performWebSocketHandshake(ctx, request);
            Map<String, String> params = extractUrlParameters(request.uri());
            processConnectionParameters(ctx, params);
        } catch (Exception e) {
            LoggerUtil.logError(log, "WebSocket握手", "WebSocket握手过程中发生错误", e);
            sendServerErrorResponse(ctx, request);
        }
    }

    /**
     * 处理连接参数并设置客户端类型
     */
    private void processConnectionParameters(ChannelHandlerContext ctx, Map<String, String> params) {
        Optional<ClientType> clientType = validateAndGetClientType(params);
        if (clientType.isEmpty()) {
            LoggerUtil.logWarning(log, "客户端连接", "未知的客户端类型，关闭连接。参数：" + params);
            log.warn("未知的客户端类型，关闭连接。参数：{}", params);
            ctx.close();
            return;
        }

        setupClientChannel(ctx, clientType.get(), params);
    }

    /**
     * 验证WebSocket请求的有效性
     */
    private boolean isValidWebSocketRequest(FullHttpRequest request) {
        return request.decoderResult().isSuccess() &&
                WEBSOCKET_UPGRADE_HEADER.equalsIgnoreCase(request.headers().get(HttpHeaderNames.UPGRADE));
    }

    /**
     * 发送错误请求响应
     */
    private void sendBadRequestResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
        sendHttpResponse(ctx, request, new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
    }

    /**
     * 发送服务器错误响应
     */
    private void sendServerErrorResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
        sendHttpResponse(ctx, request, new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * 执行WebSocket握手
     */
    private void performWebSocketHandshake(ChannelHandlerContext ctx, FullHttpRequest request) {
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(request), null, true);
        handshaker = factory.newHandshaker(request);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    /**
     * 验证并获取客户端类型
     */
    private Optional<ClientType> validateAndGetClientType(Map<String, String> params) {
        return Optional.ofNullable(params.get("type"))
                .filter(StringUtils::isNotBlank)
                .flatMap(ClientType::fromString);
    }

    /**
     * 设置客户端通道属性并处理连接逻辑
     */
    private void setupClientChannel(ChannelHandlerContext ctx, ClientType clientType, Map<String, String> params) {
        Channel channel = ctx.channel();
        setBasicChannelAttributes(channel, clientType);

        switch (clientType) {
            case PLAYER:
                handlePlayerConnection(ctx, channel, params);
                break;
            case STREAMER:
                handleStreamerConnection(channel, params);
                break;
            case ADMIN:
                handleAdminConnection(channel);
                break;
            default:
                log.warn("Unhandled client type: {}", clientType);
                ctx.close();
        }
    }

    /**
     * 设置通道基本属性
     */
    private void setBasicChannelAttributes(Channel channel, ClientType clientType) {
        channel.attr(ClientManager.TYPE).set(clientType.getValue());
        channel.attr(ClientManager.CONNECTED_TIME).set(DateUtil.now());

        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String clientInfo = socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
        channel.attr(ClientManager.IP).set(clientInfo);
    }

    /**
     * 处理播放器连接
     */
    private void handlePlayerConnection(ChannelHandlerContext ctx, Channel player, Map<String, String> params) {
        String playerId = String.valueOf(playerIdPool.getPlayerId());
        LoggerUtil.logConnection("播放器", playerId, "已连接",
            String.format("IP地址：%s", ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress()));
        log.info("播放器 {} 已连接", playerId);
        player.attr(ClientManager.PLAYERID).set(playerId);

        Channel streamer = clientManager.findStreamerForPlayer(params);
        if (streamer != null) {
            establishPlayerStreamerConnection(player, streamer, playerId);
        } else {
            LoggerUtil.logConnection("播放器", playerId, "连接失败", "未找到可用的像素流实例");
            log.warn("播放器 {} 未找到可用的像素流实例，关闭连接", playerId);
            playerIdPool.releasePlayerId(playerId);
            ctx.close();
        }
    }

    /**
     * 建立播放器和像素流实例连接
     */
    private void establishPlayerStreamerConnection(Channel player, Channel streamer, String playerId) {
        clientManager.addChannel(player);
        clientManager.subscribeRelation(player.id(), streamer.id());
        messageHelper.sendConfigMessage(player);
        messageHelper.subscribeToStreamer(playerId, streamer);
    }

    /**
     * 处理像素流实例连接
     */
    private void handleStreamerConnection(Channel streamer, Map<String, String> params) {
        setStreamerAttributes(streamer, params);

        String insId = params.get("insid");
        LoggerUtil.logConnection("像素流实例", insId, "已连接", 
            String.format("IP地址：%s，项目ID：%s", 
                ((InetSocketAddress) streamer.remoteAddress()).getAddress().getHostAddress(),
                params.get("projectid")));
        log.info("像素流实例 {} 已连接", insId);

        messageHelper.sendConfigMessage(streamer);
        messageHelper.sendIdentifyMessage(streamer);
        clientManager.addChannel(streamer);
    }

    /**
     * 设置像素流实例属性
     */
    private void setStreamerAttributes(Channel channel, Map<String, String> params) {
        boolean isOneself = "1".equals(params.get("oneself"));
        channel.attr(ClientManager.ONESELF).set(isOneself);

        Optional.ofNullable(params.get("insid"))
                .ifPresent(insId -> channel.attr(ClientManager.INS_ID).set(insId));

        Optional.ofNullable(params.get("projectid"))
                .ifPresent(projectId -> channel.attr(ClientManager.PROJECT_ID).set(projectId));
    }

    /**
     * 处理管理员连接
     */
    private void handleAdminConnection(Channel channel) {
        clientManager.addChannel(channel);
        LoggerUtil.logConnection("管理员", "admin", "已连接", 
            String.format("IP地址：%s", ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress()));
        log.info("管理员客户端已连接");
    }

    /**
     * 处理WebSocket帧消息
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, Object frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handleCloseFrame(ctx, (CloseWebSocketFrame) frame);
        } else if (frame instanceof PingWebSocketFrame) {
            handlePingFrame(ctx);
        } else if (frame instanceof TextWebSocketFrame) {
            handleTextFrame(ctx, (TextWebSocketFrame) frame);
        } else {
            log.debug("Received unsupported frame type: {}", frame.getClass().getSimpleName());
        }
    }

    /**
     * 处理关闭帧
     */
    private void handleCloseFrame(ChannelHandlerContext ctx, CloseWebSocketFrame closeFrame) {
        handshaker.close(ctx.channel(), closeFrame.retain());
    }

    /**
     * 处理Ping帧
     */
    private void handlePingFrame(ChannelHandlerContext ctx) {
        log.debug("Ping received from client {}", ctx.channel().id());
        // 可以在这里添加ping响应逻辑如果需要
    }

    /**
     * 处理文本帧
     */
    private void handleTextFrame(ChannelHandlerContext ctx, TextWebSocketFrame textFrame) {
        try {
            JSONObject message = parseMessage(textFrame.text());
            if (message == null || message.isEmpty()) {
                log.warn("Received empty or invalid message");
                return;
            }
            Channel channel = ctx.channel();
            processMessage(channel, message);

        } catch (Exception e) {
            log.error("Error processing text frame: {}", textFrame.text(), e);
            ctx.close();
        }
    }

    /**
     * 解析消息
     */
    private JSONObject parseMessage(String text) {
        try {
            return JSONObject.parseObject(text);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", text, e);
            return null;
        }
    }

    /**
     * 处理解析后的消息
     */
    private void processMessage(Channel channel, JSONObject message) {
        String msgTypeStr = message.getString("type");
        Optional<MessageType> messageType = MessageType.fromString(msgTypeStr);

        if (messageType.isEmpty()) {
            log.error("Unsupported message type: {}", msgTypeStr);
            return;
        }

        switch (messageType.get()) {
            case OFFER:
            case ANSWER:
            case ICE_CANDIDATE:
                Channel to = clientManager.findTargetChannel(channel, message);
                messageHelper.forwardMessage(channel, to, message);
                break;
            case DISCONNECT_PLAYER:
                String playerId = String.valueOf(message.get("playerId"));
                Optional<Channel> player = clientManager.getPlayerByPlayerId(playerId);
                player.ifPresent(value -> messageHelper.disconnectPlayer(value, message));
                break;
            case PING:
                messageHelper.sendPong(channel);
                break;
            case PONG:
                // 处理pong响应
                break;
            case LIST_STREAMERS:
            case ENDPOINT_ID:
                // 这些消息类型暂时不需要处理
                break;
            default:
                log.warn("Unhandled message type: {}", messageType.get());
        }
    }

    /**
     * 提取URL参数
     */
    private Map<String, String> extractUrlParameters(String uri) {
        String query = uri.replace("/", "");
        Map<String, String> params = new HashMap<>();

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * 发送HTTP响应
     */
    public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        boolean keepAlive = HttpUtil.isKeepAlive(req) && res.status().code() == HttpResponseStatus.OK.code();
        HttpUtil.setKeepAlive(res, keepAlive);

        ChannelFuture future = ctx.channel().writeAndFlush(res);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 获取WebSocket位置
     */
    private static String getWebSocketLocation(FullHttpRequest req) {
        String host = req.headers().get(HttpHeaderNames.HOST);
        return "ws://" + host + WEBSOCKET_PATH;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent event) {
            if (event.state() == IdleState.READER_IDLE ||
                    event.state() == IdleState.WRITER_IDLE ||
                    event.state() == IdleState.ALL_IDLE) {
                messageHelper.sendPing(ctx.channel());
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String type = channel.attr(ClientManager.TYPE).get();

        if (type == null) {
            return;
        }

        Optional<ClientType> clientType = ClientType.fromString(type);
        if (clientType.isEmpty()) {
            return;
        }

        switch (clientType.get()) {
            case PLAYER:
                handlePlayerDisconnection(channel);
                break;
            case STREAMER:
                handleStreamerDisconnection(channel);
                break;
            case ADMIN:
                LoggerUtil.logConnection("管理员", "admin", "已断开连接", "正常断开");
                log.info("管理员客户端已断开连接");
                break;
        }
    }

    /**
     * 处理播放器断开连接
     */
    private void handlePlayerDisconnection(Channel channel) {
        String playerId = channel.attr(ClientManager.PLAYERID).get();

        if (playerId != null) {
            LoggerUtil.logConnection("播放器", playerId, "已断开连接", "正常断开");
            log.info("播放器 {} 已断开连接", playerId);
            playerIdPool.releasePlayerId(playerId);
            Optional<Channel> streamer = clientManager.getSubscribedStreamer(channel.id());
            streamer.ifPresent(value -> messageHelper.unsubscribeStreamer(value, playerId));
        }
        clientManager.removeChannel(channel);
    }

    /**
     * 处理像素流实例断开连接
     */
    private void handleStreamerDisconnection(Channel channel) {
        String insId = channel.attr(ClientManager.INS_ID).get();
        LoggerUtil.logConnection("像素流实例", insId, "已断开连接", "正常断开");
        log.info("像素流实例 {} 已断开连接", insId);

        // 断开所有连接的播放器
        List<ChannelId> players = clientManager.getPlayers(channel.id());
        for (ChannelId channelId : players) {
            Optional<Channel> playerOpt = clientManager.getChannel(channelId);
            playerOpt.ifPresent(ChannelOutboundInvoker::close);
        }

        clientManager.removeChannel(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught in channel {}", ctx.channel().id(), cause);
        ctx.close();
    }
}