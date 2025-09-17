package com.xudri.cloudrenderserver.core.client;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson2.JSONObject;
import com.xudri.cloudrenderserver.common.constant.ClientType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName ClientManager
 * @Description 像素流客户端管理
 * @Author MaxYun
 * @Since 2025/9/15 16:55
 * @Version 1.0
 */


@Component
@Log4j2
public class ClientManager {

    // 属性键定义
    public static final AttributeKey<String> TYPE = AttributeKey.valueOf("Type");
    public static final AttributeKey<String> CONNECTED_TIME = AttributeKey.valueOf("ConnectedTime");
    public static final AttributeKey<Boolean> ONESELF = AttributeKey.valueOf("OneSelf");
    public static final AttributeKey<String> PLAYERID = AttributeKey.valueOf("PlayerId");
    public static final AttributeKey<String> INS_ID = AttributeKey.valueOf("InsId");
    public static final AttributeKey<String> PROJECT_ID = AttributeKey.valueOf("ProjectId");
    public static final AttributeKey<String> IP = AttributeKey.valueOf("IP");

    // 订阅关系映射：玩家ID -> 像素流实例ID
    private final Map<ChannelId, ChannelId> subscriptionRelations = new ConcurrentHashMap<>();

    // 所有连接的通道组
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 获取指定玩家订阅的像素流实例通道
     */
    public Optional<Channel> getSubscribedStreamer(ChannelId playerChannelId) {
        return Optional.ofNullable(subscriptionRelations.get(playerChannelId))
                .map(channels::find);
    }

    /**
     * 根据通道ID获取通道
     */
    public Optional<Channel> getChannel(ChannelId channelId) {
        return Optional.ofNullable(channels.find(channelId));
    }

    /**
     * 建立订阅关系
     */
    public void subscribeRelation(ChannelId playerChannelId, ChannelId streamerChannelId) {
        subscriptionRelations.put(playerChannelId, streamerChannelId);
    }

    /**
     * 移除订阅关系
     */
    public void unsubscribeRelation(ChannelId playerChannelId) {
        subscriptionRelations.remove(playerChannelId);
    }

    /**
     * 添加通道到管理器
     */
    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    /**
     * 移除通道从管理器
     */
    public void removeChannel(Channel channel) {
        // 清理相关的订阅关系
        ChannelId channelId = channel.id();
        subscriptionRelations.remove(channelId);
        subscriptionRelations.entrySet().removeIf(entry -> entry.getValue().equals(channelId));

        channels.remove(channel);
    }

    /**
     * 获取通道类型
     */
    public String getChannelType(ChannelId channelId) {
        return getChannel(channelId)
                .map(channel -> channel.attr(TYPE).get())
                .orElse(null);
    }

    /**
     * 根据实例ID获取像素流实例通道
     */
    public Optional<Channel> getStreamerByInsId(String insId) {
        if (insId == null) {
            return Optional.empty();
        }

        return channels.stream()
                .filter(this::isStreamer)
                .filter(channel -> insId.equals(getAttributeValue(channel, INS_ID)))
                .findFirst();
    }

    /**
     * 根据属性键值对获取通道
     */
    public <T> Optional<Channel> getChannelByAttribute(AttributeKey<T> key, T value) {
        if (value == null) {
            return Optional.empty();
        }

        return channels.stream()
                .filter(channel -> Objects.equals(getAttributeValue(channel, key), value))
                .findFirst();
    }

    /**
     * 根据玩家ID获取玩家通道
     */
    public Optional<Channel> getPlayerByPlayerId(String playerId) {
        return getChannelByAttribute(PLAYERID, playerId);
    }

