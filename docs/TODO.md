# Gridelyx TODO and validation ledger

This is the live implementation ledger. Capability maturity is summarized in `FEATURE_MAP.md`; staged sequencing is in `ROADMAP.md`. Complete retained scope is in `CHAT_REQUIREMENTS_TRACEABILITY.md` / `../platform/chat-requirements.json`. Tool and program prerequisites are in `DEPENDENCIES_AND_TOOLCHAIN.md`, `CAPABILITY_DEPENDENCY_MATRIX.md` and `../platform/toolchain-requirements.json`.

## Whole-chat requirements, identity and dependency control

- [x] Add canonical human-readable conversation requirements ledger.
- [x] Add machine-readable **33-group** requirements manifest (`CR-001`…`CR-033`).
- [x] Add `tools/chat_requirements_check.py` and require it in Gridelyx continuity CI.
- [x] Select **Gridelyx** as canonical root brand and **Gridelyx Studio** as integrated suite.
- [x] Freeze canonical identity/compatibility state in `platform/brand.json` and decision/ADR records.
- [x] Add staged terminology manifest and CI checker; classify legacy `VFSB`/Gridelyx identifiers as migration debt.
- [x] Add complete dependency/tool inventory and machine-readable toolchain manifest.
- [x] Add capability→dependency→target→validation matrix.
- [x] Add toolchain evidence-path checker to Gridelyx continuity CI.
- [x] Add community entrypoint, contributor onboarding, architecture tour, evidence guide, support guide, conduct rules and glossary.
- [x] Route AI context through retained requirements, brand and toolchain manifests.
- [ ] Generate readiness/evidence summaries automatically from the requirements graph where practical.
- [ ] Keep every new human-requested capability mapped to implementation evidence or an explicit planned CR.
- [ ] Complete retired source/path/protocol/ABI/persisted terminology migration and switch terminology CI to strict mode.

## Immediate architecture / CI

- [x] Establish Gridelyx as launcher + manager + creator + production umbrella platform.
- [x] Add Java/Bedrock cross-edition architecture and native bridge foundation.
- [x] Add GUI-independent `studio/core` instance/provider/provenance/dependency contracts.
- [x] Add official/authorized provider and loader-adapter manifests.
- [x] Add deterministic repository index and task-context tooling.
- [x] Add AI handoff/context map and full product/roadmap/feature/structure docs.
- [x] Add capability-scoped AI organization, work-state, decision/assumption ledgers and drift mitigation.
- [x] Add additive deep-integration architecture covering loader/JVM/native/bootstrap/binary-patch/project-owned component escalation.
- [x] Compile Rust/C++ native examples on Windows and Linux.
- [x] Add continuity CI for brand, requirements, toolchain, terminology, Studio core, repo index and context retrieval.
- [ ] Add representative NeoForge 26.2 GameTest fixtures and require them in nightly CI.
- [ ] Add client-side rendering smoke-world capture/benchmark.
- [ ] Add SBOM and automated dependency/licence/provenance report.

## Dependency / toolchain hardening

- [x] Pin Java `25.0.4+7`, Gradle `9.2.1`, ModDevGradle `2.0.144`, NeoForge `26.2.0.67`, Minecraft template target `26.2` and locked Java libraries/plugins.
- [x] Document Python, Rust/Cargo, CMake/C++, Go, .NET, Dev Containers, FFmpeg/encoder, decompiler, Bedrock target and AI/MCP requirements without inventing versions.
- [ ] Freeze/test a minimum supported Python version for repository tooling.
- [ ] Freeze/test supported Rust toolchain(s) for Studio/native crates.
- [ ] Freeze/test CMake + MSVC/GCC/Clang support matrix and add macOS native validation.
- [ ] Add Go bridge conformance CI and supported Go-version policy.
- [ ] Add .NET/C# bridge conformance CI and supported SDK-version policy.
- [ ] Define external encoder acquisition/version/provenance policy.
- [ ] Define supported external decompiler adapters/version/provenance policy.
- [ ] Finish importing/verifying exact remote reference-vault binary bytes if GitHub is intended to contain the supplied payload; do not remove the pending marker before verification.

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
- [ ] Advanced mode: Prism/MultiMC-level runtime graph, Java/JVM args, loader, content, hashes, provenance, patches and override controls.

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
- [ ] Support legitimate historical version families through latest/snapshot metadata where available and maintainable.

