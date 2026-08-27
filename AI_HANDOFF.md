# Gridelyx Studio AI handoff

## Product

Gridelyx Studio is the umbrella product for this repository: launcher + instance/content manager + cross-loader/cross-edition creator toolkit + machinima/production suite. It should be approachable in simple mode and fully inspectable in advanced mode.

## Current architecture

- Java advanced runtime: loader-neutral UAL, hotload, scripting, world/asset editing and advanced native/polyglot mechanisms under `templates/neoforge-26.2/src/advanced`.
- Bedrock: stable Add-On runtime, preview Editor extension and VFSB native companion under `bedrock/` + `native/bedrock/`.
- Native ABI/protocol: `gridelyx_*` C ABI and `VFSB` binary frames.
- Studio launcher core: `studio/core` defines GUI-independent instance, provider, provenance and dependency-resolution contracts.
- Provider policy: `studio/providers/providers.json` and `loader-adapters.json`; use official/authorized upstreams only.
- Product docs: `docs/PROJECT_OVERVIEW.md`, `ROADMAP.md`, `FEATURE_MAP.md`, `PROJECT_STRUCTURE.md`.
- Production: `docs/MACHINIMA_PRODUCTION.md`.
- AI consumption: `docs/AI_CONTEXT_SYSTEM.md`, `ai/context-map.json`, `tools/repo_index.py`, `tools/ai_context_pack.py`.

## Non-negotiable invariants

1. Gridelyx desktop must be able to start without Java installed; Java is resolved per Java Edition instance.
2. “Any loader” means an extensible loader-adapter contract. Never fabricate unknown loader metadata.
3. Downloads use official/authorized channels. Do not scrape/bypass CurseForge author distribution restrictions.
4. Every downloaded artifact gets local SHA-256 provenance; verify authoritative upstream hashes/signatures when available.
5. Instance writable state is isolated; immutable cache blobs may be deduplicated.
6. Simple UX and expert inspection use the same resolver/lockfile truth.
7. Production/replay/timeline formats are neutral; Java/Bedrock adapters declare actual capabilities.
8. Native/bytecode/preview paths remain version-gated and fail closed on drift.
9. AI summaries/indexes point to authoritative source; they never supersede it.
10. Do not claim runtime support beyond the repository readiness evidence level.

## Provider baseline

- Minecraft/version metadata: Mojang launcher metadata.
- Managed Java: Mojang runtime when available; compatible local Java; Adoptium Temurin fallback.
- Loaders: Fabric Meta, Quilt Meta, official Forge distribution/Maven, NeoForge Maven/installer.
- Content: Modrinth API; CurseForge third-party API only with approved key/current terms.
- Legacy/local: explicit user import with hash/provenance.

## Next implementation priorities

1. Make `studio/core` + repository-index tooling pass CI.
2. Implement native desktop shell/settings/storage paths.
3. Implement Mojang metadata + managed Java resolver.
4. Create vanilla instance and launch plan end-to-end.
5. Add Fabric/Quilt/Forge/NeoForge adapters.
6. Add Modrinth then authorized CurseForge provider implementations.
7. Implement content-addressed cache + per-instance lockfiles/snapshots.
8. Build simple and advanced UI over the same service layer.
9. Implement neutral production timeline/replay model and Java camera/capture MVP.
10. Extend the same production protocol to Bedrock capability adapters.

## Validation

Run existing platform gates plus:

```bash
cargo test --manifest-path studio/Cargo.toml
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
```

Then run applicable advanced/native/Bedrock CI and target-specific runtime tests.

## Read first for a task

Use `ai/context-map.json`; do not scan the full vault or generated/binary trees by default.
