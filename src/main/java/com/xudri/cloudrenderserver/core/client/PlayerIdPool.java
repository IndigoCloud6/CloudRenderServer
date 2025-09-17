package com.xudri.cloudrenderserver.core.client;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName PlayerIdPool
 * @Description 号码池子
 * @Author MaxYun
 * @Since 2025/9/15 18:25
 * @Version 1.0
 */

@Component
public class PlayerIdPool {
    private final AtomicInteger nextNumber = new AtomicInteger(101);
    private final Queue<Integer> availableNumbers = new ConcurrentLinkedQueue<>();

    public int getPlayerId() {
        // Try to get a recycled ID first
        Integer recycledId = availableNumbers.poll();
        if (recycledId != null) {
            return recycledId;
        }
        return nextNumber.getAndIncrement();
    }

    public void releasePlayerId(int id) {
        if (id < 101) {
            throw new IllegalArgumentException("Player ID must be at least 101");
        }
        availableNumbers.offer(id);
    }

    public void releasePlayerId(String id) {
        try {
            int playerId = Integer.parseInt(id);
            if (playerId < 101) {
                throw new IllegalArgumentException("Player ID must be at least 101");
            }
            availableNumbers.offer(playerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Player ID must be a valid integer");
        }
    }

    public int getAvailableIdsCount() {
        return availableNumbers.size();
    }
}