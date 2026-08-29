package com.example.examplemod.advanced.sandbox;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionDelta.java
import com.example.examplemod.advanced.worldedit.SectionDelta;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionKey.java
import com.example.examplemod.advanced.worldedit.SectionKey;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/WorldMutationSink.java
import com.example.examplemod.advanced.worldedit.WorldMutationSink;
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

        Set<SectionKey> dirty = new LinkedHashSet<>();
        try {
            for (PreparedWorldTransaction.MutationPair mutation : transaction.mutations()) {
                sink.applyWithoutLighting(mutation.forward());
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
        } catch (RuntimeException | LinkageError failure) {
            return rollback(
                    sink,
                    transaction.transactionId(),
                    transaction.mutations(),
                    reconcileLighting,
                    failure);
        }
    }

    private static TransactionResult rollback(
            WorldMutationSink sink,
            long transactionId,
            List<PreparedWorldTransaction.MutationPair> candidates,
            boolean reconcileLighting,
            Throwable originalFailure) {
        Set<SectionKey> rolledBack = new LinkedHashSet<>();
        boolean complete = true;
        for (int index = candidates.size() - 1; index >= 0; index--) {
            PreparedWorldTransaction.MutationPair mutation = candidates.get(index);
            SectionDelta forward = mutation.forward();
            SectionDelta rollback = mutation.rollback();
            long actual = sink.currentRevision(rollback.key());
            if (actual == forward.baseRevision()) {
                continue;
            }
            if (actual != rollback.baseRevision()) {
                complete = false;
                continue;
            }
            try {
                sink.applyWithoutLighting(rollback);
                rolledBack.add(rollback.key());
            } catch (RuntimeException | LinkageError rollbackFailure) {
                complete = false;
            }
        }
        try {
            sink.markForSave(rolledBack);
            if (reconcileLighting && !rolledBack.isEmpty()) {
                sink.reconcileLighting(rolledBack);
            }
        } catch (RuntimeException | LinkageError reconciliationFailure) {
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
