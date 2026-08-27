package com.example.examplemod.advanced.clientdev;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class KeyBindingRouter {
    private final Map<String, Runnable> actions = new ConcurrentHashMap<>();

    public void registerAction(String actionId, Runnable action) {
        Runnable previous = actions.putIfAbsent(actionId, Objects.requireNonNull(action));
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate key action: " + actionId);
        }
    }

    public boolean trigger(String actionId) {
        Runnable action = actions.get(actionId);
        if (action == null) {
            return false;
        }
        action.run();
        return true;
    }

    public Map<String, Runnable> snapshot() {
        return Map.copyOf(actions);
    }
}
