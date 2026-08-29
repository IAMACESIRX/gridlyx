package com.example.examplemod.advanced.runtime;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.advanced.scripting.PolyglotScriptHost;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/** Boots the public Gridelyx hotload runtime when the advanced JAR and explicit enable flag are present. */
public final class GridelyxRuntimeBootstrap {
    private static ReloadOrchestrator orchestrator;
    private static NeoForgeReloadTargetBindings targets;
    private static Thread shutdownHook;

    private GridelyxRuntimeBootstrap() {}

    public static synchronized void startIfEnabled() {
        if (orchestrator != null || !enabled()) {
            return;
        }

        ExternalHotloadCore nextCore = null;
        NeoForgeReloadTargetBindings nextTargets = null;
        ReloadOrchestrator nextOrchestrator = null;
        try {
            Path workspace = Path.of(System.getProperty(
                            "gridelyx.hotload.root",
                            "run/gridelyx-hotload"))
                    .toAbsolutePath()
                    .normalize();
            Path classes = workspace.resolve("classes");
            Files.createDirectories(classes);

            ClassLoader loader = GridelyxRuntimeBootstrap.class.getClassLoader();
            nextCore = new ExternalHotloadCore();
            nextCore.addRoot(workspace);
            nextTargets = new NeoForgeReloadTargetBindings(loader);
            nextOrchestrator = new ReloadOrchestrator(
                    nextCore,
                    new ClassHotSwapService(),
                    new PolyglotScriptHost(),
                    nextTargets.bindings(),
                    workspace,
                    classes,
                    loader);
            nextOrchestrator.addResultListener(GridelyxRuntimeBootstrap::logResult);
            nextOrchestrator.start();
            installShutdownHook();

            targets = nextTargets;
            orchestrator = nextOrchestrator;
            ExampleMod.LOGGER.info(
                    "Gridelyx hotload runtime started at {} (runtime epoch driver: {})",
                    workspace,
                    nextTargets.hasRuntimeEpochDriver() ? "available" : "not installed");
        } catch (Exception | LinkageError failure) {
            ExampleMod.LOGGER.error("Gridelyx hotload runtime failed to start", failure);
            closeStartupFailure(nextOrchestrator, nextCore, nextTargets, failure);
        }
    }

    public static synchronized boolean isRunning() {
        return orchestrator != null;
    }

    public static synchronized void stop() {
        ReloadOrchestrator currentOrchestrator = orchestrator;
        NeoForgeReloadTargetBindings currentTargets = targets;
        orchestrator = null;
        targets = null;

        Throwable failure = null;
        if (currentOrchestrator != null) {
            try {
                currentOrchestrator.close();
            } catch (Exception | LinkageError closeFailure) {
                failure = closeFailure;
            }
        }
        if (currentTargets != null) {
            try {
                currentTargets.close();
            } catch (Exception | LinkageError closeFailure) {
                if (failure == null) {
                    failure = closeFailure;
                } else {
                    failure.addSuppressed(closeFailure);
                }
            }
        }
        if (failure != null) {
            ExampleMod.LOGGER.error("Gridelyx hotload runtime did not shut down cleanly", failure);
        }
    }

    private static void closeStartupFailure(
            ReloadOrchestrator nextOrchestrator,
            ExternalHotloadCore nextCore,
            NeoForgeReloadTargetBindings nextTargets,
            Throwable primary) {
        try {
            if (nextOrchestrator != null) {
                nextOrchestrator.close();
            } else if (nextCore != null) {
                nextCore.close();
            }
        } catch (Exception | LinkageError closeFailure) {
            primary.addSuppressed(closeFailure);
        }
        if (nextTargets != null) {
            try {
                nextTargets.close();
            } catch (Exception | LinkageError closeFailure) {
                primary.addSuppressed(closeFailure);
            }
        }
    }

    private static boolean enabled() {
        String canonical = System.getProperty("gridelyx.hotload.enabled");
        if (canonical != null) {
            return Boolean.parseBoolean(canonical);
        }
        return Boolean.parseBoolean(System.getProperty("madk.devHotload", "false"));
    }

    private static void installShutdownHook() {
        if (shutdownHook != null) {
            return;
        }
        shutdownHook = new Thread(
                GridelyxRuntimeBootstrap::stop,
                "gridelyx-hotload-shutdown");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private static void logResult(ReloadOrchestrator.ReloadResult result) {
        String strategy = result.strategy() == null
                ? "none"
                : result.strategy().name().toLowerCase(Locale.ROOT);
        if (result.status() == ReloadOrchestrator.Status.FAILED
                || result.status() == ReloadOrchestrator.Status.REJECTED) {
            ExampleMod.LOGGER.warn(
                    "Gridelyx reload {}: kind={}, strategy={}, target={}, path={}, message={}, error={}",
                    result.status(),
                    result.kind(),
                    strategy,
                    result.target(),
                    result.path(),
                    result.message(),
                    result.error());
            return;
        }
        ExampleMod.LOGGER.info(
                "Gridelyx reload {}: epoch={}, kind={}, strategy={}, target={}, path={}, message={}",
                result.status(),
                result.activationEpoch(),
                result.kind(),
                strategy,
                result.target(),
                result.path(),
                result.message());
    }
}