## Loader adapters and Polyloader

- [ ] Vanilla adapter end-to-end.
- [ ] Fabric Meta adapter.
- [ ] Quilt Meta adapter.
- [ ] Forge official installer/Maven adapter.
- [ ] NeoForge Maven/installer Gridelyx adapter.
- [ ] Liteloader/legacy-family compatibility research and adapter manifests.
- [ ] External/legacy signed adapter manifest loader.
- [ ] Existing-launcher-profile import adapter.
- [ ] Client/server side constraints and loader migration diagnostics.
- [ ] Standalone prelaunch instrumentation bootstrap with transformation dependencies.
- [ ] Runtime loader/version fingerprint database and confidence-gated semantic bindings.
- [ ] First real source-loader → UAL → foreign-loader translation adapter pair.
- [ ] Sideload compatible foreign-loader JARs through isolated classloaders only where capability analysis marks them safe/emulatable.
- [ ] Mark Mixins/coremods/access wideners/transformation services prelaunch when lifecycle cannot safely be recreated after startup.
- [ ] Split legacy Java 8/transitional/current bootstrap families behind a stable neutral Gridelyx bridge protocol.

## Content providers / dependencies

- [ ] Modrinth project search, version filtering, dependencies and downloads.
- [ ] CurseForge provider using approved API key/current terms.
- [ ] Respect CurseForge author distribution-disable state; never scrape around it.
- [ ] Required transitive dependency expansion.
- [ ] Optional dependency recommendation/selection UX.
- [x] Deterministic required install order and incompatibility-detection core.
- [ ] Version-range/loader/game/side constraint solver.
- [ ] Explain selected/rejected candidate reasons.
- [ ] Pin/exclude/replace user overrides.
- [ ] Safe update transaction + rollback snapshot.
- [ ] Mods/resource packs/shaders/datapacks/worlds/plugins content categories.

## Instances and packs

- [x] Initial `instance.json` and `content.lock.json` schemas.
- [ ] Implement persistence and schema migrations.
- [ ] Isolated writable overlays and shared immutable blobs.
- [ ] Instance clone/fork/diff/merge.
- [ ] Snapshot/restore configs/saves/content locks.
- [ ] Modrinth `.mrpack` import/export.
- [ ] CurseForge pack import/export under distribution rules.
- [ ] Prism Launcher instance import.
- [ ] MultiMC-compatible instance import.
- [ ] Vanilla launcher/raw `.minecraft` import.
- [ ] Gridelyx portable project/instance bundle.
- [ ] Server-instance generation and client/server mod filtering.

## Creator / external hotload

- [ ] Validate script/data hotreload in a running current Java client.
- [ ] Resource-reload adapter for generated textures/models without manual F3+T.
- [ ] Versioned URL/module classloader for schema-changing service implementations.
- [ ] Class-schema comparator to select redefine vs service replacement.
- [ ] Rollback to previous known-good script/data/service version.
- [ ] Validate registry indirection under multiplayer reconnect/datapack reload.
- [ ] Bind selected desktop instance directly to in-game IDE/AI/toolkit project.
- [ ] Gridelyx module enable/disable/profile manager.
- [ ] Preserve editor source/layout/selection/undo/project context across supervised component/game restarts.
- [ ] Explicit reload-scope controller: data/script → service → classloader → sidecar/native process → game process → patched-runtime rebuild.

## In-game IDE / AI / live execution

- [ ] Bind developer screen injection model to actual client `Screen` lifecycle.
- [ ] User-configurable keybinds/menu buttons for IDE/editor/AI/overlay/automation toggles.
- [ ] Expand embedded IDE with files, tabs, compile diagnostics, runtime logs and command history.
- [ ] Connect direct Java string compilation to live compilation/hotswap gateway.
- [ ] Connect GraalJS/GraalPy editing/execution to embedded IDE.
- [ ] Connect MCP/local AI/approved remote AI adapters through bounded development bridge.
- [ ] High-level autonomous client action/tool API without raw OS-input dependency.
- [ ] Capability/permission prompts and server-side authority checks for remote/AI world actions.
- [ ] Alt-tabless edit → compile/script → reload → test → inspect loops.

## Non-Java modification and external-tool SDK

