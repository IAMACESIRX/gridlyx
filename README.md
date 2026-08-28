# Gridelyx

**Gridelyx** is the root brand for an experimental cross-edition Minecraft launcher, development environment, creator/sandbox engine, world editor, modding interoperability layer and machinima/production suite. The integrated product is **Gridelyx Studio**.

This repository is deliberately broader than a normal mod. It contains the launcher/resolver control plane, canonical Java mod-development template, advanced runtime frameworks, Bedrock targets, native bridges, cross-language adapters, AI/project-intelligence tooling, production foundations and the project-management/evidence system required to develop them coherently.

> **Evidence rule:** source or an interface existing does not mean a capability is production-ready. Gridelyx uses R0-R6 readiness and target-specific validation. See `docs/FEATURE_MAP.md` and `docs/CHAT_REQUIREMENTS_TRACEABILITY.md`.

## Start here

For humans:

1. `COMMUNITY.md` — community entrypoint.
2. `docs/community/GETTING_STARTED.md` — setup and first validation.
3. `CONTRIBUTING.md` — contribution workflow.
4. `docs/community/ARCHITECTURE_TOUR.md` — subsystem map.
5. `docs/community/TESTING_AND_EVIDENCE.md` — what counts as evidence.
6. `docs/DEPENDENCIES_AND_TOOLCHAIN.md` — required/optional tools and programs.
7. `docs/FEATURE_DECISION_FRAMEWORK.md` — mandatory substantial-feature analysis process.
8. `docs/DEVELOPMENT_MAP.md` — critical path, work lanes, horizons and Kanban state.
9. `docs/BENCHMARKING_MATRIX.md` — comparison targets and benchmark workflow.

For AI/agent work:

1. `AGENTS.md`
2. `AI_HANDOFF.md`
3. `docs/CHAT_REQUIREMENTS_TRACEABILITY.md`
4. `docs/FEATURE_DECISION_FRAMEWORK.md`
5. `platform/chat-requirements.json`
6. `platform/feature-analysis.schema.json`
7. `platform/toolchain-requirements.json`
8. `ai/context-map.json`
9. `ai/work-state.json`, decision ledger and assumption ledger

## Canonical identity

- Root brand: **Gridelyx**
- Integrated suite: **Gridelyx Studio**
- Product/API slug target: `gridelyx`
- Requested GitHub repository slug: `gridlyx`
- Protocol prefix target: `GLYX`
- Executable target: `gridelyx`

Machine-readable identity: `platform/brand.json`. Requested GitHub metadata: `platform/repository-metadata.json`.

The previous Gridelyx branding is retired. Existing `VFSB`, `gridelyx_*` and `Gridelyx*` technical identifiers are temporary compatibility/migration debt and are governed by `docs/REBRAND_PLAN.md`. They are not the current product identity.

## What Gridelyx is intended to provide

### Launcher and instance platform

Gridelyx Studio is designed to start without requiring Java merely to open the desktop application. For Java Edition instances it will resolve/acquire the appropriate Java runtime, Minecraft version, loader, libraries, assets and content graph through legitimate/authorized channels.

Target loader/content support includes:

- vanilla;
- Fabric;
- Quilt;
- Forge;
- NeoForge;
- extensible legacy/future loader adapters;
- Modrinth;
- authorized CurseForge access;
- resource packs, shader packs, datapacks, worlds and related content;
- deterministic dependency resolution, hashes, provenance and content locks;
- isolated instances, snapshots, clone/fork/diff/import/export;
- simple consumer UX with an expert graph/detail mode.

Provider policy is in `studio/providers/providers.json` and `docs/ACQUISITION_AND_RESOLUTION.md`.

### Java creator/runtime plane

The current NeoForge 26.2 template is the canonical validated construction target, while the architecture is designed to become version/loader-neutral through Polyloader/UAL adapters.

Advanced frameworks include:

- Java Instrumentation agents and compatible HotSwap;
- ASM runtime bytecode generation/transformation;
- dynamic Mixin/redirector infrastructure;
- Reflection/MethodHandle runtime discovery;
- direct Java source-string compilation;
- NIO.2 external hotload monitoring;
- replaceable classloader/service implementations;
- bounded worker pools and state synchronization;
- GraalVM JavaScript/Python embedding;
- MCP and local vector indexing;
- Netty development/edit channels and web endpoints;
- shared-memory IPC and FFM/Panama;
- Rust/C++ native extensions;
- Python/Go/C# sidecar protocols;
- direct LWJGL/GPU buffer frameworks;
- profiling, telemetry and chaos-engineering foundations.

