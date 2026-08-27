# Project plan

## Objective

Turn the repository into a reproducible Minecraft/NeoForge engineering platform that supports ordinary mods, advanced runtime experimentation, AI-assisted development, native/polyglot extensions, headless verification and in-game construction systems without forcing every generated mod to depend on experimental machinery.

## Readiness levels

| Level | Meaning |
|---|---|
| R0 | Idea only |
| R1 | Interface/contract defined |
| R2 | Compiles or static tooling passes |
| R3 | Automated unit/integration test passes |
| R4 | Headless Minecraft/GameTest validation passes |
| R5 | Interactive client validation passes |
| R6 | Production/release candidate |

## Workstreams

1. **Build and provenance** — master Gradle lock, dependency/version locks, script gatekeeper, licensing, CodeQL.
2. **Testing** — JUnit/ArchUnit, logic mocks, headless GameTest, bytecode regression analysis, chaos tests.
3. **AI/context** — MCP 2026-07-28 endpoint, local vector index, auto-documentation, project knowledge adapters.
4. **Runtime hotload** — NIO.2 watcher, script reload, asset reload, class redefine, versioned service loaders, virtual registries.
5. **Polyglot/native** — GraalJS/GraalPy, Panama FFM, Rust/C++, Python/Go/C# sidecars, IPC/Netty bridge protocol.
6. **Construction sandbox** — tool gun, raycast selection, constraints, custom physics, procedural matrices.
7. **World/render engine** — dynamic dimension definitions, teleport channels, render overrides, custom geometry and collision-shape adapters.
8. **Operations** — telemetry, JFR, chaos engineering, profiling, deterministic diagnostics and release evidence.

## Release gates

A capability is not called validated until the corresponding level has evidence. Experimental bytecode, registry, renderer, networking and native hooks must remain isolated behind explicit feature flags. GameTest success does not substitute for client rendering validation, and simulation does not substitute for runtime measurement.

## Milestones

### M1 — Platform control plane
Master build lock, script gatekeeper, project metadata, auto-doc, unit architecture rules and headless test orchestration.

### M2 — Runtime development plane
MCP/local index, telemetry, JFR, WatchService hotload bus, polyglot sandbox and development Netty endpoint.

### M3 — Cross-language plane
Panama ABI, Rust/C++ extensions, Python/Go/C# bridges, shared-memory/Netty transports and bytecode-diff diagnostics.

### M4 — Construction plane
Tool-gun command model, constraints, physics bodies, procedural generation, collision composition and dynamic rendering providers.

### M5 — Minecraft adapters
Version-locked NeoForge 26.2 adapters for GameTest, client render events, VoxelShape, dimensions, registry indirection and safe reload boundaries.

### M6 — Hardening
Chaos campaigns, performance baselines, compatibility matrix, migration tests, release packaging, provenance/SBOM and rollback playbooks.