- [ ] One permissioned neutral modification gateway across MCP, local HTTP/Netty, IPC/shared memory, filesystem hotload and sidecars.
- [ ] Extension manifest/schema for capabilities, target edition/version and trust level.
- [ ] GraalVM JavaScript SDK.
- [ ] GraalPy/Python SDK.
- [ ] External Python AI/tool sidecar SDK.
- [ ] Go sidecar SDK + conformance tests.
- [ ] C# sidecar SDK + conformance tests.
- [ ] Rust/C++ native extension SDK + ABI conformance tests.
- [ ] Bedrock Script/Editor extension transport adapter.
- [ ] Sandbox resource limits/timeouts/per-extension capability revocation.

## Live world editor / structures / events

- [ ] Bind abstract section mutation sink to actual target chunk-section storage.
- [ ] Preserve parallel 16×16×16 section blitting and immutable worker snapshots.
- [ ] Commit authoritative mutations through controlled server-thread scheduling.
- [ ] Bulk-edit path deferring per-block lighting work with consolidated reconciliation.
- [ ] Reconcile lighting, heightmaps, POI, block entities, save flags and client chunk state.
- [ ] Complete NBT structure metadata/block-entity materialization.
- [ ] Place compiled structures across generated chunk/section boundaries.
- [ ] Live ore/structure/environment generation APIs for mods/scripts/AI.
- [ ] Dynamic Event and Structure Matrix triggers for meteor/citadel/corruption/restoration/etc.
- [ ] Durable undo/redo + transaction journal/WAL and restart recovery.
- [ ] Bounded edit partitioning for huge transformations.
- [ ] Bedrock live-world-editor adapter with explicit parity/capability gaps.

## Terraria-style world systems

- [ ] Dynamic Liquid Simulation Cell grid with bounded cellular-automata update budgets.
- [ ] Fluid/material properties and cross-cell transfer rules separated from authoritative block mutation.
- [ ] Arbitrary per-block/per-face paint layer matrices.
- [ ] Sub-voxel paint/material/density overlay buffers.
- [ ] Progressive World Transmutation global state machine with progression/trigger locks.
- [ ] Reversible staged transmutation and transactional rollback.
- [ ] Replication/culling and persistence formats for liquid/paint/transmutation state.
- [ ] Java and Bedrock target adapters.

## Microgeometry / rendering / collision

- [ ] Microgrid occupancy/placement below one vanilla block.
- [ ] Parametric/rasterized circles, cylinders, curved profiles, wedges, slopes and slanted surfaces.
- [ ] Separate authoring mesh, render mesh and collision representation.
- [ ] Dynamic collision-to-`VoxelShape` adapter where adequate.
- [ ] Deeper collision subsystem augmentation/replacement when `VoxelShape` cannot represent required semantics efficiently.
- [ ] Runtime custom geometry render adapter and culling/batching benchmarks.
- [ ] Direct vertex-buffer/GPU upload path with render-thread/context validation.
- [ ] Live mesh/model registry and vertex-consumer override integration.
- [ ] Live texture painting/patching and dynamic atlas/resource integration.
- [ ] Volumetric matrix preview integration.
- [ ] PoseStack/matrix interception and IK integration tests.
- [ ] Java and Bedrock renderer/collision adapters.

## Sandbox construction / scene engine

- [ ] Wire raycast selection to actual client/server interactions.
- [ ] Weld, hinge, slider, spring and rope constraints.
- [ ] Server authority model for physics/constraint truth.
- [ ] Deterministic replication/prediction budgets.
- [ ] Undo/redo transaction log for tool-gun actions.
- [ ] Physically manipulable runtime part/entity abstraction independent of marker entities where practical.
- [ ] Unified real-time scene graph with stable IDs/hierarchical parenting.
- [ ] Hierarchical instance-property serialization/versioned property schemas.
- [ ] Translate/rotate/scale gizmos and drag/drop part manipulation.
- [ ] Properties/asset browser integration for Roblox-Studio-like live execution.
- [ ] Full scene/property/gizmo integration with desktop creator project.

## Multiplayer state synchronization

