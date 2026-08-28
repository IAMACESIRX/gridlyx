package com.example.examplemod.advanced.polyloader;

/**
 * Stable service contract for a Gridelyx-owned H3 hotload module.
 *
 * <p>Implementations are discovered from a versioned JAR through {@link java.util.ServiceLoader}. The module
 * must register every reversible side effect in the supplied {@link ModuleScope}; Gridelyx only switches the
 * revision after activation and the module health check complete successfully.
 */
public interface GridelyxHotloadModule extends AutoCloseable {
    /** Stable logical module identifier shared by all revisions of the module. */
    String moduleId();

    /**
     * Stage and activate this revision. Any listener, executor, handle, registration or other side effect that
     * must be retired on replacement belongs in {@code scope}.
     */
    void activate(ModuleScope scope) throws Exception;

    /** Called after activation but before the revision is allowed to replace the previous authority. */
    default void healthCheck() throws Exception {}

    /** Module-local cleanup. Scope-owned resources are retired deterministically around this callback. */
    @Override
    default void close() throws Exception {}
}
