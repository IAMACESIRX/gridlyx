# Minecraft Advanced Mod Development Kit

Private R&D platform for **AI-assisted Minecraft/NeoForge engineering, live scripting, runtime experimentation, multi-language extensions and in-game construction systems**. The stable mod template stays reproducible while advanced bytecode/native/polyglot/render/network/world-edit systems remain isolated and opt-in.

## Canonical toolchain

| Component | Locked version |
|---|---:|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.67 |
| ModDevGradle | 2.0.144 |
| Gradle | 9.2.1 |
| Java | Temurin 25.0.4+7 |
| Spotless | 8.10.0 |
| Checkstyle | 14.0.0 |
| JUnit | 6.1.3 |
| ArchUnit | 1.4.2 |
| ASM | 9.10.1 |
| LWJGL reference | 3.4.1 |
| GraalVM Polyglot | 25.3.4.1 |
| MCP target | 2026-07-28 |

The canonical `templates/neoforge-26.2/build.gradle` is protected by `platform/master-build.lock.json`; generated mod workspaces must match it byte-for-byte unless the lock is deliberately refreshed.

## Multi-mod workspaces

```bash
python tools/new_mod.py spectral_tools "Spectral Tools" com.iamacesirx.mods.spectraltools
python tools/new_mod.py world_lab "World Lab" com.iamacesirx.mods.worldlab
python tools/workspace.py list
python tools/workspace.py build spectral_tools
python tools/workspace.py build world_lab --advanced
```

Every `mods/<mod_id>` is an independent NeoForge project with isolated generated resources, runs and JARs while sharing the locked platform contract.

## Gridelyx Studio polyloader plane

The advanced runtime now includes the first loader-neutral Gridelyx Studio substrate:

- Java Instrumentation prelaunch bootstrap integrated with the existing ASM transform engine;
- runtime loader/JVM fingerprinting without compile-time Minecraft or loader API imports;
- Unified Abstraction Layer domains for registry, event, network, resource, render, world, input and lifecycle operations;
- exact-descriptor ASM invocation translation rules;
- capability-negotiated mod JAR analysis and isolated sideload classloaders;
- explicit `LIVE_SAFE`, `EMULATED`, `PRELAUNCH_REQUIRED` and `UNSUPPORTED` decisions;
- reflection/MethodHandle structural scanning for runtime symbol discovery;
- revisioned dynamic mesh and texture registries with a vertex override pipeline;
- version-neutral voxel editor workspace state;
- cooperative asynchronous script deadlines and recoverable event fault boundaries;
- prepared forward/inverse world transactions with rollback reporting.

The project deliberately does **not** claim that arbitrary Fabric/Forge/NeoForge/Quilt/Liteloader mods are universally hot-injectable. Mixins, access wideners, coremods, transformation services, frozen registries and early lifecycle hooks default to prelaunch or restart-required behaviour until an adapter proves otherwise.

Run the Gridelyx structural gate with:

```bash
python tools/polyloader_check.py
```

Read `docs/POLYLOADER_ARCHITECTURE.md`, `docs/LIVE_ASSET_EDITING.md` and `docs/FAULT_TOLERANCE.md` before implementing loader-specific adapters or renderer bindings.

## Live world authoring plane

The advanced template includes a palette-indexed, server-authoritative world-edit architecture for MCEdit-style edits over already-generated chunks:

- parallel 16x16x16 section-array blitting;
- asynchronous immutable snapshot/delta preparation;
- server-thread-only commit scheduling;
- deferred bulk lighting with explicit reconciliation;
- compressed/uncompressed `.nbt` structure blueprint decoding;
- blueprint slicing across existing chunk/section boundaries;
- dynamic event/structure matrices for triggered procedural events;
- sparse sub-voxel paint/overlay buffers;
- progressive world transmutation state machine;
- real-time volumetric density/material frame streaming;
- hierarchical scene graph, typed property serialization and transform gizmos;
- embedded IDE/console models, live Java compilation, key/menu action routing and AI passthrough;
- Netty edit framing, section revision consensus, ACK state and near-player replication culling.

Run the structural gate with:

```bash
python tools/world_editor_check.py
```

Read `docs/WORLD_EDIT_RUNTIME.md`, `docs/INGAME_DEVELOPMENT_ENVIRONMENT.md`, `docs/MULTIPLAYER_WORLD_EDIT.md` and `docs/WORLD_EDITOR_ROADMAP.md` before wiring target-specific Minecraft adapters.

## Quality, tests and automation

