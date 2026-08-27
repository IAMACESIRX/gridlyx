package com.example.examplemod.advanced.worldedit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class BlueprintSectionCompiler {
    public Map<SectionKey, AsyncSubChunkBlitter.SectionPlan> compile(
            StructureBlueprint blueprint,
            WorldPoint origin,
            SectionSnapshotSource snapshots,
            PaletteResolver paletteResolver) {
        Objects.requireNonNull(blueprint);
        Objects.requireNonNull(origin);
        Map<SectionKey, MutablePlan> plans = new LinkedHashMap<>();
        for (int y = 0; y < blueprint.sizeY(); y++) {
            for (int z = 0; z < blueprint.sizeZ(); z++) {
                for (int x = 0; x < blueprint.sizeX(); x++) {
                    int paletteIndex = blueprint.paletteIndexAt(x, y, z);
                    String state = blueprint.palette().get(paletteIndex);
                    int runtimeState = paletteResolver.resolve(state);
                    int worldX = Math.addExact(origin.x(), x);
                    int worldY = Math.addExact(origin.y(), y);
                    int worldZ = Math.addExact(origin.z(), z);
                    writeVoxel(plans, snapshots, worldX, worldY, worldZ, runtimeState);
                }
            }
        }
        Map<SectionKey, AsyncSubChunkBlitter.SectionPlan> result = new LinkedHashMap<>();
        for (Map.Entry<SectionKey, MutablePlan> entry : plans.entrySet()) {
            MutablePlan plan = entry.getValue();
            result.put(entry.getKey(), new AsyncSubChunkBlitter.SectionPlan(
                    entry.getKey(), plan.revision, plan.before, plan.after));
        }
        return Map.copyOf(result);
    }

    private static void writeVoxel(
            Map<SectionKey, MutablePlan> plans,
            SectionSnapshotSource snapshots,
            int worldX,
            int worldY,
            int worldZ,
            int runtimeState) {
        int chunkX = Math.floorDiv(worldX, 16);
        int sectionY = Math.floorDiv(worldY, 16);
        int chunkZ = Math.floorDiv(worldZ, 16);
        SectionKey key = new SectionKey(chunkX, sectionY, chunkZ);
        MutablePlan plan = plans.computeIfAbsent(key, ignored -> {
            SectionSnapshot snapshot = snapshots.snapshot(key);
            return new MutablePlan(snapshot.revision(), snapshot.buffer(), snapshot.buffer().copy());
        });
        int localX = Math.floorMod(worldX, 16);
        int localY = Math.floorMod(worldY, 16);
        int localZ = Math.floorMod(worldZ, 16);
        plan.after.set(localX, localY, localZ, runtimeState);
    }

    public record WorldPoint(int x, int y, int z) {
    }

    public record SectionSnapshot(long revision, SectionBuffer buffer) {
        public SectionSnapshot {
            buffer = buffer.copy();
        }
    }

    @FunctionalInterface
    public interface SectionSnapshotSource {
        SectionSnapshot snapshot(SectionKey key);
    }

    @FunctionalInterface
    public interface PaletteResolver {
        int resolve(String canonicalBlockState);
    }

    private static final class MutablePlan {
        private final long revision;
        private final SectionBuffer before;
        private final SectionBuffer after;

        private MutablePlan(long revision, SectionBuffer before, SectionBuffer after) {
            this.revision = revision;
            this.before = before.copy();
            this.after = after;
        }
    }
}
