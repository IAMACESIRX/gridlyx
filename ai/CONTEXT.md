# Compact project context

## Identity state

This repository is the Minecraft Advanced Mod Development Platform project. Its replacement public product brand is not yet selected. Do not introduce new project-owned terminology derived from the retiring brand while the rebrand is unresolved.

## Vocabulary

- **UAL**: Unified Abstraction Layer used to keep authoring/runtime operations loader-neutral where possible.
- **instance**: isolated Minecraft runtime configuration with exact version/loader/Java/content lock.
- **provider**: authoritative/authorized metadata/download source adapter.
- **loader adapter**: version-aware procedure that materializes one loader into an instance launch plan.
- **content lock**: exact resolved artifacts, hashes, sources, dependency edges and overrides.
- **production project**: non-destructive replay/scene/timeline/capture project tied to an exact source instance.
- **work state**: machine-readable current cross-session objective and verification state in `ai/work-state.json`.
- **decision ledger**: compact trace of accepted project-control/architecture choices.
- **assumption ledger**: unresolved statements that must not silently become facts.
- **drift**: unintended change in requirements, architecture, terminology, evidence, versions, scope, assumptions or handoff state.

The existing bridge/protocol identifiers still contain legacy-brand terminology and are intentionally left unchanged until the replacement brand and migration prefix are chosen.

## Architecture direction

`studio/core` is the product truth for instance/resolver/provider/provenance concepts. Desktop/CLI/AI frontends consume it. Java advanced tooling remains in the Java runtime plane; Bedrock uses supported Add-On/Editor APIs plus the optional native companion. Production timeline data stays neutral and adapters expose real target capabilities.

## AI operating model

Read in this order for substantial work:

1. [`AGENTS.md`](../AGENTS.md)
2. [`AI_HANDOFF.md`](../AI_HANDOFF.md)
3. [`ai/AI_ORGANISATION.md`](AI_ORGANISATION.md)
4. [`ai/DRIFT_MITIGATION.md`](DRIFT_MITIGATION.md)
5. `ai/work-state.json`
6. `ai/decision-ledger.json`
7. `ai/assumption-ledger.json`
8. task-specific paths from `ai/context-map.json`

## Evidence discipline

Use R0-R6 readiness from the project planning docs. Interface presence is not runtime support. Read [`docs/FEATURE_MAP.md`](../docs/FEATURE_MAP.md) before describing capability maturity. Runtime/compatibility claims must name the evidence and target scope when material.

## Source discipline

Use `studio/providers/providers.json` and [`docs/ACQUISITION_AND_RESOLUTION.md`](../docs/ACQUISITION_AND_RESOLUTION.md). Never invent URLs/loader metadata, scrape around API restrictions, commit credentials or treat local imports as redistributable by default.

## Navigation

- project control: [`docs/PROJECT_PLAN.md`](../docs/PROJECT_PLAN.md), [`AI_HANDOFF.md`](../AI_HANDOFF.md), [`ai/AI_ORGANISATION.md`](AI_ORGANISATION.md), [`ai/DRIFT_MITIGATION.md`](DRIFT_MITIGATION.md)
- launcher/instances/providers: `studio/`, [`docs/PROJECT_OVERVIEW.md`](../docs/PROJECT_OVERVIEW.md), [`docs/ACQUISITION_AND_RESOLUTION.md`](../docs/ACQUISITION_AND_RESOLUTION.md)
- creator/runtime: [`docs/POLYLOADER_ARCHITECTURE.md`](../docs/POLYLOADER_ARCHITECTURE.md), [`docs/WORLD_EDIT_RUNTIME.md`](../docs/WORLD_EDIT_RUNTIME.md), `templates/neoforge-26.2/src/advanced`
- Bedrock: [`docs/BEDROCK_ARCHITECTURE.md`](../docs/BEDROCK_ARCHITECTURE.md), `bedrock/`, `native/bedrock/`
- native bridge: current bridge protocol docs under `docs/` plus `native/`
- production: [`docs/MACHINIMA_PRODUCTION.md`](../docs/MACHINIMA_PRODUCTION.md)
- project state: [`docs/ROADMAP.md`](../docs/ROADMAP.md), [`docs/FEATURE_MAP.md`](../docs/FEATURE_MAP.md), [`docs/TODO.md`](../docs/TODO.md), [`AI_HANDOFF.md`](../AI_HANDOFF.md), `ai/work-state.json`
