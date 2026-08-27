package com.example.examplemod.advanced.sandbox;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class FaultBoundary {
    public <T> Outcome<T> run(String boundaryName, Callable<T> action, Consumer<Throwable> recoveryHook) {
        try {
            return new Outcome<>(true, action.call(), null, boundaryName);
        } catch (VirtualMachineError | ThreadDeath critical) {
            throw critical;
        } catch (Throwable failure) {
            recoveryHook.accept(failure);
            return new Outcome<>(false, null, failure.toString(), boundaryName);
        }
    }

    public record Outcome<T>(boolean success, T value, String error, String boundaryName) {}
}
