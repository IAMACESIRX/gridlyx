package com.example.examplemod.advanced.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class StateSyncPipeline<K, V> {
    private final ConcurrentHashMap<K, V> pending = new ConcurrentHashMap<>();
    private final AtomicBoolean scheduled = new AtomicBoolean();
    private final Executor executor;
    private final Consumer<Map<K, V>> sink;

    public StateSyncPipeline(Executor executor, Consumer<Map<K, V>> sink) {
        this.executor = executor;
        this.sink = sink;
    }

    public void submit(K key, V value) {
        pending.put(key, value);
        if (scheduled.compareAndSet(false, true)) {
            executor.execute(this::drain);
        }
    }

    private void drain() {
        try {
            while (!pending.isEmpty()) {
                Map<K, V> batch = new HashMap<>();
                pending.forEach((key, value) -> {
                    if (pending.remove(key, value)) {
                        batch.put(key, value);
                    }
                });
                if (!batch.isEmpty()) {
                    sink.accept(Map.copyOf(batch));
                }
            }
        } finally {
            scheduled.set(false);
            if (!pending.isEmpty() && scheduled.compareAndSet(false, true)) {
                executor.execute(this::drain);
            }
        }
    }
}
