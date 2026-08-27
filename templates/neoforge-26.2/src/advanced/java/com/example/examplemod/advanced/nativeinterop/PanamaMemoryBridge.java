package com.example.examplemod.advanced.nativeinterop;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public final class PanamaMemoryBridge implements AutoCloseable {
    private final Arena arena = Arena.ofShared();

    public MemorySegment allocate(long bytes, long alignment) {
        if (bytes <= 0 || alignment <= 0) {
            throw new IllegalArgumentException("bytes and alignment must be positive");
        }
        return arena.allocate(bytes, alignment);
    }

    public MemorySegment allocateLong(long value) {
        MemorySegment segment = arena.allocate(ValueLayout.JAVA_LONG);
        segment.set(ValueLayout.JAVA_LONG, 0, value);
        return segment;
    }

    public long readLong(MemorySegment segment) {
        return segment.get(ValueLayout.JAVA_LONG, 0);
    }

    @Override
    public void close() {
        arena.close();
    }
}
