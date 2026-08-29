package com.example.examplemod.advanced.runtime;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Process/runtime supervisor hook for H6 Runtime Epoch Handoff.
 *
 * <p>The advanced library deliberately does not fake process replacement. A launcher or supervisor that can
 * quiesce, checkpoint, launch a successor runtime, restore state, switch authority and roll back exposes that
 * capability through this service contract. Without a driver, H6 remains explicitly unavailable and reloads
 * fail closed.
 */
@FunctionalInterface
public interface RuntimeEpochDriver {
    void handoff(ExternalHotloadCore.ReloadEvent event, String reason) throws Exception;

    static Optional<RuntimeEpochDriver> discover(ClassLoader loader) {
        Objects.requireNonNull(loader, "loader");
        return ServiceLoader.load(RuntimeEpochDriver.class, loader).findFirst();
    }
}
