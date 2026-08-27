package com.example.examplemod.advanced.polyloader;

import java.lang.instrument.Instrumentation;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class RuntimeEnvironmentScanner {
    public RuntimeEnvironment scan(Instrumentation instrumentation) {
        Set<String> loaded = new LinkedHashSet<>();
        if (instrumentation != null) {
            for (Class<?> type : instrumentation.getAllLoadedClasses()) {
                loaded.add(type.getName());
            }
        }
        String classPath = System.getProperty("java.class.path", "").toLowerCase(Locale.ROOT);
        Set<LoaderFamily> families = detectFamilies(loaded, classPath);
        if (families.isEmpty()) {
            families.add(LoaderFamily.VANILLA);
        }
        return new RuntimeEnvironment(
                families,
                System.getProperty("java.version", "unknown"),
                detectMinecraftVersion(),
                instrumentation != null,
                classAvailable("org.objectweb.asm.ClassReader"));
    }

    private static Set<LoaderFamily> detectFamilies(Set<String> loaded, String classPath) {
        Set<LoaderFamily> result = new LinkedHashSet<>();
        if (containsClass(loaded, "net.fabricmc.loader.api.FabricLoader") || classPath.contains("fabric-loader")) {
            result.add(LoaderFamily.FABRIC);
        }
        if (containsClass(loaded, "org.quiltmc.loader.api.QuiltLoader") || classPath.contains("quilt-loader")) {
            result.add(LoaderFamily.QUILT);
        }
        if (containsPrefix(loaded, "net.neoforged.fml") || classPath.contains("neoforge")) {
            result.add(LoaderFamily.NEOFORGE);
        }
        if (containsPrefix(loaded, "net.minecraftforge.fml") || classPath.contains("forge")) {
            result.add(LoaderFamily.FORGE);
        }
        if (containsPrefix(loaded, "com.mumfrey.liteloader") || classPath.contains("liteloader")) {
            result.add(LoaderFamily.LITELOADER);
        }
        return result;
    }

    private static boolean containsClass(Set<String> loaded, String name) {
        return loaded.contains(name);
    }

    private static boolean containsPrefix(Set<String> loaded, String prefix) {
        for (String name : loaded) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private static String detectMinecraftVersion() {
        String[] properties = {"minecraft.version", "fabric.gameVersion", "fml.mcVersion"};
        for (String property : properties) {
            String value = System.getProperty(property);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "unknown";
    }

    private static boolean classAvailable(String name) {
        try {
            Class.forName(name, false, RuntimeEnvironmentScanner.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
