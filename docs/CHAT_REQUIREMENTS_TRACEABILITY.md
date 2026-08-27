# Whole-chat requirements traceability

Status: **canonical scope ledger**

This file preserves the project requirements requested across the development conversation. It exists so a later human or AI cannot silently lose scope because a feature was discussed in a different subsystem document or an older handoff.

Presence here means **the requirement remains part of project scope** unless a later explicit decision supersedes it. It does **not** mean the capability is already implemented or validated. Implementation maturity remains governed by R0-R6 evidence, `docs/FEATURE_MAP.md`, target-specific capability manifests and CI/runtime evidence.

The public product brand is currently under rebrand. Requirements are therefore written using neutral project terminology.

## Requirement state vocabulary

- **implemented/validated** — source exists and relevant automated/target evidence exists.
- **framework** — reusable implementation/contract exists but target integration or stronger evidence remains.
- **planned** — retained as explicit development work.
- **transition** — migration/rebrand/compatibility work is intentionally pending.

---

## CR-001 — Reproducible Minecraft R&D foundation

Retain:

- NeoForge/Minecraft/Java/Gradle version locks;
- exact supplied MDK, NeoForge installer, JDK and LWJGL reference-vault provenance;
- deterministic vault reconstruction and indexes;
- AI-readable/on-demand JDK, LWJGL and MDK source lookup;
- separation between reference corpora and actual Minecraft runtime dependencies;
- independent `mods/<mod_id>` workspaces so multiple JARs can be developed side-by-side.

State: **implemented/framework**. See `docs/ARCHITECTURE.md`, `docs/REFERENCE_VAULT.md`, `platform/versions.json`, `vault/manifest.json`, `tools/vault.py`, `tools/reference_lookup.py`, `tools/new_mod.py` and `tools/workspace.py`.

## CR-002 — Java workspace quality, build integrity and GitHub development environment

Retain:

- Spotless;
- Checkstyle;
- GitHub issue templates and PR template;
- CodeQL;
- Codespaces/devcontainer support;
- Copilot setup workflow and project `copilot-setup-steps.toml` tuning;
- automated diagnostics;
- Gradle executable/permission checks;
- canonical master `build.gradle` SHA-256 lock;
- script gatekeeper for non-Java executable content;
- `.gitignore`, editor and workspace rules;
- licensing/provenance requirements.

State: **implemented/framework**. See `.github/`, `.devcontainer/`, `platform/master-build.lock.json`, `tools/build_lock.py`, `tools/script_gatekeeper.py`, `tools/diagnose.py`, `docs/ARCHITECTURE_WORKFLOW.md`, `docs/TEST_STRATEGY.md`, `CONTRIBUTING.md` and `SECURITY.md`.

## CR-003 — Canonical mod template and data/asset generation

Retain:

- global Gradle variables;
- loader manifest configuration;
- main mod class;
- global registry controller;
- creative-mode tab anchor;
- automated language provider;
- localization and asset blueprints;
- deterministic data generation and generated-resource source sets;
- algorithmic asset providers and data-generation loops;
- codec-driven world-generation extension points;
- CSV/external spreadsheet to recipe/data conversion;
- deterministic procedural matrices and procedural content generation.

State: **implemented/framework**. See `templates/neoforge-26.2/`, `docs/ASSET_BLUEPRINTS.md`, `tools/csv_to_recipes.py`, `scripts/`, `platform/capabilities.json` and generated-resource tooling.

## CR-004 — Advanced JVM and bytecode engine

Retain:

- Java Instrumentation agent/bootstrap;
- ASM generation and runtime class transformation;
- Mixin/runtime redirector infrastructure;
- bytecode-diff/disassembly/decompiler analysis;
- reflection and `MethodHandle` runtime discovery;
- compatible live class redefinition;
- isolated/reloadable classloaders for structural changes;
- direct string-to-Java compilation;
- exact mapping/descriptor fingerprinting and fail-closed behavior.

