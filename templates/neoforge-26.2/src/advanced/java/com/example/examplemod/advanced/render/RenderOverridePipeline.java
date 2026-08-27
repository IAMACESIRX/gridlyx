package com.example.examplemod.advanced.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RenderOverridePipeline<C> {
    private final CopyOnWriteArrayList<Entry<C>> entries = new CopyOnWriteArrayList<>();

    public void register(int priority, RenderOverride<C> override) {
        entries.add(new Entry<>(priority, override));
        entries.sort(Comparator.comparingInt(Entry<C>::priority).reversed());
    }

    public boolean render(C context) {
        for (Entry<C> entry : entries) {
            if (entry.override().render(context)) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> priorities() {
        List<Integer> result = new ArrayList<>(entries.size());
        for (Entry<C> entry : entries) {
            result.add(entry.priority());
        }
        return List.copyOf(result);
    }

    @FunctionalInterface
    public interface RenderOverride<C> {
        boolean render(C context);
    }

    private record Entry<C>(int priority, RenderOverride<C> override) {}
}
