# Licensing and provenance requirements

Every generated mod workspace must declare `mod_license` and contain a `LICENSE`, `LICENSE.txt`, or equivalent licence/proprietary notice before release.

Third-party source, binaries, textures, sounds, models, fonts, mappings and generated derivatives must record, where applicable: upstream project, version/commit, source URL, licence, modifications, integrity evidence and redistribution constraints. Public or private repository access does not remove third-party licence obligations.

## Public source-repository boundary

Gridelyx's source repository is **not an upstream binary mirror**. The supported repository model stores Gridelyx-owned source/assets plus dependency coordinates, version locks, official provider URLs, immutable upstream revisions/checksums where available and acquisition scripts.

Unless an artifact has been separately reviewed and intentionally approved for redistribution, do not commit or publish from this repository:

- Minecraft client/server JARs, assets, mappings copies, or decompiled/reconstructed Mojang source;
- NeoForge/Forge/Fabric/Quilt installer or runtime JARs copied from their distribution channels;
- JDK or Gradle distribution archives;
- LWJGL distribution/native bundles;
- Maven-resolved dependency JARs/classes;
- third-party DLL/SO/DYLIB/native payloads;
- archive chunks or reconstructed upstream archives;
- hydrated upstream MDK/source/reference trees.

`vault/manifest.json` records acquisition/provenance metadata only. `tools/redistribution_guard.py` enforces the tracked-file boundary using the actual Git index. `.gitignore` is defense in depth, not the sole control.

## Acquisition versus redistribution

A dependency may be legitimately acquired into a developer, CI runner or end-user cache without being redistributable from the Gridelyx source repository.

- CI installs the locked JDK and Gradle dynamically.
- NeoForge ModDevGradle resolves Minecraft, NeoForge, mappings and development dependencies into local/runner caches.
- Gradle/Maven resolves Java libraries from their configured repositories.
- Gridelyx Studio may acquire runtime artifacts for an authorized user's local instance through supported provider channels when permitted by provider terms.
- Optional MDK comparison material is cloned to ignored `.reference-cache/` from the pinned official source revision.

Those local/cache copies must not be copied back into tracked source or release artifacts merely because Gridelyx was able to download them.

## Derivatives and project releases

Do not copy Minecraft proprietary assets or decompiled code into a distributable mod or Gridelyx release. Patched/derived runtime work must preserve base-artifact provenance and patch metadata, and release packaging must respect the upstream redistribution terms for the base artifact. Prefer distributing Gridelyx-owned patches/recipes that reconstruct a derived runtime from a legitimately acquired local base when direct redistribution is not permitted.

Generated assets should be attributable to their generator/source pipeline where practical. AI-generated code still requires normal dependency, source and asset licence review.

Before a public release, generate an SBOM and dependency/licence/provenance report from the resolved dependency graph and review any component whose redistribution rights are unclear. This policy reduces accidental redistribution risk; it does not replace project-specific legal review or the upstream terms that apply to each dependency.
