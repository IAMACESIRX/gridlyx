# Project TODO and validation ledger

This is the live implementation ledger. Capability maturity is summarized in `FEATURE_MAP.md`; staged sequencing is in `ROADMAP.md`. The complete retained conversation scope is in `CHAT_REQUIREMENTS_TRACEABILITY.md` and `../platform/chat-requirements.json`.

## Whole-chat requirements reconciliation

- [x] Add canonical human-readable conversation requirements ledger (`CHAT_REQUIREMENTS_TRACEABILITY.md`).
- [x] Add machine-readable 32-group requirements manifest (`platform/chat-requirements.json`).
- [x] Add `tools/chat_requirements_check.py` and require it in Studio/project-continuity CI.
- [x] Add community entrypoint, contributor onboarding, architecture tour, evidence guide, support guide, conduct rules and glossary.
- [x] Route AI context through the retained-requirements ledger for broad/scope-affecting work.
- [ ] Generate readiness/evidence summaries automatically from the requirements graph where practical.
- [ ] Keep every new user-requested project capability mapped to either implementation evidence or an explicit planned requirement.
- [ ] After replacement brand selection, complete the retired-terminology migration and add forbidden-term CI.

## Immediate architecture / CI

- [x] Establish the project as launcher + manager + creator + production umbrella platform.
- [x] Add Java/Bedrock cross-edition architecture and native bridge foundation.
- [x] Add GUI-independent `studio/core` instance/provider/provenance/dependency contracts.
- [x] Add official/authorized provider and loader-adapter manifests.
- [x] Add deterministic repository index and task-context tooling.
- [x] Add AI handoff/context map and full product/roadmap/feature/structure docs.
- [x] Add Project-Athena-equivalent engineering continuity, AI role organization and drift mitigation without copying its institutional identity model.
- [x] Add additive deep-integration architecture covering loader/JVM/native/bootstrap/binary-patch/project-owned component escalation.
- [x] Compile Rust/C++ native examples on Windows and Linux.
- [x] Add Studio/project-continuity CI requiring continuity, requirements, Studio core, repo index and context retrieval checks.
- [ ] Add first NeoForge 26.2 GameTest fixture and require it in nightly CI.
- [ ] Add client-side rendering smoke-world capture/benchmark.
- [ ] Add SBOM and dependency/licence/provenance report.

## Desktop shell

- [ ] Select/freeze desktop UI framework; shell must run with no Java installed.
- [ ] Add application data/config/cache path service for Windows/Linux/macOS.
- [ ] Add secure credential-store abstraction.
- [ ] Add settings/versioned configuration migrations.
- [ ] Add self-update channel metadata/signature verification.
- [ ] Add background download queue, pause/resume/retry and bandwidth limits.
- [ ] Add process launcher/log capture/crash attribution.
- [ ] Build Simple and Advanced modes over identical service state.
- [ ] Simple mode: consumer-launcher-level one-click install/play/update flows.
- [ ] Advanced mode: Prism/MultiMC-level runtime graph, Java/JVM args, loader, content, hashes, provenance and override controls.

## Accounts and authentication

- [ ] Implement supported Microsoft/Minecraft OAuth account flow.
- [ ] Store refresh/access credentials only through OS credential storage.
- [ ] Multi-account selector and per-instance default account.
- [ ] Offline launch only when legitimate cached account/session/game conditions allow it.
- [ ] Redact tokens/account secrets from diagnostics and exports.

## Minecraft / Java acquisition

- [ ] Mojang version-manifest client with conditional requests/cache policy.
- [ ] Version JSON inheritance/materialization and launch-argument resolver.
- [ ] Asset index/library/native classifier resolution.
- [ ] Managed Mojang Java runtime component resolution.
- [ ] Local compatible JVM discovery.
- [ ] Adoptium Temurin fallback provider.
- [ ] Architecture/Java-major verification before launch.
- [ ] Content-addressed immutable artifact cache with local SHA-256.
- [ ] Atomic `.part` download promotion and corruption recovery.
- [ ] Support legitimate installation of historical version families through latest/snapshot metadata where available and maintainable.

## Loader adapters and Polyloader

