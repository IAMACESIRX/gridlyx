package com.example.examplemod.advanced.worldedit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class BulkEditTransaction {
    private final long transactionId;
    private final List<SectionDelta> deltas = new ArrayList<>();

    public BulkEditTransaction(long transactionId) {
        if (transactionId < 0) {
            throw new IllegalArgumentException("transactionId must be non-negative");
        }
        this.transactionId = transactionId;
    }

    public synchronized void add(SectionDelta delta) {
        deltas.add(Objects.requireNonNull(delta));
    }

    public synchronized CommitResult commit(WorldMutationSink sink, LightingMode lightingMode) {
        Set<SectionKey> dirty = new LinkedHashSet<>();

        // Preflight every section before mutating anything. Server-side commits are serialized through
        // ServerEditScheduler, so a failed revision check cannot leave a partially applied transaction.
        for (SectionDelta delta : deltas) {
            long actual = sink.currentRevision(delta.key());
            if (actual != delta.baseRevision()) {
                return new CommitResult(false, transactionId, Set.of(), delta.key(), actual);
            }
            dirty.add(delta.key());
        }

        for (SectionDelta delta : deltas) {
            sink.applyWithoutLighting(delta);
        }
        sink.markForSave(dirty);
        if (lightingMode == LightingMode.DEFER_AND_RECONCILE) {
            sink.reconcileLighting(dirty);
        }
        return new CommitResult(true, transactionId, dirty, null, -1L);
    }

    public enum LightingMode {
        DEFER_AND_RECONCILE,
        MANUAL_RECONCILE
    }

    public record CommitResult(
            boolean committed,
            long transactionId,
            Set<SectionKey> dirtySections,
            SectionKey conflict,
            long actualRevision) {
        public CommitResult {
            dirtySections = Set.copyOf(dirtySections);
        }
    }
}
