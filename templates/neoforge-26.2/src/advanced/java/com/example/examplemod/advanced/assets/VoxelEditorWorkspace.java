package com.example.examplemod.advanced.assets;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

public final class VoxelEditorWorkspace {
    private final Set<Integer> selectedVertices = new ConcurrentSkipListSet<>();
    private final AtomicLong revision = new AtomicLong();
    private volatile String activeModelId;
    private volatile Tool tool = Tool.SELECT;

    public long activateModel(String modelId) {
        activeModelId = modelId;
        selectedVertices.clear();
        return revision.incrementAndGet();
    }

    public long setTool(Tool nextTool) {
        tool = nextTool;
        return revision.incrementAndGet();
    }

    public long selectVertex(int vertexIndex, boolean selected) {
        if (vertexIndex < 0) {
            throw new IllegalArgumentException("vertexIndex must be non-negative");
        }
        if (selected) {
            selectedVertices.add(vertexIndex);
        } else {
            selectedVertices.remove(vertexIndex);
        }
        return revision.incrementAndGet();
    }

    public long clearSelection() {
        selectedVertices.clear();
        return revision.incrementAndGet();
    }

    public Snapshot snapshot() {
        return new Snapshot(revision.get(), activeModelId, tool, Set.copyOf(selectedVertices));
    }

    public enum Tool {
        SELECT,
        SCULPT,
        MOVE_VERTEX,
        PAINT,
        UV,
        TRANSFORM
    }

    public record Snapshot(long revision, String activeModelId, Tool tool, Set<Integer> selectedVertices) {
        public Snapshot {
            selectedVertices = Set.copyOf(selectedVertices);
        }
    }
}
