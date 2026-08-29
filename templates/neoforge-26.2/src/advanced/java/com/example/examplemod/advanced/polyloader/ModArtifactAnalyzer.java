package com.example.examplemod.advanced.polyloader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ModArtifactAnalyzer {
    public ModArtifactProfile analyze(Path jarPath) throws IOException {
        Set<LoaderFamily> loaders = new LinkedHashSet<>();
        boolean mixins = false;
        boolean accessWideners = false;
        boolean transformationService = false;
        boolean nativeLibraries = false;
        try (JarFile jar = new JarFile(jarPath.toFile(), false)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                String lower = name.toLowerCase(Locale.ROOT);
                if (name.equals("fabric.mod.json")) {
                    loaders.add(LoaderFamily.FABRIC);
                } else if (name.equals("quilt.mod.json")) {
                    loaders.add(LoaderFamily.QUILT);
                // Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/main/templates/META-INF/neoforge.mods.toml
                } else if (name.equals("META-INF/neoforge.mods.toml")) {
                    loaders.add(LoaderFamily.NEOFORGE);
                } else if (name.equals("META-INF/mods.toml") || name.equals("mcmod.info")) {
                    loaders.add(LoaderFamily.FORGE);
                } else if (name.equals("litemod.json")) {
                    loaders.add(LoaderFamily.LITELOADER);
                }
                mixins |= lower.endsWith(".mixins.json") || lower.contains("mixin") && lower.endsWith(".json");
                accessWideners |= lower.endsWith(".accesswidener");
                transformationService |= name.equals(
                        "META-INF/services/cpw.mods.modlauncher.api.ITransformationService");
                transformationService |= name.equals("META-INF/coremods.json");
                nativeLibraries |= lower.endsWith(".dll") || lower.endsWith(".so") || lower.endsWith(".dylib");
            }
        }
        SideloadMode mode = recommend(loaders, mixins, accessWideners, transformationService, nativeLibraries);
        return new ModArtifactProfile(
                jarPath.toAbsolutePath().normalize(),
                loaders,
                mixins,
                accessWideners,
                transformationService,
                nativeLibraries,
                mode);
    }

    private static SideloadMode recommend(
            Set<LoaderFamily> loaders,
            boolean mixins,
            boolean accessWideners,
            boolean transformationService,
            boolean nativeLibraries) {
        if (mixins || accessWideners || transformationService || nativeLibraries) {
            return SideloadMode.PRELAUNCH_REQUIRED;
        }
        if (loaders.isEmpty()) {
            return SideloadMode.LIVE_SAFE;
        }
        return SideloadMode.EMULATED;
    }
}
