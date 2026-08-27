package com.example.examplemod.advanced.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public final class DynamicTextureRegistry {
    private final Map<String, TextureAsset> textures = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<TextureUpdate> updates = new ConcurrentLinkedQueue<>();
    private final AtomicLong revisions = new AtomicLong();

    public TextureAsset publish(String textureId, int width, int height, int[] rgbaPixels) {
        TextureAsset asset = new TextureAsset(revisions.incrementAndGet(), width, height, rgbaPixels);
        textures.put(textureId, asset);
        updates.add(new TextureUpdate(textureId, asset.revision()));
        return asset;
    }

    public TextureAsset patch(String textureId, int x, int y, int width, int height, int[] rgbaPixels) {
        TextureAsset current = textures.get(textureId);
        if (current == null) {
            throw new IllegalArgumentException("Unknown texture: " + textureId);
        }
        if (width < 1 || height < 1 || rgbaPixels.length != width * height) {
            throw new IllegalArgumentException("Invalid patch dimensions");
        }
        if (x < 0 || y < 0 || x + width > current.width() || y + height > current.height()) {
            throw new IllegalArgumentException("Patch is outside texture bounds");
        }
        int[] target = current.rgbaPixels();
        for (int row = 0; row < height; row++) {
            int sourceOffset = row * width;
            int targetOffset = (y + row) * current.width() + x;
            System.arraycopy(rgbaPixels, sourceOffset, target, targetOffset, width);
        }
        return publish(textureId, current.width(), current.height(), target);
    }

    public Optional<TextureAsset> texture(String textureId) {
        return Optional.ofNullable(textures.get(textureId));
    }

    public List<TextureUpdate> drainUpdates(int maximum) {
        if (maximum < 1) {
            throw new IllegalArgumentException("maximum must be positive");
        }
        List<TextureUpdate> result = new ArrayList<>(maximum);
        while (result.size() < maximum) {
            TextureUpdate update = updates.poll();
            if (update == null) {
                break;
            }
            result.add(update);
        }
        return List.copyOf(result);
    }

    public int[] copyPixels(String textureId) {
        TextureAsset asset = textures.get(textureId);
        if (asset == null) {
            return new int[0];
        }
        return Arrays.copyOf(asset.rgbaPixels(), asset.rgbaPixels().length);
    }

    public record TextureUpdate(String textureId, long revision) {}
}
