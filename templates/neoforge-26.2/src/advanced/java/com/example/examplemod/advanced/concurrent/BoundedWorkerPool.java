package com.example.examplemod.advanced.concurrent;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class BoundedWorkerPool implements AutoCloseable {
    private final ThreadPoolExecutor executor;

    public BoundedWorkerPool(String name, int workers, int queueCapacity) {
        if (workers < 1 || queueCapacity < 1) {
            throw new IllegalArgumentException("workers and queueCapacity must be positive");
        }
        AtomicInteger sequence = new AtomicInteger();
        ThreadFactory factory = task -> {
            Thread thread = new Thread(task, name + "-" + sequence.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
        RejectedExecutionHandler rejection = new ThreadPoolExecutor.CallerRunsPolicy();
        executor = new ThreadPoolExecutor(
                workers,
                workers,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                factory,
                rejection);
        executor.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }

    public int queuedTasks() {
        return executor.getQueue().size();
    }

    public void shutdown(Duration timeout) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
