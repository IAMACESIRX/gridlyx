package com.example.examplemod.advanced.assets;

import java.util.Arrays;

public record MeshAsset(long revision, int vertexStride, float[] vertices, int[] indices) {
    public MeshAsset {
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
        if (vertexStride < 1 || vertices.length % vertexStride != 0) {
            throw new IllegalArgumentException("vertices must align to vertexStride");
        }
        vertices = Arrays.copyOf(vertices, vertices.length);
        indices = Arrays.copyOf(indices, indices.length);
        int vertexCount = vertices.length / vertexStride;
        for (int index : indices) {
            if (index < 0 || index >= vertexCount) {
                throw new IllegalArgumentException("mesh index outside vertex range");
            }
        }
    }

    @Override
    public float[] vertices() {
        return Arrays.copyOf(vertices, vertices.length);
    }

    @Override
    public int[] indices() {
        return Arrays.copyOf(indices, indices.length);
    }

    public int vertexCount() {
        return vertices.length / vertexStride;
    }
}