```bash
python tools/build_lock.py --check
python tools/script_gatekeeper.py
python tools/ecosystem_check.py
python tools/world_editor_check.py
python tools/polyloader_check.py
python tools/validate_platform.py
python tools/diagnose.py --static
python tools/autodoc.py --check
python tools/ai_autodoc.py --self-test
python tools/bytecode_diff.py --self-test
python tools/csv_recipe_pipeline.py --self-test
```

The platform uses Spotless, Checkstyle, JUnit, ArchUnit, CodeQL, deterministic diagnostics, headless NeoForge GameTest orchestration, bytecode diffing and independent native/advanced CI lanes. MCTester is treated as an optional adapter until exact target-version compatibility is verified; the native NeoForge GameTest API is the locked baseline.

## AI, MCP and local context

The advanced AI plane includes a stateless MCP 2026-07-28 endpoint contract and a local cosine vector index with replaceable embedding providers. `tools/ai_autodoc.py` can hand a provenance-aware documentation request to a local model or sidecar without hard-wiring a cloud vendor/API key.

## Polyglot, native and cross-process execution

- GraalJS and GraalPy contexts are replaceable and deny host access by default.
- Java 25 FFM/Panama binds versioned native C ABIs.
- Rust and C++ native examples compile in their own CI lane.
- Python, Go and C# share the same bounded big-endian bridge-frame protocol.
- Existing shared-memory IPC and Netty injection are extended by a loopback-first development HTTP endpoint.

## External hotloading core

`ExternalHotloadCore` uses NIO.2 `WatchService` to recursively monitor approved development roots and publish debounced typed reload events for scripts, data, assets and bytecode.

Restartless strategy is deliberately split:

- scripts: replace GraalVM context/module;
- data/procedural definitions: validate then atomically replace versioned runtime state;
- assets: revisioned Gridelyx registries and target-specific render upload/override paths;
- compatible Java class changes: `Instrumentation.redefineClasses`;
- schema-changing Java: new implementation JAR behind a stable service interface and replaceable classloader;
- dynamic gameplay content: virtual/versioned registries;
- frozen vanilla/loader registry additions: not falsely claimed to be universally hotloadable.

See `docs/HOTLOAD_ARCHITECTURE.md` and `docs/POLYLOADER_ARCHITECTURE.md`.

## Construction sandbox

The construction plane contains a deterministic physics world, constraint graph, raycast tool-gun controller, procedural matrix engine, algorithmic asset provider, dynamic collision-shape model, Minecraft `VoxelShape` composer, zero-entity teleport channels, dynamic dimension lifecycle manager, custom geometry provider and prioritised client render-event/override pipelines.

The live world editor extends this into section-array world manipulation, event-driven structure placement, volumetric previews, scene graph editing and multiplayer replication infrastructure.

## Profiling and chaos engineering

The telemetry plane includes bounded real-time event storage, deterministic opt-in fault injection and JDK Flight Recorder control. Chaos mode is development/test-only and must not silently activate in release builds.

## Mod import/fork analysis

`tools/fork_mod.py` safely extracts an authorised JAR, records its SHA-256/provenance, creates `javap` disassembly and can invoke an explicitly supplied local decompiler. Nothing is downloaded or executed automatically merely because a JAR was imported.

## Project management

Read:

- `docs/PROJECT_PLAN.md` — R0-R6 readiness model, workstreams, milestones and release gates.
- `docs/WORLD_EDITOR_ROADMAP.md` — R0-R5 world-editor integration and validation roadmap.
- `docs/POLYLOADER_ARCHITECTURE.md` — loader-neutral bootstrap, UAL and compatibility contract.
- `docs/LIVE_ASSET_EDITING.md` — dynamic mesh/texture and voxel authoring pipeline.
- `docs/FAULT_TOLERANCE.md` — script isolation and transactional recovery rules.
- `docs/TODO.md` — implementation/validation ledger.
- `docs/DECISIONS.md` — architecture decision records.
- `docs/TEST_STRATEGY.md` — layered verification model.
- `docs/AUTO_CAPABILITIES.md` — generated capability status.
- `docs/AI_AUTODOC.md` — provider-independent AI documentation pipeline.
- `SECURITY.md` and `CONTRIBUTING.md` — operational rules.

The repository issue tracker is the live execution backlog. Use the world-editor and Gridelyx work items to separate framework presence from loader-specific, client-specific and production validation.

## Reference vault

`references/index/` is the fast lookup layer; `vault/` is exact recovery/deep-inspection storage. Large supplied binary payloads remain represented by exact checksums/chunk manifests until their deterministic local import is pushed. Do not treat pending remote binary payloads as hydrated merely because their manifests exist.
