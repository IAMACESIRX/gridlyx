package com.example.examplemod.advanced.telemetry;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;

public final class ChaosLoop {
    private final Map<String, Fault> faults = new ConcurrentHashMap<>();
    private final SplittableRandom random;
    private final boolean enabled;

    public ChaosLoop(long seed, boolean enabled) {
        random = new SplittableRandom(seed);
        this.enabled = enabled;
    }

    public void register(String name, double probability, Runnable action) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("Chaos probability must be between zero and one");
        }
        faults.put(name, new Fault(probability, action));
    }

    public int tick() {
        if (!enabled) {
            return 0;
        }
        int triggered = 0;
        for (Fault fault : faults.values()) {
            if (random.nextDouble() < fault.probability()) {
                fault.action().run();
                triggered++;
            }
        }
        return triggered;
    }

    private record Fault(double probability, Runnable action) {}
}