- [ ] Vanilla adapter end-to-end.
- [ ] Fabric Meta adapter.
- [ ] Quilt Meta adapter.
- [ ] Forge official installer/Maven adapter.
- [ ] NeoForge Maven/installer Studio adapter.
- [ ] Liteloader/legacy-family compatibility research and adapter manifests.
- [ ] External/legacy signed adapter manifest loader.
- [ ] Explicit existing-launcher-profile import adapter.
- [ ] Client/server side constraints and loader migration diagnostics.
- [ ] Standalone prelaunch instrumentation bootstrap with its own required transformation dependencies.
- [ ] Runtime loader/version fingerprint database and confidence-gated semantic bindings.
- [ ] Implement first real source-loader → UAL → foreign-loader translation adapter pair.
- [ ] Sideload compatible foreign-loader JARs through isolated classloaders where capability analysis marks them live-safe/emulatable.
- [ ] Mark Mixins/coremods/access wideners/transformation services prelaunch when lifecycle requirements cannot be recreated safely after startup.
- [ ] Split legacy Java 8/transitional/current bootstrap families behind a stable neutral bridge protocol.

## Content providers / dependencies

- [ ] Modrinth project search, version filter, dependency and file downloader.
- [ ] CurseForge provider using approved API key/current terms.
- [ ] Respect CurseForge author distribution-disable state; no scraping bypass.
- [ ] Required transitive dependency expansion.
- [ ] Optional dependency recommendation/selection UX.
- [x] Deterministic required install order and incompatibility detection core.
- [ ] Version-range/loader/game/side constraint solver.
- [ ] Explain selected/rejected candidate reasons.
- [ ] Pin/exclude/replace user overrides.
- [ ] Safe update transaction + rollback snapshot.
- [ ] Mods/resource packs/shaders/datapacks/worlds/plugins content categories.

## Instances and packs

- [x] Add first `instance.json` and `content.lock.json` schemas.
- [ ] Implement instance/lock persistence and migrations against those schemas.
- [ ] Isolated writable overlays and shared immutable blobs.
- [ ] Instance clone/fork/diff/merge.
- [ ] Snapshot/restore of configs/saves/content locks.
- [ ] Modrinth `.mrpack` import/export.
- [ ] CurseForge pack import/export under distribution rules.
- [ ] Prism Launcher instance import.
- [ ] MultiMC-compatible instance import.
- [ ] Vanilla launcher/raw `.minecraft` import.
- [ ] Native portable project/instance bundle after the replacement brand is selected.
- [ ] Server-instance generation and client/server mod filtering.

## Creator / external hotload

- [ ] Validate script and data hotreload in a running current Java client.
- [ ] Add resource-reload adapter for generated textures/models without requiring manual F3+T.
- [ ] Add versioned URL/module classloader for schema-changing service implementations.
- [ ] Build class-schema comparator to select redefine vs service replacement.
- [ ] Add rollback to previous known-good script/data/service version.
- [ ] Validate registry indirection under multiplayer reconnect/datapack reload.
- [ ] Bind selected desktop instance directly to in-game IDE/AI/toolkit project.
- [ ] Toolkit-module enable/disable/profile manager.
- [ ] Preserve editor source, layout, selection, undo state and project context across supervised component/game restarts.
- [ ] Add explicit reload-scope controller: data/script → service → classloader → sidecar/native process → game process → patched runtime rebuild.

## In-game IDE / AI / live execution

- [ ] Bind developer screen injection model to real client `Screen` lifecycle.
- [ ] Register user-configurable keybinds and menu buttons for IDE/editor/AI/overlay/automation toggles.
- [ ] Expand embedded IDE with files, editor tabs, compile diagnostics, runtime logs and command history.
- [ ] Connect direct Java string compilation to live compilation/hotswap gateway.
- [ ] Connect GraalJS/GraalPy module editing/execution to the embedded IDE.
- [ ] Connect MCP/local AI/approved remote AI adapters through the bounded development bridge.
- [ ] Add high-level autonomous client action/tool API for AI automation without raw OS input dependency.
- [ ] Implement capability/permission prompts and server-side authority checks for remote/AI world actions.
- [ ] Support alt-tabless edit → compile/script → reload → test → inspect loops.

## Non-Java modification and external-tool SDK

