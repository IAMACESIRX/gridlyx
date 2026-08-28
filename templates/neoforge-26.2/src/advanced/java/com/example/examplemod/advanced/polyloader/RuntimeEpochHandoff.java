package com.example.examplemod.advanced.polyloader;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record RuntimeEpochHandoff(
        long fromEpoch,
        long toEpoch,
        String targetFingerprint,
        List<String> moduleGraph,
        Map<String, String> stateCheckpoints,
        Phase phase) {

    public RuntimeEpochHandoff {
        if (toEpoch <= fromEpoch) {
            throw new IllegalArgumentException("toEpoch must advance");
        }
        Objects.requireNonNull(targetFingerprint, "targetFingerprint");
        moduleGraph = List.copyOf(moduleGraph);
        stateCheckpoints = Map.copyOf(stateCheckpoints);
        Objects.requireNonNull(phase, "phase");
    }

    public enum Phase {
        QUIESCE,
        CHECKPOINT,
        RESOLVE,
        LAUNCH_SUCCESSOR,
        CONNECT,
        RESTORE,
        HEALTH_CHECK,
        SWITCH_AUTHORITY,
        RETIRE_PREVIOUS,
        COMPLETE,
        ROLLBACK
    }
}
