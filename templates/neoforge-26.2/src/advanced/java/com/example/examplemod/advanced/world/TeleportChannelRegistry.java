package com.example.examplemod.advanced.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportChannelRegistry {
    private final Map<String, Destination> channels = new ConcurrentHashMap<>();

    public void put(String channelId, Destination destination) {
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("Teleport channel id is required");
        }
        channels.put(channelId, destination);
    }

    public Destination resolve(String channelId) {
        Destination destination = channels.get(channelId);
        if (destination == null) {
            throw new IllegalArgumentException("Unknown teleport channel: " + channelId);
        }
        return destination;
    }

    public boolean remove(String channelId) {
        return channels.remove(channelId) != null;
    }

    public record Destination(
            String dimensionId,
            double x,
            double y,
            double z,
            float yaw,
            float pitch) {}
}
