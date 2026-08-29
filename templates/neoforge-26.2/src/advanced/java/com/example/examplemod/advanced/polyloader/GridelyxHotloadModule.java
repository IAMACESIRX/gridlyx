package com.example.examplemod.advanced.polyloader;

/**
 * Stable service contract for a Gridelyx-owned H3 hotload module.
 *
 * <p>Implementations are discovered from a versioned JAR through {@link java.util.ServiceLoader}. The module
 * must register every reversible side effect in the supplied {@link ModuleScope}. Preparation is deliberately
 * separated from authority switching so a candidate revision can be built and checked before the previous
 * revision is retired.
 */
public interface GridelyxHotloadModule extends AutoCloseable {
    /** Stable logical module identifier shared by all revisions of the module. */
    String moduleId();

    /**
     * Prepare the candidate revision without making it authoritative. Resources allocated here must be owned by
     * {@code scope} so a failed candidate can be completely retired.
     */
    void prepare(ModuleScope scope) throws Exception;

    /** Validate the prepared candidate before any authority switch occurs. */
    default void healthCheck() throws Exception {}

    /**
     * Make the prepared candidate authoritative. Implementations should keep this transition small and atomic,
     * typically by switching Gridelyx-owned handles rather than registering new global side effects here.
     */
    void activate() throws Exception;

    /** Module-local cleanup. Scope-owned resources are retired deterministically around this callback. */
    @Override
    default void close() throws Exception {}
}
