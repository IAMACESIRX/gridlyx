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

## ADR-007 — Gridelyx is the canonical root brand
The root brand is **Gridelyx** and the integrated product suite is **Gridelyx Studio**. New public/project-owned terminology uses Gridelyx. Retired Gridelyx/VFSB source, wire, ABI and persisted identifiers are treated as versioned migration boundaries rather than being blindly search/replaced. `platform/brand.json` is the machine-readable identity source and `docs/REBRAND_PLAN.md` governs the migration.

## ADR-008 — Requirements and toolchains are separate controlled ledgers
`platform/chat-requirements.json` proves that every retained conversation requirement has implementation or planning evidence. `platform/toolchain-requirements.json` independently records the programs/libraries needed to build or exercise those capabilities. A feature is not considered runtime-validated merely because its dependency is installed, and an optional dependency is not considered universally required merely because one subsystem uses it.
