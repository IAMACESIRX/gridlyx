package com.example.examplemod.advanced.sandbox;

import com.example.examplemod.advanced.worldedit.SectionDelta;
import com.example.examplemod.advanced.worldedit.SectionKey;
import com.example.examplemod.advanced.worldedit.WorldMutationSink;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TransactionalWorldSandbox {
    public TransactionResult commit(
            WorldMutationSink sink,
            PreparedWorldTransaction transaction,
            boolean reconcileLighting) {
        for (PreparedWorldTransaction.MutationPair mutation : transaction.mutations()) {
            SectionDelta forward = mutation.forward();
            long actual = sink.currentRevision(forward.key());
            if (actual != forward.baseRevision()) {
                return new TransactionResult(
                        State.CONFLICT,
                        transaction.transactionId(),
                        Set.of(),
                        forward.key(),
                        actual,
                        null);
            }
        }

        List<PreparedWorldTransaction.MutationPair> applied = new ArrayList<>();
        Set<SectionKey> dirty = new LinkedHashSet<>();
        try {
            for (PreparedWorldTransaction.MutationPair mutation : transaction.mutations()) {
                sink.applyWithoutLighting(mutation.forward());
                applied.add(mutation);
                dirty.add(mutation.forward().key());
            }
            sink.markForSave(dirty);
            if (reconcileLighting) {
                sink.reconcileLighting(dirty);
            }
            return new TransactionResult(
                    State.COMMITTED,
                    transaction.transactionId(),
                    dirty,
                    null,
                    -1L,
                    null);
        } catch (VirtualMachineError | ThreadDeath critical) {
            throw critical;
        } catch (Throwable failure) {
            return rollback(sink, transaction.transactionId(), applied, reconcileLighting, failure);
        }
    }

    private static TransactionResult rollback(
            WorldMutationSink sink,
            long transactionId,
            List<PreparedWorldTransaction.MutationPair> applied,
            boolean reconcileLighting,
            Throwable originalFailure) {
        Set<SectionKey> rolledBack = new LinkedHashSet<>();
        boolean complete = true;
        for (int index = applied.size() - 1; index >= 0; index--) {
            SectionDelta rollback = applied.get(index).rollback();
            if (sink.currentRevision(rollback.key()) != rollback.baseRevision()) {
                complete = false;
                continue;
            }
            try {
                sink.applyWithoutLighting(rollback);
                rolledBack.add(rollback.key());
            } catch (VirtualMachineError | ThreadDeath critical) {
                throw critical;
            } catch (Throwable rollbackFailure) {
                complete = false;
            }
        }
        try {
            sink.markForSave(rolledBack);
            if (reconcileLighting && !rolledBack.isEmpty()) {
                sink.reconcileLighting(rolledBack);
            }
        } catch (VirtualMachineError | ThreadDeath critical) {
            throw critical;
        } catch (Throwable reconciliationFailure) {
            complete = false;
        }
        return new TransactionResult(
                complete ? State.ROLLED_BACK : State.ROLLBACK_INCOMPLETE,
                transactionId,
                rolledBack,
                null,
                -1L,
                originalFailure.toString());
    }

    public enum State {
        COMMITTED,
        CONFLICT,
        ROLLED_BACK,
        ROLLBACK_INCOMPLETE
    }

    public record TransactionResult(
            State state,
            long transactionId,
            Set<SectionKey> affectedSections,
            SectionKey conflict,
            long actualRevision,
            String error) {
        public TransactionResult {
            affectedSections = Set.copyOf(affectedSections);
        }
    }
}
