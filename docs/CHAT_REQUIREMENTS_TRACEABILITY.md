# Gridelyx whole-chat requirements traceability

Status: **canonical retained-scope ledger**

This document preserves the requirements requested throughout the Gridelyx development conversation. A requirement listed here remains in project scope until an explicit human-approved decision supersedes it. Presence does **not** mean the feature already works: implementation maturity is evidence-bound through R0-R6, target capability manifests, tests and runtime validation.

Canonical machine-readable mirror: `../platform/chat-requirements.json`.

Canonical dependency/tool inventory: [`DEPENDENCIES_AND_TOOLCHAIN.md`](DEPENDENCIES_AND_TOOLCHAIN.md) and `../platform/toolchain-requirements.json`.

Canonical feature-analysis system: [`FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md), [`DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md) and `../platform/feature-analysis.schema.json`.

Canonical stakeholder/documentation layer: [`STAKEHOLDER_DASHBOARD.md`](STAKEHOLDER_DASHBOARD.md), [`ARCHITECTURE_DIAGRAMS.md`](ARCHITECTURE_DIAGRAMS.md), `../mkdocs.yml` and [`DOCUMENTATION_DRIVEN_MARKETING.md`](DOCUMENTATION_DRIVEN_MARKETING.md).

## Brand state

The selected root brand is **Gridelyx** and the integrated suite is **Gridelyx Studio**. Previous Gridelyx terminology is retired project branding. Existing `VFSB`/`gridelyx_*` protocol, ABI, persisted or source identifiers are temporary compatibility/migration debt and must be changed through the versioned rebrand plan rather than blind replacement.

## State vocabulary

- **implemented** — the requested project/repository artifact exists; runtime claims still require target evidence.
- **framework** — reusable implementation/contracts exist but integration or stronger evidence remains.
- **planned** — explicitly retained development work.
- **mixed** — meaningful foundations exist and substantial work remains.
- **transition** — controlled migration work.

## Retained requirements

### CR-001 — Reproducible Minecraft R&D foundation and reference vault — mixed

Retain exact Minecraft/NeoForge/Java/Gradle locks, the supplied MDK/NeoForge-installer/JDK/LWJGL reference-vault provenance, deterministic reconstruction/indexing, on-demand JDK/LWJGL/MDK lookup, separation of reference corpora from runtime dependencies, and independent `mods/<mod_id>` Gradle roots so multiple JARs can be developed side-by-side.

### CR-002 — Quality gates, GitHub development environment and build integrity — implemented

Retain Spotless, Checkstyle, GitHub Issue/PR templates, CodeQL, Codespaces/Dev Containers, Copilot environment tuning and setup workflow, automated diagnostics, Gradle execution permissions, global ignore/editor rules, master `build.gradle` SHA-256 lock, non-Java Script Gatekeeper, security/licensing/provenance requirements and CI separation by failure domain.

### CR-003 — Canonical mod template, registries, localization, data and asset generation — mixed

Retain global Gradle variables, loader manifest, main mod class, global registry controller, creative-mode tab anchor, automated language provider, localization/asset blueprints, deterministic datagen/generated resources, codec-driven registry/worldgen extensions, algorithmic asset providers, procedural matrix/content generators and external CSV/spreadsheet-to-recipe/data conversion.

### CR-004 — Advanced JVM/bytecode modification engine — framework

Retain Java Agent Instrumentation (`premain`/`agentmain`), ASM generation/transformation, advanced Mixin/redirector architecture, bytecode diff/disassembly/decompiler analysis, Reflection/MethodHandles runtime discovery, compatible hotswap, isolated classloader/service replacement for structural changes, direct Java source-string compilation, exact mapping/descriptor fingerprints and fail-closed behavior.

### CR-005 — Multithreaded computation, synchronization and dynamic data — framework

Retain custom bounded worker pools, asynchronous computational tasking, multithreaded/coalesced syncing, versioned dynamic data, thread-safe server scheduling, backpressure, revisions and concurrency-consensus models for collaborative/live mutation.