State: **framework**. See `docs/ADVANCED_ENGINES.md`, `docs/POLYLOADER_ARCHITECTURE.md`, `docs/HOTLOAD_ARCHITECTURE.md`, `docs/ADVANCED_VALIDATION.md`, `templates/neoforge-26.2/src/advanced` and bytecode-analysis tools.

## CR-005 — Multithreaded computation, state synchronization and dynamic data

Retain:

- custom bounded worker pools;
- asynchronous computational tasking;
- coalescing/multithreaded synchronization;
- dynamic/versioned data engines;
- thread-safe server scheduling;
- bounded queues and backpressure;
- revision/consensus models where multiple editors or systems can mutate state.

State: **framework**. See `docs/ADVANCED_ENGINES.md`, `docs/MULTIPLAYER_WORLD_EDIT.md`, advanced runtime source and validation matrices.

## CR-006 — Native, GPU, FFM/Panama and IPC execution planes

Retain:

- Java Foreign Function & Memory API / Project Panama;
- Rust and C++ high-performance native extensions;
- shared-memory IPC;
- multi-process OS pipelines;
- Python/Go/C# sidecar bridges;
- neutral framed protocols;
- Netty/IPC pipelines and local development web endpoints;
- direct LWJGL/OpenGL vertex/buffer control;
- render-thread/context safety;
- native ABI/version/fingerprint checks.

State: **framework**. See `native/`, `bridges/`, `docs/ADVANCED_ENGINES.md`, `docs/BEDROCK_ARCHITECTURE.md`, platform capability manifests and native CI.

## CR-007 — MCP, local indexing and AI repository consumption

Retain:

- Model Context Protocol endpoint/tool routing;
- local vector indexing;
- deterministic repository/chunk indexing;
- task-scoped context packs;
- future incremental embeddings keyed by path/range/hash;
- AI auto-documentation generation;
- project-aware AI handoff/context files;
- Project-Athena-inspired-but-not-copied AI organisation, decision/assumption ledgers and drift mitigation.

State: **implemented/framework/planned**. See `platform/capabilities.json`, `docs/AI_CONTEXT_SYSTEM.md`, `docs/AI_AUTODOC.md`, `docs/AI_CONTINUITY_DESIGN.md`, `AGENTS.md`, `AI_HANDOFF.md`, `ai/`, `tools/repo_index.py`, `tools/ai_context_pack.py` and `tools/continuity_check.py`.

## CR-008 — Autonomous testing, diagnostics, profiling and chaos engineering

Retain:

- JUnit logic tests and mocks/test doubles;
- ArchUnit architectural constraints;
- NeoForge GameTest/headless validation;
- optional MCTester adapter only after version compatibility is demonstrated;
- automated diagnostics;
- JFR/JVM profiling;
- telemetry streams;
- deterministic chaos/fault-injection loops;
- network delay/drop, worker saturation, reload-storm and bridge-disconnect campaigns;
- CodeQL and security scanning.

State: **framework/planned**. See `docs/TEST_STRATEGY.md`, `docs/ADVANCED_VALIDATION.md`, `docs/TODO.md`, platform capabilities and CI workflows.

## CR-009 — Full project planning, issue tracking and development management

Retain:

- project overview;
- project structure/ownership map;
- roadmap and milestones;
- feature/readiness map;
- TODO/implementation ledger;
- GitHub issue/PR workflows;
- decisions, assumptions, active work state and recovery points;
- requirements-to-evidence traceability;
- community onboarding and contribution documentation.

State: **implemented/framework**, with this file becoming the conversation-scope anchor. See `docs/PROJECT_PLAN.md`, `docs/PROJECT_OVERVIEW.md`, `docs/PROJECT_STRUCTURE.md`, `docs/ROADMAP.md`, `docs/FEATURE_MAP.md`, `docs/TODO.md`, `ai/` and `.github/`.

## CR-010 — Loader-neutral Polyloader and Unified Abstraction Layer

Retain the ground-up and top-down architecture:

- a prelaunch bootstrap below ordinary loader lifecycle when needed;
- Java Instrumentation/ASM interception;
- Unified Abstraction Layer for registry/event/network/resource/render/world/input/lifecycle operations;
- Fabric, Quilt, Forge, NeoForge, vanilla and Liteloader/legacy-family research;
- source-loader operation translation into neutral operations;
- isolated sideload containers;
- live-safe/emulated/prelaunch-required/unsupported capability negotiation;
- potential Fabric-on-NeoForge or equivalent cross-loader execution where compatibility adapters genuinely support it;
- no fabricated universal compatibility claim;
- target adapters per Minecraft/JVM generation.

State: **framework/planned**. See `docs/POLYLOADER_ARCHITECTURE.md`, `platform/polyloader-capabilities.json`, advanced runtime sources, `studio/providers/loader-adapters.json` and roadmap work.

## CR-011 — Broad Minecraft-version independence

Retain the target of supporting historical families (including roughly the 1.7.10 era) through current/latest releases and snapshots where technically maintainable, using:

- version-family bootstrap/runtime lanes;
- runtime fingerprinting;
- mappings where available;
- structural/semantic method discovery;
- reflection/MethodHandles;
- compatibility manifests;
- low-confidence fail-closed behavior;
- deeper additive patch layers when ordinary adaptation is insufficient.

State: **planned/framework**. See `docs/POLYLOADER_ARCHITECTURE.md`, `docs/DEEP_INTEGRATION_ARCHITECTURE.md`, `studio/providers/loader-adapters.json` and compatibility planning.

## CR-012 — External hotloading and restart-minimized development

Retain:

- NIO.2 `WatchService` directory monitoring;
- live script/data/procedural-definition reload;
- dynamic model/texture/resource reload;
- Java Instrumentation for compatible changes;
- replaceable services/classloaders for schema-changing code;
- virtual registries for restartless dynamic definitions;
- supervised broader restart scopes when technically unavoidable;
- preserved IDE/workspace/selection/state across restart scopes;
- additive patch-runtime rebuild as the deepest reload scope;
- last-known-good rollback.

State: **framework/planned**. See `docs/HOTLOAD_ARCHITECTURE.md`, advanced runtime source and `docs/DEEP_INTEGRATION_ARCHITECTURE.md`.

## CR-013 — Live world editor, parallel section blitting and generated-world modification

Retain:

- MCEdit/Bedrock-editor-like live editing of already-generated Java worlds;
- palette-indexed 16x16x16 section arrays;
- parallel array blitting;
- asynchronous sub-chunk workers;
- workers calculate immutable deltas off-thread;
- authoritative server-thread commit scheduling;
- deferred bulk lighting updates with explicit reconciliation;
- heightmap/POI/block-entity/save/client-state reconciliation;
- undo/redo and transactional rollback/WAL development path;
- mod APIs that can place ores, structures and environment changes into existing chunks.

State: **framework/planned**. See `docs/WORLD_EDIT_RUNTIME.md`, `docs/WORLD_EDITOR_ROADMAP.md`, `docs/MULTIPLAYER_WORLD_EDIT.md`, `platform/world-editor-capabilities.json` and world-edit advanced source.

## CR-014 — Dynamic Event and Structure Matrix

Retain:

- `.nbt` structure-blueprint loading;
- slicing structures across chunk/section boundaries;
- real-time structure streaming;
- procedural structures and environmental assets;
- meteor, alien citadel, corruption/restoration and similar event triggers;
- live placement over generated terrain;
- mod/AI/script-triggered event activation;
- bounded transactional application.

State: **framework/planned**. See `docs/WORLD_EDIT_RUNTIME.md` and world-edit/runtime source.

## CR-015 — Terraria-style world systems

Retain:

- dynamic liquid simulation cells using bounded cellular-automata-style simulation;
- arbitrary block-paint layer matrices;
- sub-voxel paint/material overlays;
- progressive/progression-locked global world transmutation states;
- reversible staged transformation and rollback;
- eventual Java/Bedrock capability adapters.

