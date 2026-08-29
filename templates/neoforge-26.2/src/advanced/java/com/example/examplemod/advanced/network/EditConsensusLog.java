package com.example.examplemod.advanced.network;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionKey.java
import com.example.examplemod.advanced.worldedit.SectionKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class EditConsensusLog {
    private final Map<SectionKey, AtomicLong> revisions = new ConcurrentHashMap<>();

    public Reservation reserve(SectionKey key, long expectedBaseRevision) {
        AtomicLong revision = revisions.computeIfAbsent(key, ignored -> new AtomicLong(0L));
        long next = Math.addExact(expectedBaseRevision, 1L);
        boolean accepted = revision.compareAndSet(expectedBaseRevision, next);
        return new Reservation(accepted, accepted ? next : revision.get());
    }

    public void initialise(SectionKey key, long revision) {
        if (revision < 0) {
            throw new IllegalArgumentException("revision must be non-negative");
        }
        revisions.compute(key, (ignored, current) -> {
            if (current == null) {
                return new AtomicLong(revision);
            }
            current.accumulateAndGet(revision, Math::max);
            return current;
        });
    }

    public long revision(SectionKey key) {
        AtomicLong value = revisions.get(key);
        return value == null ? 0L : value.get();
    }

    public record Reservation(boolean accepted, long revision) {
    }
}