### CR-006 — Native, GPU, Panama, IPC and cross-language execution — framework

Retain Java FFM/Project Panama, Rust/C++ native extensions, shared-memory IPC, multi-process pipelines, Python/Go/C# sidecars, neutral framed protocols, Netty/IPC pipelines, development web-port endpoints, direct LWJGL/OpenGL/vertex-buffer control, render-thread/context safety and native ABI/version/fingerprint checks.

### CR-007 — MCP, local vector indexing and AI project intelligence — mixed

Retain Model Context Protocol routing, local vector indexing, deterministic file/chunk indexes, task-scoped context packs, future hash-keyed incremental embeddings, AI-driven auto-documentation, AI handoff/context/navigation, role boundaries, active work state, decision/assumption ledgers and drift mitigation. AI continuity may borrow useful structural principles from broader agent projects but Gridelyx remains its own project architecture.

### CR-008 — Autonomous testing, diagnostics, profiling and chaos engineering — mixed

Retain JUnit/tests/mocks, ArchUnit architecture tests, NeoForge GameTest/headless validation, optional MCTester only after target compatibility is proven, diagnostics, JFR/JVM profiling, telemetry, deterministic chaos/fault injection, packet delay/drop, worker saturation, reload storms, bridge disconnect/recovery, CodeQL and security scanning.

### CR-009 — Full project planning, issue tracking and development management — implemented

Retain project overview, full structure/ownership map, roadmap, milestones, feature/readiness map, detailed TODO, development/critical-path map, GitHub Issues/PR project workflow, decisions, assumptions, active work/recovery state, requirements-to-evidence traceability, community onboarding, human contributor docs, AI handoff and efficient repository indexing/context consumption.

### CR-010 — Ground-up/top-down Polyloader and Unified Abstraction Layer — framework

Retain a prelaunch bootstrap below ordinary loaders where required; Instrumentation/ASM interception; a UAL for registry/event/network/resource/render/world/input/lifecycle operations; Fabric, Quilt, Forge, NeoForge, vanilla and legacy/Liteloader-family research; loader-call translation; isolated sideload containers; dynamic dependency mapping; capability states such as live-safe/emulated/prelaunch-required/unsupported; and cross-loader execution such as Fabric-on-NeoForge only when real adapters prove compatibility.

### CR-011 — Broad Minecraft-version and modloader-version independence — planned

Retain the target of supporting historical Java families from roughly the 1.7.10 era through current releases/latest snapshots where maintainable, plus arbitrary loader versions through adapter/fingerprint lanes. Use runtime fingerprints, mappings where available, semantic/structural scanning, Reflection/MethodHandles, compatibility manifests and fail-closed confidence gates instead of assuming one compiled `net.minecraft.*` API can span all versions.

### CR-012 — External hotload and restart-minimized development — framework

Retain NIO.2 `WatchService`, live script/data/procedural reload, dynamic model/texture/resource reload, Instrumentation redefine for compatible class changes, replaceable classloaders/services for schema changes, virtual registries, known-good rollback, preserved IDE/workspace/selection/undo context and supervised escalation from script/service reload to sidecar/game-process/patched-runtime rebuild only when truly necessary.

### CR-013 — Live MCEdit/Bedrock-style world editor and asynchronous sub-chunk blitting — framework

Retain live editing of generated worlds, palette-indexed 16×16×16 section buffers, parallel array blitting, async workers that calculate immutable deltas, authoritative server-thread commits, deliberate bulk suppression/bypass of per-block lighting churn followed by explicit lighting/heightmap/POI/block-entity/save/client reconciliation, undo/redo/WAL/rollback and mod APIs that can add ores/structures/world changes to already-generated chunks.

### CR-014 — Dynamic Event and Structure Matrix — framework

