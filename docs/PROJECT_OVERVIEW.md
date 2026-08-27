# Gridelyx Studio — full project overview

## Product definition

**Gridelyx** is the root brand. **Gridelyx Studio** is a cross-edition Minecraft launcher, instance manager, mod/content manager, development environment, live creator/sandbox engine, world/voxel authoring suite and machinima/production platform.

It combines a low-friction default UX comparable to consumer launchers with an expert mode that exposes the complete resolved runtime graph: Minecraft version, loader, Java runtime, libraries, mods, dependencies, hashes, provenance, JVM/game arguments, Gridelyx modules and compatibility decisions.

The desktop product must launch even when Java is not installed. Java is a managed dependency of Java Edition instances, not a prerequisite of the Gridelyx desktop shell.

The complete retained capability contract is `CHAT_REQUIREMENTS_TRACEABILITY.md` / `../platform/chat-requirements.json`; this overview describes the product-level composition rather than duplicating every CR item.

## Product planes

1. **Desktop control plane** — profiles, accounts, settings, downloads, instances, updates, diagnostics and launch orchestration.
2. **Acquisition/provenance plane** — official/authorized source adapters, hash verification, licensing/provenance records, cache policy and resumable downloads.
3. **Resolution plane** — Minecraft/loader/Java compatibility, transitive dependencies, optional dependencies, incompatibilities, pack imports and deterministic lockfiles.
4. **Instance plane** — isolated instances, shared content-addressed caches, overrides, saves, configs, resource packs, shaders, servers and portable exports.
5. **Polyloader/runtime plane** — prelaunch bootstrap, UAL, loader adapters, bytecode/native/sidecar escalation and capability negotiation.
6. **Toolkit/creator plane** — live scripting, in-game IDE/AI control, world editing, voxel/mesh/texture editing, microgeometry, scene manipulation, physics/construction and Java/Bedrock adapters.
7. **Production plane** — replay/event capture, camera direction, animation, actor blocking, timeline sequencing, recording, offline frame rendering, audio stems and export.
8. **AI intelligence plane** — compact context maps, generated repository indexes, feature/status maps, decisions, handoff state and task-scoped context packs.
9. **Validation/operations plane** — CI, GameTest, Bedrock gates, native builds, security, provenance, telemetry, profiling, chaos/fault validation, release evidence and rollback.

## User experience modes

### Simple mode

- Choose Minecraft version or modpack.
- Gridelyx resolves the appropriate Java runtime and compatible loader.
- Search Modrinth and authorized CurseForge content.
- One-click install/update with dependency resolution.
- Clear conflicts with suggested fixes.
- Play, edit, create or produce without seeing implementation detail unless attention is needed.

### Advanced mode

- Pin exact Minecraft, loader, Java vendor/build and architecture.
- Inspect/override memory, JVM args, game args and environment.
- View the dependency graph and why each artifact was selected/rejected.
- Inspect hashes, source, licence, compatibility metadata and lockfile diffs.
- Toggle Gridelyx modules, native bridges and experimental/deep capabilities.
- Clone/fork/compare instances and modpacks.
- Inspect reload requirements and target capability state.
- Export diagnostic bundles without secrets.

## Supported acquisition principle

“Any version / any loader / any mod” is implemented as an extensible adapter/resolver target, not by guessing arbitrary URLs or claiming impossible universal compatibility.

Built-in/planned adapters cover Mojang/vanilla, Fabric, Quilt, Forge and NeoForge. Legacy/future loaders use the same adapter contract or explicit user-supplied launcher-profile import. Content acquisition uses official or authorized channels and never bypasses an author/provider distribution restriction.

The dependency/tool/provider inventory is in `DEPENDENCIES_AND_TOOLCHAIN.md`, `../platform/toolchain-requirements.json` and `../studio/providers/providers.json`.

## Managed Java

Resolution order:

