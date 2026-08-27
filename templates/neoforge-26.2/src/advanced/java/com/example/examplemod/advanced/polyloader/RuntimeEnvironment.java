package com.example.examplemod.advanced.polyloader;

import java.util.Set;

public record RuntimeEnvironment(
        Set<LoaderFamily> loaders,
        String javaVersion,
        String minecraftVersion,
        boolean instrumentationAvailable,
        boolean asmAvailable) {
    public RuntimeEnvironment {
        loaders = Set.copyOf(loaders);
        javaVersion = javaVersion == null ? "unknown" : javaVersion;
        minecraftVersion = minecraftVersion == null ? "unknown" : minecraftVersion;
    }

    public LoaderFamily primaryLoader() {
        for (LoaderFamily family : LoaderFamily.values()) {
            if (family != LoaderFamily.VANILLA && family != LoaderFamily.UNKNOWN && loaders.contains(family)) {
                return family;
            }
        }
        return loaders.contains(LoaderFamily.VANILLA) ? LoaderFamily.VANILLA : LoaderFamily.UNKNOWN;
    }
}
