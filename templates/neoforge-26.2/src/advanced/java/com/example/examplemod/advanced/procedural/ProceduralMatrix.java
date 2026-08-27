package com.example.examplemod.advanced.procedural;

import java.util.SplittableRandom;

public final class ProceduralMatrix {
    public int[] generate(int width, int height, int depth, long seed, CellRule rule) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("Procedural matrix dimensions must be positive");
        }
        int cellCount = Math.multiplyExact(Math.multiplyExact(width, height), depth);
        int[] cells = new int[cellCount];
        SplittableRandom random = new SplittableRandom(seed);
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    cells[index++] = rule.valueAt(x, y, z, random.split());
                }
            }
        }
        return cells;
    }

    @FunctionalInterface
    public interface CellRule {
        int valueAt(int x, int y, int z, SplittableRandom random);
    }
}
