package com.example.examplemod.advanced.assets;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class VertexOverridePipeline {
    private final DynamicModelRegistry registry;
    private final List<MeshOverrideProvider> providers = new CopyOnWriteArrayList<>();

    public VertexOverridePipeline(DynamicModelRegistry registry) {
        this.registry = registry;
    }

    public void addProvider(MeshOverrideProvider provider) {
        providers.add(provider);
    }

    public MeshAsset resolve(String modelId, MeshAsset fallback) {
        MeshAsset current = registry.model(modelId).orElse(fallback);
        for (MeshOverrideProvider provider : providers) {
            MeshAsset replacement = provider.override(modelId, current);
            if (replacement != null) {
                current = replacement;
            }
        }
        return current;
    }

    @FunctionalInterface
    public interface MeshOverrideProvider {
        MeshAsset override(String modelId, MeshAsset current);
    }
}
