package com.example.examplemod.advanced.polyloader;

import java.nio.file.Path;

public record SideloadContext(
        Path jar,
        ModArtifactProfile profile,
        ClassLoader classLoader,
        UnifiedAbstractionLayer abstractionLayer,
        RuntimeEnvironment environment) {}
