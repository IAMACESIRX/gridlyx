# Gridelyx dependencies and toolchain

Status: **canonical dependency/tooling inventory**

This document records the software, runtimes, libraries, external programs and provider channels needed to build, test, run or extend Gridelyx. It distinguishes **project dependencies**, **developer prerequisites**, **optional subsystem tools**, **target runtimes**, **external provider services** and **dynamically acquired upstream references**.

Machine-readable inventory: [`../platform/toolchain-requirements.json`](../platform/toolchain-requirements.json).

## Dependency policy

1. Do not guess versions when the repository already pins one.
2. Acquire third-party toolchains/runtime dependencies from official or authorized upstream channels at install/build/run time.
3. Do **not** commit Minecraft, NeoForge, JDK, Gradle, LWJGL, Maven-cache or other upstream binary payloads to the repository.
4. Prefer supported package/toolchain resolvers over custom direct-download logic where one exists.
5. Record provider, version, immutable revision/checksum where appropriate, and verify upstream integrity when available.
6. Keep optional hydrated reference material in ignored local caches.
7. Optional deep/native/polyglot/production tooling must fail visibly when absent rather than silently changing behavior.
8. Before release, presently-unpinned developer toolchains must gain a supported-version policy and CI coverage.

The acquisition/no-redistribution contract is machine-readable in [`../vault/manifest.json`](../vault/manifest.json) and enforced by [`../tools/redistribution_guard.py`](../tools/redistribution_guard.py).

## Core Java mod-development lane

| Component | Version/policy | Acquisition/use |
|---|---|---|
| Minecraft Java target | `26.2` current canonical template | Resolved by the supported NeoForge ModDevGradle development toolchain from Mojang metadata/runtime services into local/runner caches. |
| NeoForge | `26.2.0.67` | Resolved by ModDevGradle from official NeoForged infrastructure; installer/runtime JARs are not vendored. |
| Java | Eclipse Temurin `25.0.4+7`, language level 25 | CI dynamically installs exact Temurin through `actions/setup-java`; local developers may use a compatible installed JDK. |
| Gradle | `9.2.1` | CI dynamically installs the locked distribution through `gradle/actions/setup-gradle`; the repository launcher is a system-Gradle shim rather than a vendored wrapper JAR. |
| ModDevGradle | `2.0.144` | Resolved from Gradle plugin repositories. |
| ASM | `9.10.1` | Maven Central, advanced source set. |
| LWJGL API reference | `3.4.1` | Maven-resolved compile-only/reference modules for advanced GPU work; no LWJGL distribution bundle is stored. |
| GraalVM Polyglot | `25.3.4.1` | Maven/GraalVM artifacts for opt-in advanced JavaScript/Python scripting. |
| Spotless | `8.10.0` | Gradle quality plugin. |
| Google Java Format | `1.36.0` | Formatting policy/reference. |
| Checkstyle | `14.0.0` | Gradle/Maven static style gate. |
| JUnit | `6.1.3` | Maven-resolved logic/unit testing. |
| ArchUnit | `1.4.2` | Maven-resolved architecture tests. |

The authoritative locks are [`../platform/versions.json`](../platform/versions.json), [`../vault/manifest.json`](../vault/manifest.json) and [`../templates/neoforge-26.2/gradle.properties`](../templates/neoforge-26.2/gradle.properties).

## Dynamic GitHub Actions bootstrap

Java build workflows use the repository-local composite action:

```yaml
- uses: actions/checkout@v7
- uses: ./.github/actions/gridelyx-toolchain
```

The action:

1. installs the exact Temurin JDK into the GitHub runner tool cache;
2. installs Gradle `9.2.1` into the runner/tool cache;
3. validates `vault/manifest.json` as acquisition-only metadata;
4. runs the tracked-file redistribution guard;
5. confirms Java and Gradle are available.

The subsequent Gradle invocation then resolves Minecraft, NeoForge and Java libraries on demand. Git does not supply those bytes.

## Repository automation prerequisites

### Python

Gridelyx repository tooling is Python 3 based (`tools/*.py`). An exact minimum Python version is not yet frozen; CI currently uses the Python available on GitHub-hosted runners. Before a release-support matrix is declared, pin and test a minimum supported Python version.

Used for:

- platform and continuity checks;
- local reference acquisition/indexing;
- no-redistribution enforcement;
- workspace generation;
- build orchestration and diagnostics;
- bytecode analysis wrappers;
- CSV/data conversion;
- toolchain/requirements/terminology validation.

### Git and GitHub

Git is required for normal source development and optional pinned upstream reference hydration. GitHub Actions hosts CI; GitHub Issues/PRs are the project-management workflow. GitHub CLI is optional rather than required by the source tree.

### Dev Containers / Docker

The repository contains `.devcontainer/` for a reproducible interactive environment. A Docker-compatible container runtime and an editor/client supporting Dev Containers are optional developer conveniences, not runtime dependencies of Gridelyx itself.

## Native and desktop development lane

### Rust

