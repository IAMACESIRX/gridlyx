# Gridelyx Studio

Private R&D platform for **AI-assisted Minecraft engineering, live scripting, polyloader compatibility, in-game world/asset authoring, native acceleration and cross-edition runtime tooling**. Gridelyx Studio targets both Minecraft Java Edition and Minecraft Bedrock Edition through neutral operation, asset, transaction and bridge contracts while keeping engine-specific adapters isolated.

## Current target matrix

| Target | Baseline |
|---|---:|
| Minecraft Java | 26.2 |
| NeoForge | 26.2.0.67 |
| Minecraft Bedrock stable | 1.26.40 |
| Bedrock `@minecraft/server` | 2.9.0 |
| Bedrock Editor preview | 1.26.50-preview.26 |
| Java | Temurin 25.0.4+7 |
| Gradle | 9.2.1 |
| ModDevGradle | 2.0.144 |
| ASM | 9.10.1 |
| GraalVM Polyglot | 25.3.4.1 |
| VFSB bridge protocol | 1 |
| Gridelyx native ABI | 1 |

The canonical Java template `templates/neoforge-26.2/build.gradle` remains protected by `platform/master-build.lock.json`; generated Java mod workspaces must match it byte-for-byte unless the lock is deliberately refreshed.

Canonical product and ABI naming lives in `platform/brand.json`.

## Architecture

```text
                          Gridelyx Studio
                                 |
                AI / IDE / scripts / authoring tools
                                 |
                  Unified Abstraction Layer (UAL)
                        /                    \
                       /                      \
           Java Edition target             Bedrock target
                    |                            |
       loader/version adapters          Script API adapter
                    |                     Editor adapter
      Instrumentation + ASM                   |
                    |                    behavior/resource packs
                    |                            |
                    +--------- VFSB ------------+
                                 |
                         Java FFM / Panama
                                 |
                         gridelyx_native
                                 |
                      named shared memory
                                 |
                     native Bedrock companion
                                 |
                       versioned adapter
```

Gridelyx Studio deliberately distinguishes neutral platform contracts from engine-specific integration. Minecraft Java classes and Bedrock C++ object pointers are not allowed in portable UAL/VFSB payloads.

## Java polyloader plane

The advanced Java runtime includes:

- Java Instrumentation prelaunch bootstrap integrated with the ASM transform engine;
- runtime loader/JVM fingerprinting without compile-time Minecraft or loader API imports in the polyloader core;
- UAL domains for registry, event, network, resource, render, world, input and lifecycle operations;
- exact-descriptor ASM invocation translation rules;
- capability-negotiated mod JAR analysis and isolated sideload classloaders;
- explicit `LIVE_SAFE`, `EMULATED`, `PRELAUNCH_REQUIRED` and `UNSUPPORTED` decisions;
- reflection/MethodHandle structural scanning for runtime symbol discovery;
- revisioned dynamic mesh and texture registries with vertex overrides;
- version-neutral voxel editor workspace state;
- cooperative script deadlines and recoverable event fault boundaries;
- prepared forward/inverse world transactions with rollback reporting.

Gridelyx does **not** claim arbitrary Fabric/Forge/NeoForge/Quilt/Liteloader mods are universally hot-injectable. Mixins, access wideners, coremods, transformation services, frozen registries and early lifecycle hooks remain prelaunch/restart-sensitive until an adapter proves otherwise.

Read `docs/POLYLOADER_ARCHITECTURE.md`, `docs/LIVE_ASSET_EDITING.md` and `docs/FAULT_TOLERANCE.md`.

## Bedrock runtime plane

`bedrock/` makes Bedrock a first-class Gridelyx target instead of duplicating the Java feature stack.

### Stable Add-On runtime

`bedrock/addon` contains a behavior pack and resource pack targeting Bedrock 1.26.40 and `@minecraft/server` 2.9.0. The script runtime exposes a neutral dispatcher through:

```text
/scriptevent gridelyx:<action> <payload>
```

Built-in actions currently provide bridge diagnostics/capability reporting and establish the supported ingress used by future UAL operation adapters.

### Bedrock Editor extension

`bedrock/editor-extension` is a separately pinned preview scaffold for Gridelyx world/asset authoring tools. Because `@minecraft/server-editor` is pre-release, Editor changes cannot alter or destabilise the stable Add-On/runtime protocol.

### Project Panama native bridge

Java 25 binds the Gridelyx native C ABI through FFM. `gridelyx_native` provides named cross-process shared memory with bounded payload capacity, CRC metadata and atomic publication sequences.

The portable VFSB envelope carries:

1. control messages;
2. UAL operations;
3. mesh revisions;
4. texture patches;
5. world deltas/transactions;
6. telemetry;
7. script results.

`native/bedrock` consumes those frames behind a `BedrockAdapter` interface. Its checked-in default adapter validates/logs frames; it does not hard-code Bedrock addresses or require executable patching. A lower-level Bedrock renderer adapter remains version-scoped and must be validated separately.

