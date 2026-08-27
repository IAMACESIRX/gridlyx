package com.example.examplemod.advanced.worldedit;

import java.util.Arrays;

public final class SectionBuffer {
    public static final int EDGE = 16;
    public static final int VOLUME = EDGE * EDGE * EDGE;

    private final int[] paletteIndices;

    public SectionBuffer() {
        this.paletteIndices = new int[VOLUME];
    }

    public SectionBuffer(int[] paletteIndices) {
        if (paletteIndices.length != VOLUME) {
            throw new IllegalArgumentException("A section must contain exactly 4096 palette indices");
        }
        this.paletteIndices = Arrays.copyOf(paletteIndices, VOLUME);
    }

    public int get(int x, int y, int z) {
        return paletteIndices[index(x, y, z)];
    }

    public void set(int x, int y, int z, int paletteIndex) {
        paletteIndices[index(x, y, z)] = paletteIndex;
    }

    public int[] copyArray() {
        return Arrays.copyOf(paletteIndices, VOLUME);
    }

    public SectionBuffer copy() {
        return new SectionBuffer(paletteIndices);
    }

    public static int index(int x, int y, int z) {
        if ((x | y | z) < 0 || x >= EDGE || y >= EDGE || z >= EDGE) {
            throw new IndexOutOfBoundsException("Section coordinate outside 0..15");
        }
        return (y << 8) | (z << 4) | x;
    }
}
