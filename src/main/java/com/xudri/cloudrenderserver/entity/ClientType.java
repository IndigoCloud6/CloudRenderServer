package com.xudri.cloudrenderserver.entity;

import lombok.Getter;

import java.util.Optional;

/**
 * @ClassName ClientType
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/3/7 10:17
 * @Version 1.0
 */

@Getter
public enum ClientType {
    PLAYER("player"),
    STREAMER("streamer"),
    ADMIN("admin");

    private final String value;

    ClientType(String value) {
        this.value = value;
    }

    public static Optional<ClientType> fromString(String value) {
        for (ClientType type : values()) {
            if (type.value.equals(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
