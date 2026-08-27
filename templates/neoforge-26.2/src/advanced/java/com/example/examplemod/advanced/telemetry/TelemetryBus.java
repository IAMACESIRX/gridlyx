package com.example.examplemod.advanced.telemetry;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public final class TelemetryBus {
    private final ArrayDeque<Event> events;
    private final LongAdder published = new LongAdder();
    private final int capacity;

    public TelemetryBus(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Telemetry capacity must be positive");
        }
        this.capacity = capacity;
        events = new ArrayDeque<>(capacity);
    }

    public synchronized void publish(String channel, Map<String, Object> fields) {
        while (events.size() >= capacity) {
            events.removeFirst();
        }
        events.addLast(new Event(published.longValue(), Instant.now(), channel, Map.copyOf(fields)));
        published.increment();
    }

    public synchronized List<Event> snapshot() {
        return List.copyOf(new ArrayList<>(events));
    }

    public long publishedCount() {
        return published.longValue();
    }

    public record Event(long sequence, Instant time, String channel, Map<String, Object> fields) {}
}