Retain compiled `.nbt` blueprint loading, chunk/section slicing, real-time massive block-array streaming, procedural environment/structure generation, meteor/citadel/corruption/restoration/ore-style events, AI/mod/script-driven triggers and bounded transactional live placement.

### CR-015 — Terraria-style liquids, paint and world transmutation — planned

Retain Dynamic Liquid Simulation Cells using bounded cellular-automata-style simulation, Arbitrary Block-Paint Layer Matrices including per-face/sub-voxel overlays, Progressive/Progression-Locked World Transmutation State Machines, reversible staged transformations, persistence/replication and Java/Bedrock target adapters.

### CR-016 — Microgrid geometry, curved/slanted shapes and dynamic collision — planned

Retain microgrid/sub-voxel placement, circles, cylinders, curves, wedges, slopes/slanted blocks and arbitrary high-detail meshes; separate authoring/render/collision representations; dynamic `VoxelShape` composition when sufficient; deeper collision-engine augmentation/replacement when vanilla boxes are insufficient; physics-aware manipulation and Java/Bedrock render/collision adapters.

### CR-017 — Hytale-like live in-game asset/model/texture creator — mixed

Retain dynamic model and texture registries, live mesh/vertex sculpting, voxel/sub-voxel editing, texture painting/patching, asset browser/properties, immediate living-world preview, dynamic GPU/resource updates, hot resource refresh without manual F3+T as the target workflow, and target adapters for Java and Bedrock.

### CR-018 — Garry's Mod-like sandbox physics, construction and manipulation — mixed

Retain physically manipulable parts/entities, custom entity physics, force/impulse integration, dynamic constraint graph, weld/hinge/slider/spring/rope constraints, raycast tool-gun, selection/manipulation/undo-redo, server-authoritative multiplayer physics and deterministic replication/prediction budgets.

### CR-019 — Roblox-Studio-like live game-engine execution and tools — mixed

Retain a unified hierarchical real-time scene graph, stable instance IDs, hierarchical property serialization, parenting/transforms, property inspector, translate/rotate/scale gizmos, autonomous drag/drop manipulation, runtime script execution, live world/asset editing, target capability state and integrated desktop/in-game creator workflows.

### CR-020 — Alt-tabless in-game IDE, native console and AI control plane — framework

Retain client-side screen/menu injection, embedded IDE/code console, direct Java compilation, GraalJS/GraalPy execution, input hotkey/keybind registration, menu-button toggles, in-game AI API communication/passthrough, MCP/local-AI/sidecar integration, autonomous client automation and permission-aware live edit/build/test/debug/control loops without leaving Minecraft.

### CR-021 — Generic non-Java modification and external-tool SDK — mixed

Retain a capability-gated modification gateway for embedded JavaScript/Python, external Python, Go, C#, Rust/C++, MCP, Netty/TCP/HTTP/WebSocket where appropriate, shared memory/IPC, filesystem hotload, native plugins, Bedrock Script/Editor and future patch/capability modules. External connection alone must never grant world/server authority.

### CR-022 — Multiplayer live editing, synchronization, culling and concurrency consensus — framework

Retain live authoring with users connected, server-authoritative mutations, custom Netty edit channels, thread-safe/tick-budgeted server scheduling, per-section revisions/optimistic consensus, ACK/retry/reconciliation, permissions/authentication and network replication culling by chunk/view-distance/interest so only relevant edit/physics/world state is streamed.

### CR-023 — Fault-tolerant sandbox and anti-crash architecture — mixed

Retain asynchronous script isolation, budgets, interruption/timeouts, stronger process isolation for non-cooperative/native/untrusted work, global event recovery wrappers, off-thread transactional world sandboxing, inverse deltas/WAL rollback, last-known-good hotload state, crash attribution and supervised restart. Do not falsely promise same-process survival from fatal JVM OOM/native corruption; isolate fault domains where required.

### CR-024 — Bedrock first-class support and cross-edition parity target — mixed

