package com.example.examplemod.advanced.worldedit;

import java.util.Set;

public interface WorldMutationSink {
    long currentRevision(SectionKey key);

    void applyWithoutLighting(SectionDelta delta);

    void reconcileLighting(Set<SectionKey> dirtySections);

    void markForSave(Set<SectionKey> dirtySections);
}