### Polyloader / UAL

Gridelyx aims to sit both **below and above** ordinary Java loader APIs:

- prelaunch bootstrap/instrumentation when required;
- neutral Unified Abstraction Layer operations;
- loader-family adapters and bytecode-call translation;
- runtime environment/fingerprint scanning;
- isolated sideload containers;
- virtual/indirected runtime definitions;
- explicit `LIVE_SAFE`, emulated, prelaunch-required and unsupported capability states.

Cross-loader execution is a target, not an automatic compatibility claim. See `docs/POLYLOADER_ARCHITECTURE.md`.

### Live world editing and procedural events

Frameworks exist for:

- parallel section-array blitting;
- asynchronous sub-chunk computation;
- authoritative server-thread commits;
- controlled bulk-lighting reconciliation;
- `.nbt` structure-blueprint loading;
- Dynamic Event and Structure Matrix triggers;
- editing already-generated terrain;
- transactional edit/rollback foundations;
- multiplayer revisions, consensus and replication culling;
- volumetric client-preview streams.

Planned extensions explicitly include Terraria-style Dynamic Liquid Simulation Cells, arbitrary block/face paint and sub-voxel overlay matrices, and progression-locked/reversible world-transmutation states.

### Creator, geometry and sandbox systems

The retained creator target combines ideas associated with live game editors/sandboxes:

- dynamic model/texture registries;
- live mesh, voxel and texture editing;
- microgrid/sub-voxel placement;
- circles, cylinders, curves, slopes and slanted blocks;
- custom rendering and dynamic collision/hitbox composition;
- deeper collision/renderer augmentation when ordinary Minecraft hooks are insufficient;
- scene graph, hierarchical properties and transform gizmos;
- physically manipulable entities/parts;
- custom physics and constraint graphs;
- weld/hinge/slider/spring/rope construction;
- raycast tool-gun controls;
- in-game IDE/console, keybind/menu toggles and AI automation.

### Non-Java modification gateway

Other tools are intended to modify/extend the game through permissioned Gridelyx capability surfaces rather than needing to become ordinary Java mods. Supported/framework planes include:

- embedded JavaScript/Python via GraalVM;
- external Python;
- Go;
- C#/.NET;
- Rust/C++;
- MCP;
- Netty/TCP/HTTP endpoints where appropriate;
- shared-memory IPC;
- native FFM/Panama bridges;
- filesystem hotload;
- Bedrock Script/Editor adapters;
- future versioned patch modules.

A connected external tool does not automatically gain world/server authority.

### Anti-crash / fault containment

Gridelyx uses layered containment rather than claiming impossible crash immunity:

- bounded asynchronous execution;
- script budgets/timeouts;
- deny-by-default scripting capabilities;
- process isolation for crash-prone/untrusted/native workloads;
- global recovery boundaries;
- transactional world edits and rollback/WAL development;
- last-known-good hotload state;
- crash attribution and supervised restart.

Fatal same-process native corruption or JVM OOM cannot be guaranteed recoverable; those failure modes require process isolation/recovery design.

### Bedrock plane

The repository includes:

- Bedrock behavior/resource pack targets;
- Preview Editor extension target;
- Bedrock capability manifest;
- Java FFM/Panama bridge classes;
- native companion code;
- shared-memory/framed bridge foundations.

Gridelyx targets feature parity where technically achievable while recording real parity gaps. Unsupported Bedrock API surfaces may require deeper version/fingerprint-gated integration; no undocumented technique is advertised as universally stable.

### Recording, animation and production

Gridelyx Production retains:

- deterministic replay/event logging;
- rational-time timelines;
- camera tracks and multiple camera-rig modes;
- actor transform/animation/pose/IK tracks;
- shots, takes, sequences and cues;
- real-time capture;
- offline deterministic rendering where target stepping permits it;
- image-sequence output;
- replaceable FFmpeg/encoder bridge;
- audio stems/mix metadata;
- advanced render-pass research.

See `docs/MACHINIMA_PRODUCTION.md`.

## Advanced feature planning and decision system