State: **planned**, with sub-voxel overlay/transmutation foundations already present. Track in `docs/TODO.md`, `docs/ROADMAP.md` and world-system capability planning.

## CR-016 — Microgeometry, curved/slanted blocks and collision representation

Retain:

- microgrid placement;
- sub-voxel geometry;
- circles, cylinders, curves, wedges/slopes and slanted surfaces;
- custom high-detail meshes;
- dynamic `VoxelShape`/collision synthesis or a deeper replacement collision layer when vanilla shapes are insufficient;
- render geometry and collision geometry as separate representations where useful;
- physics-aware manipulation of these shapes;
- Java and Bedrock rendering/collision adapters.

State: **planned/framework**. Existing custom geometry/render/collision boundaries are in `platform/capabilities.json`; full microgeometry rasterization/render/collision integration remains development work in `docs/TODO.md` and the creator-runtime roadmap.

## CR-017 — Hytale-like in-game asset/model/texture creator

Retain:

- dynamic model registry;
- live mesh/vertex editing;
- texture painting and patching;
- voxel/sub-voxel sculpting;
- asset browser;
- model/texture/material properties;
- direct preview in the living game world;
- dynamic GPU-buffer/resource updates;
- resource reload without manual F3+T as the target workflow;
- Java and Bedrock adapters.

State: **framework/planned**. See project overview/creator workspace, dynamic asset/runtime architecture and rendering integration TODOs.

## CR-018 — Garry's Mod-like sandbox physics and construction

Retain:

- physically manipulable entities/parts;
- custom entity physics;
- forces/impulses;
- dynamic constraint graph;
- weld, hinge, slider, spring, rope and related constraints;
- raycast tool-gun controller;
- selection/manipulation/undo-redo;
- server-authoritative multiplayer physics;
- deterministic replication/prediction budgets.

State: **framework/planned**. See `platform/capabilities.json`, advanced construction source and `docs/TODO.md`.

## CR-019 — Roblox-Studio-like live scene/game engine execution

Retain:

- live scene hierarchy;
- hierarchical instance-property serialization;
- transforms, parenting and properties;
- translate/rotate/scale gizmos;
- drag/drop manipulation;
- runtime script execution;
- live asset/world editing;
- developer console/IDE;
- AI-assisted runtime automation;
- inspectable properties and target capability state;
- hot-reload/restart-minimized workflow.

State: **framework/planned**. See creator/runtime project overview, in-game development architecture and scene/production roadmap.

## CR-020 — In-game IDE, console, compilation and AI control plane

Retain:

- client-side screen/menu injection pipeline;
- embedded IDE/code console;
- direct Java source-string compilation;
- GraalJS/GraalPy execution;
- hotkey/key-binding registration;
- menu-button toggles;
- AI development/API communication channel;
- MCP/local-AI/sidecar passthrough;
- autonomous client automation controller;
- alt-tabless edit/build/test/control workflows;
- explicit multiplayer permission boundaries.

State: **framework/planned**. See `docs/INGAME_DEVELOPMENT_ENVIRONMENT.md`, advanced source, MCP bridge and creator-roadmap work.

## CR-021 — Non-Java modification and external-tool SDK

Retain a generic capability-gated way for external tools to modify/extend the game through:

- GraalVM JavaScript and Python;
- local/external Python;
- Go and C# sidecars;
- Rust/C++ native extensions;
- MCP;
- Netty/TCP/HTTP/WebSocket where appropriate;
- shared memory/IPC;
- file-system hotload;
- Bedrock Script API/Editor integration;
- future capability modules and patch adapters.

External tools must use explicit permissions/capabilities and may not gain world authority merely by connecting.

State: **framework/planned**. See `bridges/`, `native/`, `docs/ADVANCED_ENGINES.md`, `docs/INGAME_DEVELOPMENT_ENVIRONMENT.md`, `AGENTS.md` and roadmap/TODO.

## CR-022 — Multiplayer live editing, synchronization, culling and consensus

