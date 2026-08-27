package com.example.examplemod.advanced.network;

import com.example.examplemod.advanced.worldedit.SectionKey;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EditorStateSynchronizer {
    private final Map<UUID, Long> clientAcknowledgements = new ConcurrentHashMap<>();

    public void acknowledge(UUID clientId, long transactionId) {
        clientAcknowledgements.merge(clientId, transactionId, Math::max);
    }

    public long acknowledgedThrough(UUID clientId) {
        return clientAcknowledgements.getOrDefault(clientId, 0L);
    }

    public SyncDecision decide(long transactionId, SectionKey section, ReplicationCuller.Endpoint endpoint) {
        boolean newer = transactionId > acknowledgedThrough(endpoint.id());
        long deltaX = Math.abs((long) endpoint.chunkX() - section.chunkX());
        long deltaZ = Math.abs((long) endpoint.chunkZ() - section.chunkZ());
        boolean interested = Math.max(deltaX, deltaZ) <= endpoint.viewDistanceChunks();
        return new SyncDecision(newer && interested, newer, interested);
    }

    public record SyncDecision(boolean send, boolean newerThanAck, boolean withinInterestArea) {
    }
}
