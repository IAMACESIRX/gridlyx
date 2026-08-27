package com.example.examplemod.advanced.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class DynamicDataEngine<K, V> {
    private final AtomicLong version = new AtomicLong();
    private final AtomicReference<Map<K, V>> state = new AtomicReference<>(Map.of());
    private volatile Consumer<Snapshot<K, V>> listener = snapshot -> {};

    public Snapshot<K, V> snapshot() {
        return new Snapshot<>(version.get(), state.get());
    }

    public Snapshot<K, V> update(UnaryOperator<Map<K, V>> transaction) {
        while (true) {
            Map<K, V> before = state.get();
            Map<K, V> mutable = new HashMap<>(before);
            Map<K, V> after = Map.copyOf(transaction.apply(mutable));
            if (state.compareAndSet(before, after)) {
                Snapshot<K, V> snapshot = new Snapshot<>(version.incrementAndGet(), after);
                listener.accept(snapshot);
                return snapshot;
            }
        }
    }

    public void onCommit(Consumer<Snapshot<K, V>> listener) {
        this.listener = listener;
    }

    public record Snapshot<K, V>(long version, Map<K, V> values) {}
}
