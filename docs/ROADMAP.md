# Project roadmap

The public product identity is under rebrand. This roadmap uses neutral terminology; retained project scope is canonicalized in `CHAT_REQUIREMENTS_TRACEABILITY.md` and `../platform/chat-requirements.json`.

## Readiness scale

- **R0** idea only
- **R1** contract/schema defined
- **R2** compiles or deterministic static validation passes
- **R3** automated unit/integration tests pass
- **R4** headless Minecraft/Bedrock integration evidence passes
- **R5** interactive client validation passes
- **R6** release candidate with packaging, migration and rollback evidence

## Phase 0 — project skeleton, continuity and requirements control

**Goal:** make the repository understandable, recoverable and resistant to scope/evidence drift before product implementation accelerates.

- [x] Cross-edition launcher + creator + runtime + production product definition.
- [x] Java/Bedrock split behind neutral capability contracts.
- [x] Native bridge foundation.
- [x] Studio core crate and provider/dependency contracts.
- [x] Human project overview, structure, roadmap, feature map and TODO.
- [x] AI handoff/context/index design.
- [x] Project-Athena-equivalent engineering continuity, AI role organization and drift mitigation.
- [x] Deterministic repository index/context-pack generation in CI.
- [x] Canonical 32-group whole-chat requirements traceability ledger and machine-readable manifest.
- [x] Requirements-path CI enforcement.
- [x] Community onboarding/support/conduct/evidence documentation.
- [x] Additive deep-integration architecture from normal APIs through project-owned runtime components.
- [ ] Generate readiness/evidence summaries from manifests/tests automatically where practical.

## Phase 1 — launcher and instance foundation

- [ ] Native desktop shell that runs without Java installed.
- [ ] OS paths, settings, secure credential storage and update channel.
- [ ] Supported Microsoft/Minecraft authentication flow.
- [ ] Mojang version-manifest ingestion and version JSON resolution.
- [ ] Legitimate installation of historical/current/snapshot Minecraft versions exposed by supported metadata.
- [ ] Managed Java runtime resolver/download/cache plus compatible local JVM discovery.
- [ ] Vanilla instance creation and launch-plan generation.
- [ ] Content-addressed download cache with resumable transfers and SHA-256.
- [x] Initial instance/content-lock schemas.
- [ ] Per-instance persistence, overlay directories and diagnostics.
- [ ] Simple consumer UX and Advanced Prism/MultiMC-style inspection/control over the same resolver truth.

**Exit:** create and launch a clean vanilla instance on supported desktop platforms at R5.

## Phase 2 — loaders, Polyloader and content providers

- [ ] Fabric/Quilt/Forge/NeoForge end-to-end launcher adapters.
- [ ] Legacy/future external loader adapter contract and version-family bootstrap lanes.
- [ ] Standalone prelaunch Java agent/bootstrap.
- [ ] Runtime fingerprint/mapping/MethodHandle binding database.
- [ ] First real source-loader → UAL → foreign-loader compatibility adapter.
- [ ] Safe sideloading of compatible foreign-loader mods behind capability analysis.
- [ ] Explicit prelaunch handling for Mixins/coremods/access wideners/early transform services.
- [ ] Modrinth provider.
- [ ] Authorized CurseForge provider respecting distribution restrictions.
- [x] Core required/incompatible dependency graph and deterministic install ordering.
- [ ] Full transitive/range/loader/game/side-aware dependency solver with explanation graph.
- [ ] Mods/resource packs/shaders/datapacks/worlds/plugins categories.

**Exit:** consumer-friendly one-click modded instance creation with expert dependency/runtime inspection at R5.

## Phase 3 — modpack/import/export and instance fleet management

- [ ] Modrinth/CurseForge pack interchange under provider rules.
- [ ] Prism/MultiMC and vanilla/raw-instance import.
- [ ] Native portable project/instance format after rebrand identity is frozen.
- [ ] Instance clone/fork/diff/merge.
- [ ] Snapshot/restore and transactional update rollback.
- [ ] Server-instance generation and client/server side filtering.
- [ ] Authorized mod-JAR fork/decompile/diff/provenance workflow.

## Phase 4 — integrated live creator runtime

### 4A — in-game development and hotload

- [ ] Real client screen/menu/keybind integration for embedded IDE and creator tools.
- [ ] Direct Java compile/redefine/service-replacement workflow.
- [ ] GraalJS/GraalPy live editing.
- [ ] MCP/local-AI/sidecar communication and bounded automation controller.
- [ ] Generic non-Java extension SDK across script, sidecar, native, IPC/web and filesystem transports.
- [ ] External `WatchService` hotload with last-known-good rollback.
- [ ] Preserve editor/session state across broader supervised restart scopes.

### 4B — live world authoring

