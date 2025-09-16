package com.xudri.cloudrenderserver.util;

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

    /**
     * Retrieves an available player ID. If there are recycled IDs available,
     * one of them will be returned. Otherwise, a new ID is generated.
     *
     * @return a unique player ID
     */
    public int getPlayerId() {
        // Try to get a recycled ID first
        Integer recycledId = availableNumbers.poll();
        if (recycledId != null) {
            return recycledId;
        }
        // Generate a new ID if no recycled ones are available
        return nextNumber.getAndIncrement();
    }

    /**
     * Returns a player ID to the pool for reuse.
     *
     * @param id the player ID to be recycled
     * @throws IllegalArgumentException if the ID is less than 101
     */
    public void releasePlayerId(int id) {
        if (id < 101) {
            throw new IllegalArgumentException("Player ID must be at least 101");
        }
        // Add the ID back to the available numbers for reuse
        availableNumbers.offer(id);
    }

    public void releasePlayerId(String id) {
        try {
            int playerId = Integer.parseInt(id);
            if (playerId < 101) {
                throw new IllegalArgumentException("Player ID must be at least 101");
            }
            // Add the ID back to the available numbers for reuse
            availableNumbers.offer(playerId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Player ID must be a valid integer");
        }
    }

    /**
     * Returns the number of available recycled IDs.
     * This is primarily for monitoring and testing purposes.
     *
     * @return the count of available recycled IDs
     */
    public int getAvailableIdsCount() {
        return availableNumbers.size();
    }
}