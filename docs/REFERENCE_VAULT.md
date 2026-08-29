# Upstream Acquisition and Reference Policy

Gridelyx is designed to be safe to publish as a public source repository without turning the repository into a redistribution mirror for Minecraft, NeoForge, JDK distributions, LWJGL bundles, Gradle distributions, or other third-party binaries.

## Core rule

The repository stores **instructions and provenance, not upstream payloads**.

Tracked content may include:

- Gridelyx-owned source, assets, schemas, tests, documentation and build logic;
- dependency coordinates and canonical version locks;
- official upstream URLs and repository identifiers;
- upstream-provided checksums or immutable source revision identifiers;
- acquisition scripts that write only to ignored local/tool caches.

Tracked content must not include upstream JARs, class files, ZIP/TAR archives, installers, native libraries, chunked archive parts, decompiled Minecraft source trees, or hydrated upstream reference trees.

## Build-time acquisition

The Java/NeoForge build deliberately relies on supported dependency resolution instead of checked-in binaries:

1. GitHub Actions installs Eclipse Temurin JDK 25 through [`actions/setup-java`](https://github.com/actions/setup-java).
2. GitHub Actions installs the locked Gradle release through [`gradle/actions/setup-gradle`](https://github.com/gradle/actions/tree/main/setup-gradle).
3. The [`net.neoforged.moddev`](https://docs.neoforged.net/toolchain/docs/plugins/mdg/) plugin resolves the required Minecraft/NeoForge development runtime and mappings into the runner/developer Gradle cache.
4. Maven dependencies such as LWJGL, ASM, GraalVM Polyglot, JUnit and ArchUnit are resolved from configured package repositories.
5. Build outputs contain Gridelyx/mod artifacts; upstream game/runtime dependencies are not committed back to Git.

The supported GitHub Action entry point is [`.github/actions/gridelyx-toolchain/action.yml`](../.github/actions/gridelyx-toolchain/action.yml).

## Optional source/reference hydration

The NeoForge 26.2 MDK is an optional comparison/provenance reference, not a build input. Its official repository and pinned revision are recorded in [`vault/manifest.json`](../vault/manifest.json).

To hydrate it locally with [`tools/hydrate_references.py`](../tools/hydrate_references.py):

```bash
python tools/hydrate_references.py --mdk
```

The checkout is written to `.reference-cache/upstream/mdk-26.2` and remains ignored by Git.

## Enforcement

Run [`tools/hydrate_references.py`](../tools/hydrate_references.py) and [`tools/redistribution_guard.py`](../tools/redistribution_guard.py):

```bash
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
```

[`tools/redistribution_guard.py`](../tools/redistribution_guard.py) scans the actual Git index using `git ls-files`. It rejects tracked JARs, class files, archives, native binaries, installer formats, chunked parts, and known upstream reference trees. This means [`.gitignore`](../.gitignore) is not the only protection: even a force-added prohibited payload fails validation.

## Minecraft source policy

Do not commit decompiled or reconstructed Mojang game source into this repository. Use the licensed development toolchain and mappings to materialise development sources locally when required. The same principle applies to other upstream source/reference trees unless their inclusion has been explicitly reviewed for redistribution compatibility.

This architecture reduces redistribution risk but does not replace project-specific license review. Each upstream dependency remains governed by its own license, terms and distribution policy.