- [ ] Add one permissioned neutral modification gateway across MCP, local HTTP/Netty, IPC/shared memory, filesystem hotload and sidecar transports.
- [ ] Define extension manifest/schema describing requested capabilities, target edition/version and trust level.
- [ ] GraalVM JavaScript extension SDK.
- [ ] GraalPy/Python extension SDK.
- [ ] External Python AI/tool sidecar SDK.
- [ ] Go sidecar SDK and conformance tests.
- [ ] C# sidecar SDK and conformance tests.
- [ ] Rust/C++ native extension SDK and ABI conformance tests.
- [ ] Bedrock Script/Editor extension transport adapter.
- [ ] Sandbox resource limits, timeouts and per-extension capability revocation.

## Live world editor / structures / events

- [ ] Bind abstract section mutation sink to actual target chunk-section storage.
- [ ] Preserve parallel 16x16x16 section blitting and immutable worker snapshots.
- [ ] Commit all authoritative world mutations through controlled server-thread scheduling.
- [ ] Bulk-edit path that defers per-block lighting work and performs explicit consolidated reconciliation.
- [ ] Reconcile lighting, heightmaps, POI, block entities, save flags and client chunk state after structural batches.
- [ ] Complete NBT structure metadata/block-entity materialization.
- [ ] Place compiled structures across already-generated chunk/section boundaries.
- [ ] Live ore/structure/environment generation APIs for mods, scripts and AI.
- [ ] Dynamic Event and Structure Matrix runtime triggers for meteor/citadel/corruption/restoration/etc.
- [ ] Durable undo/redo + transaction journal/WAL and restart recovery.
- [ ] Bounded edit partitioning for very large transformations.
- [ ] Bedrock live-world-editor adapter with explicit parity/capability gaps.

## Terraria-style world systems

- [ ] Dynamic liquid simulation cell grid with bounded cellular-automata update budgets.
- [ ] Multiple fluid/material properties and cross-cell transfer rules separated from authoritative block mutation.
- [ ] Arbitrary per-block/per-face paint layer matrices.
- [ ] Sub-voxel paint/material/density overlay buffers.
- [ ] Progressive world-transmutation global state machine with progression/trigger locks.
- [ ] Reversible staged transmutation and transactional rollback.
- [ ] Replication/culling and persistence formats for liquid/paint/transmutation state.
- [ ] Java and Bedrock target adapters.

## Microgeometry / rendering / collision

- [ ] Microgrid occupancy/placement representation below one vanilla block.
- [ ] Parametric/rasterized circles, cylinders, curved profiles, wedges, slopes and slanted surfaces.
- [ ] Separate authoring mesh, render mesh and collision representation.
- [ ] Dynamic collision-to-`VoxelShape` adapter where vanilla boxes are adequate.
- [ ] Deeper collision subsystem augmentation/replacement when vanilla `VoxelShape` cannot represent required semantics efficiently.
- [ ] Runtime custom geometry render adapter and culling/batching benchmarks.
- [ ] Direct vertex-buffer/GPU upload path with render-thread/context validation.
- [ ] Live mesh/model registry and vertex-consumer override integration.
- [ ] Live texture painting/patching and dynamic atlas/resource integration.
- [ ] Volumetric matrix preview integration.
- [ ] PoseStack/matrix interception and IK integration tests.
- [ ] Java and Bedrock renderer/collision adapters.

## Sandbox construction / scene engine

- [ ] Wire raycast selection to actual client/server interaction packets.
- [ ] Add weld, hinge, slider, spring and rope constraints.
- [ ] Add server authority model for physics/constraint truth.
- [ ] Deterministic replication/prediction budgets.
- [ ] Undo/redo transaction log for tool-gun actions.
- [ ] Physically manipulable runtime part/entity abstraction independent of marker entities where practical.
- [ ] Unified real-time scene graph with stable IDs and hierarchical parenting.
- [ ] Hierarchical instance-property serialization and versioned property schemas.
- [ ] Translate/rotate/scale gizmos and drag/drop part manipulation.
- [ ] Properties/asset browser integration for Roblox-Studio-like live execution workflow.
- [ ] Full scene/property/gizmo integration with desktop creator project.

## Multiplayer state synchronization

