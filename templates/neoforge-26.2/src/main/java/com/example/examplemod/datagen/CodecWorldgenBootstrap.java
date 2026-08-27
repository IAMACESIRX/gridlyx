package com.example.examplemod.datagen;

import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class CodecWorldgenBootstrap {
    private CodecWorldgenBootstrap() {}

    public static RegistrySetBuilder newBuilder() {
        return new RegistrySetBuilder();
    }

    public static void install(GatherDataEvent.Client event, RegistrySetBuilder builder) {
        event.createDatapackRegistryObjects(builder);
    }
}