Retain:

- live server editing while users are connected;
- server-authoritative mutations;
- custom Netty edit channels;
- thread-safe server commit scheduling;
- per-section revisions/optimistic consensus;
- client acknowledgements;
- network replication culling by chunk/view-distance/interest;
- bounded transactions so huge edits do not starve ticks;
- permissions/authentication of edit requests.

State: **framework/planned**. See `docs/MULTIPLAYER_WORLD_EDIT.md` and world-editor advanced source.

## CR-023 — Fault-tolerant sandbox and anti-crash architecture

Retain:

- script execution isolation and budgets;
- cooperative interruption/timeouts;
- stronger process isolation for non-cooperative or crash-prone workloads;
- global event recovery boundaries;
- transactional world edits and inverse deltas/WAL;
- rollback after script/runtime errors;
- last-known-good hotload state;
- crash attribution/diagnostics;
- native/JVM failure-domain separation;
- explicit statement that same-process native corruption or fatal OOM cannot be magically guaranteed recoverable.

State: **framework/planned**. See security, hotload, world-edit and operations docs/TODO.

## CR-024 — Bedrock Edition first-class support and feature parity target

Retain:

- Bedrock behavior/resource-pack plane;
- Bedrock Editor extension plane;
- Dedicated Server network adapters where API scope permits;
- Java-to-native Project Panama bridge;
- named shared-memory/native companion;
- neutral binary bridge frames for UAL, mesh, texture, world, telemetry and script traffic;
- versioned native Bedrock adapter boundary;
- target of equivalent creator/world/AI/production capabilities on Bedrock, with explicit parity gaps until validated;
- permission to escalate to deeper additive native/executable integration where supported APIs cannot provide a required capability.

State: **framework/planned**. See `docs/BEDROCK_ARCHITECTURE.md`, `bedrock/`, `native/bedrock/`, `platform/bedrock-capabilities.json` and `docs/DEEP_INTEGRATION_ARCHITECTURE.md`.

## CR-025 — Launcher, runtime acquisition and dependency resolution

Retain a consumer-friendly launcher/manager that can:

- start without Java installed;
- acquire the appropriate Java runtime;
- install any Minecraft version that legitimate metadata/providers make available;
- resolve vanilla/Fabric/Quilt/Forge/NeoForge and extensible legacy/future loader adapters;
- install mods, resource packs, shaders, datapacks, worlds and related content;
- obtain dependencies transitively;
- use Mojang/official loader metadata and authorized content APIs;
- use supported Microsoft/Minecraft login flows;
- maintain hashes/provenance/content locks;
- provide one-click/simple UX comparable to consumer launchers;
- provide detailed expert controls comparable to Prism/MultiMC;
- isolate instances and support clone/fork/diff/snapshot/import/export.

State: **framework/planned**. See `studio/`, `docs/PROJECT_OVERVIEW.md`, `docs/ACQUISITION_AND_RESOLUTION.md`, `docs/ROADMAP.md` and `docs/TODO.md`.

## CR-026 — Mod forking, decompilation and compatibility-analysis pipelines

Retain:

- authorized local mod-JAR inspection;
- metadata/dependency classification;
- archive extraction;
- bytecode disassembly/diff;
- optional user-supplied decompiler integration;
- provenance records;
- compatibility/fork workflows that do not assume decompiled code is redistributable.

State: **framework/planned**. See bytecode/mod-fork tools, `CONTRIBUTING.md`, security/provenance rules and TODOs.

## CR-027 — Machinima, recording, animation and production suite

Retain:

- in-game recording/replay capture;
- deterministic event logging;
- rational-time timeline;
- free/first-person/third-person/target/orbit/rail/spline/crane/vehicle camera rigs;
- actor/entity transform and animation tracks;
- poses, IK and equipment/visibility/particle/dialogue/command cues;
- shots, takes, sequences and time remapping;
- real-time capture;
- deterministic/offline rendering where target integration allows it;
- image sequences and replaceable encoder bridge;
- audio stems/mixing metadata;
- render passes such as beauty/depth/normals/object IDs/motion vectors when supported;
- Java and Bedrock production adapters.

