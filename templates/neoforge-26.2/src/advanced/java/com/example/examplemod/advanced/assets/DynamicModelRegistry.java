package com.example.examplemod.advanced.assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class DynamicModelRegistry {
    private final Map<String, MeshAsset> models = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<ModelUpdate> updates = new ConcurrentLinkedQueue<>();
    private final AtomicLong revisions = new AtomicLong();

    public MeshAsset publish(String modelId, int vertexStride, float[] vertices, int[] indices) {
        MeshAsset asset = new MeshAsset(revisions.incrementAndGet(), vertexStride, vertices, indices);
        models.put(modelId, asset);
        updates.add(new ModelUpdate(modelId, asset.revision(), UpdateType.REPLACE));
        return asset;
    }

    public Optional<MeshAsset> model(String modelId) {
        return Optional.ofNullable(models.get(modelId));
    }

    public boolean remove(String modelId) {
        MeshAsset removed = models.remove(modelId);
        if (removed == null) {
            return false;
        }
        long revision = revisions.incrementAndGet();
        updates.add(new ModelUpdate(modelId, revision, UpdateType.REMOVE));
        return true;
    }

    public List<ModelUpdate> drainUpdates(int maximum) {
        if (maximum < 1) {
            throw new IllegalArgumentException("maximum must be positive");
        }
        List<ModelUpdate> result = new ArrayList<>(maximum);
        while (result.size() < maximum) {
            ModelUpdate update = updates.poll();
            if (update == null) {
                break;
            }
            result.add(update);
        }
        return List.copyOf(result);
    }

    public enum UpdateType {
        REPLACE,
        REMOVE
    }

    public record ModelUpdate(String modelId, long revision, UpdateType type) {}
}
