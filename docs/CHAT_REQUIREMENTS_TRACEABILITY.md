# Whole-chat requirements traceability

Status: **canonical retained-scope ledger**

This document preserves the requirements requested throughout the project-development conversation. It is intentionally brand-neutral while the public identity is under rebrand.

A requirement appearing here is **in project scope** until an explicit human-approved decision supersedes it. Presence does not imply that the feature already works. Evidence/readiness remains R0-R6 and is tracked through implementation, tests, `FEATURE_MAP.md`, target capability manifests and CI.

Machine-readable coverage is in `../platform/chat-requirements.json`; `../tools/chat_requirements_check.py` verifies that every CR group has a valid state and at least one existing implementation/evidence/planning path.

## State vocabulary

- **implemented** — the requested repository/project artifact exists; runtime claims still depend on target evidence.
- **framework** — reusable contracts/source exist but target integration or stronger validation remains.
- **planned** — explicitly retained development work.
- **mixed** — some foundations exist and substantial work remains.
- **transition** — controlled migration work, currently the rebrand.

## Retained requirements

### CR-001 — Reproducible Minecraft R&D foundation — mixed

NeoForge/Minecraft/Java/Gradle locks; exact MDK/NeoForge-installer/JDK/LWJGL vault provenance; deterministic reconstruction/indexing; on-demand source/reference lookup; reference corpora isolated from runtime dependencies; independent `mods/<mod_id>` Gradle workspaces for multiple JARs side-by-side.

### CR-002 — Quality, GitHub environment and build integrity — implemented

Spotless, Checkstyle, CodeQL, issue/PR templates, Codespaces/devcontainer, Copilot setup/tuning, automated diagnostics, Gradle execution permissions, global ignore/editor rules, master `build.gradle` SHA-256 lock, non-Java script gatekeeper, security/licensing/provenance controls.

### CR-003 — Canonical mod template, registries and generated data/assets — mixed

Global Gradle variables; loader manifest; main mod class; global registry controller; creative-tab anchor; automated language provider; localization/asset blueprints; deterministic datagen and generated resources; codec-driven worldgen/registry extensions; algorithmic asset/data loops; procedural matrix generation; CSV/spreadsheet-to-recipe pipelines.

### CR-004 — Advanced JVM/bytecode modification engine — framework

Java Instrumentation (`premain`/`agentmain`), ASM generation/transformation, dynamic Mixin/redirector architecture, bytecode diff/disassembly/decompiler analysis, MethodHandles/reflection binding, compatible class redefine/hotswap, replaceable classloaders/services for schema changes, direct Java source-string compilation, mapping/descriptor fingerprinting and fail-closed behavior.

### CR-005 — Multithreaded tasking, synchronization and dynamic data — framework

Custom bounded worker pools, asynchronous computational tasking, coalescing/multithreaded syncing, versioned dynamic data, server-safe scheduling, backpressure, revisioned state and consensus mechanisms.

### CR-006 — Native/GPU/Panama/IPC/cross-language execution — framework

Java FFM/Project Panama, Rust and C++ native extensions, shared memory, multi-process pipelines, Python/Go/C# bridges, neutral framed protocols, Netty/IPC and local web endpoints, direct LWJGL/OpenGL vertex-buffer control, render-thread/context rules and versioned native ABI/fingerprints.

### CR-007 — MCP, local indexing, AI documentation and project intelligence — mixed

MCP endpoint/tool routing, local vector indexing, deterministic repository/chunk indexes, task-scoped AI context packs, future hash-keyed incremental embeddings, AI auto-documentation, project handoff/context files, and Project-Athena-equivalent engineering continuity: AI role boundaries, work state, decision/assumption ledgers and drift mitigation without copying Athena's identity/institutional model.

### CR-008 — Autonomous testing, security, profiling and chaos — mixed

JUnit/test doubles, ArchUnit, headless NeoForge GameTest, optional version-confirmed MCTester adapter, CodeQL, automated diagnostics, JFR/advanced profiling, telemetry, deterministic chaos/fault injection, packet delay/drop, worker saturation, bridge-disconnect and reload-storm validation.

### CR-009 — Full project planning/management and human/AI continuity — implemented

Project overview, ownership structure, roadmap, feature/readiness map, detailed TODO, issues/PR workflow, decisions/assumptions/recovery state, requirements traceability, AI handoff/organization/drift controls, community onboarding and evidence-first contribution rules.

### CR-010 — Ground-up/top-down Polyloader and UAL — framework

Prelaunch bootstrap below loaders when needed; Instrumentation/ASM interception; Unified Abstraction Layer for registry/event/network/resource/render/world/input/lifecycle operations; Fabric, Quilt, Forge, NeoForge, vanilla and Liteloader/legacy research; loader-call translation; isolated sideload containers; `LIVE_SAFE`/emulated/prelaunch-required/unsupported negotiation; cross-loader execution such as Fabric-on-NeoForge only when real adapters prove compatibility.