    /**
     * 获取指定项目的空闲像素流实例列表
     */
    public List<ChannelId> getIdleStreamers(String projectId) {
        if (projectId == null) {
            return List.of();
        }

        return channels.stream()
                .filter(this::isStreamer)
                .filter(channel -> projectId.equals(getAttributeValue(channel, PROJECT_ID)))
                .filter(this::isIdleStreamer)
                .map(Channel::id)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取通道列表
     */
    public List<Channel> getChannelsByType(String type) {
        if (type == null) {
            return List.of();
        }

        return channels.stream()
                .filter(channel -> type.equals(getAttributeValue(channel, TYPE)))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定像素流实例的所有玩家
     */
    public List<ChannelId> getPlayers(ChannelId streamerId) {
        return subscriptionRelations.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), streamerId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有连接的详细信息
     */
    public JSONObject getDetails() {
        JSONObject result = new JSONObject();

        List<JSONObject> players = channels.stream()
                .filter(this::isPlayer)
                .map(this::buildPlayerDetails)
                .collect(Collectors.toList());

        List<JSONObject> streamers = channels.stream()
                .filter(this::isStreamer)
                .map(this::buildStreamerDetails)
                .collect(Collectors.toList());

        result.put("streamer", streamers);
        result.put("player", players);
        return result;
    }

    /**
     * 获取连接统计信息
     */
    public JSONObject getStatistics() {
        JSONObject stats = new JSONObject();
        stats.put("totalChannels", channels.size());
        stats.put("playerCount", getChannelsByType(ClientType.PLAYER.getValue()).size());
        stats.put("streamerCount", getChannelsByType(ClientType.STREAMER.getValue()).size());
        stats.put("subscriptionCount", subscriptionRelations.size());
        return stats;
    }

    public Channel findStreamerForPlayer(Map<String, String> params) {
        String insId = params.get("insid");
        if (insId != null) {
            if (getStreamerByInsId(insId).isPresent()) {
                return getStreamerByInsId(insId).get();
            }
        }

        String projectId = params.get("projectid");
        if (projectId != null) {
            return getIdleStreamers(projectId)
                    .stream()
                    .findFirst()
                    .flatMap(this::getChannel)
                    .orElse(null);
        }

        return null;
    }

    public Channel findTargetChannel(Channel channel, JSONObject message) {
        String channelType = getChannelType(channel.id());

        switch (channelType) {
            case "player":
                // 如果是玩家Channel，查找其订阅的Streamer
                Optional<Channel> streamerOpt = getSubscribedStreamer(channel.id());
                if (streamerOpt.isPresent()) {
                    return streamerOpt.get();
                } else {
                    log.error("Player {} has no subscribed streamer", channel.id());
                    return null;
                }

            case "streamer":
                // 如果是Streamer Channel，从消息中获取playerId并查找对应的Player
                String playerId = Convert.toStr(message.get("playerId"));
                if (playerId == null || playerId.isEmpty()) {
                    log.error("No playerId specified in message from streamer {}", channel.id());
                    return null;
                }

                Optional<Channel> playerOpt = getPlayerByPlayerId(playerId);
                if (playerOpt.isPresent()) {
                    return playerOpt.get();
                } else {
                    log.error("Player {} not found for streamer {}", playerId, channel.id());
                    return null;
                }

            default:
                log.error("Unknown channel type: {} for channel {}", channelType, channel.id());
                return null;
        }
    }

    private boolean isPlayer(Channel channel) {
        return ClientType.PLAYER.getValue().equals(getAttributeValue(channel, TYPE));
    }

    private boolean isStreamer(Channel channel) {
        return ClientType.STREAMER.getValue().equals(getAttributeValue(channel, TYPE));
    }

    private boolean isIdleStreamer(Channel channel) {
        // 判断像素流实例是否空闲（没有玩家连接）
        return getPlayers(channel.id()).isEmpty();
    }

    private <T> T getAttributeValue(Channel channel, AttributeKey<T> key) {
        return channel.hasAttr(key) ? channel.attr(key).get() : null;
    }

    private JSONObject buildPlayerDetails(Channel channel) {
        JSONObject player = new JSONObject();
        player.put(CONNECTED_TIME.name(), getAttributeValue(channel, CONNECTED_TIME));
        player.put(PLAYERID.name(), getAttributeValue(channel, PLAYERID));
        player.put(IP.name(), getAttributeValue(channel, IP));
        return player;
    }

    private JSONObject buildStreamerDetails(Channel channel) {
        JSONObject streamer = new JSONObject();
        streamer.put(INS_ID.name(), getAttributeValue(channel, INS_ID));
        streamer.put(PROJECT_ID.name(), getAttributeValue(channel, PROJECT_ID));
        streamer.put(ONESELF.name(), getAttributeValue(channel, ONESELF));
        streamer.put(CONNECTED_TIME.name(), getAttributeValue(channel, CONNECTED_TIME));
        streamer.put(IP.name(), getAttributeValue(channel, IP));
        return streamer;
    }
}