- [ ] Authenticate/authorize editor requests per server role/capability.
- [ ] Bind custom edit packet channel to actual target loader/network lifecycle.
- [ ] Per-section optimistic revisions and conflict/rebase handling.
- [ ] ACK tracking and retry/reconciliation policy.
- [ ] Network culling by chunk/view-distance/interest sets.
- [ ] Server-thread safe commit queue with tick-budget limits.
- [ ] Replicate scene/physics/liquid/overlay state only to relevant players.
- [ ] Chaos tests for delayed/duplicated/reordered/dropped edit/physics packets.

## Fault tolerance / anti-crash / live debugging

- [ ] Per-script CPU/time/memory budget policy.
- [ ] Cooperative interruption for bounded guest tasks.
- [ ] Process-isolated lane for non-cooperative/untrusted/crash-prone workloads.
- [ ] Global event recovery wrappers that report faults without hiding corruption signals.
- [ ] Transactional off-thread world simulation before authoritative commit.
- [ ] Inverse-delta/WAL rollback after script/mutation failure.
- [ ] Last-known-good script/data/service/asset restoration.
- [ ] Crash attribution across Java/native/sidecar/game-process boundaries.
- [ ] Supervised process restart + automatic editor reconnect/state restore.
- [ ] Explicit fatal-failure policy for JVM-wide OOM/native corruption instead of impossible same-process guarantees.

## Dynamic dimensions / worldgen / teleport

- [ ] Complete dynamic-dimension materialization against exact target lifecycle.
- [ ] Codec-driven generated-worldgen round-trip tests.
- [ ] Live structure/ore/event generation over existing chunks.
- [ ] Zero-entity teleport-channel target integration.
- [ ] Virtual registry path for dynamic definitions.
- [ ] Deep registry/engine integration path where native registry semantics are required.

## Production / machinima

- [x] Neutral rational-time camera/timeline foundation.
- [x] Production camera interpolation smoke test in Advanced CI.
- [x] Initial production-project schema with exact source-lock linkage/rational frame rate/scenes/render presets.
- [ ] Typed shots/takes/tracks/replays/render-job schemas.
- [ ] Free/first-person/third-person/target/orbit/rail/spline/crane/vehicle cameras.
- [ ] Camera curve interpolation/easing and baked deterministic sampling.
- [ ] Entity/actor transform + pose/animation tracks.
- [ ] Equipment/visibility/particle/command/dialogue cues.
- [ ] Replay/event logger with tick anchors + exact instance-lock reference.
- [ ] In-game animation editor and curve/keyframe tools.
- [ ] Shot/take/sequence editor.
- [ ] Real-time frame/video capture with explicit dropped-frame policy.
- [ ] Deterministic offline capture where renderer stepping permits.
- [ ] Image-sequence writer/manifest.
- [ ] FFmpeg/encoder bridge with separately recorded executable provenance/licence.
- [ ] Audio mix/stem adapters.
- [ ] Java production interactive validation.
- [ ] Bedrock camera/animation/recording/production adapter; native capture behind versioned gate.
- [ ] Render passes: beauty/depth/normals/object IDs/motion vectors/material IDs.

## Native / bridge / polyglot

- [ ] Bridge ABI compatibility vectors across Java/C++/Rust.
- [ ] Python/Go/C# bridge conformance tests.
- [ ] Sandbox resource limits and script execution budgets.
- [ ] GraalJS/GraalPy cold-start/hot-reload/steady-state benchmarks.
- [ ] Shared-memory throughput/latency/dropped-revision benchmarks.
- [ ] Complete Bedrock native render/world adapter research under exact fingerprints.
- [ ] Keep supported Bedrock Script/Editor and deeper native/patch paths behind one neutral capability model.

## Mod forking / decompilation / compatibility analysis

- [ ] Complete authorized local JAR fork/extraction workflow with provenance manifest.
- [ ] Optional user-supplied decompiler command with tool hash/version recorded.
- [ ] Structural bytecode/decompiled-source diff reports between mod versions.
- [ ] Feed loader metadata, Mixins, access wideners, native libs and transform services into Polyloader classification.
- [ ] Licence/redistribution review before generated fork output can be published.

## Deep integration / patch manager

