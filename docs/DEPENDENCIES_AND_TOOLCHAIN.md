# Gridelyx dependencies and toolchain

Status: **canonical dependency/tooling inventory**

This document records the software, runtimes, libraries, external programs and provider channels needed to build, test, run or extend Gridelyx. It distinguishes **project dependencies**, **developer prerequisites**, **optional subsystem tools**, **target runtimes**, **external provider services** and **reference-only artifacts**.

Machine-readable inventory: `../platform/toolchain-requirements.json`.

## Dependency policy

1. Do not guess versions when the repository already pins one.
2. Prefer wrapper/build-manager acquisition for build dependencies.
3. Prefer official or authorized upstreams for Minecraft, loaders, content and runtimes.
4. Record SHA-256/provenance for downloaded artifacts; verify upstream hashes/signatures when available.
5. Keep reference corpora out of the default Minecraft runtime classpath.
6. Optional deep/native/polyglot/production tooling must fail visibly when absent rather than silently changing behavior.
7. Before release, presently-unpinned developer toolchains must gain a supported-version policy and CI coverage.

## Core Java mod-development lane

| Component | Version/policy | Acquisition/use |
|---|---|---|
| Minecraft Java target | `26.2` current canonical template | Resolved through project version lock; broader versions use loader/version adapters rather than hard-coding this target everywhere. |
| NeoForge | `26.2.0.67` | Official NeoForged Maven/installer channels. |
| Java | Eclipse Temurin `25.0.4+7`, language level 25 | CI uses exact Temurin; launcher eventually resolves Java per instance from Mojang metadata/local JVM/Adoptium fallback. |
| Gradle | `9.2.1` | Gradle wrapper / CI setup. |
| ModDevGradle | `2.0.144` | Gradle plugin. |
| ASM | `9.10.1` | Maven Central, advanced source set. |
| LWJGL API reference | `3.4.1` | Compile-only/reference use for advanced GPU work. Do not force the supplied reference bundle into Minecraft's runtime classpath. |
| GraalVM Polyglot | `25.3.4.1` | Opt-in advanced scripting runtime for JavaScript/Python. |
| Spotless | `8.10.0` | Gradle quality plugin. |
| Google Java Format | `1.36.0` | Formatting policy/reference. |
| Checkstyle | `14.0.0` | Gradle static style gate. |
| JUnit | `6.1.3` | Logic/unit testing. |
| ArchUnit | `1.4.2` | Architecture tests. |

The authoritative locks are `../platform/versions.json` and `../templates/neoforge-26.2/gradle.properties`.

## Repository automation prerequisites

### Python

Gridelyx repository tooling is Python 3 based (`tools/*.py`). An exact minimum Python version is not yet frozen; CI currently uses the Python available on GitHub-hosted runners. Before a release-support matrix is declared, pin and test a minimum supported Python version.

Used for:

- platform and continuity checks;
- repository indexing/context packs;
- vault import/reconstruction/reference lookup;
- workspace generation;
- build orchestration and diagnostics;
- bytecode analysis wrappers;
- CSV/data conversion;
- toolchain/requirements/terminology validation.

### Git and GitHub

Git is required for normal source development. GitHub Actions hosts CI; GitHub Issues/PRs are the project-management workflow. GitHub CLI is optional rather than required by the source tree.

### Dev Containers / Docker

The repository contains `.devcontainer/` for a reproducible interactive environment. A Docker-compatible container runtime and an editor/client supporting Dev Containers are optional developer conveniences, not runtime dependencies of Gridelyx itself.

## Native and desktop development lane

### Rust

`studio/` and `native/rust/` use Cargo/Rust. The Studio workspace declares **Rust edition 2024**. The exact Rust toolchain release is not yet pinned; CI should remain the source of evidence until a supported release toolchain is frozen.

Required for:

- launcher/Studio core;
- native extension examples;
- future desktop/runtime services written in Rust.

### C/C++ and CMake

CMake plus a compatible C++ compiler is required for `native/cpp/` and `native/bedrock/`.

Expected development compilers:

- Windows: MSVC/Visual Studio Build Tools;
- Linux: GCC or Clang;
- macOS support requires a later validated Apple Clang lane.

Exact minimum CMake/compiler versions are not yet pinned and must be added before release packaging claims are made.

## Cross-process language bridges

These are **subsystem prerequisites**, not requirements to use every Gridelyx feature:

- **Python 3** — Python bridge/AI sidecars;
- **Go toolchain** — `bridges/go/` conformance/example programs;
- **.NET SDK / C# compiler** — `bridges/csharp/` conformance/example programs;
- **Rust/C++ toolchains** — native sidecars and ABI implementations;
- **GraalVM Polyglot libraries** — embedded JavaScript/Python inside the Java advanced runtime.

