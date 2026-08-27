package com.example.examplemod.advanced.worldedit;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ServerEditScheduler {
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean draining = new AtomicBoolean();

    public void submit(Runnable task) {
        queue.add(Objects.requireNonNull(task));
    }

    public int drainOnServerThread(int maximumTasks) {
        if (maximumTasks <= 0 || !draining.compareAndSet(false, true)) {
            return 0;
        }
        int completed = 0;
        try {
            Runnable task;
            while (completed < maximumTasks && (task = queue.poll()) != null) {
                task.run();
                completed++;
            }
            return completed;
        } finally {
            draining.set(false);
        }
    }

    public int queuedTasks() {
        return queue.size();
    }
}
