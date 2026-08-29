package com.example.examplemod.datagen;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/main/java/com/example/examplemod/ExampleMod.java
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