Read `docs/BEDROCK_ARCHITECTURE.md` and `docs/GRIDELYX_BRIDGE_PROTOCOL.md`.

## Live world authoring

The advanced platform contains a palette-indexed, server-authoritative world-edit architecture for already-generated chunks:

- parallel 16x16x16 section-array blitting;
- asynchronous immutable snapshot/delta preparation;
- server-thread-only commit scheduling;
- deferred bulk lighting and reconciliation;
- compressed/uncompressed `.nbt` structure blueprint decoding;
- blueprint slicing across chunk/section boundaries;
- dynamic event/structure matrices;
- sparse sub-voxel paint/overlay buffers;
- progressive world transmutation;
- volumetric density/material frame streaming;
- hierarchical scene graph and transform gizmos;
- embedded IDE/console, live Java compilation and AI passthrough;
- edit framing, revision consensus, ACK state and replication culling.

The neutral authoring state is shared conceptually across editions; final Java and Bedrock world/render adapters remain engine-specific.

## Multi-mod Java workspaces

```bash
python tools/new_mod.py spectral_tools "Spectral Tools" com.iamacesirx.mods.spectraltools
python tools/new_mod.py world_lab "World Lab" com.iamacesirx.mods.worldlab
python tools/workspace.py list
python tools/workspace.py build spectral_tools
python tools/workspace.py build world_lab --advanced
```

`mods/<mod_id>` remains a generated Java workspace area. Placeholder package/mod identifiers such as `examplemod` are template variables, not Gridelyx Studio product branding.

## Quality gates

```bash
python tools/build_lock.py --check
python tools/script_gatekeeper.py
python tools/ecosystem_check.py
python tools/world_editor_check.py
python tools/polyloader_check.py
python tools/bedrock_check.py
python tools/validate_platform.py
python tools/diagnose.py --static
python tools/autodoc.py --check
python tools/ai_autodoc.py --self-test
python tools/bytecode_diff.py --self-test
python tools/csv_recipe_pipeline.py --self-test
```

CI is split by responsibility:

- **Gridelyx Advanced Engine CI** — Java advanced engines, UAL/polyloader, Bedrock bridge codec and polyglot smoke tests;
- **Gridelyx Bedrock CI** — Bedrock manifests and Script/Editor JavaScript syntax;
- **Gridelyx Native CI** — Rust ABI plus Windows/Linux C++ shared-memory and Bedrock companion builds;
- **Minecraft Mod Platform CI** — locked Java template/workspace validation;
- **CodeQL** — security/static analysis.

## AI, polyglot and sidecars

- GraalJS and GraalPy contexts are replaceable and deny host access by default.
- Java 25 FFM/Panama binds the versioned `gridelyx_` native C ABI.
- Rust and C++ have independent native validation lanes.
- Python, Go and C# use bounded bridge protocols.
- VFSB is transport-neutral and can be carried over shared memory or future validated network transports.
- Bedrock Dedicated Server networking is optional because `@minecraft/server-net` is not a normal-client or Realm transport.

## External hotloading

`ExternalHotloadCore` recursively monitors approved development roots and publishes debounced typed reload events for scripts, data, assets and bytecode.

Restartless strategy remains capability-based:

- scripts: replace script context/module;
- data/procedural definitions: validate then atomically replace versioned state;
- assets: revisioned Gridelyx registries and target-specific upload/override paths;
- compatible Java class changes: `Instrumentation.redefineClasses`;
- schema-changing Java: replaceable implementation JAR/classloader;
- dynamic gameplay content: virtual/versioned registries;
- frozen Java loader registries: prelaunch/restart when required;
- Bedrock stable content: Creator API/pack lifecycle rules;
- Bedrock Editor/native integrations: explicit version capability checks.

## Native safety boundary

Native extensions are trusted code. FFM/native ABI mismatch, invalid pointers or native memory corruption can terminate a process. Untrusted AI-generated/native workloads belong in a separate worker-process fault domain. Shared memory is a transport and must never be treated as authority over world state; authoritative edits retain transaction/rollback rules.

## Project management and architecture docs

Read:

- `docs/PROJECT_PLAN.md`
- `docs/WORLD_EDITOR_ROADMAP.md`
- `docs/POLYLOADER_ARCHITECTURE.md`
- `docs/BEDROCK_ARCHITECTURE.md`
- `docs/GRIDELYX_BRIDGE_PROTOCOL.md`
- `docs/LIVE_ASSET_EDITING.md`
- `docs/FAULT_TOLERANCE.md`
- `docs/TEST_STRATEGY.md`
- `docs/TODO.md`
- `docs/DECISIONS.md`
- `SECURITY.md`
- `CONTRIBUTING.md`

The issue tracker is the live execution backlog. Capabilities are promoted from framework/preview status only when their relevant compatibility and regression cells are green.

## Reference vault

`references/index/` is the fast lookup layer; `vault/` is exact recovery/deep-inspection storage. Large supplied binary payloads remain represented by exact checksums/chunk manifests until deterministic hydration is complete.
