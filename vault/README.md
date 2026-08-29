# Upstream Acquisition Manifest

This directory contains **metadata only**. Gridelyx does not use Git as a binary vault for Minecraft, NeoForge, the JDK, LWJGL, Gradle distributions, installer JARs, or other third-party runtime/development payloads.

[`manifest.json`](manifest.json) records the canonical versions, official providers, resolver strategy, optional pinned reference source, and the project rule that upstream binaries must not be stored in the repository.

Build and run behavior:

1. GitHub Actions installs the locked Temurin JDK and Gradle dynamically.
2. NeoForge ModDevGradle resolves Minecraft, NeoForge, mappings, and the development runtime into local Gradle caches.
3. Maven dependencies such as LWJGL, ASM, GraalVM Polyglot, JUnit and ArchUnit are resolved from their configured repositories.
4. Optional upstream source/reference material is hydrated only into `.reference-cache/`, which is ignored by Git.
5. [`tools/redistribution_guard.py`](../tools/redistribution_guard.py) rejects tracked JARs, archives, native binaries, class files and upstream reference trees.

Validation uses [`tools/hydrate_references.py`](../tools/hydrate_references.py) and [`tools/redistribution_guard.py`](../tools/redistribution_guard.py):

```bash
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
```

Optional pinned MDK reference checkout also uses [`tools/hydrate_references.py`](../tools/hydrate_references.py):

```bash
python tools/hydrate_references.py --mdk
```

No command in the supported workflow copies the acquired upstream payloads back into tracked repository paths.