A bridge is capability-gated. Installing its language runtime does not automatically grant game/world/server authority.

## Bedrock target lane

Current target locks in `../platform/versions.json` include:

- Bedrock stable `1.26.40`;
- `@minecraft/server` `2.9.0`;
- preview `1.26.50-preview.26`;
- Server Editor `0.1.0-beta.1.26.50-preview.26`;
- Server Net `1.0.0-beta.1.26.50-preview.26`.

Bedrock development/validation may require the appropriate Bedrock client/Preview/Editor or Dedicated Server target. Stable, preview and native-companion features must remain separated because API availability differs.

The Java/native bridge uses project-owned FFM/Panama + native ABI code; an installed JDK alone does not turn unsupported Bedrock engine internals into stable APIs.

## Launcher/provider services

The launcher/resolver is designed to acquire files through these legitimate/authorized channels:

- Mojang/Piston metadata — Minecraft versions, version JSON, libraries, assets and managed runtime metadata;
- Fabric Meta;
- Quilt Meta;
- official Forge distribution/Maven;
- official NeoForge Maven/installer;
- Modrinth API;
- CurseForge approved third-party API with required API key and author distribution restrictions respected;
- Eclipse Adoptium API for Temurin fallback;
- explicit local import for user-owned/legacy files.

Canonical provider policy: `../studio/providers/providers.json`.

Microsoft/Minecraft account authentication is a planned launcher dependency and must use supported OAuth/account flows. Credentials must be kept in OS credential storage, never committed to the repository.

## Production / machinima external tools

### FFmpeg or equivalent encoder

A replaceable external encoder is planned for video/audio export. FFmpeg is the primary planned adapter, but it is **not bundled or assumed licensed for redistribution by this repository**. The eventual launcher must record the executable version, provenance and licence, invoke it with structured arguments, and make encoder absence a visible capability state.

### Image/audio interchange tools

Image-sequence export is designed to work without requiring a specific NLE/DCC. OTIO/EDL/glTF/USD and other interchange tooling remain research/optional adapters and must be version/provenance gated when added.

## Mod analysis / decompilation tools

`tools/bytecode_diff.py` and `tools/fork_mod.py` support structural inspection/fork workflows. An external decompiler may be attached where configured, but decompiler output is not automatically redistributable source. Any specific decompiler added to a supported lane must be documented with version, licence and acquisition source.

`javap` is supplied by the JDK and is the baseline bytecode/disassembly dependency.

## AI / MCP dependencies

Gridelyx's AI layer is intentionally provider-neutral. Repository code includes MCP/context/indexing interfaces, but no cloud AI provider or API key is required to build the core project.

Possible adapters include:

- local model processes;
- Python AI sidecars;
- MCP-connected tools/services;
- explicitly configured remote AI providers.

Secrets/API keys are external configuration and must not be stored in source, diagnostics or exported project bundles.

## Large reference vault

The reference vault records exact supplied artifacts for deep inspection/recovery:

- NeoForge/ModDevGradle MDK archive;
- NeoForge installer;
- Eclipse Temurin JDK archive;
- LWJGL reference bundle.

Their exact hashes/chunk layout are recorded in `../vault/manifest.json` and `../references/index/supplied-artifacts.tsv`.

**Remote-state caveat:** [`../vault/REMOTE_BINARY_IMPORT_PENDING.md`](../vault/REMOTE_BINARY_IMPORT_PENDING.md) currently exists. Therefore the GitHub repository contains the manifest/control layer, but the exact large binary payload has not yet been fully imported to remote GitHub. This is a tracked deployment task, not hidden completion.

## Tools that must not become implicit runtime dependencies

The following are reference/development aids unless a target explicitly opts in:

- supplied JDK ZIP;
- supplied LWJGL bundle;
- decompilers;
- FFmpeg;
- Docker/Dev Containers;
- Go/.NET toolchains;
- CMake/C++ compilers;
- AI providers/models.

The launcher should acquire only what the selected Gridelyx feature/instance requires.

## Release hardening still required

Before Gridelyx can advertise a reproducible public toolchain, complete these items:

1. freeze/test minimum Python version;
2. freeze/test supported Rust toolchain;
3. freeze/test CMake and C/C++ compiler matrix;
4. add Go and .NET bridge conformance CI or mark those adapters unsupported on release platforms;
5. define encoder acquisition/provenance policy;
6. produce SBOM and licence/provenance reports;
7. finish importing/verifying the exact reference-vault bytes where desired;
8. validate platform-specific Bedrock/Java/native prerequisites interactively.