### CR-011 — Broad Minecraft/loader version independence — planned

Target historical families including approximately 1.7.10-era Java through current/latest/snapshots where maintainable; version-family bootstrap lanes; runtime fingerprinting; mappings where available; semantic/structural scanning; Reflection/MethodHandles; compatibility manifests; confidence gates; deeper additive patch layers when normal adapters are insufficient.

### CR-012 — External hotloading and restart-minimized development — framework

NIO.2 `WatchService`; live script/data/procedural reload; dynamic asset/model/texture reload; Instrumentation redefine; schema-changing service/classloader replacement; virtual registries; last-known-good rollback; preserved IDE/editor/selection/undo context; reload scope escalation from script/service to sidecar/game-process and patched-runtime rebuild when technically necessary.

### CR-013 — Live MCEdit/Bedrock-style world editor and parallel sub-chunk blitter — framework

Live editing of already-generated worlds; palette-indexed 16×16×16 section arrays; parallel array blitting; async workers producing immutable deltas; authoritative server-thread commits; deliberate bulk bypass of per-block lighting churn followed by explicit lighting/heightmap/POI/block-entity/save/client reconciliation; undo/redo/WAL; mod APIs for live ores, structures and world modification.

### CR-014 — Dynamic Event and Structure Matrix — framework

Compiled `.nbt` blueprints; slicing across chunks/sub-chunks; real-time massive block-array streaming; procedural structures/environment assets; live meteor/citadel/corruption/restoration/ore-style events; AI/mod/script triggers; transactional bounded application over generated terrain.

### CR-015 — Terraria-style liquid, paint and world-transmutation systems — planned

Bounded cellular-automata liquid cells, arbitrary per-block/per-face paint matrices, sub-voxel paint/material overlays, progressive/progression-locked global transmutation state machines, staged reversible transformation/rollback, persistence/replication and Java/Bedrock target adapters.

### CR-016 — Microgeometry and non-cubic collision/rendering — planned

Microgrid/sub-voxel placement; circles, cylinders, curves, wedges, slopes and slanted blocks; arbitrary high-detail meshes; separate authoring/render/collision representations; dynamic `VoxelShape` synthesis where sufficient; deeper collision-engine augmentation/replacement where vanilla boxes are inadequate; physics-aware manipulation; Java/Bedrock adapters.

### CR-017 — Hytale-like live asset/model/texture creator — mixed

Dynamic model registry, mesh/vertex sculpting, texture painting/patching, voxel/sub-voxel editor, asset browser/properties, immediate in-world preview, dynamic GPU/resource updates, resource refresh without manual F3+T as the target workflow, and cross-edition adapters.

### CR-018 — Garry's Mod-like sandbox physics/construction — mixed

Physically manipulable parts/entities, custom physics, force/impulse integration, dynamic constraint graph, weld/hinge/slider/spring/rope constraints, raycast tool-gun, selection/manipulation/undo, server-authoritative multiplayer physics and deterministic replication/prediction budgets.

### CR-019 — Roblox-Studio-like live game/scene engine — mixed

Unified hierarchical scene graph, stable instance IDs, property serialization, parent/child transforms, translate/rotate/scale gizmos, drag/drop manipulation, live runtime script execution, asset/world editing, properties inspector, target capability state, hot reload and desktop/in-game creator integration.

### CR-020 — Alt-tabless in-game IDE, console and AI control plane — framework

Client screen/menu injection, embedded code IDE/console, direct Java compilation, GraalJS/GraalPy execution, hotkeys/menu toggles, AI API/MCP/local-sidecar passthrough, autonomous high-level client automation, live logs/diagnostics and permission-aware edit/build/test/control loops without leaving the game.

### CR-021 — Generic non-Java modification/external-tool SDK — mixed

Permissioned modification gateway for GraalJS/GraalPy, external Python, Go, C#, Rust/C++, MCP, Netty/TCP/HTTP/WebSocket where appropriate, shared memory/IPC, filesystem hotload, Bedrock Script/Editor and future patch/capability modules. Connection alone never grants world/server authority.

### CR-022 — Multiplayer live authoring, state synchronization, culling and consensus — framework

Live editing with users connected; server-authoritative mutation; custom Netty edit channels; thread-safe/tick-budgeted server scheduling; per-section revisions/optimistic consensus; ACK/retry/reconciliation; network culling by chunk/view distance/interest; permissions/authentication; relevant-only replication of creator/physics/world state.

### CR-023 — Fault-tolerant sandbox and anti-crash architecture — mixed

