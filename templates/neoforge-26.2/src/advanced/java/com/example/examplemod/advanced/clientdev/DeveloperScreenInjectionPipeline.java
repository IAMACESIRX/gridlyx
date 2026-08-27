package com.example.examplemod.advanced.clientdev;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class DeveloperScreenInjectionPipeline {
    private final Map<String, Supplier<ScreenModel>> screens = new ConcurrentHashMap<>();
    private volatile ScreenModel active;

    public void register(String id, Supplier<ScreenModel> factory) {
        Supplier<ScreenModel> previous = screens.putIfAbsent(id, Objects.requireNonNull(factory));
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate developer screen: " + id);
        }
    }

    public ScreenModel open(String id) {
        Supplier<ScreenModel> factory = screens.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown developer screen: " + id);
        }
        active = factory.get();
        return active;
    }

    public void close() {
        active = null;
    }

    public ScreenModel active() {
        return active;
    }

    public record ScreenModel(String title, Map<String, String> fields, Map<String, Boolean> toggles) {
        public ScreenModel {
            fields = Map.copyOf(fields);
            toggles = Map.copyOf(toggles);
        }
    }
}
