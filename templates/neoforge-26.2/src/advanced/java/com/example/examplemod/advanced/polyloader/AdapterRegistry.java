package com.example.examplemod.advanced.polyloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AdapterRegistry {
    private final Map<LoaderFamily, LoaderAdapter> adapters = new ConcurrentHashMap<>();

    public void register(LoaderAdapter adapter) {
        adapters.put(adapter.family(), adapter);
    }

    public Optional<LoaderAdapter> adapter(LoaderFamily family) {
        return Optional.ofNullable(adapters.get(family));
    }

    public Collection<LoaderAdapter> adapters() {
        return List.copyOf(adapters.values());
    }

    public List<CallTranslationRule> translationRules(RuntimeEnvironment environment) {
        List<CallTranslationRule> result = new ArrayList<>();
        for (LoaderAdapter adapter : adapters.values()) {
            if (adapter.supports(environment)) {
                result.addAll(adapter.translationRules());
            }
        }
        return List.copyOf(result);
    }
}