Retain Bedrock behavior/resource packs, Preview Editor extension, Dedicated Server adapters where APIs allow them, Java→Panama→native/shared-memory bridge, neutral bridge frames for UAL/mesh/texture/world/telemetry/script/production traffic, a versioned native companion and the goal of equivalent world/editor/AI/physics/geometry/production capabilities. Where supported Bedrock APIs cannot express a required capability, deeper additive native/bootstrap/executable integration is allowed under exact version/fingerprint and rollback controls.

### CR-025 — User-friendly launcher, Java/runtime acquisition and dependency resolution — mixed

Retain a launcher that starts without Java, downloads/resolves the correct Java runtime, installs legitimate Minecraft versions, supports vanilla/Fabric/Quilt/Forge/NeoForge plus extensible legacy/future adapters, installs mods/resource packs/shaders/datapacks/worlds and all required dependencies, uses the normal supported Microsoft/Minecraft login pipeline, obtains files through legitimate/authorized channels, records hashes/provenance/content locks, offers CurseForge-like ease plus MultiMC/Prism-like detail, and supports isolated instances/snapshots/clone/fork/diff/import/export.

### CR-026 — Mod forking, decompilation and compatibility-analysis pipelines — mixed

Retain authorized local JAR inspection/extraction, metadata/dependency/Mixin/native classification, bytecode structural diff/disassembly, optional user-supplied decompiler integration, provenance, compatibility analysis and fork workflows that do not assume decompiled source can legally be redistributed.

### CR-027 — In-game recording, animation, machinima and production — mixed

Retain deterministic replay/event capture, rational-time timeline, free/first-person/third-person/target/orbit/rail/spline/crane/vehicle cameras, actor/entity transform/animation/pose/IK/equipment/visibility/particle/dialogue/command tracks, shots/takes/sequences/time remapping, real-time capture, deterministic/offline rendering where target stepping permits it, image sequences, replaceable encoder bridge, audio stems/mixing metadata and render passes such as beauty/depth/normals/object IDs/motion vectors where validated, across Java and Bedrock adapters.

### CR-028 — Dynamic dimensions, codec worldgen and zero-entity teleport — mixed

Retain dynamic dimension management, codec-driven worldgen, generated ores/structures/data, zero-entity teleport channels, procedural runtime environment changes, virtual registries and escalation into instrumentation/patch/project-owned registry systems when frozen native registries block required semantics.

### CR-029 — Client rendering interception, volumetrics, PoseStack/IK and custom hitboxes — mixed

Retain real-time rendering overrides, client rendering event pipelines, custom 3D geometry, direct GPU/vertex-buffer paths, PoseStack/matrix interception, inverse kinematics, streamed volumetric rendering matrices, dynamic collision/hitbox integration, culling/batching/performance validation and deeper renderer augmentation/replacement when public hooks are insufficient.

### CR-030 — Additive deep integration, patch manager and engine augmentation — planned

Normal mod APIs are not the product ceiling. Retain loader transforms, JVM agents/instrumentation, native in-process extensions, sidecars, custom launch/bootstrap chains, deterministic version-pinned executable/shared-library patch sets, engine-subsystem augmentation/replacement and project-owned runtime components/forks when necessary. Preserve/recover verified upstream bases, exact hashes/fingerprints, patch graphs, derived-runtime verification and rollback; never use this architecture to bypass authentication, entitlement, DRM or anti-cheat controls.

### CR-031 — Community onboarding and open collaboration — implemented

Retain community landing/onboarding, getting started, contributor setup, architecture tour, testing/evidence guide, support/security routing, code of conduct, glossary, issue/PR templates and explicit distinctions between planned, framework, tested and target-validated features.

### CR-032 — Gridelyx identity and retired-terminology migration — transition

Root brand **Gridelyx** and suite **Gridelyx Studio** are selected. Retain a frozen identity record, inventory of previous Gridelyx naming across docs/source/paths/schemas/protocol/ABI/persisted/generated data, coherent public/source migration, versioned compatibility aliases where required, forbidden-term CI after the safe migration boundary, regenerated AI indexes/docs, and zero unexplained retired project branding in the final current tree. Git-history rewriting is a separate destructive decision.

