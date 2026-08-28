package com.example.examplemod.advanced.runtime;

import com.example.examplemod.advanced.polyloader.ActivationStrategy;
import com.example.examplemod.advanced.polyloader.VersionedModuleRuntime;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

/** Concrete NeoForge/Minecraft target adapter for the neutral reload orchestrator. */
public final class NeoForgeReloadTargetBindings implements AutoCloseable {
    private final ClassLoader applicationClassLoader;
    private final VersionedModuleRuntime modules;
    private final RuntimeEpochDriver epochDriver;
    private final ReloadTargetBindings bindings;

    public NeoForgeReloadTargetBindings(ClassLoader applicationClassLoader) {
        this(
                applicationClassLoader,
                new VersionedModuleRuntime(applicationClassLoader),
                RuntimeEpochDriver.discover(applicationClassLoader).orElse(null));
    }

    public NeoForgeReloadTargetBindings(
            ClassLoader applicationClassLoader,
            VersionedModuleRuntime modules,
            RuntimeEpochDriver epochDriver) {
        this.applicationClassLoader = Objects.requireNonNull(applicationClassLoader, "applicationClassLoader");
        this.modules = Objects.requireNonNull(modules, "modules");
        this.epochDriver = epochDriver;

        ReloadTargetBindings.Builder builder = ReloadTargetBindings.builder()
                .dataReload(this::reloadServerResources)
                .assetReload(this::reloadClientResources)
                .moduleReload(this::reloadModule)
                .otherReload(this::reloadOther);
        if (epochDriver != null) {
            builder.runtimeEpochHandoff(epochDriver::handoff);
        }
        bindings = builder.build();
    }

    public ReloadTargetBindings bindings() {
        return bindings;
    }

    public boolean hasRuntimeEpochDriver() {
        return epochDriver != null;
    }

    private void reloadServerResources(Path changed, ReloadTargetBindings.ChangeKind change) throws Exception {
        Class<?> hooks = Class.forName(
                "net.neoforged.neoforge.server.ServerLifecycleHooks", false, applicationClassLoader);
        Object server = hooks.getMethod("getCurrentServer").invoke(null);
        if (server == null) {
            throw new IllegalStateException("No active NeoForge MinecraftServer is available for data reload");
        }

        Object repository = server.getClass().getMethod("getPackRepository").invoke(server);
        Object selected = repository.getClass().getMethod("getSelectedPacks").invoke(repository);
        if (!(selected instanceof Collection<?> packs)) {
            throw new IllegalStateException("NeoForge pack repository returned a non-collection selected-pack set");
        }

        List<String> selectedIds = new ArrayList<>(packs.size());
        for (Object pack : packs) {
            Object id = pack.getClass().getMethod("getId").invoke(pack);
            selectedIds.add(Objects.toString(id));
        }

        Method reload = findSingleArgumentMethod(server.getClass(), "reloadResources");
        await(reload.invoke(server, selectedIds));
    }

    private void reloadClientResources(Path changed, ReloadTargetBindings.ChangeKind change) throws Exception {
        Class<?> minecraft = Class.forName("net.minecraft.client.Minecraft", false, applicationClassLoader);
        Object client = minecraft.getMethod("getInstance").invoke(null);
        if (client == null) {
            throw new IllegalStateException("Minecraft client is not available for asset reload");
        }

        Method reload = findZeroArgumentMethod(client.getClass(), "reloadResourcePacks");
        await(reload.invoke(client));
    }

    private ActivationStrategy reloadModule(Path path, ReloadTargetBindings.ChangeKind change) throws Exception {
        if (change == ReloadTargetBindings.ChangeKind.DELETE) {
            return modules.remove(path);
        }
        return modules.reload(path);
    }

    private void reloadOther(Path path, ReloadTargetBindings.ChangeKind change) throws Exception {
        if (containsSegment(path, "assets")) {
            reloadClientResources(path, change);
            return;
        }
        if (containsSegment(path, "data")) {
            reloadServerResources(path, change);
            return;
        }
        throw new UnsupportedOperationException("No NeoForge target binding accepts " + path);
    }

    private static boolean containsSegment(Path path, String expected) {
        for (Path segment : path) {
            if (segment.toString().toLowerCase(Locale.ROOT).equals(expected)) {
                return true;
            }
        }
        return false;
    }

    private static Method findZeroArgumentMethod(Class<?> type, String name) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 0) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "." + name + "()");
    }

    private static Method findSingleArgumentMethod(Class<?> type, String name) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "." + name + "(<one argument>)");
    }

    private static void await(Object result) throws Exception {
        if (result == null) {
            return;
        }
        if (result instanceof CompletionStage<?> stage) {
            try {
                stage.toCompletableFuture().join();
                return;
            } catch (RuntimeException failure) {
                Throwable cause = failure.getCause();
                if (cause instanceof Exception exception) {
                    throw exception;
                }
                throw failure;
            }
        }
        throw new IllegalStateException("Reload API returned unsupported async result type: " + result.getClass());
    }

    @Override
    public void close() throws Exception {
        modules.close();
    }

    static Exception unwrapInvocationFailure(InvocationTargetException failure) {
        Throwable cause = failure.getCause();
        if (cause instanceof Exception exception) {
            return exception;
        }
        return failure;
    }
}
