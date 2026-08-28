package com.example.examplemod.advanced.polyloader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ModuleScope implements AutoCloseable {
    private final String moduleId;
    private final long epoch;
    private final Deque<OwnedResource> resources = new ArrayDeque<>();
    private final AtomicBoolean closed = new AtomicBoolean();

    public ModuleScope(String moduleId, long epoch) {
        this.moduleId = Objects.requireNonNull(moduleId, "moduleId");
        this.epoch = epoch;
    }

    public synchronized <T extends AutoCloseable> T own(String kind, String name, T resource) {
        ensureOpen();
        Objects.requireNonNull(resource, "resource");
        resources.push(new OwnedResource(kind, name, resource));
        return resource;
    }

    public synchronized int ownedResourceCount() {
        return resources.size();
    }

    public String moduleId() {
        return moduleId;
    }

    public long epoch() {
        return epoch;
    }

    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public synchronized void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }

        List<ScopeCloseFailure> failures = new ArrayList<>();
        while (!resources.isEmpty()) {
            OwnedResource owned = resources.pop();
            try {
                owned.resource().close();
            } catch (Exception | LinkageError failure) {
                failures.add(new ScopeCloseFailure(owned.kind(), owned.name(), failure));
            }
        }
        if (!failures.isEmpty()) {
            throw new ModuleScopeCloseException(moduleId, epoch, failures);
        }
    }

    private void ensureOpen() {
        if (closed.get()) {
            throw new IllegalStateException("Module scope is already closed: " + moduleId + "@" + epoch);
        }
    }

    private record OwnedResource(String kind, String name, AutoCloseable resource) {
        private OwnedResource {
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(resource, "resource");
        }
    }

    public record ScopeCloseFailure(String kind, String name, Throwable cause) {}

    public static final class ModuleScopeCloseException extends RuntimeException {
        private final List<ScopeCloseFailure> failures;

        private ModuleScopeCloseException(String moduleId, long epoch, List<ScopeCloseFailure> failures) {
            super("Failed to retire " + failures.size() + " resource(s) from " + moduleId + "@" + epoch);
            this.failures = List.copyOf(failures);
        }

        public List<ScopeCloseFailure> failures() {
            return failures;
        }
    }
}
