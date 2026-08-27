package com.example.examplemod.advanced.worldedit;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class DynamicEventStructureMatrix {
    private final Map<String, EventDefinition> events = new ConcurrentHashMap<>();

    public void register(EventDefinition definition) {
        EventDefinition previous = events.putIfAbsent(definition.id(), Objects.requireNonNull(definition));
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate event id: " + definition.id());
        }
    }

    public EventActivation evaluate(String eventId, EventContext context) {
        EventDefinition definition = events.get(eventId);
        if (definition == null || !definition.trigger().test(context)) {
            return null;
        }
        return new EventActivation(definition.id(), definition.blueprint(), context.anchor(), Instant.now());
    }

    public record EventDefinition(
            String id,
            StructureBlueprint blueprint,
            Predicate<EventContext> trigger) {
    }

    public record EventContext(long worldTime, Point anchor, Map<String, Object> state) {
        public EventContext {
            state = Map.copyOf(state);
        }
    }

    public record EventActivation(String id, StructureBlueprint blueprint, Point anchor, Instant activatedAt) {
    }

    public record Point(int x, int y, int z) {
    }
}
