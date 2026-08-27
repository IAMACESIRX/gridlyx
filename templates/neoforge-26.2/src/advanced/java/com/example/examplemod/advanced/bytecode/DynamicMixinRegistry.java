package com.example.examplemod.advanced.bytecode;

import java.lang.reflect.Method;

public final class DynamicMixinRegistry {
    private DynamicMixinRegistry() {}

    public static void addTrustedConfiguration(String resourceName) {
        if (!resourceName.matches("[A-Za-z0-9_./-]+\\.json") || resourceName.contains("..")) {
            throw new IllegalArgumentException("Unsafe mixin configuration path");
        }
        try {
            Class<?> mixins = Class.forName("org.spongepowered.asm.mixin.Mixins");
            Method addConfiguration = mixins.getMethod("addConfiguration", String.class);
            addConfiguration.invoke(null, resourceName);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Mixin runtime is unavailable or incompatible", exception);
        }
    }
}
