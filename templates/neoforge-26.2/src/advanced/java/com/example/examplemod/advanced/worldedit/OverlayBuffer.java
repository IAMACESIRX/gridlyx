package com.example.examplemod.advanced.worldedit;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class OverlayBuffer {
    private final int subdivisions;
    private final Map<OverlayKey, OverlayCell> cells = new ConcurrentHashMap<>();

    public OverlayBuffer(int subdivisions) {
        if (subdivisions < 1 || subdivisions > 16) {
            throw new IllegalArgumentException("subdivisions must be within 1..16");
        }
        this.subdivisions = subdivisions;
    }

    public void paint(int x, int y, int z, int subX, int subY, int subZ, OverlayCell cell) {
        validateSub(subX, subY, subZ);
        cells.put(new OverlayKey(x, y, z, subX, subY, subZ), Objects.requireNonNull(cell));
    }

    public OverlayCell read(int x, int y, int z, int subX, int subY, int subZ) {
        validateSub(subX, subY, subZ);
        return cells.get(new OverlayKey(x, y, z, subX, subY, subZ));
    }

    public void erase(int x, int y, int z, int subX, int subY, int subZ) {
        validateSub(subX, subY, subZ);
        cells.remove(new OverlayKey(x, y, z, subX, subY, subZ));
    }

    public int size() {
        return cells.size();
    }

    private void validateSub(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= subdivisions || y >= subdivisions || z >= subdivisions) {
            throw new IllegalArgumentException("Sub-voxel coordinate outside overlay grid");
        }
    }

    public record OverlayKey(int x, int y, int z, int subX, int subY, int subZ) {
    }

    public record OverlayCell(int rgba, int materialId, float density) {
        public OverlayCell {
            if (!Float.isFinite(density) || density < 0.0F || density > 1.0F) {
                throw new IllegalArgumentException("density must be finite and within 0..1");
            }
        }
    }
}
