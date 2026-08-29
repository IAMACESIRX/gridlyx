package com.example.examplemod.advanced.sandbox;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionDelta.java
import com.example.examplemod.advanced.worldedit.SectionDelta;
import java.util.List;

public record PreparedWorldTransaction(long transactionId, List<MutationPair> mutations) {
    public PreparedWorldTransaction {
        if (transactionId < 0) {
            throw new IllegalArgumentException("transactionId must be non-negative");
        }
        mutations = List.copyOf(mutations);
        for (MutationPair mutation : mutations) {
            if (!mutation.forward().key().equals(mutation.rollback().key())) {
                throw new IllegalArgumentException("forward and rollback deltas must target the same section");
            }
            if (mutation.rollback().baseRevision() != mutation.forward().newRevision()) {
                throw new IllegalArgumentException("rollback must start at the forward delta new revision");
            }
        }
    }

    public record MutationPair(SectionDelta forward, SectionDelta rollback) {}
}
