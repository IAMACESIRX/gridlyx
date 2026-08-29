package com.example.examplemod.advanced.runtime;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/ActivationStrategy.java
import com.example.examplemod.advanced.polyloader.ActivationStrategy;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/VersionedModuleRuntime.java
import com.example.examplemod.advanced.polyloader.VersionedModuleRuntime;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
                "net.neoforged.neoforge.server.ServerLifecycleHooks",
                false,
                applicationClassLoader);
        Object server = hooks.getMethod("getCurrentServer").invoke(null);
        if (server == null) {
            throw new IllegalStateException("No active NeoForge MinecraftServer is available for data reload");
        }

        invokeOnOwnerExecutor(server, () -> {
            Object repository = server.getClass().getMethod("getPackRepository").invoke(server);
            Object selected = repository.getClass().getMethod("getSelectedPacks").invoke(repository);
            if (!(selected instanceof Collection<?> packs)) {
                throw new IllegalStateException(
                        "NeoForge pack repository returned a non-collection selected-pack set");
            }

            List<String> selectedIds = new ArrayList<>(packs.size());
            for (Object pack : packs) {
                Object id = pack.getClass().getMethod("getId").invoke(pack);
                selectedIds.add(Objects.toString(id));
            }

            Method reload = findCollectionArgumentMethod(server.getClass(), "reloadResources");
            return reload.invoke(server, selectedIds);
        });
    }

    private void reloadClientResources(Path changed, ReloadTargetBindings.ChangeKind change) throws Exception {
        Class<?> minecraft = Class.forName(
                "net.minecraft.client.Minecraft",
                false,
                applicationClassLoader);
        Object client = minecraft.getMethod("getInstance").invoke(null);
        if (client == null) {
            throw new IllegalStateException("Minecraft client is not available for asset reload");
        }

        invokeOnOwnerExecutor(client, () -> {
            Method reload = findZeroArgumentMethod(client.getClass(), "reloadResourcePacks");
            return reload.invoke(client);
        });
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

    private static void invokeOnOwnerExecutor(Object owner, AsyncAction action) throws Exception {
        CompletableFuture<Void> completion = new CompletableFuture<>();
        Method execute = findRunnableArgumentMethod(owner.getClass(), "execute");
        Runnable task = () -> {
            try {
                completeFromResult(action.run(), completion);
            } catch (InvocationTargetException failure) {
                completion.completeExceptionally(failure.getCause());
            } catch (Exception | LinkageError failure) {
                completion.completeExceptionally(failure);
            }
        };
        execute.invoke(owner, task);
        await(completion);
    }

    private static void completeFromResult(Object result, CompletableFuture<Void> completion) {
        if (result == null) {
            completion.complete(null);
            return;
        }
        if (result instanceof CompletionStage<?> stage) {
            stage.whenComplete((unused, failure) -> {
                if (failure == null) {
                    completion.complete(null);
                } else {
                    completion.completeExceptionally(failure);
                }
            });
            return;
        }
        completion.completeExceptionally(new IllegalStateException(
                "Reload API returned unsupported async result type: " + result.getClass()));
    }

    private static void await(CompletionStage<?> stage) throws Exception {
        try {
            stage.toCompletableFuture().join();
        } catch (RuntimeException failure) {
            Throwable cause = failure.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw failure;
        }
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

    private static Method findCollectionArgumentMethod(Class<?> type, String name)
            throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(List.class)) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "." + name + "(Collection)");
    }

    private static Method findRunnableArgumentMethod(Class<?> type, String name)
            throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name)
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(Runnable.class)) {
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "." + name + "(Runnable)");
    }

    @Override
    public void close() throws Exception {
        modules.close();
    }

    @FunctionalInterface
    private interface AsyncAction {
        Object run() throws Exception;
    }
}
