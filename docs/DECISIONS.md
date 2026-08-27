# Architecture decision log

## ADR-001 — Advanced systems are opt-in
Ordinary mods compile from `src/main`. Instrumentation, native memory, polyglot execution, direct GPU control and injected networking remain in isolated advanced source sets.

## ADR-002 — Restartless registries use indirection
Frozen vanilla/NeoForge registries are not treated as arbitrarily mutable. Runtime construction content uses versioned virtual definitions and stable registry-backed hosts where needed.

## ADR-003 — Schema-changing Java uses replaceable service implementations
`Instrumentation` handles compatible redefinition. New fields/method signatures move behind stable interfaces and versioned classloaders instead of relying on unsupported JVM HotSwap semantics.

## ADR-004 — Script host access is deny-by-default
GraalVM contexts start with `HostAccess.NONE` and no host class lookup. Game/runtime services are exposed as explicit capability surfaces.

## ADR-005 — Development web endpoints bind loopback by default
Remote access requires a separate authenticated transport configuration. Telemetry/MCP/control ports are not silently exposed on all interfaces.

## ADR-006 — Physics authority is server-side
Client physics/render prediction may improve responsiveness, but construction constraints and authoritative object state belong to the server simulation for multiplayer consistency and abuse resistance.
