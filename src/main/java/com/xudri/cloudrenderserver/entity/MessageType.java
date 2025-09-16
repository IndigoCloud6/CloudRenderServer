package com.xudri.cloudrenderserver.entity;

import lombok.Getter;

import java.util.Optional;

/**
 * @ClassName MessageType
 * @Description MessageType
 * @Author MaxYun
 * @Since 2025/8/21 17:56
 * @Version 1.0
 */

@Getter
public enum MessageType {
    OFFER("offer"),
    ANSWER("answer"),
    ICE_CANDIDATE("iceCandidate"),
    DISCONNECT_PLAYER("disconnectPlayer"),
    PING("ping"),
    PONG("pong"),
    LIST_STREAMERS("listStreamers"),
    ENDPOINT_ID("endpointId");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public static Optional<MessageType> fromString(String value) {
        for (MessageType type : values()) {
            if (type.value.equals(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}