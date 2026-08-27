package com.example.examplemod.advanced.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DynamicDimensionManager {
    private final Map<String, DimensionDefinition> definitions = new ConcurrentHashMap<>();

    public long publish(String id, long version, String generator, long seed, Map<String, String> properties) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Dimension id is required");
        }
        DimensionDefinition next = new DimensionDefinition(
                id,
                version,
                generator,
                seed,
                Map.copyOf(properties),
                Lifecycle.VIRTUAL);
        definitions.compute(id, (key, current) -> {
            if (current != null && current.version() >= version) {
                throw new IllegalArgumentException("Dimension version must increase");
            }
            return next;
        });
        return version;
    }

    public DimensionDefinition definition(String id) {
        return definitions.get(id);
    }

    public void markMaterialized(String id) {
        definitions.computeIfPresent(id, (key, value) -> value.withLifecycle(Lifecycle.MATERIALIZED));
    }

    public void retire(String id) {
        definitions.computeIfPresent(id, (key, value) -> value.withLifecycle(Lifecycle.RETIRED));
    }

    public enum Lifecycle {
        VIRTUAL,
        MATERIALIZED,
        RETIRED
    }

    public record DimensionDefinition(
            String id,
            long version,
            String generator,
            long seed,
            Map<String, String> properties,
            Lifecycle lifecycle) {
        private DimensionDefinition withLifecycle(Lifecycle next) {
            return new DimensionDefinition(id, version, generator, seed, properties, next);
        }
    }
}
