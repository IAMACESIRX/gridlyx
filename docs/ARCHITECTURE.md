# Platform Architecture

## Purpose

The platform separates **knowledge/reference state** from **build/execution state**.

```text
                           +-----------------------+
request / AI intent -----> |  mod workspace       |
                           |  mods/<mod_id>        |
                           +-----------+-----------+
                                       |
                                       v
+----------------+        +-----------+-----------+        +----------------+
| reference index| -----> | static validation     | -----> | Gradle build   |
| + source locks |        | API/version checks    |        | NeoForge MDK   |
+--------+-------+        +-----------+-----------+        +-------+--------+
         |                            |                            |
         v                            v                            v
+--------+-------+             validation report            built mod JAR
| immutable vault|                    |                            |
| supplied bytes |                    +-------------+--------------+
+--------+-------+                                  |
         |                                          v
         +------ on-demand hydrate ----------> GameTest / JAR audit
```

## Zones

### `templates/`
Known-good starting points. `templates/neoforge-26.2` is derived from the supplied official-style MDK snapshot and is the source for new workspaces.

### `mods/`
Each directory is a standalone Gradle mod project. This sacrifices some deduplication in exchange for isolation: one broken experimental mod does not poison the build model of every other mod.

### `references/`
Human/AI-readable evidence and lookup material. The original MDK is preserved as an upstream snapshot. Archive indexes allow an agent to locate a reference before reconstructing hundreds of megabytes of binary material.

### `vault/`
Content-addressed supplied artifacts. Large files are split into deterministic parts smaller than GitHub's per-file hard limit. `tools/vault.py` verifies every part and reconstructed artifact using SHA-256.

### `.reference-cache/`
Ignored local reconstruction/extraction area. This is where an agent may materialise JDK/LWJGL/installer content for deep investigation without polluting Git history or a mod classpath.

## Why the JDK and LWJGL bundles are not normal dependencies

The JDK is a compiler/runtime toolchain. LWJGL is already selected transitively by the Minecraft/NeoForge runtime. Treating the supplied bundles as ordinary implementation dependencies can produce duplicate classes, mismatched natives and launch failures. The vault therefore provides **reference availability without dependency injection**.

## Validation levels

1. Repository invariants and version locks.
2. Static workspace checks.
3. Java compilation/resource processing.
4. Unit tests.
5. Dependency graph checks.
6. Optional NeoForge GameTest server.
7. Built-JAR structure inspection.
8. Manual client/server behavioural test where automation is insufficient.
