package com.example.examplemod.advanced.render;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ClientRenderEventPipeline<C> {
    private final CopyOnWriteArrayList<StageListener<C>> listeners = new CopyOnWriteArrayList<>();

    public void register(StageListener<C> listener) {
        listeners.add(listener);
    }

    public void fire(Stage stage, C context) {
        for (StageListener<C> listener : listeners) {
            listener.onStage(stage, context);
        }
    }

    public List<StageListener<C>> listeners() {
        return List.copyOf(listeners);
    }

    public enum Stage {
        BEFORE_WORLD,
        WORLD_OPAQUE,
        WORLD_TRANSLUCENT,
        ENTITIES,
        PARTICLES,
        AFTER_WORLD,
        HUD
    }

    @FunctionalInterface
    public interface StageListener<C> {
        void onStage(Stage stage, C context);
    }
}
