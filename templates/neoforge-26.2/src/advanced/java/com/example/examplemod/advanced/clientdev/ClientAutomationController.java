package com.example.examplemod.advanced.clientdev;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ClientAutomationController {
    private final Queue<ClientAction> actions = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean enabled = new AtomicBoolean();

    public void setEnabled(boolean value) {
        enabled.set(value);
        if (!value) {
            actions.clear();
        }
    }

    public void submit(ClientAction action) {
        if (!enabled.get()) {
            throw new IllegalStateException("Client automation is disabled");
        }
        actions.add(Objects.requireNonNull(action));
    }

    public int drain(int budget, ActionExecutor executor) {
        if (!enabled.get() || budget <= 0) {
            return 0;
        }
        int count = 0;
        ClientAction action;
        while (count < budget && (action = actions.poll()) != null) {
            executor.execute(action);
            count++;
        }
        return count;
    }

    public record ClientAction(String type, String target, double x, double y, double z) {
    }

    @FunctionalInterface
    public interface ActionExecutor {
        void execute(ClientAction action);
    }
}