- [ ] Authenticate/authorize editor requests per server role/capability.
- [ ] Bind custom edit packet channel to the actual target loader/network lifecycle.
- [ ] Maintain per-section optimistic revisions and conflict/rebase handling.
- [ ] ACK tracking and retry/reconciliation policy.
- [ ] Network culling by chunk/view-distance/interest sets.
- [ ] Server-thread safe commit queue with tick-budget limits.
- [ ] Replicate creator scene/physics/liquid/overlay state only to relevant players.
- [ ] Chaos tests for delayed, duplicated, reordered and dropped edit/physics packets.

## Fault tolerance / anti-crash / live debugging

- [ ] Per-script CPU/time/memory/budget policy.
- [ ] Cooperative interruption for bounded guest tasks.
- [ ] Process-isolated execution lane for non-cooperative/untrusted/crash-prone workloads.
- [ ] Global event recovery wrappers that report faults without hiding corruption signals.
- [ ] Transactional off-thread world simulation before authoritative commit.
- [ ] Inverse-delta/WAL rollback after script or mutation failure.
- [ ] Last-known-good script/data/service/asset version restoration.
- [ ] Crash attribution across Java/native/sidecar/game-process boundaries.
- [ ] Supervised process restart and automatic editor reconnect/state restore.
- [ ] Explicit fatal-failure policy for JVM-wide OOM/native corruption rather than impossible same-process recovery promises.

## Dynamic dimensions / worldgen / teleport

- [ ] Complete dynamic dimension materialization against exact target lifecycle.
- [ ] Codec-driven generated worldgen round-trip tests.
- [ ] Live structure/ore/event generation over existing chunks.
- [ ] Zero-entity teleport-channel target integration.
- [ ] Virtual registry path for dynamic definitions.
- [ ] Deep registry/engine integration path where native registry semantics are required.

## Production / machinima

- [x] Define neutral rational-time camera/timeline foundation.
- [x] Add production camera interpolation smoke test to advanced runtime and pass Advanced CI.
- [x] Add first production-project schema with exact source-lock linkage, rational frame rate, scenes and render presets.
- [ ] Expand production schemas for typed shots/takes/tracks/replays/render jobs.
- [ ] Free/first-person/third-person/target/orbit/rail/spline/crane/vehicle camera rigs.
- [ ] Camera curve interpolation/easing and baked deterministic sampling.
- [ ] Entity/actor transform and pose/animation tracks.
- [ ] Equipment/visibility/particle/command/dialogue cue tracks.
- [ ] Replay/event logger with tick anchors + exact instance-lock reference.
- [ ] In-game animation editor and curve/keyframe tools.
- [ ] Shot/take/sequence editor.
- [ ] Real-time frame/video capture queue with explicit dropped-frame policy.
- [ ] Deterministic offline capture adapter where renderer stepping permits.
- [ ] Image-sequence writer and manifest.
- [ ] FFmpeg/encoder bridge with separately recorded executable provenance/licence.
- [ ] Audio mix/stem capture capability adapters.
- [ ] Java production adapter interactive validation.
- [ ] Bedrock camera/animation/recording/production adapter; native capture behind versioned capability gate.
- [ ] Render-pass research: beauty/depth/normals/object IDs/motion vectors/material IDs.

## Native / bridge / polyglot

- [ ] Add bridge ABI compatibility test vectors across Java/C++/Rust.
- [ ] Add Python/Go/C# bridge conformance tests.
- [ ] Add sandbox resource limits and script execution budgets.
- [ ] Benchmark GraalJS/GraalPy cold start, hot reload and steady-state execution.
- [ ] Benchmark shared-memory throughput/latency and dropped-revision behavior.
- [ ] Complete Bedrock native render/world adapter research under exact fingerprints.
- [ ] Keep supported Bedrock Script/Editor paths and deeper native/patch paths behind the same neutral capability model.

## Mod forking / decompilation / compatibility analysis

- [ ] Complete authorized local JAR fork/extraction workflow with provenance manifest.
- [ ] Integrate optional user-supplied decompiler command and record tool hash/version.
- [ ] Produce structural bytecode/decompiled-source diff reports between mod versions.
- [ ] Feed loader metadata, Mixins, access wideners, native libraries and transformation services into Polyloader capability classification.
- [ ] Add license/redistribution review step before generated fork output can be published.

## Deep integration / patch manager

