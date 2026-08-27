# Compact Gridelyx context

## Vocabulary

- **Gridelyx Studio**: launcher + instance/content manager + creator toolkit + production suite.
- **UAL**: Unified Abstraction Layer used to keep authoring/runtime operations loader-neutral where possible.
- **VFSB**: Gridelyx Studio Binary Bridge, the edition/process-neutral frame envelope.
- **instance**: isolated Minecraft runtime configuration with exact version/loader/Java/content lock.
- **provider**: authoritative/authorized metadata/download source adapter.
- **loader adapter**: version-aware procedure that materializes one loader into an instance launch plan.
- **content lock**: exact resolved artifacts, hashes, sources, dependency edges and overrides.
- **production project**: non-destructive replay/scene/timeline/capture project tied to an exact source instance.

## Architecture direction

`studio/core` is the product truth for instance/resolver/provider/provenance concepts. Desktop/CLI/AI frontends consume it. Existing Java advanced tooling remains in the Java runtime plane; Bedrock uses supported Add-On/Editor APIs plus the optional VFSB native companion. Production timeline data stays neutral and adapters expose target capabilities.

## Evidence discipline

Use R0-R6 readiness from `docs/ROADMAP.md`. Interface presence is not runtime support. Read `docs/FEATURE_MAP.md` before describing capability maturity.

## Source discipline

Use `studio/providers/providers.json` and `docs/ACQUISITION_AND_RESOLUTION.md`. Never invent URLs/loader metadata, scrape around API restrictions, commit credentials or treat local imports as redistributable by default.

## Navigation

- launcher/instances/providers: `studio/`, `docs/PROJECT_OVERVIEW.md`, `docs/ACQUISITION_AND_RESOLUTION.md`
- creator/runtime: `docs/POLYLOADER_ARCHITECTURE.md`, `docs/WORLD_EDIT_RUNTIME.md`, `templates/neoforge-26.2/src/advanced`
- Bedrock: `docs/BEDROCK_ARCHITECTURE.md`, `bedrock/`, `native/bedrock/`
- native bridge: `docs/GRIDELYX_BRIDGE_PROTOCOL.md`, `native/`
- production: `docs/MACHINIMA_PRODUCTION.md`
- project state: `docs/ROADMAP.md`, `docs/FEATURE_MAP.md`, `docs/TODO.md`, `AI_HANDOFF.md`
