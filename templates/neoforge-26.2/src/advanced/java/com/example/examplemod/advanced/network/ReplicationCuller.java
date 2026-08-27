package com.example.examplemod.advanced.network;

import com.example.examplemod.advanced.worldedit.SectionKey;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class ReplicationCuller {
    public List<Endpoint> interested(Collection<Endpoint> endpoints, SectionKey section) {
        return endpoints.stream().filter(endpoint -> withinRange(endpoint, section)).toList();
    }

    private static boolean withinRange(Endpoint endpoint, SectionKey section) {
        long deltaX = Math.abs((long) endpoint.chunkX() - section.chunkX());
        long deltaZ = Math.abs((long) endpoint.chunkZ() - section.chunkZ());
        return Math.max(deltaX, deltaZ) <= endpoint.viewDistanceChunks();
    }

    public record Endpoint(UUID id, int chunkX, int chunkZ, int viewDistanceChunks) {
        public Endpoint {
            if (viewDistanceChunks < 0 || viewDistanceChunks > 64) {
                throw new IllegalArgumentException("viewDistanceChunks outside supported range");
            }
        }
    }
}
