# Platform Architecture

## Purpose

The platform separates **Gridelyx-owned source/control state** from **dynamically acquired upstream/build state**.

```text
                           +-----------------------+
request / AI intent -----> |  mod workspace       |
                           |  mods/<mod_id>        |
                           +-----------+-----------+
                                       |
                                       v
+--------------------+    +-----------+-----------+    +----------------------+
| provenance/version | -> | static validation     | -> | Gradle / ModDevGradle|
| acquisition locks  |    | policy/API checks     |    | dependency resolver  |
+---------+----------+    +-----------+-----------+    +----------+-----------+
          |                           |                           |
          v                           v                           v
 official upstream            validation report          ignored local caches
 providers/repos                      |                  + resolved dev runtime
          |                            |                           |
          +---- optional hydrate ---->+---------------------------+
                                                               |
                                                               v
                                                        GameTest / build / run
```

## Zones

### [`templates/`](../templates/)
Known-good Gridelyx-owned starting points. [`templates/neoforge-26.2`](../templates/neoforge-26.2/) is the canonical construction target and applies NeoForge ModDevGradle directly; it does not require a checked-in MDK archive or NeoForge installer.

### [`mods/`](../mods/)
Each directory is a standalone Gradle mod project. This sacrifices some deduplication in exchange for isolation: one broken experimental mod does not poison the build model of every other mod.

### [`references/`](../references/)
Human/AI-readable Gridelyx documentation, proposals and navigation metadata. Complete third-party snapshots are not tracked here. Optional reference checkouts live under `.reference-cache/`.

### [`vault/`](../vault/)
Despite the historical directory name, this is **metadata-only**. [`vault/manifest.json`](../vault/manifest.json) records official providers, version locks, acquisition mechanisms and the rule that upstream binary payloads are prohibited from repository storage.

### `.reference-cache/`
Ignored local acquisition/reference area. Optional upstream source checkouts and locally generated reference indexes may be materialised here without entering Git history.

### Gradle / runner caches
Minecraft, NeoForge, mappings, Maven libraries, JDK installations and Gradle distributions are resolver/toolchain state. GitHub Actions and local developer machines acquire these from the relevant upstream providers and cache them outside the tracked tree.

## Why JDK, Minecraft, NeoForge and LWJGL are not vendored

The JDK is a compiler/runtime toolchain. Minecraft and NeoForge are development/runtime inputs resolved by the supported NeoForge toolchain. LWJGL and other libraries are package-manager dependencies. Vendoring those payloads would duplicate upstream distribution, enlarge clones, complicate updates and can create licensing/redistribution risk.

Gridelyx therefore preserves **version/provenance reproducibility without binary redistribution**:

- JDK: dynamically installed by [`actions/setup-java`](https://github.com/actions/setup-java) in CI or supplied by the developer locally;
- Gradle: dynamically installed by [`gradle/actions/setup-gradle`](https://github.com/gradle/actions/tree/main/setup-gradle) in CI;
- Minecraft + NeoForge + mappings: resolved by [`net.neoforged.moddev`](https://docs.neoforged.net/toolchain/docs/plugins/mdg/);
- Java libraries: resolved from configured Maven repositories;
- optional MDK comparison source: pinned Git checkout under `.reference-cache/`.

[`tools/redistribution_guard.py`](../tools/redistribution_guard.py) independently checks the tracked Git index and rejects prohibited binary/archive payloads.

## Validation levels

1. Acquisition-manifest and no-redistribution invariants.
2. Repository invariants and version locks.
3. Static workspace checks.
4. Java compilation/resource processing with dynamically resolved dependencies.
5. Unit tests.
6. Dependency graph checks.
7. Optional NeoForge GameTest server.
8. Built-JAR structure inspection.
9. Manual client/server behavioural test where automation is insufficient.
