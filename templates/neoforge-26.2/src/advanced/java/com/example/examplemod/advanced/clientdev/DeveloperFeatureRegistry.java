package com.example.examplemod.advanced.clientdev;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class DeveloperFeatureRegistry {
    private final Map<String, Feature> features = new ConcurrentHashMap<>();

    public void register(String id, boolean enabledByDefault, Consumer<Boolean> listener) {
        Feature previous = features.putIfAbsent(id, new Feature(enabledByDefault, Objects.requireNonNull(listener)));
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate developer feature: " + id);
        }
    }

    public boolean toggle(String id) {
        Feature feature = require(id);
        boolean next = !feature.enabled;
        feature.enabled = next;
        feature.listener.accept(next);
        return next;
    }

    public void setEnabled(String id, boolean enabled) {
        Feature feature = require(id);
        feature.enabled = enabled;
        feature.listener.accept(enabled);
    }

    public boolean isEnabled(String id) {
        return require(id).enabled;
    }

    private Feature require(String id) {
        Feature feature = features.get(id);
        if (feature == null) {
            throw new IllegalArgumentException("Unknown developer feature: " + id);
        }
        return feature;
    }

    private static final class Feature {
        private volatile boolean enabled;
        private final Consumer<Boolean> listener;

        private Feature(boolean enabled, Consumer<Boolean> listener) {
            this.enabled = enabled;
            this.listener = listener;
        }
    }
}
