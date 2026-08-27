package com.example.examplemod.advanced.worldedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ParallelArrayBlitter {
    private final Executor executor;

    public ParallelArrayBlitter(Executor executor) {
        this.executor = Objects.requireNonNull(executor);
    }

    public CompletableFuture<SectionBuffer> blit(
            SectionBuffer source,
            SectionBuffer destination,
            Box sourceBox,
            Point destinationOrigin) {
        validateBounds(sourceBox, destinationOrigin);
        SectionBuffer result = destination.copy();
        List<CompletableFuture<Void>> rows = new ArrayList<>();
        for (int y = 0; y < sourceBox.sizeY(); y++) {
            final int rowY = y;
            rows.add(CompletableFuture.runAsync(() -> copyPlane(source, result, sourceBox, destinationOrigin, rowY),
                    executor));
        }
        return CompletableFuture.allOf(rows.toArray(CompletableFuture[]::new)).thenApply(ignored -> result);
    }

    private static void copyPlane(
            SectionBuffer source,
            SectionBuffer destination,
            Box box,
            Point origin,
            int yOffset) {
        int sourceY = box.y() + yOffset;
        int destinationY = origin.y() + yOffset;
        for (int z = 0; z < box.sizeZ(); z++) {
            for (int x = 0; x < box.sizeX(); x++) {
                int value = source.get(box.x() + x, sourceY, box.z() + z);
                destination.set(origin.x() + x, destinationY, origin.z() + z, value);
            }
        }
    }

    private static void validateBounds(Box box, Point origin) {
        if (box.x() < 0 || box.y() < 0 || box.z() < 0
                || box.x() + box.sizeX() > SectionBuffer.EDGE
                || box.y() + box.sizeY() > SectionBuffer.EDGE
                || box.z() + box.sizeZ() > SectionBuffer.EDGE
                || origin.x() < 0 || origin.y() < 0 || origin.z() < 0
                || origin.x() + box.sizeX() > SectionBuffer.EDGE
                || origin.y() + box.sizeY() > SectionBuffer.EDGE
                || origin.z() + box.sizeZ() > SectionBuffer.EDGE) {
            throw new IllegalArgumentException("Blit bounds exceed a section");
        }
    }

    public record Point(int x, int y, int z) {
    }

    public record Box(int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        public Box {
            if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
                throw new IllegalArgumentException("Box dimensions must be positive");
            }
        }
    }
}
