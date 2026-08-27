package com.example.examplemod.advanced.clientdev;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class AiDevelopmentBridge {
    private final Map<UUID, CompletableFuture<String>> pending = new ConcurrentHashMap<>();
    private final Consumer<Request> outbound;
    private final int maximumPending;

    public AiDevelopmentBridge(Consumer<Request> outbound, int maximumPending) {
        this.outbound = Objects.requireNonNull(outbound);
        if (maximumPending <= 0) {
            throw new IllegalArgumentException("maximumPending must be positive");
        }
        this.maximumPending = maximumPending;
    }

    public CompletableFuture<String> request(String method, String payload, Duration timeout) {
        if (pending.size() >= maximumPending) {
            return CompletableFuture.failedFuture(new IllegalStateException("AI request queue is full"));
        }
        UUID id = UUID.randomUUID();
        CompletableFuture<String> future = new CompletableFuture<>();
        pending.put(id, future);
        outbound.accept(new Request(id, method, payload));
        future.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS).whenComplete((value, error) -> pending.remove(id));
        return future;
    }

    public boolean complete(UUID id, String payload) {
        CompletableFuture<String> future = pending.remove(id);
        return future != null && future.complete(payload);
    }

    public boolean fail(UUID id, Throwable error) {
        CompletableFuture<String> future = pending.remove(id);
        return future != null && future.completeExceptionally(error);
    }

    public record Request(UUID id, String method, String payload) {
    }
}
