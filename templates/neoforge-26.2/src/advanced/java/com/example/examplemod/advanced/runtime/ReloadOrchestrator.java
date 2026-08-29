package com.example.examplemod.advanced.runtime;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/ActivationStrategy.java
import com.example.examplemod.advanced.polyloader.ActivationStrategy;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/scripting/PolyglotScriptHost.java
import com.example.examplemod.advanced.scripting.PolyglotScriptHost;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Serializes public hotload changes and selects the smallest safe activation mechanism.
 *
 * <p>The orchestrator is intentionally target-neutral. Minecraft/loader-specific operations are supplied by
 * {@link ReloadTargetBindings}; project-owned JVM and polyglot mechanisms are invoked directly.
 */
public final class ReloadOrchestrator implements AutoCloseable {
    private static final int DEFAULT_HISTORY_LIMIT = 256;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;

    private final ExternalHotloadCore hotloadCore;
    private final ClassHotSwapService classHotSwapService;
    private final PolyglotScriptHost scriptHost;
    private final ReloadTargetBindings bindings;
    private final Path workspaceRoot;
    private final Path classesRoot;
    private final ClassLoader applicationClassLoader;
    private final ExecutorService activationExecutor = Executors.newSingleThreadExecutor();
    private final AtomicLong activationEpoch = new AtomicLong();
    private final List<Consumer<ReloadResult>> listeners = new CopyOnWriteArrayList<>();
    private final Deque<ReloadResult> history = new ArrayDeque<>();
    private final int historyLimit;

    public ReloadOrchestrator(
            ExternalHotloadCore hotloadCore,
            ClassHotSwapService classHotSwapService,
            PolyglotScriptHost scriptHost,
            ReloadTargetBindings bindings,
            Path workspaceRoot,
            Path classesRoot,
            ClassLoader applicationClassLoader) {
        this(
                hotloadCore,
                classHotSwapService,
                scriptHost,
                bindings,
                workspaceRoot,
                classesRoot,
                applicationClassLoader,
                DEFAULT_HISTORY_LIMIT);
    }

    public ReloadOrchestrator(
            ExternalHotloadCore hotloadCore,
            ClassHotSwapService classHotSwapService,
            PolyglotScriptHost scriptHost,
            ReloadTargetBindings bindings,
            Path workspaceRoot,
            Path classesRoot,
            ClassLoader applicationClassLoader,
            int historyLimit) {
        this.hotloadCore = Objects.requireNonNull(hotloadCore);
        this.classHotSwapService = Objects.requireNonNull(classHotSwapService);
        this.scriptHost = Objects.requireNonNull(scriptHost);
        this.bindings = Objects.requireNonNull(bindings);
        this.workspaceRoot = canonical(Objects.requireNonNull(workspaceRoot));
        this.classesRoot = canonical(Objects.requireNonNull(classesRoot));
        this.applicationClassLoader = Objects.requireNonNull(applicationClassLoader);
        if (historyLimit < 1) {
            throw new IllegalArgumentException("historyLimit must be positive");
        }
        this.historyLimit = historyLimit;
        hotloadCore.addListener(this::accept);
    }

    public void start() {
        hotloadCore.start();
    }

