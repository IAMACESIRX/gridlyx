package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.registry.ModRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, ExampleMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModRegistries.EXAMPLE_ITEM.get(), "Example Item");
        add(ModRegistries.EXAMPLE_BLOCK.get(), "Example Block");
        add("itemGroup.examplemod.main", "Example Mod");
    }
}
