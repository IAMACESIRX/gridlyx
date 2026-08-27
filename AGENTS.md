# AI Engineering Contract

This repository is a controlled Minecraft mod R&D environment. AI agents may design, generate, refactor, compile and validate mods, but must preserve reproducibility and reference integrity.

## Canonical target

- Minecraft: `26.2`
- NeoForge: `26.2.0.67`
- ModDevGradle: `2.0.144`
- Gradle: `9.2.1`
- Java language/toolchain: `25`
- Exact CI JDK preference: Eclipse Temurin `25.0.4+7`
- LWJGL bundle available for reference: `3.4.1`

`platform/versions.json` is the machine-readable lock.

## Required workflow

1. Read the request and identify target side(s): common/client/server/data generation/GameTest.
2. Inspect the canonical template and `references/index/` before inventing API calls.
3. If a required API is uncertain, resolve it from hydrated references, Gradle dependency sources, NeoForge documentation, or a minimal compile probe. Do not guess signatures.
4. Create or modify a workspace under `mods/<mod_id>/`.
5. Run `python tools/validate_platform.py --mod <mod_id>`.
6. Run the workspace Gradle `build` task.
7. Add unit tests for pure logic. Add NeoForge GameTests for behaviour that requires Minecraft state when practical.
8. Run GameTest only when the workspace declares tests; an empty GameTest server may fail by design.
9. Inspect the built JAR and verify metadata/resources.
10. Report assumptions, unresolved API uncertainty, validation actually performed, and output artifact path.

## Reference boundary

Treat `vault/` and `references/upstream/` as immutable evidence.

Never:

- silently edit or replace supplied reference artifacts;
- add the bundled JDK or entire LWJGL distribution to a mod runtime classpath;
- add a second Minecraft/NeoForge runtime to `implementation` just because it exists in the vault;
- fabricate Minecraft/NeoForge methods, registries, event names, mappings or signatures;
- commit generated Gradle caches, run directories, decompiled Minecraft source, authentication tokens or secrets.

The LWJGL bundle is primarily an API/native-reference corpus. Minecraft/NeoForge already controls the runtime LWJGL graph. A direct LWJGL dependency is permitted only for an explicitly justified experiment and must pass dependency-conflict validation.

## Security / trust boundary

Generated mods and third-party JARs are untrusted code. CI workflows use read-only repository permissions by default and must not expose secrets to compilation, tests or GameTests. Do not execute arbitrary downloaded installers during ordinary validation.

## Architecture preference

Prefer small explicit modules, deterministic data generation, testable pure logic, side-safe code, version-pinned dependencies and replaceable integrations. Keep experiments outside canonical mod code until validated.