1. Read selected Minecraft version metadata and honor its declared Java runtime/component when available.
2. Prefer Mojang-managed runtime for ordinary play when available.
3. Allow a user-selected compatible local JVM.
4. Allow managed Eclipse Temurin/Adoptium fallback.
5. Record vendor, version, architecture, path and provenance in the instance lock.

Gridelyx must not infer Java major solely from a hard-coded Minecraft-version table where authoritative metadata exists.

## Content and dependency resolution

Every resolved artifact becomes a dependency-graph node. Edges are `required`, `optional`, `embedded` or `incompatible`. The resolver must:

- filter by game version, loader, side/client-server constraints and release channel;
- follow required transitive dependencies;
- expose optional dependencies instead of silently forcing them;
- reject incompatible pairs;
- produce deterministic install order;
- retain the reason each candidate was selected/rejected;
- generate a content lock with provider/source/hash/provenance;
- avoid silently replacing a specifically requested file with “latest”.

## Modpack and instance interchange

Planned adapters include:

- Modrinth `.mrpack`;
- CurseForge manifests where API/distribution rules permit;
- Prism Launcher;
- MultiMC-compatible instances;
- vanilla launcher profiles;
- raw `.minecraft`/mods-folder import;
- Gridelyx portable instance bundle.

Imports are normalized into Gridelyx instance/resolution state rather than executed blindly.

## Polyloader and version independence

Gridelyx's Java runtime is designed both below and above ordinary loader APIs:

- prelaunch bootstrap and Java Instrumentation/ASM where required;
- loader-neutral UAL operations;
- Fabric/Quilt/Forge/NeoForge/vanilla/legacy-family adapters;
- runtime fingerprints and semantic/structural discovery;
- Reflection/MethodHandles where stable compile-time symbols are unavailable;
- isolated sideload containers and virtual definitions;
- explicit capability classification: live-safe, emulated, prelaunch-required or unsupported.

The target spans older version families (including approximately the 1.7.10 era) through current/snapshot families where technically maintainable. This is a roadmap target, not a present claim that every version/modloader combination already works.

## Creator suite

Gridelyx Studio creator mode unifies:

- in-game IDE, native/developer console and AI communication plane;
- direct Java compilation and live Java/GraalJS/GraalPy scripting;
- non-Java/external-tool modification via capability-gated bridges;
- loader-neutral UAL operations;
- live world/structure editing over already-generated chunks;
- asynchronous section-array blitting and transactional commits;
- procedural generation and Dynamic Event/Structure Matrix triggers;
- dynamic liquid/paint/transmutation roadmap;
- mesh, voxel, texture and microgeometry authoring;
- curved/cylindrical/slanted custom render and collision geometry;
- scene hierarchy, property serialization and transform gizmos;
- physics, constraints and tool-gun construction;
- AI-assisted content/runtime automation;
- Bedrock Add-On/Editor/native-companion targets;
- hot reload/restart classification, supervised recovery and rollback.

## External and non-Java modification

Gridelyx provides/plans explicit capability surfaces for:

- embedded JavaScript/Python via GraalVM;
- external Python;
- Go;
- C#/.NET;
- Rust/C++;
- MCP;
- Netty/TCP/HTTP endpoints where appropriate;
- shared-memory/IPC;
- native FFM/Panama;
- filesystem hotload;
- Bedrock Script/Editor/native adapters.

Connecting a tool does not grant implicit multiplayer/server authority.

## World, geometry and sandbox systems

The retained architecture includes:

- MCEdit/Bedrock-style live world editing;
- parallel 16×16×16 section blitting and async computation;
- explicit lighting/heightmap/POI/block-entity/client/save reconciliation;
- multiplayer revisions/consensus/culling;
- dynamic NBT/procedural events and structures;
- Terraria-style liquid cells, paint matrices and progression transmutation;
- microgrids, sub-voxel overlays, curves/circles/cylinders/slopes;
- dynamic/custom hitboxes and deeper collision augmentation where needed;
- real-time rendering overrides and volumetric matrices;
- Garry's Mod-like physics/constraint/tool-gun construction;
- Roblox-Studio-like hierarchy/properties/gizmos/live execution;
- Hytale-like live model/mesh/voxel/texture creation.

