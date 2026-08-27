package com.example.examplemod.advanced.runtime;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class HotSwapSlot<T> {
    private final AtomicReference<T> value;
    private final AtomicLong version = new AtomicLong();

    public HotSwapSlot(T initialValue) {
        value = new AtomicReference<>(Objects.requireNonNull(initialValue));
    }

    public T get() {
        return value.get();
    }

    public long version() {
        return version.get();
    }

    public long swap(T replacement) {
        value.set(Objects.requireNonNull(replacement));
        return version.incrementAndGet();
    }
}
