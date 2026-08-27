package com.example.examplemod.advanced.collision;

import java.util.ArrayList;
import java.util.List;

public final class DynamicCollisionShape {
    private final List<Box> boxes = new ArrayList<>();

    public synchronized DynamicCollisionShape add(Box box) {
        boxes.add(box.normalized());
        return this;
    }

    public synchronized DynamicCollisionShape clear() {
        boxes.clear();
        return this;
    }

    public synchronized List<Box> boxes() {
        return List.copyOf(boxes);
    }

    public synchronized boolean contains(double x, double y, double z) {
        return boxes.stream().anyMatch(box -> box.contains(x, y, z));
    }

    public synchronized DynamicCollisionShape translated(double x, double y, double z) {
        DynamicCollisionShape translated = new DynamicCollisionShape();
        for (Box box : boxes) {
            translated.add(box.translated(x, y, z));
        }
        return translated;
    }

    public record Box(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ) {
        public Box normalized() {
            return new Box(
                    Math.min(minX, maxX),
                    Math.min(minY, maxY),
                    Math.min(minZ, maxZ),
                    Math.max(minX, maxX),
                    Math.max(minY, maxY),
                    Math.max(minZ, maxZ));
        }

        public boolean contains(double x, double y, double z) {
            Box box = normalized();
            return x >= box.minX
                    && x <= box.maxX
                    && y >= box.minY
                    && y <= box.maxY
                    && z >= box.minZ
                    && z <= box.maxZ;
        }

        public Box translated(double x, double y, double z) {
            return new Box(
                    minX + x,
                    minY + y,
                    minZ + z,
                    maxX + x,
                    maxY + y,
                    maxZ + z);
        }
    }
}
