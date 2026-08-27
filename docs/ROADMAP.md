# Gridelyx Studio roadmap

## Readiness scale

- **R0** idea only
- **R1** contract/schema defined
- **R2** compiles or deterministic static validation passes
- **R3** automated unit/integration tests pass
- **R4** headless Minecraft/Bedrock integration evidence passes
- **R5** interactive client validation passes
- **R6** release candidate with packaging, migration and rollback evidence

## Phase 0 — product skeleton and knowledge system

**Goal:** make the repository understandable and extensible before UI work accelerates.

- [x] Gridelyx Studio product definition.
- [x] Java/Bedrock engine split behind common abstractions.
- [x] Native VFSB bridge foundation.
- [x] Studio core crate and provider/dependency contracts.
- [x] Human project overview, structure, roadmap and feature map.
- [x] AI handoff/context/index design.
- [ ] Make repository-index/context-pack generation mandatory in CI.
- [ ] Add architecture decision record template and decision index generation.
- [ ] Add machine-readable capability/readiness evidence ledger.

**Exit:** R2 repository intelligence and architecture gate.

## Phase 1 — launcher and instance foundation

- [ ] Native desktop shell that runs without Java installed.
- [ ] OS paths, settings, secure credential storage and update channel.
- [ ] Microsoft/Minecraft account authentication adapter using supported OAuth flows.
- [ ] Mojang version-manifest ingestion and version JSON resolution.
- [ ] Managed Java runtime resolver/download/cache.
- [ ] Vanilla instance creation and launch-plan generation.
- [ ] Content-addressed download cache with resumable transfers and SHA-256.
- [ ] Per-instance lockfile, overlay directories and diagnostics.
- [ ] Simple/advanced UI modes.

**Exit:** create and launch a clean vanilla instance on Windows/Linux/macOS at R5.

## Phase 2 — loaders and content providers

- [ ] Fabric adapter using Fabric Meta profiles.
- [ ] Quilt adapter using Quilt Meta profiles.
- [ ] Forge adapter using official installer/Maven data.
- [ ] NeoForge adapter using official Maven/installer data.
- [ ] Generic external/legacy loader adapter contract.
- [ ] Modrinth search/version/download/dependency provider.
- [ ] CurseForge provider behind user/developer API-key configuration and current API terms.
- [ ] Required/optional/incompatible dependency solver with explanation graph.
- [ ] Mod enable/disable, update, pin, replace and conflict repair.
- [ ] Resource pack, shader, datapack and world content categories.

**Exit:** consumer-friendly one-click modded instance creation with expert graph inspection at R5.

## Phase 3 — modpack/import/export and fleet management

- [ ] Modrinth pack import/export.
- [ ] CurseForge pack import/export within provider distribution rules.
- [ ] Prism/MultiMC instance import.
- [ ] Vanilla launcher/raw `.minecraft` import.
- [ ] Gridelyx portable instance format.
- [ ] Instance clone/fork/diff/merge for configs and content locks.
- [ ] Server-instance generation and client/server side filtering.
- [ ] Update transaction with snapshot and rollback.
- [ ] Offline mode using already-authorized cached artifacts.

## Phase 4 — integrated creator studio

- [ ] Desktop workspace/project browser tied to selected instance.
- [ ] In-game IDE, console and AI command plane integration.
- [ ] Java/Fabric/Forge/NeoForge/Quilt adapter capability matrix.
- [ ] Bedrock Add-On and Editor project import/export.
- [ ] Live world/structure/voxel/mesh/texture editing.
- [ ] Scene hierarchy, property inspector and transform gizmos.
- [ ] Hot reload classification: live-safe, emulated, prelaunch/restart, unsupported.
- [ ] Toolkit module manager per instance.
- [ ] Creator project templates and build/publish workflows.

## Phase 5 — animation, replay and machinima

- [x] Neutral production architecture defined.
- [ ] Production timeline data model and deterministic sampling tests.
- [ ] Camera director with free/orbit/target/rail/spline modes.
- [ ] Entity/actor animation and pose tracks.
- [ ] Replay/event recording with tick anchors and compatibility metadata.
- [ ] Shot/take/sequence editor and marker/cue tracks.
- [ ] Real-time screenshot/video capture adapter.
- [ ] Deterministic offline frame renderer where target hooks permit.
- [ ] Audio stem capture/mix metadata.
- [ ] FFmpeg/image-sequence encoder bridge with explicit external-tool provenance/licensing.
- [ ] Java client production adapter at R5.
- [ ] Bedrock production adapter using supported Script/Camera APIs plus optional versioned native capture adapter.

## Phase 6 — professional production and collaboration

- [ ] Render passes: beauty/depth/normals/object IDs/motion vectors where supported.
- [ ] Motion blur, depth-of-field, supersampling and tiled high-resolution capture.
- [ ] Non-destructive colour/exposure metadata and LUT workflow.
- [ ] Multi-camera/multi-take sync.
- [ ] Collaborative project lock/merge model.
- [ ] Remote render worker protocol.
- [ ] Asset/replay dependency packaging.
- [ ] NLE/DCC interchange research (EDL/OTIO/glTF/USD where technically/licensing appropriate).

## Phase 7 — hardening and release

- [ ] SBOM and licence/provenance reports.
- [ ] Fuzz archive/parser/provider inputs.
- [ ] Download/cache corruption recovery tests.
- [ ] Account/token threat model and credential-redaction tests.
- [ ] Crash recovery and instance transactional rollback.
- [ ] Compatibility telemetry with explicit opt-in and privacy design.
- [ ] Signed binaries/installers/update metadata.
- [ ] Migration tests across Gridelyx versions.
- [ ] Release channels: stable/beta/nightly.

## Ongoing rule

Every provider, loader, Minecraft release and production feature is capability-negotiated. New versions may move a capability backward in readiness until revalidated; the UI must show that state instead of presenting stale compatibility as fact.
