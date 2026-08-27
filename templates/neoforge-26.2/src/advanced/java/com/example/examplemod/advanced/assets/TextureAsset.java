package com.example.examplemod.advanced.assets;

import java.util.Arrays;

public record TextureAsset(long revision, int width, int height, int[] rgbaPixels) {
    public TextureAsset {
        if (revision < 0 || width < 1 || height < 1) {
            throw new IllegalArgumentException("invalid texture dimensions or revision");
        }
        if ((long) width * height != rgbaPixels.length) {
            throw new IllegalArgumentException("pixel count does not match texture dimensions");
        }
        rgbaPixels = Arrays.copyOf(rgbaPixels, rgbaPixels.length);
    }

    @Override
    public int[] rgbaPixels() {
        return Arrays.copyOf(rgbaPixels, rgbaPixels.length);
    }
}
