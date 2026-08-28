package com.example.examplemod.advanced.polyloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gridelyx-owned H3 module runtime.
 *
 * <p>Only JARs that expose {@link GridelyxHotloadModule} through {@link ServiceLoader} are eligible for an H3
 * classloader epoch. Artifacts that require loader lifecycle replay, early transforms, native replacement, or
 * another structural mechanism are rejected with an explicit escalation strategy instead of being treated as
 * successfully hotloaded.
 */
public final class VersionedModuleRuntime implements AutoCloseable {
    private final ModArtifactAnalyzer analyzer;
    private final PublicHotloadPlanner planner;
    private final ClassLoader parent;
    private final AtomicLong epochs = new AtomicLong();
    private final Map<Path, ActiveModule> activeByPath = new LinkedHashMap<>();

    public VersionedModuleRuntime(ClassLoader parent) {
        this(new ModArtifactAnalyzer(), new PublicHotloadPlanner(), parent);
    }

    public VersionedModuleRuntime(
            ModArtifactAnalyzer analyzer,
            PublicHotloadPlanner planner,
            ClassLoader parent) {
        this.analyzer = Objects.requireNonNull(analyzer, "analyzer");
        this.planner = Objects.requireNonNull(planner, "planner");
        this.parent = Objects.requireNonNull(parent, "parent");
    }

    public synchronized ActivationStrategy reload(Path jar) throws Exception {
        Path key = logicalKey(jar);
        Path source = canonicalExistingJar(jar);
        ModArtifactProfile profile = analyzer.analyze(source);
        PublicHotloadPlanner.ActivationPlan plan = planner.plan(profile);
        if (plan.strategy() != ActivationStrategy.CLASSLOADER_EPOCH) {
            throw new StructuralReloadRequiredException(plan.strategy(), plan.rationale());
        }

        URLClassLoader loader = new URLClassLoader(new URL[] {source.toUri().toURL()}, parent);
        GridelyxHotloadModule candidate;
        try {
            candidate = ServiceLoader.load(GridelyxHotloadModule.class, loader)
                    .findFirst()
                    .orElseThrow(() -> new StructuralReloadRequiredException(
                            ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                            "JAR does not expose the GridelyxHotloadModule service contract"));
        } catch (Exception | LinkageError failure) {
            closeQuietly(loader, failure);
            throw failure;
        }

        String moduleId = Objects.requireNonNull(candidate.moduleId(), "moduleId").trim();
        if (moduleId.isEmpty()) {
            closeQuietly(candidate, null);
            closeQuietly(loader, null);
            throw new StructuralReloadRequiredException(
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "GridelyxHotloadModule returned a blank moduleId");
        }

        long epoch = epochs.incrementAndGet();
        ModuleScope candidateScope = new ModuleScope(moduleId, epoch);
        candidateScope.own("classloader", source.toString(), loader);
        candidateScope.own("module", moduleId, candidate);

        try {
            candidate.prepare(candidateScope);
            candidate.healthCheck();
        } catch (Exception | LinkageError failure) {
            closeScope(candidateScope, failure);
            throw failure;
        }

        ActiveModule previous = activeByPath.get(key);
        if (previous != null) {
            if (!previous.moduleId().equals(moduleId)) {
                closeScope(candidateScope, null);
                throw new StructuralReloadRequiredException(
                        ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                        "Replacement moduleId changed from "
                                + previous.moduleId()
                                + " to "
                                + moduleId);
            }
            try {
                previous.scope().close();
            } catch (RuntimeException | LinkageError retirementFailure) {
                closeScope(candidateScope, retirementFailure);
                throw new StructuralReloadRequiredException(
                        ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                        "Previous module epoch could not be retired cleanly",
                        retirementFailure);
            }
        }

        try {
            candidate.activate();
        } catch (Exception | LinkageError activationFailure) {
            closeScope(candidateScope, activationFailure);
            activeByPath.remove(key);
            throw new StructuralReloadRequiredException(
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "Prepared module failed during authority switch; process-level recovery is required",
                    activationFailure);
        }

        activeByPath.put(key, new ActiveModule(moduleId, candidateScope));
        return ActivationStrategy.CLASSLOADER_EPOCH;
    }

    public synchronized ActivationStrategy remove(Path jar) throws Exception {
        Path key = logicalKey(jar);
        ActiveModule previous = activeByPath.remove(key);
        if (previous == null) {
            return ActivationStrategy.CLASSLOADER_EPOCH;
        }
        try {
            previous.scope().close();
            return ActivationStrategy.CLASSLOADER_EPOCH;
        } catch (RuntimeException | LinkageError failure) {
            throw new StructuralReloadRequiredException(
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "Removed module epoch could not be retired cleanly",
                    failure);
        }
    }

    public synchronized int activeModuleCount() {
        return activeByPath.size();
    }

    private static Path logicalKey(Path jar) {
        return Objects.requireNonNull(jar, "jar").toAbsolutePath().normalize();
    }

    private static Path canonicalExistingJar(Path jar) throws IOException {
        Path canonical = logicalKey(jar).toRealPath();
        String fileName = canonical.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!Files.isRegularFile(canonical) || !fileName.endsWith(".jar")) {
            throw new IOException("Expected an existing module JAR: " + canonical);
        }
        return canonical;
    }

    private static void closeScope(ModuleScope scope, Throwable primary) {
        try {
            scope.close();
        } catch (RuntimeException | LinkageError closeFailure) {
            if (primary != null) {
                primary.addSuppressed(closeFailure);
            } else {
                throw closeFailure;
            }
        }
    }

    private static void closeQuietly(AutoCloseable closeable, Throwable primary) {
        try {
            closeable.close();
        } catch (Exception | LinkageError closeFailure) {
            if (primary != null) {
                primary.addSuppressed(closeFailure);
            }
        }
    }

    @Override
    public synchronized void close() throws Exception {
        List<Throwable> failures = new ArrayList<>();
        for (ActiveModule active : List.copyOf(activeByPath.values())) {
            try {
                active.scope().close();
            } catch (RuntimeException | LinkageError failure) {
                failures.add(failure);
            }
        }
        activeByPath.clear();
        if (!failures.isEmpty()) {
            Exception aggregate = new Exception(
                    "Failed to retire " + failures.size() + " Gridelyx module epoch(s)");
            failures.forEach(aggregate::addSuppressed);
            throw aggregate;
        }
    }

    private record ActiveModule(String moduleId, ModuleScope scope) {}

    public static final class StructuralReloadRequiredException extends Exception {
        private final ActivationStrategy requiredStrategy;

        public StructuralReloadRequiredException(ActivationStrategy requiredStrategy, String message) {
            super(message);
            this.requiredStrategy = Objects.requireNonNull(requiredStrategy, "requiredStrategy");
        }

        public StructuralReloadRequiredException(
                ActivationStrategy requiredStrategy,
                String message,
                Throwable cause) {
            super(message, cause);
            this.requiredStrategy = Objects.requireNonNull(requiredStrategy, "requiredStrategy");
        }

        public ActivationStrategy requiredStrategy() {
            return requiredStrategy;
        }
    }
}
