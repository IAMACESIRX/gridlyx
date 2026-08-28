# Gridelyx benchmarking matrix

This document records **comparison targets and questions**, not claims that Gridelyx already matches them. Before a feature decision relies on a benchmark, verify the reference project's current behaviour and licensing/API constraints.

## Primary comparison targets

| Gridelyx area | Benchmark targets | What to study | What Gridelyx should avoid copying blindly |
|---|---|---|---|
| Consumer mod discovery/install | CurseForge, Modrinth App | low-friction discovery, install/update UX, dependency presentation | provider-specific lock-in, opaque resolver decisions |
| Expert instance control | Prism Launcher, MultiMC | isolated instances, runtime/JVM inspection, import/export, logs | exposing complexity without a simple mode |
| Live in-game creation | Hytale creator/editor concepts | asset/model/voxel workflows, immediate preview, creator ergonomics | assuming another game's engine/data model maps directly to Minecraft |
| Sandbox construction/physics | Garry's Mod | tool-gun workflow, constraints, manipulable props, multiplayer sandbox interaction | client-authoritative physics or unbounded arbitrary execution |
| Live scene/game authoring | Roblox Studio | hierarchy, properties, gizmos, live script execution, asset workflow | proprietary platform assumptions or irreversible project-format coupling |
| World editing | MCEdit, WorldEdit concepts, Bedrock Editor | bulk spatial operations, selection, structure editing, generated-chunk manipulation | per-block mutation paths that destroy performance/rollback semantics |
| World simulation/content progression | Terraria | liquids, paint/material presentation, world-state progression/transmutation | assuming tile-engine rules fit Minecraft chunks/networking unchanged |
| Loader/mod ecosystems | Fabric, Quilt, Forge, NeoForge | lifecycle, registries, transformers, mappings, metadata and dependency semantics | pretending API names are semantically identical across loaders/versions |
| Java hot development | Java Instrumentation, classloader/service replacement, DCEVM-style concepts where relevant | redefine limits, service replacement, state preservation | claiming structural Java changes can always use standard HotSwap |
| Polyglot scripting | GraalVM | sandboxing, language embedding, host access, lifecycle/performance | unrestricted host/JVM authority |
| Native bridge architecture | Project Panama/FFM, shared-memory IPC systems | ABI contracts, ownership, zero/low-copy transfer, isolation | undocumented native pointer sharing without validation/versioning |
| Multiplayer replication | established authoritative multiplayer engine patterns | interest management, revisions, ACK/reconciliation, server authority | broadcasting every edit to every client or mutating world state off-thread |
| Digital content creation / machinima | Blender/NLE/DCC timeline concepts, replay/camera mods | rational time, keyframes, shots/takes, project portability, render/export queues | tying projects to one renderer or lossy timeline representation |
| AI-assisted development | MCP-capable developer tools, local code indexes | tool contracts, scoped context, deterministic repository navigation | giving AI implicit authority or treating generated summaries as source truth |

## Benchmark workflow

For each benchmarked capability:

1. define the user outcome being compared;
2. verify the reference product/project's current workflow;
3. separate observable workflow from hidden implementation assumptions;
4. identify the operational primitive that makes the experience successful;
5. identify failure/recovery behaviour;
6. record platform constraints, licensing/provider/API boundaries and version assumptions;
7. determine what Gridelyx should **emulate**, **exceed**, **reject**, or **generalize**;
8. feed the result into the Feature Decision Packet.

## Reverse-engineering questions

Ask:

- What is the smallest core primitive behind the benchmark's strongest workflow?
- Which state is authoritative?
- Which work is synchronous vs asynchronous?
- What is cached, streamed, persisted or regenerated?
- What happens when a plugin/script/editor action fails halfway?
- How are versions/dependencies/assets represented?
- How does multiplayer scope data to relevant users?
- Which parts are product-specific and which can become neutral Gridelyx contracts?
- What would break if Minecraft/Bedrock/loader/runtime versions change?

## Benchmarking rule

Gridelyx does not aim to become a collage of other products. Benchmarks expose successful operational patterns. Final architecture must be rebuilt from Gridelyx first principles, retained requirements, target constraints and evidence.
