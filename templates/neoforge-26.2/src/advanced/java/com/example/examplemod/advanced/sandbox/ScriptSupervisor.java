package com.example.examplemod.advanced.sandbox;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class ScriptSupervisor implements AutoCloseable {
    private final ExecutorService workers;
    private final ScheduledExecutorService watchdog;
    private final Map<Long, Future<?>> active = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public ScriptSupervisor(int workerCount) {
        if (workerCount < 1) {
            throw new IllegalArgumentException("workerCount must be positive");
        }
        workers = Executors.newFixedThreadPool(workerCount);
        watchdog = Executors.newSingleThreadScheduledExecutor();
    }

    public <T> CompletableFuture<ExecutionResult<T>> submit(Callable<T> task, Duration timeout) {
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        long id = ids.incrementAndGet();
        CompletableFuture<ExecutionResult<T>> completion = new CompletableFuture<>();
        FutureTask<Void> future = new FutureTask<>(() -> {
            runTask(id, task, completion);
            return null;
        });
        active.put(id, future);
        workers.execute(future);
        watchdog.schedule(
                () -> timeout(id, completion),
                timeout.toNanos(),
                TimeUnit.NANOSECONDS);
        return completion;
    }

    private <T> void runTask(long id, Callable<T> task, CompletableFuture<ExecutionResult<T>> completion) {
        try {
            T value = task.call();
            completion.complete(new ExecutionResult<>(id, Status.SUCCESS, value, null, false));
        } catch (Exception | LinkageError failure) {
            completion.complete(new ExecutionResult<>(id, Status.FAILED, null, failure.toString(), false));
        } finally {
            active.remove(id);
        }
    }

    private <T> void timeout(long id, CompletableFuture<ExecutionResult<T>> completion) {
        Future<?> future = active.remove(id);
        if (future == null || completion.isDone()) {
            return;
        }
        boolean interrupted = future.cancel(true);
        completion.complete(new ExecutionResult<>(
                id,
                Status.TIMED_OUT,
                null,
                "Script exceeded its execution deadline",
                interrupted));
    }

    public int activeTaskCount() {
        return active.size();
    }

    @Override
    public void close() {
        for (Future<?> future : active.values()) {
            future.cancel(true);
        }
        active.clear();
        workers.shutdownNow();
        watchdog.shutdownNow();
    }

    public enum Status {
        SUCCESS,
        FAILED,
        TIMED_OUT
    }

    public record ExecutionResult<T>(
            long id,
            Status status,
            T value,
            String error,
            boolean interruptionRequested) {}
}
