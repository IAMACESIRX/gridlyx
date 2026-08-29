# Gridelyx capability dependency matrix

This matrix answers a different question from [`DEPENDENCIES_AND_TOOLCHAIN.md`](DEPENDENCIES_AND_TOOLCHAIN.md): **what does each major Gridelyx capability need in order to build, exercise or validate?**

`Required` means needed for that subsystem's development/validation lane, not necessarily needed by every Gridelyx user. `Optional` means the subsystem can exist without it but gains an adapter or output path when present.

| Capability / CR | Internal prerequisites | External/build prerequisites | Target/runtime prerequisites | Validation lane |
|---|---|---|---|---|
| CR-001 R&D foundation | version locks, vault tools, reference indexes, workspace generator | Python 3, Git; Java/Gradle for Java builds | exact reference artifacts only for deep reference/recovery | vault hash verification, platform static check, workspace build |
| CR-002 quality/CI | Gradle quality config, GitHub workflows, build lock, Script Gatekeeper | Java, Gradle, Python, GitHub Actions; Dev Container optional | none | Spotless, Checkstyle, CodeQL, diagnostics, CI |
| CR-003 template/datagen | NeoForge template, registries, data providers, blueprints | Java 25, Gradle 9.2.1, ModDevGradle 2.0.144 | NeoForge/Minecraft target for runtime checks | datagen diff, build, GameTest/client inspection |
| CR-004 bytecode/JVM engine | advanced source set, agent bootstrap, ASM transformer, hotswap services | JDK 25, ASM 9.10.1 | exact target class/mapping fingerprints | ASM verification, descriptor locks, redefine tests, runtime client |
| CR-005 workers/sync | bounded worker pool, revision/state-sync primitives | JDK concurrency APIs | server/client integration for live state | unit saturation/shutdown tests, multiplayer integration |
| CR-006 native/GPU/IPC | FFM bridge, ABI contracts, shared-memory frames, GPU wrappers | JDK 25 FFM, Rust/Cargo, CMake + C++ compiler; LWJGL compile-only reference | compatible OS/architecture/GPU/driver | ABI tests, bounds/lifetime tests, native build, render-context client tests |
| CR-007 MCP/AI intelligence | MCP endpoint, repo index, context packs, local vector abstraction | Python for repo tools; AI provider/model optional | local/remote AI only when configured | deterministic index/check, protocol tests, no-secret/provenance checks |
| CR-008 autonomous validation | test harnesses, diagnostics, telemetry/chaos controls | JUnit 6.1.3, ArchUnit 1.4.2, JDK/JFR | Minecraft/NeoForge for GameTest/client validation | unit/architecture/GameTest/interactive/chaos lanes |
| CR-010 Polyloader/UAL | bootstrap, UAL, loader adapters, translators, sideload container | JDK, ASM; mappings/metadata per target | selected Minecraft + source/host loader versions | adapter contract tests, exact loader/version launch tests |
| CR-011 version independence | fingerprint scanner, MethodHandle binder, compatibility manifests | JDK; mappings when available | each historical/current/snapshot target being claimed | family-by-family launch/API/behavior matrix |
| CR-012 hotload | WatchService, redefine service, versioned service loader, virtual definitions | JDK Instrumentation/compiler | running client/server/sidecar target | reload/redefine/schema-change/rollback tests |
| CR-013 live world editor | section buffers, delta compiler, scheduler, transactions, sync | Java target lane | running Java server/client; Bedrock adapter for parity | GameTest + large-edit interactive + persistence/reload checks |
| CR-014 dynamic events/structures | NBT loader, blueprint compiler, event matrix, world transactions | Java target lane | world/server target | deterministic blueprint decode, bounded event application, reload/persistence |
| CR-015 liquids/paint/transmutation | section/overlay buffers, worker/sync/transaction primitives | no extra mandatory external library currently | Java and Bedrock world adapters | deterministic simulation, persistence, replication, rollback, performance |
| CR-016 microgeometry/collision | mesh representation, render override, collision adapter, scene tools | LWJGL API for direct GPU path; Java build lane | renderer/client and physics/collision target | geometry unit tests, visual/client tests, collision behavior/performance |
| CR-017 live asset editor | dynamic model/texture registries, voxel editor workspace, resource reload/hotload | Java build; LWJGL for direct GPU updates | running client renderer | asset reload, atlas/resource lifetime, visual regression/performance |
| CR-018 sandbox physics | physics world, constraint graph, tool gun, sync | Java build lane | authoritative server + clients for multiplayer | deterministic solver tests, constraint tests, replication tests |
| CR-019 scene/Studio tools | scene graph, instance serializer, gizmo manipulator, creator project model | Java + desktop Studio development tools | running target client/editor | serialization round-trip, transform/gizmo integration, client UX |
| CR-020 in-game IDE/AI | screen injection, IDE console, direct compiler, keybinds, AI bridge | JDK compiler; GraalVM for JS/Python; AI provider optional | running client | compile/execute/reload/error isolation, permission tests |
| CR-021 non-Java SDK | neutral bridge protocol, permission/capability model, sidecars/native adapters | Python; Go optional; .NET optional; Rust/C++ optional; GraalVM optional | selected game/bridge host | protocol conformance, capability denial, disconnect/reconnect, resource limits |
| CR-022 multiplayer editing | edit packets/channel, revision log, culler, server scheduler | Java/Netty via game runtime | multiplayer dedicated/integrated server + multiple clients | ordering, auth, culling, consensus/conflict, tick-budget tests |
| CR-023 anti-crash | script supervisor, fault boundaries, transactions/WAL, process supervisor roadmap | OS process facilities; optional container/sandbox mechanisms later | client/server/sidecars | timeouts, process crash/restart, rollback, fault-injection campaigns |
| CR-024 Bedrock parity | behavior/resource packs, Editor extension, Bedrock bridge/native adapter | Bedrock target packages; CMake/C++ for native companion; JDK FFM for Java bridge | legitimate Bedrock Stable/Preview/Editor/Dedicated Server as applicable | stable/preview separation, script checks, native ABI, target interaction tests |
| CR-025 launcher/resolver | Studio core, provider adapters, schemas, content locks | Rust/Cargo; network/TLS/OS credential implementations as selected | Microsoft/Minecraft auth and provider services | unit/provider mocks, integration downloads, launch tests on supported OSes |
| CR-026 mod forking/decompilation | archive/JAR analysis and bytecode diff tools | JDK `javap`; external decompiler optional/user-supplied | local authorized mod JARs | deterministic extraction/classification/diff, provenance/licence review |
| CR-027 machinima/production | replay/timeline/camera/track schemas, capture adapters | external FFmpeg/encoder optional; future interchange tools optional | target renderer/audio hooks | rational-time unit tests, replay reproducibility, interactive/offline capture tests |
| CR-028 dimensions/worldgen/teleport | dimension manager, codecs/datagen, teleport channels, virtual registries | Java/NeoForge current lane | target game version/loader | codec round trip, deterministic seed, teleport/world lifecycle tests |
| CR-029 render/volumetric/IK | render pipeline, GPU buffer, PoseStack interception, IK, volumetric stream | LWJGL compile-only/direct GPU path | compatible client renderer/GPU/driver | render-thread assertions, numerical IK tests, visual/performance matrix |
| CR-030 deep integration/patch manager | immutable-base model, fingerprints, patch graph, rollback design | platform-specific patch/build tools selected per target | exact executable/library versions | byte-for-byte base verification, patch derivation, launch, rollback/recovery |
| CR-031 community | community docs, issue/PR templates, support/security routing | Git/GitHub | none | documentation/link review and contributor dry-run |
| CR-032 Gridelyx rebrand | brand manifest, rebrand plan, decision records, terminology inventory | Python checker tooling | persisted/wire/native consumers for migration testing | terminology scan, build/native/Bedrock tests, compatibility migration tests |
| CR-033 dependency inventory | toolchain docs + machine manifest + checks | Python checker | subsystem-dependent | toolchain manifest CI and evidence-path validation |

## External services and credentials

Only the selected capability should request the corresponding service/credential:

- Mojang/Microsoft account and launcher metadata for legitimate Minecraft acquisition/launch;
- Modrinth API for Modrinth content;
- CurseForge approved API key for CurseForge content;
- Adoptium API for Temurin fallback;
- optional AI provider credentials only if a configured remote AI adapter requires them.

Secrets are never repository dependencies and must not be committed.

## No hidden dependencies rule

When a change introduces a new compiler, runtime, executable, provider, Maven/Gradle dependency, native library, Bedrock package, network service or AI model/provider, the same change must update:

1. [`DEPENDENCIES_AND_TOOLCHAIN.md`](DEPENDENCIES_AND_TOOLCHAIN.md);
2. `../platform/toolchain-requirements.json` if it is a project/subsystem tool;
3. this capability matrix if it changes a major capability lane;
4. licensing/provenance records where applicable;
5. CI/setup scripts if reproducible validation depends on it.
