package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, ExampleMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("item.examplemod.example_item", "Example Item");
        add("block.examplemod.example_block", "Example Block");
        add("itemGroup.examplemod.main", "Example Mod");
    }
}
