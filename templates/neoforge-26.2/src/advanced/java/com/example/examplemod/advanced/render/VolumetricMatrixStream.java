package com.example.examplemod.advanced.render;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class VolumetricMatrixStream {
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicReference<Frame> latest = new AtomicReference<>();
    private final CopyOnWriteArrayList<Consumer<Frame>> consumers = new CopyOnWriteArrayList<>();

    public Frame publish(int sizeX, int sizeY, int sizeZ, byte[] density, int[] material) {
        int volume = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
        if (density.length != volume || material.length != volume) {
            throw new IllegalArgumentException("Volumetric arrays do not match dimensions");
        }
        Frame frame = new Frame(sequence.incrementAndGet(), sizeX, sizeY, sizeZ, density, material);
        latest.set(frame);
        for (Consumer<Frame> consumer : consumers) {
            consumer.accept(frame);
        }
        return frame;
    }

    public Frame latest() {
        return latest.get();
    }

    public AutoCloseable subscribe(Consumer<Frame> consumer) {
        consumers.add(Objects.requireNonNull(consumer));
        return () -> consumers.remove(consumer);
    }

    public record Frame(long sequence, int sizeX, int sizeY, int sizeZ, byte[] density, int[] material) {
        public Frame {
            density = Arrays.copyOf(density, density.length);
            material = Arrays.copyOf(material, material.length);
        }

        @Override
        public byte[] density() {
            return Arrays.copyOf(density, density.length);
        }

        @Override
        public int[] material() {
            return Arrays.copyOf(material, material.length);
        }
    }
}
