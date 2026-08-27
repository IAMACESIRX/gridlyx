package com.example.examplemod.advanced.runtime;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ServiceLoader;

public final class VersionedServiceLoader<T> implements AutoCloseable {
    private final Class<T> serviceType;
    private final HotSwapSlot<T> slot;
    private URLClassLoader activeLoader;

    public VersionedServiceLoader(Class<T> serviceType, T initialImplementation) {
        this.serviceType = serviceType;
        slot = new HotSwapSlot<>(initialImplementation);
    }

    public T service() {
        return slot.get();
    }

    public long version() {
        return slot.version();
    }

    public synchronized long load(Path jar) throws IOException {
        URLClassLoader nextLoader = new URLClassLoader(
                new URL[] {jar.toUri().toURL()},
                serviceType.getClassLoader());
        try {
            T replacement = ServiceLoader.load(serviceType, nextLoader)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No service implementation found for " + serviceType.getName()));
            long version = slot.swap(replacement);
            URLClassLoader previous = activeLoader;
            activeLoader = nextLoader;
            if (previous != null) {
                previous.close();
            }
            return version;
        } catch (RuntimeException exception) {
            nextLoader.close();
            throw exception;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (activeLoader != null) {
            activeLoader.close();
            activeLoader = null;
        }
    }
}
