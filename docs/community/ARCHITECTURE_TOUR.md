# Architecture tour

## Product planes

The repository is intentionally split so one subsystem does not become an unmaintainable universal layer.

- `studio/` — launcher, instance, provider, dependency and desktop-control contracts.
- `templates/` — canonical Java mod/runtime scaffolding.
- `mods/` — independent distributable mod workspaces.
- `bedrock/` — Bedrock Add-On and Editor targets.
- `native/` — native ABI, high-performance helpers and Bedrock companion.
- `bridges/` — external language/AI/sidecar protocol examples.
- `ai/` — compact AI continuity, work state, decisions, assumptions and routing.
- `platform/` — locked versions, capabilities and machine-readable project contracts.
- `references/` and `vault/` — compact reference knowledge and exact recoverable R&D inputs.

## Runtime layers

For Java Edition the project can operate at progressively deeper levels:

1. supported Minecraft/loader APIs;
2. loader transformations/Mixins;
3. JVM agents/Instrumentation/classloaders;
4. native FFM/JNI components;
5. external sidecars/shared memory;
6. custom launch/bootstrap;
7. version-pinned executable/library patches;
8. project-owned engine subsystem augmentation or runtime components.

See [`docs/DEEP_INTEGRATION_ARCHITECTURE.md`](../DEEP_INTEGRATION_ARCHITECTURE.md) for the governing rules.

## Creator runtime

The creator system combines live world editing, structures/events, procedural data, asset/model/texture editing, scene hierarchy, physics/construction, in-game IDE/AI, hotload and multiplayer synchronization. Server-authoritative world mutation is kept separate from off-thread computation.

## Cross-edition model

Java and Bedrock consume neutral project contracts where useful, but each target adapter owns final engine integration. Parity is tracked by evidence, not assumed from interface similarity.

## Planning and truth

- [`docs/PROJECT_PLAN.md`](../PROJECT_PLAN.md) — program control.
- [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](../CHAT_REQUIREMENTS_TRACEABILITY.md) — retained conversation scope.
- [`docs/FEATURE_MAP.md`](../FEATURE_MAP.md) — evidence/readiness snapshot.
- [`docs/TODO.md`](../TODO.md) — active implementation ledger.
- [`AI_HANDOFF.md`](../../AI_HANDOFF.md) — current continuation state.

When these disagree, follow the source-of-truth ordering in [`docs/PROJECT_PLAN.md`](../PROJECT_PLAN.md) and inspect implementation/test evidence.