Script budgets, cooperative interruption/timeouts, process isolation for non-cooperative/untrusted/native workloads, global recovery boundaries, off-thread transactional world sandbox, inverse deltas/WAL rollback, last-known-good hotload recovery, crash attribution and supervised restart. Do not falsely promise same-process survival from fatal JVM OOM/native corruption.

### CR-024 — Bedrock first-class support and parity target — mixed

Stable Add-On runtime, preview Editor plane, Dedicated Server adapters where APIs permit, Java→Panama→native/shared-memory bridge, neutral bridge frames for UAL/mesh/texture/world/telemetry/script/production data, versioned native companion, and the goal of equivalent world/editor/AI/physics/geometry/production features. When supported APIs are insufficient, deeper additive native/bootstrap/executable integration is explicitly allowed under exact version/fingerprint gates.

### CR-025 — User-friendly launcher/runtime/content acquisition — mixed

Desktop launcher that starts without Java; legitimate Minecraft version and Java acquisition; official/authorized loader/content channels; Microsoft/Minecraft login pipeline; vanilla/Fabric/Quilt/Forge/NeoForge plus extensible legacy/future adapters; mods/resource packs/shaders/datapacks/worlds and dependencies; hashes/provenance/content locks; CurseForge-like simple UX plus Prism/MultiMC-like expert detail; isolated instances, snapshots, clone/fork/diff/import/export.

### CR-026 — Mod forking, decompilation and compatibility analysis — mixed

Authorized local JAR inspection/extraction, metadata/dependency/Mixin/native classification, bytecode structural diff, optional user-supplied decompiler, tool/source provenance, fork workflows and license/redistribution review before publication.

### CR-027 — Recording, animation and machinima/production suite — mixed

In-game recording/replay, deterministic event log, rational-time timeline, free/first-/third-person/target/orbit/rail/spline/crane/vehicle cameras, actor animation/pose/IK/equipment/visibility/particle/dialogue/command tracks, shots/takes/sequences/time remapping, real-time capture, offline deterministic rendering where target hooks permit, image sequences, encoder bridge, audio stems/mix metadata and advanced render passes with Java/Bedrock adapters.

### CR-028 — Dynamic dimensions, worldgen and zero-entity teleport — mixed

Dynamic dimension management, codec-driven worldgen, generated ores/structures/data, zero-entity teleport channels, procedural runtime environment changes, virtual registries and escalation into instrumentation/patch/project-owned registry systems when frozen native registries block required semantics.

### CR-029 — Rendering interception, volumetrics, pose and IK — mixed

Real-time client rendering overrides and render events, custom 3D geometry, direct GPU/vertex-buffer paths, PoseStack/matrix interception, inverse kinematics, dynamic hitbox integration, streamed volumetric rendering matrices, culling/batching/performance validation and deeper renderer augmentation/replacement when public hooks cannot meet requirements.

### CR-030 — Additive deep integration and patch manager — planned

Normal mod APIs are not the ceiling. The project may escalate through loader transforms, JVM agents, native extensions, sidecars, custom bootstrap, deterministic version-pinned executable/shared-library patch sets, engine subsystem augmentation/replacement and project-owned runtime forks/components. Preserve/recover a verified upstream base, exact hashes/fingerprints, patch graph, derived-runtime verification and rollback. Do not use this to bypass authentication, entitlement, DRM or anti-cheat controls.

### CR-031 — Community onboarding and open collaboration — implemented

Community landing guide, getting started, contributor onboarding, architecture tour, testing/evidence guide, support/security routing, code of conduct, glossary, issue/PR templates and explicit distinction between planned/framework/tested/target-validated capabilities.

### CR-032 — Rebrand and complete retired-terminology migration — transition

Select a collision-screened replacement identity; freeze display/case/slug/short/protocol/package/executable/data-root values; inventory retired terms across text/paths/source/schemas/ABI/protocol/generated artifacts; migrate project-owned terminology; use compatibility aliases only when persistent/wire consumers require them; add forbidden-term CI; regenerate indexes/docs; require zero unexplained retired-brand occurrences in the current tree; treat Git-history rewriting as a separate destructive decision.

## Coverage rule

Every capability requested in this development conversation must have one of two states:

1. implementation/validation evidence; or
2. explicit retained development planning.

A later agent/contributor may not remove a requirement because it is hard, unsupported by normal modding surfaces, or not yet validated. Such constraints change the integration layer, target coverage, schedule or evidence requirement. Removal/material weakening requires an explicit human-approved superseding decision in `../ai/decision-ledger.json`.

## Synchronization rule

When implementation or planning paths move, update both this ledger and `../platform/chat-requirements.json`. When readiness changes, update `FEATURE_MAP.md`/target manifests only after evidence exists. Broad AI work must consult this ledger through `../ai/context-map.json`.
