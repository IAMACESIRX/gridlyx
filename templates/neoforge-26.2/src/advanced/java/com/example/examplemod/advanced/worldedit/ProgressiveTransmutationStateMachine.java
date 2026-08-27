package com.example.examplemod.advanced.worldedit;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class ProgressiveTransmutationStateMachine {
    private final AtomicReference<State> state = new AtomicReference<>(new State(Mode.IDLE, 0L, 0.0));

    public State transition(Mode nextMode, long generation, double progress) {
        Objects.requireNonNull(nextMode);
        if (generation < 0 || !Double.isFinite(progress) || progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("Invalid transmutation state");
        }
        return state.updateAndGet(current -> {
            if (generation < current.generation()) {
                throw new IllegalStateException("Transmutation generation cannot move backwards");
            }
            return new State(nextMode, generation, progress);
        });
    }

    public State snapshot() {
        return state.get();
    }

    public enum Mode {
        IDLE,
        SCANNING,
        PREPARING,
        TRANSMUTING,
        RECONCILING,
        COMPLETE,
        ROLLBACK
    }

    public record State(Mode mode, long generation, double progress) {
    }
}
