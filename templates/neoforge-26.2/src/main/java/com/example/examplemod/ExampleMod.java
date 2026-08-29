package com.example.examplemod;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/main/java/com/example/examplemod/datagen/ModDataGenerators.java
import com.example.examplemod.datagen.ModDataGenerators;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/main/java/com/example/examplemod/registry/CreativeTabAnchor.java
import com.example.examplemod.registry.CreativeTabAnchor;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/main/java/com/example/examplemod/registry/ModRegistries.java
import com.example.examplemod.registry.ModRegistries;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ExampleMod.MOD_ID)
public final class ExampleMod {
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExampleMod(IEventBus modBus, ModContainer container) {
        ModRegistries.register(modBus);
        CreativeTabAnchor.register(modBus);
        modBus.addListener(ModDataGenerators::gatherData);
        startGridelyxAdvancedRuntime();
        LOGGER.info("{} initialised", MOD_ID);
    }

    /**
     * The normal template JAR does not contain the optional advanced source set. Reflection keeps that artifact
     * dependency-free while allowing advancedJar to attach the Gridelyx runtime automatically when present.
     */
    private static void startGridelyxAdvancedRuntime() {
        try {
            Class<?> bootstrap = Class.forName(
                    "com.example.examplemod.advanced.runtime.GridelyxRuntimeBootstrap",
                    true,
                    ExampleMod.class.getClassLoader());
            bootstrap.getMethod("startIfEnabled").invoke(null);
        } catch (ClassNotFoundException ignored) {
            // Expected for the normal non-advanced template JAR.
        } catch (ReflectiveOperationException | LinkageError failure) {
            LOGGER.error("Gridelyx advanced runtime bootstrap failed", failure);
        }
    }
}