## Fault tolerance and deep integration

Gridelyx does not promise that arbitrary same-process corruption can never crash. Instead it uses layered containment:

- bounded workers and script budgets;
- recovery wrappers;
- transactional world changes/WAL/rollback;
- last-known-good hotload states;
- process isolation for crash-prone/untrusted/native workloads;
- supervised broader restart with preserved editor/project context.

Normal mod APIs are not the product ceiling. Where needed, capabilities may escalate through loader transforms, JVM agents, native components, sidecars, launch/bootstrap changes, deterministic executable/shared-library patches or project-owned engine/runtime components. Deep integration preserves a verified base, exact fingerprints, an attributable patch/overlay graph and rollback. See `DEEP_INTEGRATION_ARCHITECTURE.md`.

## Bedrock

Bedrock is a first-class target with:

- stable behavior/resource-pack plane;
- preview Editor extension plane;
- Dedicated Server adapters where APIs permit;
- Java FFM/Panama/native/shared-memory bridge;
- versioned native companion;
- neutral operations for creator/world/telemetry/script/production traffic;
- explicit parity gaps until target evidence exists.

Equivalent creator/world/AI/physics/geometry/production capabilities remain the goal. Where public Bedrock APIs are insufficient, deeper additive integration is a separately versioned/fingerprint-gated adapter problem, not an excuse to drop the requirement.

## Machinima and production

Production mode is a non-destructive scene/timeline layer over the selected runtime.

Core systems include:

- deterministic replay/event log with tick/time anchors;
- free, first-/third-person, target, orbit, rail, spline and future crane/vehicle cameras;
- position/rotation/FOV/focus/exposure tracks;
- actor/entity animation and pose/IK layers;
- transform, visibility, item, particle, command and dialogue cues;
- markers, takes, shots, sequences and time remapping;
- real-time capture and target-capable deterministic offline capture;
- configurable resolution/frame rate/supersampling/render passes where validated;
- audio stem capture/mix metadata;
- image sequences and replaceable provenance-recorded encoder bridge.

## Storage model

- `instances/` — isolated writable instances outside the source checkout in production.
- content-addressed cache — immutable acquired artifacts keyed by cryptographic hash.
- per-instance lockfile — exact resolved graph and provenance.
- mutable instance overlays — configs, saves, screenshots, recordings and edits.
- creator/production projects — structured scene/world/replay data referencing stable IDs and source locks.

Hard links/reflinks may deduplicate immutable artifacts; mutable state must not be accidentally shared.

## Security model

- deny unknown network hosts by default;
- no credentials in repositories/logs/locks/diagnostic exports;
- provider tokens in OS credential storage;
- local SHA-256 for downloads plus upstream integrity verification when available;
- explicit provenance for executable/native tools;
- path-sanitized archive imports;
- mods/scripts/native tools treated as executable/untrusted according to boundary;
- experimental bytecode/native/patch paths opt-in and version/fingerprint gated;
- no use of deep integration to bypass authentication, entitlement, DRM or anti-cheat controls.

## Project intelligence and community

The whole-chat scope is retained in `CHAT_REQUIREMENTS_TRACEABILITY.md` and enforced by CI. Human onboarding lives in `../COMMUNITY.md`, `../CONTRIBUTING.md`, `community/`, `../SUPPORT.md` and `../CODE_OF_CONDUCT.md`. AI continuity uses `../AI_HANDOFF.md`, `../AGENTS.md`, `../ai/context-map.json` and deterministic repository indexing/context packs.

## Definition of done

A feature is not “supported” because an interface exists. Gridelyx uses R0-R6 from idea/contract through compile/static evidence, automated tests, headless integration, interactive target validation and release-candidate packaging/migration/rollback evidence. `FEATURE_MAP.md` records current evidence state.
