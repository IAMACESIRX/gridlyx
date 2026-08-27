package com.example.examplemod.advanced.worldedit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public final class AsyncSubChunkBlitter {
    private final Executor workerExecutor;
    private final ServerEditScheduler serverScheduler;
    private final AtomicLong transactionIds = new AtomicLong();

    public AsyncSubChunkBlitter(Executor workerExecutor, ServerEditScheduler serverScheduler) {
        this.workerExecutor = Objects.requireNonNull(workerExecutor);
        this.serverScheduler = Objects.requireNonNull(serverScheduler);
    }

    public CompletableFuture<BulkEditTransaction> prepare(List<SectionPlan> plans) {
        long transactionId = transactionIds.incrementAndGet();
        List<CompletableFuture<SectionDelta>> futures = new ArrayList<>();
        for (SectionPlan plan : plans) {
            futures.add(CompletableFuture.supplyAsync(() -> buildDelta(plan), workerExecutor));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenApply(ignored -> {
            BulkEditTransaction transaction = new BulkEditTransaction(transactionId);
            for (CompletableFuture<SectionDelta> future : futures) {
                transaction.add(future.join());
            }
            return transaction;
        });
    }

    public void commitOnServerThread(
            BulkEditTransaction transaction,
            WorldMutationSink sink,
            BulkEditTransaction.LightingMode lightingMode) {
        serverScheduler.submit(() -> transaction.commit(sink, lightingMode));
    }

    private static SectionDelta buildDelta(SectionPlan plan) {
        int[] before = plan.before().copyArray();
        int[] after = plan.after().copyArray();
        int changes = 0;
        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                changes++;
            }
        }
        int[] indices = new int[changes];
        int[] palette = new int[changes];
        int cursor = 0;
        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                indices[cursor] = i;
                palette[cursor] = after[i];
                cursor++;
            }
        }
        return new SectionDelta(plan.key(), plan.baseRevision(), plan.baseRevision() + 1L, indices, palette);
    }

    public record SectionPlan(SectionKey key, long baseRevision, SectionBuffer before, SectionBuffer after) {
    }
}
