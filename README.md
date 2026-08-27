# Gridelyx Studio

**Gridelyx Studio** is a cross-edition Minecraft launcher, instance/content manager, creator/development toolkit and machinima/virtual-production suite.

The product direction is deliberately two-layered:

- **simple enough for ordinary players** — create an instance, install a modpack/mod, let Gridelyx choose compatible Java/loader/dependencies, press Play;
- **deep enough for power users and developers** — inspect exact Minecraft/loader/Java versions, libraries, mods, dependency graph, hashes, provenance, launch arguments, toolkit modules, runtime capabilities and validation state.

Gridelyx targets Minecraft Java Edition and Minecraft Bedrock Edition through neutral product/authoring/production contracts while keeping engine-specific adapters isolated.

## Start here

- `docs/PROJECT_OVERVIEW.md` — full product architecture and goals
- `docs/PROJECT_STRUCTURE.md` — repository/runtime ownership map
- `docs/ROADMAP.md` — staged implementation plan
- `docs/FEATURE_MAP.md` — current capability/readiness matrix
- `docs/TODO.md` — live implementation ledger
- `docs/ACQUISITION_AND_RESOLUTION.md` — legitimate downloads, Java/loaders/mods, dependency solving and provenance
- `docs/MACHINIMA_PRODUCTION.md` — replay, animation, camera, recording and production architecture
- `AI_HANDOFF.md` / `ai/context-map.json` — compact AI continuation/navigation state

## Product architecture

```text
                         Gridelyx Studio
                               |
         +---------------------+----------------------+
         |                     |                      |
   Desktop launcher      Creator Studio       Production Studio
         |                     |                      |
   instance/runtime      UAL + authoring       replay/timeline
   content resolver      world/assets/AI       camera/animation
         |                     |                capture/export
         +---------------------+----------------------+
                               |
                    neutral contracts / VFSB
                         /              \
                        /                \
               Java Edition            Bedrock
             loader adapters      Add-On / Editor
             advanced runtime      native companion
```

## Launcher and instance manager

Gridelyx is being structured so the **desktop application can start without Java installed**. Java is a managed dependency of Java Edition instances rather than a launcher prerequisite.

The Studio core under `studio/core` defines GUI-independent:

- Minecraft/edition/version models;
- loader/runtime/content models;
- provider contracts;
- provenance policy;
- required/optional/incompatible/embedded dependency semantics;
- deterministic dependency install ordering;
- instance validation.

The desktop layer will consume the same core for Simple and Advanced modes.

## Minecraft and Java acquisition

Gridelyx is a resolver/client, not an unofficial file mirror.

The acquisition policy is:

- Minecraft versions/libraries/assets/runtime metadata from Mojang launcher metadata;
- Mojang-managed Java where authoritative version metadata provides it;
- compatible local Java when selected;
- managed Adoptium/Temurin fallback;
- Fabric loader metadata from Fabric Meta;
- Quilt loader metadata from Quilt Meta;
- Forge from official Forge distribution/Maven channels;
- NeoForge from official NeoForged Maven/installer channels;
- Modrinth through its supported API;
- CurseForge only through its supported third-party API with an approved key and author distribution controls respected;
- local/legacy files through explicit import with hashes/provenance.

Every downloaded artifact receives a local SHA-256 and upstream hashes/signatures are verified when available. Unknown network sources are not a fallback for a failed official provider.

See `studio/providers/` and `docs/ACQUISITION_AND_RESOLUTION.md`.

## Any version / loader / mod model

Gridelyx does not implement “any loader” by hard-coding every historical loader forever. It uses a loader-adapter contract.

Built-in adapter targets:

- vanilla;
- Fabric;
- Quilt;
- Forge;
- NeoForge.

Legacy/future loaders can be added through the same adapter contract or imported from an explicit existing launcher profile. Unknown coordinates/arguments are never invented.

Content resolution produces an explainable graph and lockfile instead of silently choosing arbitrary “latest” files.

## Instances and modpacks

Planned instance interchange includes:

- Modrinth `.mrpack`;
- CurseForge packs where provider/distribution rules permit;
- Prism Launcher instances;
- MultiMC-compatible instances;
- vanilla launcher/raw `.minecraft` imports;
- Gridelyx portable bundles.

Immutable downloaded artifacts are intended to live in a content-addressed cache. Writable configs, saves, screenshots, recordings and user edits remain instance-owned unless sharing is explicitly configured.

Schemas are under `studio/schemas/`.

## Java creator / polyloader plane

The existing advanced runtime includes or scaffolds:

- Java Instrumentation + ASM bootstrap;
- loader/JVM fingerprinting;
- Unified Abstraction Layer domains for registry/event/network/resource/render/world/input/lifecycle operations;
- capability-negotiated mod analysis;
- dynamic mesh/texture registries;
- voxel/world editor state;
- live scripting and polyglot execution;
- transactional world editing/rollback;
- scene hierarchy/property systems;
- construction/physics experiments;
- in-game IDE/console/AI passthrough;
- hotload classification and recovery.

Gridelyx does **not** claim arbitrary mods can all be injected live. Mixins, coremods, access wideners, early lifecycle hooks and frozen registries remain restart/prelaunch sensitive unless a version-specific adapter proves otherwise.

Read `docs/POLYLOADER_ARCHITECTURE.md`, `docs/WORLD_EDIT_RUNTIME.md`, `docs/LIVE_ASSET_EDITING.md`, `docs/HOTLOAD_ARCHITECTURE.md` and `docs/FAULT_TOLERANCE.md`.

## Bedrock plane

Bedrock is a first-class target rather than a second copy of the Java feature stack.

- `bedrock/addon` — stable Script API behavior/resource-pack runtime;
- `bedrock/editor-extension` — isolated preview Editor extension;
- `native/bedrock` — VFSB native companion behind a versioned adapter interface;
- `docs/BEDROCK_ARCHITECTURE.md` — capability split;
- `docs/GRIDELYX_BRIDGE_PROTOCOL.md` — transport protocol.

Java 25 FFM/Panama binds Gridelyx's own native ABI. Portable VFSB frames carry control, UAL, mesh, texture, world delta, telemetry and script-result messages. The core does not depend on hard-coded Bedrock executable addresses.

## Machinima / animation / recording / production

Gridelyx Production Studio is designed to turn Minecraft into a virtual production stage.

Planned/started systems include:

- versioned replay/event capture tied to exact instance/content locks;
- rational-time production timeline;
- free/target/orbit/rail/spline cameras;
- camera keyframes for position/rotation/FOV/focus and later exposure metadata;
- actor/entity transform, pose, equipment and animation tracks;
- particles/commands/dialogue/world cues;
- shots, takes, markers and nested sequences;
- slow motion/time remapping/pause staging;
- real-time frame capture;
- deterministic offline frame capture where renderer stepping is available;
- image sequences and replaceable encoder bridge;
- audio-routing/stem support when target hooks permit;
- optional render passes such as depth/normals/object IDs/motion vectors only after target validation.

A neutral rational-time/camera track implementation and smoke test now live under `templates/neoforge-26.2/src/advanced/java/.../production`.

Read `docs/MACHINIMA_PRODUCTION.md`.

## AI-readable repository

Gridelyx is designed for long-lived human + AI development without forcing every agent to ingest the entire repository.

AI entry order:

1. `AGENTS.md`
2. `AI_HANDOFF.md`
3. `ai/CONTEXT.md`
4. `ai/context-map.json`
5. task-specific source/docs

Generate a deterministic file/chunk index:

```bash
python tools/repo_index.py
```

Get a small task-oriented context pack:

```bash
python tools/ai_context_pack.py "launcher Java Fabric dependency resolution"
python tools/ai_context_pack.py "machinima camera offline render"
```

The generated index records path, SHA-256, area, headings, chunks and lexical terms. Future semantic/vector retrieval can key embeddings to commit/path/range/hash so unchanged chunks are reused efficiently.

Read `docs/AI_CONTEXT_SYSTEM.md`.

## Quality gates

Core platform checks include:

```bash
python tools/build_lock.py --check
python tools/script_gatekeeper.py
python tools/ecosystem_check.py
python tools/world_editor_check.py
python tools/polyloader_check.py
python tools/bedrock_check.py
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
cargo test --manifest-path studio/Cargo.toml --all-targets
```

CI remains split by responsibility so failure domains stay visible: Java platform, advanced runtime, Bedrock, native, Studio core/context and security analysis.

## Evidence model

Gridelyx uses readiness levels R0-R6:

- R0 idea;
- R1 contract/schema;
- R2 compile/static validation;
- R3 automated tests;
- R4 headless target integration;
- R5 interactive validation;
- R6 release candidate with packaging/migration/rollback evidence.

A feature name never implies a higher readiness level than the available evidence. See `docs/FEATURE_MAP.md`.

## Reference vault

`references/index/` is the compact lookup layer. `vault/` is exact large recovery/deep-inspection storage and is intentionally excluded from default AI/repository indexing.