State: **framework/planned**. See `docs/MACHINIMA_PRODUCTION.md`, production schemas/source and roadmap/TODO.

## CR-028 — Dynamic dimensions, teleport channels and world-generation/runtime content

Retain:

- dynamic dimension-management abstraction;
- codec-driven worldgen;
- generated ores/structures/data;
- zero-entity teleport channels;
- procedural events and runtime environment changes;
- ability to escalate beyond frozen registries through virtual registries, instrumentation, patching or owned subsystems when required.

State: **framework/planned**. See `platform/capabilities.json`, worldgen/datagen source and deep-integration architecture.

## CR-029 — Rendering, pose/IK and engine geometry interception

Retain:

- real-time client rendering overrides;
- render-event pipelines;
- custom 3D model geometry;
- direct vertex-buffer/LWJGL paths;
- PoseStack/matrix interception;
- inverse kinematics;
- custom collision/hitbox integration;
- volumetric preview matrices;
- target-specific batching/culling/performance validation;
- deeper renderer augmentation/replacement when public hooks are insufficient.

State: **framework/planned**. See `docs/ADVANCED_ENGINES.md`, world-edit volumetric architecture, capability manifests and deep-integration plan.

## CR-030 — Additive deep integration and patch manager

Retain the explicit rule that ordinary modding APIs are not the ceiling. The project may use:

- loader transforms;
- JVM agents/instrumentation;
- native in-process extensions;
- external helper processes;
- custom launch/bootstrap chains;
- deterministic version-pinned executable/shared-library patches;
- engine subsystem augmentation/replacement;
- maintained project-owned runtime components/forks.

Deep integration must preserve or recover a verified upstream base, track exact hashes/fingerprints and patch graphs, provide rollback, and must not be used to bypass authentication, entitlement, DRM or anti-cheat controls.

State: **planned/framework**. See `docs/DEEP_INTEGRATION_ARCHITECTURE.md`, `docs/PROJECT_PLAN.md`, `docs/HOTLOAD_ARCHITECTURE.md` and decision `DEC-2026-08-28-003`.

## CR-031 — Community onboarding and open collaboration

Retain:

- community landing/onboarding guide;
- contributor setup path;
- architecture tour;
- testing/evidence guide;
- support/security routing;
- code of conduct;
- issue/PR templates;
- clear distinction between experimental framework and validated target support.

State: **planned/being added**. Community files are canonical project requirements and must remain part of repository structure.

## CR-032 — Rebrand and terminology scrub

Retain the current transition requirement:

- select a replacement brand only after collision screening;
- freeze display name, slug, short name, protocol/package/executable/data-root identifiers;
- inventory all retired-brand terms;
- rename public/source/path identifiers coherently;
- migrate persisted/protocol/ABI identifiers safely;
- add forbidden-terminology CI;
- regenerate indexes/docs;
- require zero unexplained retired-brand occurrences in the current tracked tree;
- treat Git-history rewriting as a separate destructive decision.

State: **transition**. See `docs/REBRAND_PLAN.md`, `ai/DRIFT_MITIGATION.md` and AI handoff.

---

## Coverage rule

A requested capability from this development conversation must satisfy at least one of the following:

1. it has implementation/validation evidence referenced by this ledger; or
2. it appears explicitly in this ledger and therefore remains a development requirement tracked by project planning.

A future agent may not remove a requirement because it is difficult, unsupported by a normal mod API, or not yet implemented. Such constraints change the implementation layer, schedule, readiness and validation burden. Removal or material weakening requires an explicit human-approved superseding decision recorded in `ai/decision-ledger.json`.

## Audit relationship

Machine-readable coverage lives in `platform/chat-requirements.json`. `tools/chat_requirements_check.py` validates that every tracked requirement group has a state and at least one existing repository evidence/planning path. Studio/project-continuity CI runs that checker.
