package com.example.examplemod.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class CreativeTabAnchor {
    private CreativeTabAnchor() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(CreativeTabAnchor::onBuildContents);
    }

    private static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModRegistries.EXAMPLE_BLOCK_ITEM);
        }
    }
}