[`../studio/`](../studio/) and [`../native/rust/`](../native/rust/) use Cargo/Rust. The Studio workspace declares **Rust edition 2024**. The exact Rust toolchain release is not yet pinned; CI should remain the source of evidence until a supported release toolchain is frozen.

Required for:

- launcher/Studio core;
- native extension examples;
- future desktop/runtime services written in Rust.

### C/C++ and CMake

CMake plus a compatible C++ compiler is required for [`../native/cpp/`](../native/cpp/) and [`../native/bedrock/`](../native/bedrock/).

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

Current target locks in [`../platform/versions.json`](../platform/versions.json) include:

- Bedrock stable `1.26.40`;
- `@minecraft/server` `2.9.0`;
- preview `1.26.50-preview.26`;
- Server Editor `0.1.0-beta.1.26.50-preview.26`;
- Server Net `1.0.0-beta.1.26.50-preview.26`.

Bedrock development/validation may require the appropriate Bedrock client/Preview/Editor or Dedicated Server target. Stable, preview and native-companion features must remain separated because API availability differs.

The Java/native bridge uses project-owned FFM/Panama + native ABI code; an installed JDK alone does not turn unsupported Bedrock engine internals into stable APIs.

## Launcher/provider services

The launcher/resolver is designed to acquire files through legitimate/authorized channels rather than redistributing those provider payloads from the Gridelyx repository:

- Mojang/Piston metadata — Minecraft versions, version JSON, libraries, assets and managed runtime metadata;
- Fabric Meta;
- Quilt Meta;
- official Forge distribution/Maven;
- official NeoForge Maven/services;
- Modrinth API;
- CurseForge approved third-party API with required API key and author distribution restrictions respected;
- Eclipse Adoptium/Temurin channels for Java fallback;
- explicit local import for user-owned/legacy files where permitted.

Canonical provider policy: [`../studio/providers/providers.json`](../studio/providers/providers.json).

Microsoft/Minecraft account authentication is a planned launcher dependency and must use supported OAuth/account flows. Credentials must be kept in OS credential storage, never committed to the repository.

## Production / machinima external tools

### FFmpeg or equivalent encoder

A replaceable external encoder is planned for video/audio export. FFmpeg is the primary planned adapter, but it is **not bundled or assumed licensed for redistribution by this repository**. The eventual launcher must record the executable version, provenance and licence, invoke it with structured arguments, and make encoder absence a visible capability state.

### Image/audio interchange tools

Image-sequence export is designed to work without requiring a specific NLE/DCC. OTIO/EDL/glTF/USD and other interchange tooling remain research/optional adapters and must be version/provenance gated when added.

## Mod analysis / decompilation tools

[`../tools/bytecode_diff.py`](../tools/bytecode_diff.py) and [`../tools/fork_mod.py`](../tools/fork_mod.py) support structural inspection/fork workflows. An external decompiler may be attached where configured, but decompiler output is not automatically redistributable source. Any specific decompiler added to a supported lane must be documented with version, licence and acquisition source.

`javap` is supplied by the JDK and is the baseline bytecode/disassembly dependency.

## AI / MCP dependencies

Gridelyx's AI layer is intentionally provider-neutral. Repository code includes MCP/context/indexing interfaces, but no cloud AI provider or API key is required to build the core project.

Possible adapters include:

- local model processes;
- Python AI sidecars;
- MCP-connected tools/services;
- explicitly configured remote AI providers.

Secrets/API keys are external configuration and must not be stored in source, diagnostics or exported project bundles.

## Upstream reference acquisition

There is no binary reference vault to complete or import.

The metadata-only [`../vault/manifest.json`](../vault/manifest.json) records canonical upstream providers and version locks. The NeoForge 26.2 MDK can be hydrated as an optional pinned source reference with:

```bash
python tools/hydrate_references.py --mdk
```

It is cloned to `.reference-cache/upstream/mdk-26.2`. Local reference indexes generated by [`../tools/build_reference_indexes.py`](../tools/build_reference_indexes.py) are also written under `.reference-cache/`. Both locations are ignored by Git.

Minecraft/NeoForge runtime artifacts, JDK/Gradle distributions and Maven dependencies are acquired by their normal resolvers/toolchain actions instead of reference hydration.

## Tools that must not become implicit runtime dependencies

The following are reference/development aids unless a target explicitly opts in:

- optional upstream MDK checkout;
- decompilers;
- FFmpeg;
- Docker/Dev Containers;
- Go/.NET toolchains;
- CMake/C++ compilers;
- AI providers/models.

The launcher should acquire only what the selected Gridelyx feature/instance requires.

## Release hardening still required

Before Gridelyx can advertise a fully supported reproducible public toolchain, complete these items:

1. freeze/test minimum Python version;
2. freeze/test supported Rust toolchain;
3. freeze/test CMake and C/C++ compiler matrix;
4. add Go and .NET bridge conformance CI or mark those adapters unsupported on release platforms;
5. define encoder acquisition/provenance policy;
6. produce SBOM and licence/provenance reports;
7. validate public-repository acquisition from a clean machine/runner with empty caches;
8. validate platform-specific Bedrock/Java/native prerequisites interactively.