    public void addResultListener(Consumer<ReloadResult> listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public CompletableFuture<ReloadResult> submit(ExternalHotloadCore.ReloadEvent event) {
        Objects.requireNonNull(event);
        return CompletableFuture.supplyAsync(() -> process(event), activationExecutor)
                .thenApply(result -> {
                    publish(result);
                    return result;
                });
    }

    /** Visible for deterministic validation and non-watcher callers. */
    public ReloadResult reloadNow(ExternalHotloadCore.ReloadEvent event) {
        ReloadResult result = process(Objects.requireNonNull(event));
        publish(result);
        return result;
    }

    public synchronized List<ReloadResult> history() {
        return List.copyOf(history);
    }

    private void accept(ExternalHotloadCore.ReloadEvent event) {
        submit(event);
    }

    private ReloadResult process(ExternalHotloadCore.ReloadEvent event) {
        String detail = Objects.toString(event.detail(), "");
        if (event.kind() == ExternalHotloadCore.ReloadKind.REJECTED) {
            return result(event, Status.REJECTED, null, "watcher", detail, null);
        }
        if (event.kind() == ExternalHotloadCore.ReloadKind.ERROR) {
            return result(event, Status.FAILED, null, "watcher", detail, detail);
        }
        if (event.path() == null) {
            return result(
                    event,
                    Status.REJECTED,
                    null,
                    "orchestrator",
                    "Reload event has no path",
                    null);
        }

        Path path = event.path().toAbsolutePath().normalize();
        if (!path.startsWith(workspaceRoot)) {
            return result(
                    event,
                    Status.REJECTED,
                    null,
                    "orchestrator",
                    "Path is outside the approved workspace",
                    null);
        }

        ReloadTargetBindings.ChangeKind change = changeKind(event);
        try {
            return switch (event.kind()) {
                case DATA -> reloadData(event, path, change);
                case ASSET -> reloadAsset(event, path, change);
                case SCRIPT -> reloadScript(event, path, change);
                case JAVA_BYTECODE -> reloadJava(event, path, change);
                case OTHER -> reloadOther(event, path, change);
                case REJECTED, ERROR -> throw new IllegalStateException("handled before dispatch");
            };
        } catch (Exception | LinkageError failure) {
            return escalate(event, "Activation failed: " + failure, failure);
        }
    }

    private ReloadResult reloadData(
            ExternalHotloadCore.ReloadEvent event,
            Path path,
            ReloadTargetBindings.ChangeKind change) throws Exception {
        if (!bindings.hasDataReload()) {
            return escalate(event, "No transactional data target binding is installed", null);
        }
        bindings.reloadData(path, change);
        return applied(
                event,
                ActivationStrategy.TRANSACTIONAL_RELOAD,
                "data",
                "Transactional data reload applied");
    }

    private ReloadResult reloadAsset(
            ExternalHotloadCore.ReloadEvent event,
            Path path,
            ReloadTargetBindings.ChangeKind change) throws Exception {
        if (!bindings.hasAssetReload()) {
            return escalate(event, "No asset target binding is installed", null);
        }
        bindings.reloadAsset(path, change);
        return applied(
                event,
                ActivationStrategy.ASSET_SWAP,
                "asset",
                "Asset revision activated");
    }

    private ReloadResult reloadScript(
            ExternalHotloadCore.ReloadEvent event,
            Path path,
            ReloadTargetBindings.ChangeKind change) throws Exception {
        String moduleId = moduleId(path);
        if (change == ReloadTargetBindings.ChangeKind.DELETE) {
            scriptHost.unload(moduleId);
            return applied(
                    event,
                    ActivationStrategy.SCOPED_BEHAVIOR_EPOCH,
                    "polyglot-script",
                    "Script module unloaded");
        }
        String language = scriptLanguage(path);
        if (language == null) {
            return escalate(event, "No embedded language binding for " + path.getFileName(), null);
        }
        scriptHost.reload(moduleId, language, path);
        return applied(
                event,
                ActivationStrategy.SCOPED_BEHAVIOR_EPOCH,
                "polyglot-script",
                "Script epoch activated");
    }

    private ReloadResult reloadJava(
            ExternalHotloadCore.ReloadEvent event,
            Path path,
            ReloadTargetBindings.ChangeKind change) throws Exception {
        String file = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (file.endsWith(".class") && change != ReloadTargetBindings.ChangeKind.DELETE) {
            if (!path.startsWith(classesRoot)) {
                return escalate(
                        event,
                        "Class bytecode is outside the configured classes root",
                        null);
            }
            ClassHotSwapService.RedefinitionResult redefinition =
                    classHotSwapService.redefine(classesRoot, path, applicationClassLoader);
            if (redefinition.success()) {
                return applied(
                        event,
                        ActivationStrategy.IN_PLACE_REDEFINE,
                        "instrumentation",
                        "Redefined " + redefinition.className());
            }
            return escalate(
                    event,
                    "Class redefine could not absorb change: " + redefinition.error(),
                    null);
        }

        if (file.endsWith(".jar") && bindings.hasModuleReload()) {
            ActivationStrategy strategy = bindings.reloadModule(path, change);
            return applied(
                    event,
                    strategy,
                    "module",
                    "Versioned module activation applied");
        }
        return escalate(event, "Java change requires classloader/runtime replacement", null);
    }

    private ReloadResult reloadOther(
            ExternalHotloadCore.ReloadEvent event,
            Path path,
            ReloadTargetBindings.ChangeKind change) throws Exception {
        if (!bindings.hasOtherReload()) {
            return escalate(event, "No target binding accepts this file type", null);
        }
        bindings.reloadOther(path, change);
        return applied(
                event,
                ActivationStrategy.TRANSACTIONAL_RELOAD,
                "other",
                "Target-specific reload applied");
    }

    private ReloadResult escalate(
            ExternalHotloadCore.ReloadEvent event,
            String reason,
            Throwable originalFailure) {
        if (!bindings.hasEpochHandoff()) {
            return result(
                    event,
                    Status.FAILED,
                    null,
                    "orchestrator",
                    reason + "; runtime epoch handoff is not configured",
                    originalFailure == null ? null : originalFailure.toString());
        }
        try {
            bindings.runtimeEpochHandoff(event, reason);
            return result(
                    event,
                    Status.ESCALATED,
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "runtime-epoch",
                    reason,
                    originalFailure == null ? null : originalFailure.toString());
        } catch (Exception | LinkageError handoffFailure) {
            return result(
                    event,
                    Status.FAILED,
                    ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "runtime-epoch",
                    reason + "; epoch handoff failed",
                    handoffFailure.toString());
        }
    }

    private ReloadResult applied(
            ExternalHotloadCore.ReloadEvent event,
            ActivationStrategy strategy,
            String target,
            String message) {
        return result(event, Status.APPLIED, strategy, target, message, null);
    }

    private ReloadResult result(
            ExternalHotloadCore.ReloadEvent event,
            Status status,
            ActivationStrategy strategy,
            String target,
            String message,
            String error) {
        long epoch = status == Status.APPLIED || status == Status.ESCALATED
                ? activationEpoch.incrementAndGet()
                : activationEpoch.get();
        return new ReloadResult(
                epoch,
                Instant.now(),
                event.path(),
                event.kind(),
                changeKind(event),
                status,
                strategy,
                target,
                message,
                error);
    }

    private void publish(ReloadResult result) {
        synchronized (this) {
            history.addLast(result);
            while (history.size() > historyLimit) {
                history.removeFirst();
            }
        }
        for (Consumer<ReloadResult> listener : listeners) {
            listener.accept(result);
        }
    }

    private ReloadTargetBindings.ChangeKind changeKind(ExternalHotloadCore.ReloadEvent event) {
        String detail = Objects.toString(event.detail(), "");
        if (detail.contains("ENTRY_CREATE")) {
            return ReloadTargetBindings.ChangeKind.CREATE;
        }
        if (detail.contains("ENTRY_MODIFY")) {
            return ReloadTargetBindings.ChangeKind.MODIFY;
        }
        if (detail.contains("ENTRY_DELETE")) {
            return ReloadTargetBindings.ChangeKind.DELETE;
        }
        return ReloadTargetBindings.ChangeKind.UNKNOWN;
    }

    private String moduleId(Path path) {
        return workspaceRoot.relativize(path).toString().replace('\\', '/');
    }

    private static String scriptLanguage(Path path) {
        String file = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (file.endsWith(".js") || file.endsWith(".mjs")) {
            return "js";
        }
        if (file.endsWith(".py")) {
            return "python";
        }
        return null;
    }

    private static Path canonical(Path path) {
        try {
            return path.toRealPath();
        } catch (IOException ignored) {
            return path.toAbsolutePath().normalize();
        }
    }

    @Override
    public void close() throws Exception {
        Exception failure = null;
        try {
            hotloadCore.close();
        } catch (Exception exception) {
            failure = exception;
        }

        activationExecutor.shutdown();
        try {
            if (!activationExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                activationExecutor.shutdownNow();
            }
        } catch (InterruptedException interruption) {
            activationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            if (failure == null) {
                failure = interruption;
            } else {
                failure.addSuppressed(interruption);
            }
        }

        try {
            scriptHost.close();
        } catch (RuntimeException exception) {
            if (failure == null) {
                failure = exception;
            } else {
                failure.addSuppressed(exception);
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    public enum Status {
        APPLIED,
        ESCALATED,
        REJECTED,
        FAILED
    }

    public record ReloadResult(
            long activationEpoch,
            Instant completedAt,
            Path path,
            ExternalHotloadCore.ReloadKind kind,
            ReloadTargetBindings.ChangeKind change,
            Status status,
            ActivationStrategy strategy,
            String target,
            String message,
            String error) {
        public ReloadResult {
            Objects.requireNonNull(completedAt);
            Objects.requireNonNull(kind);
            Objects.requireNonNull(change);
            Objects.requireNonNull(status);
            Objects.requireNonNull(target);
            Objects.requireNonNull(message);
        }
    }
}
