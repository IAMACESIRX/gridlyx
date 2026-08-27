package com.example.examplemod.advanced.polyloader;

import java.nio.file.Path;
import java.util.Set;

public record ModArtifactProfile(
        Path jar,
        Set<LoaderFamily> declaredLoaders,
        boolean mixins,
        boolean accessWideners,
        boolean transformationService,
        boolean nativeLibraries,
        SideloadMode recommendedMode) {
    public ModArtifactProfile {
        declaredLoaders = Set.copyOf(declaredLoaders);
    }
}
