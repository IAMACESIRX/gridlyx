package com.example.examplemod.advanced.worldedit;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class WorldEditSession {
    private final BlueprintSectionCompiler compiler;
    private final AsyncSubChunkBlitter blitter;
    private final WorldMutationSink mutationSink;

    public WorldEditSession(
            BlueprintSectionCompiler compiler,
            AsyncSubChunkBlitter blitter,
            WorldMutationSink mutationSink) {
        this.compiler = Objects.requireNonNull(compiler);
        this.blitter = Objects.requireNonNull(blitter);
        this.mutationSink = Objects.requireNonNull(mutationSink);
    }

    public CompletableFuture<BulkEditTransaction> paste(
            StructureBlueprint blueprint,
            BlueprintSectionCompiler.WorldPoint origin,
            BlueprintSectionCompiler.SectionSnapshotSource snapshots,
            BlueprintSectionCompiler.PaletteResolver paletteResolver) {
        Map<SectionKey, AsyncSubChunkBlitter.SectionPlan> plans =
                compiler.compile(blueprint, origin, snapshots, paletteResolver);
        return blitter.prepare(List.copyOf(plans.values()));
    }

    public void commit(BulkEditTransaction transaction, boolean reconcileLighting) {
        BulkEditTransaction.LightingMode mode = reconcileLighting
                ? BulkEditTransaction.LightingMode.DEFER_AND_RECONCILE
                : BulkEditTransaction.LightingMode.MANUAL_RECONCILE;
        blitter.commitOnServerThread(transaction, mutationSink, mode);
    }
}
