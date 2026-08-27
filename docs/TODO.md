# Gridelyx Studio TODO and validation ledger

This is the live implementation ledger. Capability maturity is summarized in `FEATURE_MAP.md`; staged sequencing is in `ROADMAP.md`.

## Immediate architecture / CI

- [x] Establish Gridelyx Studio as launcher + manager + creator + production umbrella product.
- [x] Add Java/Bedrock cross-edition architecture and VFSB native bridge foundation.
- [x] Add GUI-independent `studio/core` instance/provider/provenance/dependency contracts.
- [x] Add official/authorized provider and loader-adapter manifests.
- [x] Add deterministic repository index and task-context tooling.
- [x] Add AI handoff/context map and full product/roadmap/feature/structure docs.
- [x] Compile Rust/C++ native examples on Windows and Linux.
- [x] Add `studio-ci.yml` requiring Studio core tests + `studio_check.py` + `repo_index.py --check` + context retrieval smoke.
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

## Loader adapters

- [ ] Vanilla adapter end-to-end.
- [ ] Fabric Meta adapter.
- [ ] Quilt Meta adapter.
- [ ] Forge official installer/Maven adapter.
- [ ] NeoForge Maven/installer Studio adapter.
- [ ] External/legacy signed adapter manifest loader.
- [ ] Explicit existing-launcher-profile import adapter.
- [ ] Client/server side constraints and loader migration diagnostics.

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
- [ ] Instance clone/fork/diff.
- [ ] Snapshot/restore of configs/saves/content locks.
- [ ] Modrinth `.mrpack` import/export.
- [ ] CurseForge pack import/export under distribution rules.
- [ ] Prism Launcher instance import.
- [ ] MultiMC-compatible instance import.
- [ ] Vanilla launcher/raw `.minecraft` import.
- [ ] Gridelyx portable bundle.
- [ ] Server-instance generation and client/server mod filtering.

## Creator / hotload

- [ ] Validate script and data hotreload in a running 26.2 client.
- [ ] Add resource-reload adapter for generated textures/models.
- [ ] Add versioned URL/module classloader for schema-changing service implementations.
- [ ] Build class-schema comparator to select redefine vs service replacement.
- [ ] Add rollback to previous known-good script/data/service version.
- [ ] Validate registry indirection under multiplayer reconnect/datapack reload.
- [ ] Bind Studio selected instance directly to in-game IDE/AI/toolkit project.
- [ ] Toolkit-module enable/disable/profile manager.

## World / construction

- [ ] Wire raycast selection to actual client/server interaction packets.
- [ ] Add weld, hinge, slider, spring and rope constraints.
- [ ] Add server authority model for physics/constraint truth.
- [ ] Deterministic replication/prediction budgets.
- [ ] Undo/redo transaction log for tool-gun actions.
- [ ] Dynamic collision-to-`VoxelShape` adapter.
- [ ] Custom geometry render adapter and culling/batching benchmarks.
- [ ] Full scene/property/gizmo integration with desktop creator project.

## Production / machinima

- [x] Define neutral rational-time camera/timeline foundation.
- [x] Add production camera interpolation smoke test to advanced runtime and pass Advanced CI.
- [x] Add first production-project schema with exact source-lock linkage, rational frame rate, scenes and render presets.
- [ ] Expand production schemas for typed shots/takes/tracks/replays/render jobs.
- [ ] Free/target/orbit/rail/spline camera rigs.
- [ ] Camera curve interpolation/easing and baked deterministic sampling.
- [ ] Entity/actor transform and pose/animation tracks.
- [ ] Equipment/visibility/particle/command/dialogue cue tracks.
- [ ] Replay/event logger with tick anchors + exact instance-lock reference.
- [ ] Shot/take/sequence editor.
- [ ] Real-time frame capture queue with explicit dropped-frame policy.
- [ ] Deterministic offline capture adapter where renderer stepping permits.
- [ ] Image-sequence writer and manifest.
- [ ] FFmpeg/encoder bridge with separately recorded executable provenance/licence.
- [ ] Audio mix/stem capture capability adapters.
- [ ] Java production adapter interactive validation.
- [ ] Bedrock camera/production adapter; native capture only behind versioned capability gate.
- [ ] Render-pass research: depth/normals/object IDs/motion vectors.

## Native / bridge / polyglot

- [ ] Add VFSB ABI compatibility test vectors across Java/C++/Rust.
- [ ] Add Python/Go/C# bridge conformance tests.
- [ ] Add sandbox resource limits and script execution budgets.
- [ ] Benchmark GraalJS/GraalPy cold start, hot reload and steady-state execution.
- [ ] Benchmark shared-memory throughput/latency and dropped-revision behavior.
- [ ] Bedrock renderer adapter R&D without making binary patching a core dependency.

## AI / project intelligence

- [x] Keep compact human/AI canonical context instead of conversation dumps.
- [x] Generate deterministic file/chunk hashes and lexical terms.
- [x] Add task-scoped lexical context pack tool.
- [x] Run repository-index and context-pack smoke validation in dedicated Studio CI.
- [ ] Feed deterministic chunk IDs into local semantic/vector index.
- [ ] Incremental embedding reuse keyed by commit/path/range/SHA-256.
- [ ] Generate feature/readiness evidence map from tests/manifests where possible.
- [ ] ADR template + machine-readable decision index.
- [ ] CI guard that verifies every `ai/context-map.json` target exists.

## Operations / release

- [ ] JFR baseline profiles for client startup/world load/construction/production capture.
- [ ] Chaos campaigns for packet delay/drop, worker saturation, provider outage and bridge disconnect.
- [ ] OpenTelemetry/MCP trace propagation adapter.
- [ ] Archive/parser/provider fuzz tests.
- [ ] Signed desktop/native releases and update metadata.
- [ ] Instance/project migration tests.
- [ ] Crash recovery and rollback playbook.
- [ ] Stable/beta/nightly release channels.