- [ ] Exact Java chunk-section mutation adapter.
- [ ] Parallel 16³ array blitting with off-thread delta computation and server-thread commits.
- [ ] Deferred lighting during structural manipulation followed by explicit reconciliation.
- [ ] NBT blueprint import over already-generated chunks.
- [ ] Dynamic Event and Structure Matrix for meteors/citadels/ores/procedural events.
- [ ] Durable undo/redo, WAL and restart recovery.
- [ ] Multiplayer permission, revision consensus, ACK and interest/culling replication.

### 4C — world simulation and microgeometry

- [ ] Cellular-automata dynamic liquid cells.
- [ ] Arbitrary block/face paint matrices and sub-voxel overlays.
- [ ] Progression-locked/reversible world transmutation states.
- [ ] Microgrid placement below one vanilla block.
- [ ] Circles, cylinders, curves, wedges, slopes and arbitrary custom meshes.
- [ ] Dynamic collision generation and deeper collision augmentation when `VoxelShape` is insufficient.
- [ ] Volumetric preview matrices and optimized render/culling adapters.

### 4D — asset editor, scene graph and sandbox construction

- [ ] Hytale-style live voxel/model/mesh/texture authoring and immediate in-world preview.
- [ ] Dynamic model/texture registries and GPU resource updates.
- [ ] Unified hierarchical scene graph and property serialization.
- [ ] Translate/rotate/scale gizmos and drag/drop part manipulation.
- [ ] Garry's Mod-style physics bodies, tool-gun and weld/hinge/slider/spring/rope constraints.
- [ ] Roblox-Studio-like properties, runtime script execution and live scene manipulation.
- [ ] Dynamic dimensions and zero-entity teleport channels.

## Phase 5 — cross-edition creator parity and deep integration

- [ ] Maintain machine-readable Java↔Bedrock capability parity matrix.
- [ ] Bedrock stable Add-On and Editor creator adapters.
- [ ] Bedrock native shared-memory adapters for mesh/texture/world/telemetry/production frames.
- [ ] Bedrock world editor, structures/events, liquids/paint/microgeometry/physics parity targets.
- [ ] Bedrock in-game AI/IDE/automation and production parity targets where technically achievable.
- [ ] Patch-manager schema and immutable-base → derived-runtime pipeline.
- [ ] Version/fingerprint-gated executable/shared-library patch modules where shallower integration cannot supply required capability.
- [ ] Project-owned renderer/physics/collision/registry/network/storage augmentation interfaces where upstream subsystems become limiting.

## Phase 6 — animation, replay and machinima

- [x] Neutral production architecture defined.
- [x] Rational-time camera keyframe foundation and deterministic interpolation smoke test.
- [x] Initial production-project schema linked to exact source instance/content lock.
- [ ] Typed production track/timeline model.
- [ ] Free/first/third-person/target/orbit/rail/spline/crane/vehicle camera rigs.
- [ ] In-game animation, pose/IK and actor-track editor.
- [ ] Replay/event recording with tick anchors and compatibility metadata.
- [ ] Shot/take/sequence editor and cue tracks.
- [ ] Real-time video/frame capture.
- [ ] Deterministic offline frame renderer where target hooks permit.
- [ ] Image sequence/encoder bridge and audio stems.
- [ ] Java and Bedrock production adapters at R5.

## Phase 7 — professional production, collaboration and operations

- [ ] Render passes: beauty/depth/normals/object IDs/motion vectors/material IDs where supported.
- [ ] Motion blur, depth-of-field, supersampling and tiled high-resolution capture.
- [ ] Multi-camera/multi-take sync and collaborative project model.
- [ ] Remote render worker protocol and DCC/NLE interchange research.
- [ ] JFR/OpenTelemetry profiling and trace propagation.
- [ ] Chaos campaigns, fuzzing and provider/network/bridge failure testing.
- [ ] SBOM, licence/provenance and security reports.
- [ ] Signed binaries/installers/update metadata and stable/beta/nightly channels.
- [ ] Crash recovery, migration and patch rollback evidence.

## Phase 8 — rebrand completion and release hardening

- [ ] Select and freeze replacement public identity and technical identifiers.
- [ ] Inventory and migrate all retired terminology across docs/source/paths/protocol/ABI/generated material.
- [ ] Add forbidden-terminology CI with only explicit compatibility/provenance exceptions.
- [ ] Regenerate AI indexes/autodoc and require a zero-unexplained-occurrence current-tree scan.
- [ ] Update repository metadata/slug where supported.
- [ ] Keep Git-history rewriting as a separate explicit destructive decision.

## Ongoing rules

Every provider, loader, Minecraft/Bedrock release, deep patch and creator/production feature is capability-negotiated. New versions may move a capability backward in readiness until revalidated; UI and docs must show that state rather than presenting stale compatibility as fact.

Every retained request in `CHAT_REQUIREMENTS_TRACEABILITY.md` must continue to have implementation evidence or explicit planning. Difficulty or lack of a normal API is not by itself grounds for deleting scope.
