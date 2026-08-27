# Gridelyx Studio project plan

## Objective

Build Gridelyx Studio as one coherent Minecraft platform spanning launcher/instance management, legitimate content acquisition, cross-loader/cross-edition creator tooling, advanced runtime experimentation and machinima/production. Consumer simplicity and expert transparency must share the same underlying resolver, lockfiles and capability model.

## Readiness levels

| Level | Meaning |
|---|---|
| R0 | Idea only |
| R1 | Interface/contract/schema defined |
| R2 | Compiles or deterministic static tooling passes |
| R3 | Automated unit/integration test passes |
| R4 | Headless Minecraft/Bedrock integration validation passes |
| R5 | Interactive target validation passes |
| R6 | Release candidate with packaging/migration/rollback evidence |

## Workstreams

1. **Desktop/product shell** — native launch-without-Java shell, settings, credentials, updater, downloads, accounts and process lifecycle.
2. **Runtime acquisition** — Mojang metadata/libraries/assets, managed Java, cache, hashes and platform classifiers.
3. **Loaders** — vanilla/Fabric/Quilt/Forge/NeoForge plus extensible legacy/future adapter contract.
4. **Content/resolution** — Modrinth, authorized CurseForge, local import, dependency solving, lockfiles, pack import/export and instance transactions.
5. **Creator Studio** — UAL, in-game IDE, live scripting, world/voxel/mesh/texture tools, AI authoring, hotload and scene/property editing.
6. **Bedrock** — stable Add-On runtime, Editor extension, VFSB/native companion and capability-negotiated production adapters.
7. **Production** — replay, animation, camera direction, timeline, shots/takes, real-time/offline capture, audio and exports.
8. **Native/polyglot** — Panama FFM, VFSB, Rust/C++, Python/Go/C# sidecars, shared memory and replaceable encoder/render bridges.
9. **AI/project intelligence** — AGENTS contract, handoff/context maps, deterministic repo/chunk index, vector retrieval, auto-doc and evidence-aware summaries.
10. **Validation/security/operations** — CI, GameTest, client tests, CodeQL, archive safety, provenance, SBOM, profiling, chaos, signing and rollback.

## Architecture principles

- The desktop application must not require Java to start.
- Java/Bedrock/loader differences belong in adapters, not duplicated product logic.
- One source of truth for dependency resolution and instance locks.
- Official/authorized download channels only; provider restrictions are part of resolver policy.
- Immutable downloaded artifacts use content-addressed storage; writable instance data stays isolated.
- All downloads get local SHA-256 and provenance.
- Native/bytecode/preview capabilities are opt-in, version-aware and fail closed on drift.
- Production projects are non-destructive and bind to exact source instance/content locks.
- AI context is layered and source-referential rather than a duplicated repo dump.
- A capability name must never imply a readiness level that has not been evidenced.

## Milestones

### M0 — Repository/product convergence
Full Gridelyx product docs, Studio core contracts, provider policy, AI indexing/context and unified roadmap/feature/TODO structure.

### M1 — Vanilla launcher
Native shell, supported Minecraft authentication, Mojang metadata, managed Java, cache, instance lock and reliable vanilla launch.

### M2 — Modded launcher
Fabric/Quilt/Forge/NeoForge adapters, Modrinth and authorized CurseForge providers, dependency solver, simple/advanced instance UI and update rollback.

### M3 — Instance/modpack ecosystem
Pack import/export, Prism/MultiMC migration, server profiles, snapshots, portable bundles and advanced diagnostics.

### M4 — Creator integration
Desktop workspace tied to in-game Gridelyx toolkit, live authoring, loader capability matrix, Bedrock project workflows and safe hotload/rollback.

### M5 — Machinima MVP
Replay/event log, rational-time timeline, camera rigs, actor tracks, shot/take editor, image/video capture and reproducible production-project reopen/render.

### M6 — Professional production
Offline render, higher-quality passes, audio stems, multi-camera/take tooling, render queue, collaboration and interchange research.

### M7 — Hardening/release
Cross-platform packaging, security/provenance reports, fuzz/chaos/performance evidence, signed updates, migrations and stable/beta/nightly channels.

## Release gates

A milestone does not advance solely because the code compiles. Relevant tests, target validation, migration and failure recovery must be recorded. An API/provider update may demote an adapter's readiness until revalidated.

## Planning sources

- `PROJECT_OVERVIEW.md` — canonical product architecture.
- `ROADMAP.md` — staged delivery details.
- `FEATURE_MAP.md` — capability/readiness matrix.
- `TODO.md` — live implementation ledger.
- `PROJECT_STRUCTURE.md` — ownership boundaries.
- `AI_HANDOFF.md` + `ai/context-map.json` — compact continuation/navigation state.