Substantial Gridelyx features use the **Feature Decision Packet** in `docs/FEATURE_DECISION_FRAMEWORK.md`. It includes:

- W5x5x5 repeated Who/What/When/Where/How/Why questioning plus inverse Who-not/What-isn't/When-isn't/Where-isn't/How-not/Why-isn't analysis;
- task decomposition and project-values checks;
- cost/time/money/energy diagnostics;
- 10-minute, 10-hour, 10-day, 10-month, 1-year, 5-year and 10-year horizons;
- opportunity cost and regret minimisation;
- reversible vs difficult-to-reverse decisions;
- risk registers, inversion and pre-mortems;
- second-order thinking and overlap/Venn analysis;
- Eisenhower classification;
- first-principles modelling and structured brainstorming;
- current benchmark verification and reverse-engineering of operational patterns;
- Feynman explanations, MVPs and 30/60-minute research timeboxes;
- asymmetric-risk assessment and working backward;
- Pareto/80-20 analysis, Critical Path Method, Cynefin and Kanban.

This machinery guides sequencing and architecture. It does not erase a retained feature merely because its cost is high. Machine contract: `platform/feature-analysis.schema.json`; issue intake: `.github/ISSUE_TEMPLATE/feature-evaluation.yml`.

## Project control and whole-chat scope

The complete retained conversation scope is not left in chat history. It is recorded in:

- `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` — human-readable CR-001…CR-034 ledger;
- `platform/chat-requirements.json` — machine-readable requirements/evidence paths;
- `tools/chat_requirements_check.py` — CI validation;
- `docs/TODO.md` — live implementation ledger;
- `docs/ROADMAP.md` — staged sequencing;
- `docs/DEVELOPMENT_MAP.md` — critical path, lanes, horizons and Kanban;
- `docs/FEATURE_MAP.md` — evidence/readiness state;
- `docs/PROJECT_PLAN.md` — governance/program plan;
- `docs/PROJECT_VALUES.md` — decision invariants;
- `docs/FEATURE_DECISION_FRAMEWORK.md` — feature evaluation method.

A future contributor/AI may not silently remove a requested capability merely because it is difficult or not supported by a normal mod API. The integration level and validation burden change; the requirement remains until explicitly superseded.

## Dependencies and tools

Canonical dependency documentation: `docs/DEPENDENCIES_AND_TOOLCHAIN.md`.

Current locked Java lane:

- Minecraft `26.2` template target;
- NeoForge `26.2.0.67`;
- ModDevGradle `2.0.144`;
- Gradle `9.2.1`;
- Eclipse Temurin Java `25.0.4+7` / language 25;
- Spotless `8.10.0`;
- Checkstyle `14.0.0`;
- JUnit `6.1.3`;
- ArchUnit `1.4.2`;
- ASM `9.10.1`;
- LWJGL reference `3.4.1`;
- GraalVM Polyglot `25.3.4.1`.

Additional subsystem tools include Python, Rust/Cargo, CMake/C++ compiler, optional Go/.NET bridge toolchains, optional Dev Containers, external encoder/decompiler adapters and Bedrock target runtimes. Their exact pin/support state is machine-readable in `platform/toolchain-requirements.json`.

## Reference-vault status

The repository tracks exact manifests/hashes for the supplied MDK, NeoForge installer, JDK and LWJGL reference artifacts. The large binary bytes are **not yet fully present on remote GitHub** while `vault/REMOTE_BINARY_IMPORT_PENDING.md` exists. This is intentional and visible; use the documented vault import/hydration path to complete it.

## Core validation commands

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/toolchain_requirements_check.py
python tools/feature_planning_check.py
python tools/terminology_check.py
python tools/studio_check.py
python tools/validate_platform.py
python tools/diagnose.py --static
python tools/repo_index.py --check
cargo test --manifest-path studio/Cargo.toml --all-targets
```

Then run subsystem-specific Java, native, Bedrock, GameTest and interactive validation required by the files changed.

## Community and security

- `COMMUNITY.md`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SUPPORT.md`
- `SECURITY.md`
- `docs/LICENSING_REQUIREMENTS.md`

Gridelyx is not affiliated with or endorsed by Mojang/Microsoft, NeoForged, Fabric, Quilt, Forge, Modrinth, CurseForge, Hytale, Garry's Mod/Facepunch or Roblox. Those names are used only to describe comparison targets or external ecosystems.
