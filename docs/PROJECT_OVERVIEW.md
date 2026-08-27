# Gridelyx Studio — full project overview

## Product definition

Gridelyx Studio is a cross-edition Minecraft launcher, instance manager, mod/content manager, development environment, world/voxel authoring suite and machinima/production platform. It combines a low-friction default UX comparable to consumer launchers with an expert mode that exposes the complete resolved runtime graph: Minecraft version, loader, Java runtime, libraries, mods, dependencies, hashes, provenance, JVM/game arguments, toolkit modules and compatibility decisions.

The product must launch even when Java is not installed. Java is therefore a managed dependency of Java Edition instances, not a prerequisite of the Gridelyx desktop shell.

## Product planes

1. **Desktop control plane** — profiles, accounts, settings, downloads, instances, updates, diagnostics and launch orchestration.
2. **Acquisition/provenance plane** — official/authorized source adapters, hash verification, licensing/provenance records, cache policy and resumable downloads.
3. **Resolution plane** — Minecraft/loader/Java compatibility, transitive dependencies, optional dependencies, incompatibilities, pack imports and deterministic lockfiles.
4. **Instance plane** — isolated instances, shared content-addressed caches, overrides, saves, configs, resource packs, shaders, servers and portable exports.
5. **Toolkit/creator plane** — Gridelyx live scripting, UAL, world editing, voxel/mesh/texture editing, construction tools, AI authoring and Java/Bedrock adapters.
6. **Production plane** — replay/event capture, camera direction, animation, actor blocking, timeline sequencing, recording, offline frame rendering, audio stems and export.
7. **AI intelligence plane** — compact context maps, generated repository indexes, feature/status maps, decisions, handoff state and task-scoped context packs.
8. **Validation/operations plane** — CI, GameTest, Bedrock gates, native builds, security, provenance, telemetry, profiling, release evidence and rollback.

## User experience modes

### Simple mode

- Choose Minecraft version or modpack.
- Gridelyx selects the correct Java runtime and compatible loader.
- Search Modrinth and authorized CurseForge content.
- One-click install/update with dependency resolution.
- Clear conflicts with suggested fixes.
- Play, edit or create without seeing implementation details unless something needs attention.

### Advanced mode

- Pin exact Minecraft, loader, Java vendor/build and architecture.
- Inspect/override memory, JVM args, game args and environment.
- View the complete dependency graph and why each artifact was selected.
- Inspect hashes, source, licence, compatibility metadata and lockfile diff.
- Toggle individual toolkit modules, native bridges and experimental capabilities.
- Clone/fork/compare instances and modpacks.
- Export diagnostic bundles without secrets.

## Supported acquisition principle

“Any version / any loader / any mod” is implemented as an extensible adapter model, not by guessing arbitrary URLs. Built-in adapters cover Mojang/vanilla, Fabric, Quilt, Forge and NeoForge. Legacy or future loaders use the same signed/provider adapter contract or explicit user-supplied launcher-profile import. Content acquisition uses official or authorized channels and never bypasses an author/provider distribution restriction.

## Managed Java

Resolution order:

1. Read the selected Minecraft version metadata and honor its declared Java requirement/runtime component when available.
2. Prefer the official Mojang-managed runtime for ordinary play when available.
3. Allow a user-selected compatible local JVM.
4. Allow a managed Eclipse Temurin/Adoptium fallback.
5. Record vendor, version, architecture, path and provenance in the instance lock.

Gridelyx must never infer the Java major solely from a hard-coded Minecraft-version table when authoritative version metadata is available.

## Content and dependency resolution

Every resolved artifact becomes a node in a dependency graph. Edges are `required`, `optional`, `embedded` or `incompatible`. The resolver must:

- filter by game version, loader, side/client-server constraints and release channel;
- follow required transitive dependencies;
- surface optional dependencies instead of silently forcing them;
- reject incompatible pairs;
- produce deterministic install order;
- retain the reason each version was selected;
- generate a content lock with source URI, provider IDs, hashes and provenance;
- avoid replacing a specifically requested file with “latest” when a provider returns an unspecified file ID.

## Modpack and instance interchange

Planned import/export adapters:

- Modrinth `.mrpack`;
- CurseForge modpack manifests where API/distribution rules permit;
- Prism Launcher instances;
- MultiMC-compatible instances;
- vanilla launcher profiles;
- raw `.minecraft`/mods-folder import;
- Gridelyx native portable instance bundle.

Imports are normalized into the Gridelyx instance model and then re-resolved rather than executed blindly.

## Creator suite

Gridelyx Studio creator mode unifies:

- in-game IDE and console;
- live Java/GraalJS/GraalPy scripting;
- loader-neutral UAL operations;
- world/structure editing;
- procedural generation;
- mesh, voxel and texture authoring;
- scene hierarchy and property editing;
- construction/physics tooling;
- AI-assisted content generation;
- Bedrock Add-On/Editor/native-companion targets;
- hot reload/restart classification and rollback.

## Machinima and production

Production mode is a non-destructive scene/timeline layer on top of the game runtime.

Core systems:

- deterministic replay/event log with tick/time anchors;
- free, target, orbit, rail and spline cameras;
- camera keyframes with position/rotation/FOV/focus/exposure tracks;
- actor/entity animation tracks and pose layers;
- transform, visibility, item, particle, command and dialogue cue tracks;
- scene markers, takes, shots and nested sequences;
- slow motion, time remapping and pause-frame staging;
- real-time capture and deterministic offline frame capture;
- configurable resolution, frame rate, supersampling and render passes when the target adapter supports them;
- audio stem capture for game/SFX/voice/music/ambient where available;
- image-sequence export and encoder bridge (for example FFmpeg through a separately licensed/provenanced executable);
- project files that are editable without rewriting the original world/replay.

Java and Bedrock share the neutral production timeline/protocol. Renderer-specific capture and animation capabilities are declared by adapters so unsupported passes fail visibly instead of being faked.

## Storage model

- `instances/` — isolated writable user instances (outside the source repository in production).
- content-addressed global cache — immutable downloaded artifacts keyed by cryptographic hash.
- per-instance lockfile — exact resolved graph and provenance.
- mutable instance overlays — configs, saves, screenshots, recordings and user edits.
- production projects — scene/timeline/replay metadata referencing instance/world assets by stable IDs.

Hard links/reflinks may deduplicate immutable artifacts when the filesystem supports them; writable state must never be accidentally shared between independent instances.

## Security model

- unknown network hosts denied by default;
- no credentials in repositories, logs, lockfiles or diagnostic exports;
- provider-specific tokens stored in OS credential storage;
- all downloads get a local SHA-256; upstream hashes/signatures are checked when available;
- executable installers/native components are isolated, provenance-recorded and invoked with explicit arguments;
- imported archives are path-sanitized before extraction;
- mods remain untrusted executable code and are clearly identified as such;
- experimental native/bytecode paths stay opt-in and version-gated.

## Definition of done

A feature is not “supported” merely because an interface exists. Gridelyx uses the repository readiness scale from contract-only through compile/static validation, automated tests, headless game validation, interactive validation and release candidate. The feature map records the current evidence level per target.