- [ ] Patch-set manifest schema: target hashes/platform/architecture, dependencies, conflicts, output fingerprint, validation and rollback.
- [ ] Immutable-base → derived-runtime cache pipeline.
- [ ] Patch graph resolution and one-action disable/rollback.
- [ ] Invalidate derivatives when upstream fingerprints drift.
- [ ] Binary/library structural fingerprint tooling beyond raw file hash where useful.
- [ ] Supervised launcher/bootstrap composition for pre-main/runtime components.
- [ ] Project-owned augmentation interfaces for renderer, collision/physics, registries, networking, world storage and scripting where upstream becomes limiting.
- [ ] Every L5-L8 decision records blast radius, maintenance burden and recovery evidence.

## Bedrock parity

- [ ] Machine-readable Java↔Bedrock parity matrix.
- [ ] Stable Add-On adapter for neutral UAL/world/script operations.
- [ ] Editor extension bindings for creator selection/transform/brush/model workflows.
- [ ] Dedicated Server network sidecar path where APIs permit.
- [ ] Native shared-memory adapters for mesh/texture/world/telemetry/production frames.
- [ ] Deep native/executable augmentation path for unavailable required capabilities, exact-version gated.
- [ ] Bedrock live asset/model/texture editor parity target.
- [ ] Bedrock world editor/events/liquids/paint/microgeometry/physics parity target.
- [ ] Bedrock in-game IDE/AI/automation parity target where technically achievable.
- [ ] Bedrock recording/animation/machinima parity target.

## AI / project intelligence

- [x] Compact human/AI canonical context instead of conversation dumps.
- [x] Deterministic file/chunk hashes and lexical terms.
- [x] Task-scoped context-pack tool.
- [x] Repository-index/context-pack smoke validation in Gridelyx continuity CI.
- [x] Role organization, work state, decision/assumption ledgers and drift controls.
- [x] Complete conversation scope ledger and machine-verifiable paths.
- [x] Dependency/toolchain and brand/terminology domains in AI context map.
- [ ] Feed deterministic chunk IDs into local semantic/vector index.
- [ ] Incremental embeddings keyed by commit/path/range/SHA-256.
- [ ] Generate feature/readiness evidence map from tests/manifests where possible.
- [ ] ADR template + machine-readable decision index.
- [ ] Rich cross-doc drift checks between requirements, roadmap, TODO and capability manifests.

## Community / governance

- [x] `COMMUNITY.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SUPPORT.md`, security routing.
- [x] Getting-started, contributor onboarding, architecture tour, testing/evidence and glossary docs.
- [ ] Maintainer/reviewer ownership map when contributor base grows.
- [ ] Release/contribution checklist templates for deep-integration and cross-edition changes.
- [ ] Public compatibility/support matrix once target evidence is mature enough.

## Gridelyx migration

- [x] Select/freeze Gridelyx identity and core target identifiers.
- [x] Record Gridelyx decision in human/machine decision logs.
- [x] Rebrand root README, project overview, project plan, structure, roadmap, feature map, AGENTS, AI handoff and requirements/dependency control docs.
- [x] Add staged terminology manifest/checker to CI.
- [x] Classify major legacy `VFSB`/Gridelyx source/ABI/path identifiers as migration debt.
- [ ] Complete full tracked-tree occurrence inventory and migration class A-G classification.
- [ ] Rename remaining public docs/workflow display strings.
- [ ] Rename project-owned Java/Rust/C/C++/Bedrock source/path/package identifiers coherently.
- [ ] Add versioned migrations/temporary aliases for persisted/protocol/ABI compatibility where required.
- [ ] Migrate bridge magic/prefix to Gridelyx protocol only with cross-version compatibility tests.
- [ ] Switch forbidden-terminology scanner to strict whole-current-tree mode.
- [ ] Regenerate AI indexes/autodoc and run zero-unexplained-occurrence scan.
- [ ] Rename GitHub repository metadata/slug where supported/approved.
- [ ] Decide separately whether Git history should ever be rewritten.

## Operations / release

- [ ] JFR baseline profiles for client startup/world load/construction/production capture.
- [ ] Chaos campaigns for packet delay/drop, worker saturation, provider outage and bridge disconnect.
- [ ] OpenTelemetry/MCP trace propagation adapter.
- [ ] Archive/parser/provider fuzz tests.
- [ ] Signed desktop/native releases and update metadata.
- [ ] Instance/project migration tests.
- [ ] Crash recovery/rollback playbook.
- [ ] Patch compatibility/fingerprint/rollback release evidence.
- [ ] Stable/beta/nightly release channels.
