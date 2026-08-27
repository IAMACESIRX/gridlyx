package com.example.examplemod.advanced.render;

import java.util.Arrays;

@FunctionalInterface
public interface CustomGeometryProvider<C> {
    Geometry create(C context);

    record Geometry(float[] positions, float[] normals, float[] uv, int[] indices) {
        public Geometry {
            positions = positions.clone();
            normals = normals.clone();
            uv = uv.clone();
            indices = indices.clone();
            if (positions.length % 3 != 0 || normals.length != positions.length) {
                throw new IllegalArgumentException("Geometry positions/normals must contain xyz triplets");
            }
            if (uv.length != positions.length / 3 * 2) {
                throw new IllegalArgumentException("Geometry UV count must match vertex count");
            }
            int vertices = positions.length / 3;
            if (Arrays.stream(indices).anyMatch(index -> index < 0 || index >= vertices)) {
                throw new IllegalArgumentException("Geometry contains an out-of-range index");
            }
        }
    }
}
