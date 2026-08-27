package com.example.examplemod.advanced.worldedit;

import java.util.Arrays;

public record SectionDelta(
        SectionKey key,
        long baseRevision,
        long newRevision,
        int[] indices,
        int[] paletteIndices) {
    public SectionDelta {
        if (newRevision <= baseRevision) {
            throw new IllegalArgumentException("newRevision must be greater than baseRevision");
        }
        if (indices.length != paletteIndices.length) {
            throw new IllegalArgumentException("indices and paletteIndices must have equal length");
        }
        indices = Arrays.copyOf(indices, indices.length);
        paletteIndices = Arrays.copyOf(paletteIndices, paletteIndices.length);
    }

    @Override
    public int[] indices() {
        return Arrays.copyOf(indices, indices.length);
    }

    @Override
    public int[] paletteIndices() {
        return Arrays.copyOf(paletteIndices, paletteIndices.length);
    }
}