### CR-033 — Complete dependency, prerequisite, tool and external-program inventory — mixed

Retain a machine-readable and human-readable inventory for every core/subsystem/optional/reference dependency: Java, Gradle, ModDevGradle, Minecraft/NeoForge, ASM, LWJGL, GraalVM, Spotless, Checkstyle, JUnit, ArchUnit, Python, Rust/Cargo, CMake/C++ compilers, Go, .NET/C#, Docker/Dev Containers, FFmpeg/encoder tooling, decompiler integration, Bedrock targets, MCP/AI adapters, provider APIs and the exact reference vault. Known versions must be pinned; unpinned tools must be explicitly marked and receive release-version policy before support claims.

### CR-034 — Advanced feature decision, W5x5x5 and long-horizon planning system — implemented

Retain the Gridelyx Feature Decision Packet process for substantial features. It includes repeated five-level interrogation of **Who, What, When, Where, How and Why**, plus inverse **Who not, What isn't, When isn't, Where isn't, How not and Why isn't**; five analysis perspectives and evidence depths; task decomposition; project-values alignment; cost/time/money/energy diagnostics; 10-minute, 10-hour, 10-day, 10-month, 1-year, 5-year and 10-year horizons; opportunity cost; regret minimisation; reversible-vs-irreversible decisions; risk registers; inversion; second-order thinking; Eisenhower classification; Venn/overlap analysis; structured brainstorming; first-principles thinking; benchmarking; Feynman explanation; MVP; 30/60-minute research timeboxes; pre-mortem; asymmetric-risk analysis; working backward; Pareto/80-20; Critical Path Method; Cynefin; and Kanban. This framework guides sequencing and architecture but does not automatically delete retained features because they are expensive or difficult.

### CR-035 — Stakeholder visibility, diagrams, technical documentation site, API docs and release communications — implemented

Retain a presentation/documentation layer that makes Gridelyx legible without weakening evidence discipline: a **3-bullet value proposition**; Shields.io badges for stable project parameters and real dynamic evidence; a version-controlled hero/concept graphic; visual architecture mapping; Kanban-style stakeholder views; diagrams as code; a buildable technical documentation site; interactive OpenAPI/Swagger documentation; deterministic changelogs; optional AI-assisted release notes constrained to deterministic evidence and human review; machine-readable label/filter taxonomy; user-journey mapping; impact-effort diagnostics; documentation-driven marketing with claim-to-proof rules; and CI that validates/builds these surfaces. Presentation state must remain synchronized with CR requirements, R0-R6 evidence, dependencies and target capability manifests.

## Coverage rule

Every capability requested in this development conversation must have either:

1. implementation/validation evidence; or
2. explicit retained development planning.

Difficulty, absence of a normal mod/API surface, or lack of current validation does not delete a requirement. It changes the integration layer, target support, schedule, risk and evidence burden. Removal or material weakening requires an explicit human-approved superseding decision in `../ai/decision-ledger.json`.

## Synchronization rule

When implementation/planning paths move, update this file and `../platform/chat-requirements.json`. When tool requirements change, update [`DEPENDENCIES_AND_TOOLCHAIN.md`](DEPENDENCIES_AND_TOOLCHAIN.md) and `../platform/toolchain-requirements.json`. When feature-planning policy changes, update [`FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md), [`DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md), the feature-analysis schema and issue template. When stakeholder/documentation surfaces change, update [`STAKEHOLDER_DASHBOARD.md`](STAKEHOLDER_DASHBOARD.md), diagrams, docs-site/API/release communication files and `tools/docs_check.py` as applicable. When readiness changes, update [`FEATURE_MAP.md`](FEATURE_MAP.md)/target manifests only after evidence exists. Broad AI work must consult this ledger through `../ai/context-map.json`.
