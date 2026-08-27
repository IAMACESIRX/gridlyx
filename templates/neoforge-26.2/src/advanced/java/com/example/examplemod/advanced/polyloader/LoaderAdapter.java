package com.example.examplemod.advanced.polyloader;

import java.util.List;

public interface LoaderAdapter {
    LoaderFamily family();

    boolean supports(RuntimeEnvironment environment);

    List<CallTranslationRule> translationRules();

    SideloadMode assess(ModArtifactProfile profile);

    void activate(SideloadContext context) throws Exception;
}