- [ ] Define patch-set manifest schema: target hashes/platform/architecture, dependencies, conflicts, output fingerprint, validation and rollback.
- [ ] Implement immutable-base → derived-runtime cache pipeline.
- [ ] Implement patch graph resolution and one-action disable/rollback.
- [ ] Invalidate patched derivatives automatically when upstream fingerprints drift.
- [ ] Add binary/library structural fingerprint tooling beyond raw file hash where useful.
- [ ] Add supervised launcher/bootstrap composition for pre-main/runtime components.
- [ ] Create explicit project-owned augmentation interfaces for renderer, collision/physics, registries, networking, world storage and scripting where upstream systems become limiting.
- [ ] Record every L5-L8 integration decision with blast radius, maintenance burden and recovery evidence.

## Bedrock parity

- [ ] Maintain a machine-readable Java↔Bedrock capability parity matrix.
- [ ] Stable Add-On adapter for neutral UAL/world/script operations.
- [ ] Editor extension bindings for creator selection/transform/brush/model workflows.
- [ ] Dedicated Server network sidecar path where APIs permit.
- [ ] Native shared-memory companion adapters for mesh, texture, world delta, telemetry and production frames.
- [ ] Deep native/executable augmentation path for required capabilities unavailable through supported APIs, exact-version gated.
- [ ] Bedrock live asset/model/texture editor parity target.
- [ ] Bedrock world editor/events/liquids/paint/microgeometry/physics parity target.
- [ ] Bedrock in-game IDE/AI/automation parity target where technically achievable through supported or deeper additive components.
- [ ] Bedrock recording/animation/machinima parity target.

## AI / project intelligence

- [x] Keep compact human/AI canonical context instead of conversation dumps.
- [x] Generate deterministic file/chunk hashes and lexical terms.
- [x] Add task-scoped lexical context pack tool.
- [x] Run repository-index and context-pack smoke validation in dedicated Studio CI.
- [x] Add Project-Athena-equivalent role organization, work state, decision/assumption ledgers and drift controls.
- [x] Add complete conversation scope ledger and machine-verifiable requirement paths.
- [ ] Feed deterministic chunk IDs into local semantic/vector index.
- [ ] Incremental embedding reuse keyed by commit/path/range/SHA-256.
- [ ] Generate feature/readiness evidence map from tests/manifests where possible.
- [ ] ADR template + machine-readable decision index.
- [ ] Add richer automated cross-doc drift checks between requirements, roadmap, TODO and capability manifests.

## Community / governance

- [x] Add `COMMUNITY.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SUPPORT.md` and security routing.
- [x] Add community getting-started, contributor onboarding, architecture tour, testing/evidence and glossary docs.
- [ ] Add maintainer/reviewer ownership map when the contributor base grows.
- [ ] Add release/contribution checklist templates for deep-integration and cross-edition changes.
- [ ] Add public compatibility/support matrix once target validation is mature enough.

## Rebrand migration

- [x] Document controlled rebrand process and drift rules.
- [ ] Select and approve replacement brand after collision/trademark/domain screening.
- [ ] Freeze display name, canonical case, repository slug, short name, protocol prefix, package namespace, executable and data-root names.
- [ ] Inventory every retired-brand occurrence across tracked text, paths, identifiers, schemas, ABI/protocol and generated artifacts.
- [ ] Rename public/source/path identifiers coherently.
- [ ] Add migrations/temporary aliases only where persisted/protocol compatibility requires them.
- [ ] Add forbidden-terminology scanner to CI.
- [ ] Regenerate AI indexes/autodoc and run full-tree zero-unexplained-occurrence scan.
- [ ] Rename GitHub repository metadata/slug where supported.
- [ ] Decide separately whether Git history should ever be rewritten.

## Operations / release

- [ ] JFR baseline profiles for client startup/world load/construction/production capture.
- [ ] Chaos campaigns for packet delay/drop, worker saturation, provider outage and bridge disconnect.
- [ ] OpenTelemetry/MCP trace propagation adapter.
- [ ] Archive/parser/provider fuzz tests.
- [ ] Signed desktop/native releases and update metadata.
- [ ] Instance/project migration tests.
- [ ] Crash recovery and rollback playbook.
- [ ] Patch compatibility/fingerprint/rollback release evidence.
- [ ] Stable/beta/nightly release channels.
