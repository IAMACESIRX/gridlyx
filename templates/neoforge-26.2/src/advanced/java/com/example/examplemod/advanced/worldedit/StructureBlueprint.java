package com.example.examplemod.advanced.worldedit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record StructureBlueprint(
        int sizeX,
        int sizeY,
        int sizeZ,
        List<String> palette,
        int[] voxels,
        Map<Integer, Map<String, Object>> blockMetadata) {
    public StructureBlueprint {
        if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) {
            throw new IllegalArgumentException("Structure dimensions must be positive");
        }
        long expected = (long) sizeX * sizeY * sizeZ;
        if (expected > Integer.MAX_VALUE || voxels.length != expected) {
            throw new IllegalArgumentException("Voxel array does not match structure dimensions");
        }
        palette = List.copyOf(palette);
        voxels = Arrays.copyOf(voxels, voxels.length);
        blockMetadata = Map.copyOf(blockMetadata);
    }

    @Override
    public int[] voxels() {
        return Arrays.copyOf(voxels, voxels.length);
    }

    public int index(int x, int y, int z) {
        return (y * sizeZ + z) * sizeX + x;
    }

    public int paletteIndexAt(int x, int y, int z) {
        return voxels[index(x, y, z)];
    }
}
