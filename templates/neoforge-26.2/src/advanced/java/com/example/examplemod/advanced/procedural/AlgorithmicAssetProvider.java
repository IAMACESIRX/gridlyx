package com.example.examplemod.advanced.procedural;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public final class AlgorithmicAssetProvider {
    private final TreeMap<String, byte[]> outputs = new TreeMap<>();

    public void emitText(String relativePath, String content) {
        emit(relativePath, content.getBytes(StandardCharsets.UTF_8));
    }

    public void emit(String relativePath, byte[] content) {
        String canonical = relativePath.replace('\\', '/');
        if (canonical.startsWith("/") || canonical.contains("../")) {
            throw new IllegalArgumentException("Generated asset path escapes the output root");
        }
        outputs.put(canonical, content.clone());
    }

    public Map<String, byte[]> snapshot() {
        TreeMap<String, byte[]> copy = new TreeMap<>();
        outputs.forEach((path, bytes) -> copy.put(path, bytes.clone()));
        return Collections.unmodifiableMap(copy);
    }

    public void forEach(BiConsumer<String, byte[]> consumer) {
        outputs.forEach((path, bytes) -> consumer.accept(path, bytes.clone()));
    }
}
