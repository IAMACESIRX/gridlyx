# Gridelyx AI handoff

## Current identity state

The canonical root brand is **Gridelyx** and the integrated suite is **Gridelyx Studio**. `platform/brand.json` is authoritative. Gridelyx/VFSB technical identifiers that still exist are migration compatibility state only; consult `docs/REBRAND_PLAN.md` before renaming source, ABI, wire or persisted identifiers.

## Mission

Build a cross-edition Minecraft launcher, instance/content manager, cross-loader development environment, live creator/sandbox runtime, world editor and machinima/production suite with strong fault containment, provenance, hotload and AI-assisted engineering.

The project is **not constrained to conventional modding extension points**. Requirements may escalate into JVM/runtime instrumentation, native components, external services, launch/bootstrap changes, deterministic executable/library patching or project-owned runtime components when shallower layers cannot provide the capability. Deeper changes remain additive, fingerprint-gated, attributable and reversible.

## Canonical retained scope

Read `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` and `platform/chat-requirements.json` before broad design work. They preserve CR-001 through CR-033, including:

- reproducible Java/NeoForge R&D and multi-JAR workspaces;
- quality/CI/Codespaces/Copilot/CodeQL/build locks;
- registries/datagen/assets/localization;
- ASM/Instrumentation/Mixins/MethodHandles/live compilation;
- worker pools/state synchronization;
- Panama/native/GPU/shared-memory/bridges;
- MCP/vector indexing/AI autodoc;
- autonomous tests/profiling/chaos;
- project planning/issues/handoff/community docs;
- Polyloader/UAL and broad version/loader adaptation;
- restart-minimized external hotload;
- live world editing/async section blitting/NBT events;
- Terraria liquid/paint/transmutation systems;
- microgeometry/curves/slopes/collision;
- Hytale-like live asset editing;
- Garry's Mod-like physics/construction;
- Roblox-Studio-like scene/properties/gizmos/live execution;
- in-game IDE/AI control;
- non-Java modification SDK;
- multiplayer Netty/culling/consensus;
- anti-crash/fault containment;
- Bedrock parity/native bridge;
- launcher/acquisition/dependency resolution;
- mod forking/decompilation;
- replay/animation/machinima;
- dynamic dimensions/teleport/worldgen;
- rendering/volumetrics/PoseStack/IK;
- additive deep integration/patch manager;
- Gridelyx rebrand migration;
- complete dependency/toolchain inventory.

Never silently drop one of these because it is difficult or outside normal mod APIs.

## Current architecture

- **Java advanced runtime:** `templates/neoforge-26.2/src/advanced` — UAL/Polyloader, hotload, bytecode, scripting, MCP/indexing, world editing, assets, scene/physics tooling, rendering, native/IPC and production foundations.
- **Deep integration:** `docs/DEEP_INTEGRATION_ARCHITECTURE.md` — L0-L8 escalation from supported APIs through additive patch/runtime ownership.
- **Bedrock:** `bedrock/` + `native/bedrock/` + `platform/bedrock-capabilities.json`.
- **Native/IPC:** `native/`, `bridges/`, FFM/Panama/shared-memory framing.
- **Studio/launcher core:** `studio/core` plus provider/loader manifests and schemas.
- **Project control:** `docs/PROJECT_PLAN.md`, `ROADMAP.md`, `FEATURE_MAP.md`, `TODO.md`, requirements ledger.
- **AI continuity:** `ai/AI_ORGANISATION.md`, drift controls, work/decision/assumption ledgers, context map.
- **Dependencies/toolchain:** `docs/DEPENDENCIES_AND_TOOLCHAIN.md`, `platform/toolchain-requirements.json`.
- **Community:** `COMMUNITY.md`, `CONTRIBUTING.md`, `SUPPORT.md`, `CODE_OF_CONDUCT.md`, `docs/community/`.

## Source-of-truth order

When information conflicts:

1. explicit current human direction/corrections;
2. implementation, schemas and reproducible runtime evidence;
3. locked platform/version/brand/provider/toolchain manifests;
4. accepted architecture/decision records;
5. requirements ledger, project plan, roadmap, feature map and TODO;
6. handoff/context/index material;
7. generated summaries and speculative notes.

Never resolve contradictions by averaging incompatible summaries.

## Non-negotiable invariants

1. Gridelyx is the canonical root brand; compatibility identifiers do not restore retired branding.
2. The desktop application must start without Java installed; Java is resolved per Java Edition instance.
3. “Any loader/version” means extensible versioned adapters and evidence, not fabricated universal compatibility.
4. Downloads use official/authorized channels and retain hashes/provenance.
5. Writable instance state is isolated; immutable cached artifacts may be deliberately deduplicated.
6. Simple and expert UX use the same resolver/lockfile truth.
7. Java and Bedrock share neutral contracts where useful but target adapters must expose real parity gaps.
8. Native/bytecode/preview/instrumentation/patch paths are version/fingerprint gated and fail closed on drift.
9. Multiplayer live world mutation remains server-authoritative.
10. External tools do not gain authority merely because they connect through IPC/MCP/web/native/polyglot channels.
11. A feature claim cannot exceed its R0-R6 evidence.
12. AI summaries/indexes never supersede authoritative source.
13. Destructive migrations require an explicit recovery route.
14. A requirement cannot be rejected merely because public APIs cannot express it; use the shallowest reliable layer and escalate when justified.
15. Deep integration must not bypass authentication, entitlement, DRM, anti-cheat or platform security controls.
16. Tool installation is not runtime validation; dependency state and feature state remain separate.
17. Do not claim the exact remote reference-vault binary payload is complete while `vault/REMOTE_BINARY_IMPORT_PENDING.md` exists.

## Active engineering priorities

1. Preserve the Gridelyx identity/requirements/toolchain continuity gates.
2. Complete safe Gridelyx→Gridelyx public/source/protocol/ABI migration in staged phases.
3. Implement the desktop launcher/runtime acquisition path end-to-end.
4. Extend loader/content resolution through the provider/adapter model.
5. Integrate live creator runtime: world editing, assets/models, microgeometry, physics, scripting, AI/IDE and non-Java extensions.
6. Implement/validate dynamic liquids, paint layers and progression transmutation.
7. Validate Java/Bedrock parity explicitly.
8. Build the managed deep-integration/patch path with immutable base/provenance/rollback.
9. Harden hotload, rollback and process-isolated fault containment.
10. Promote replay/timeline/camera foundations into the complete production suite.
11. Pin presently-unpinned Python/Rust/CMake/compiler/Go/.NET release toolchain policies.
12. Complete exact remote reference-vault import if the repository is intended to contain those large supplied bytes.

## Session start protocol

For non-trivial work:

1. read `AGENTS.md`;
2. read this handoff;
3. read `docs/CHAT_REQUIREMENTS_TRACEABILITY.md`;
4. read `ai/AI_ORGANISATION.md` and `ai/DRIFT_MITIGATION.md`;
5. inspect `platform/brand.json`, `platform/chat-requirements.json` and `platform/toolchain-requirements.json`;
6. inspect work-state, decision and assumption ledgers;
7. use `ai/context-map.json` for task-specific canonical source;
8. inspect versions/providers/upstream references before guessing external APIs;
9. check GitHub issues/TODO for overlapping tracked work.

## Session end protocol

A meaningful session leaves:

- exact changes made;
- committed versus uncommitted/staged state;
- tests/commands actually run;
- failures and validation not performed;
- readiness changes supported by evidence;
- new/closed assumptions;
- architecture/brand/protocol decisions and rollback routes;
- dependency/toolchain changes;
- exact next work or blocker.

Update work-state when a task crosses sessions/agents. Update decision/assumption ledgers when project truth changes. Update both requirements ledgers when scope/evidence paths move.

## Capability-state vocabulary

Use both R0-R6 and a plain state where useful:

- planned;
- specified;
- scaffolded;
- implemented;
- tested;
- target-validated;
- blocked;
- regressed;
- superseded.

Do not use “supported” without target/version/loader/evidence scope when that distinction matters.

## Validation baseline

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/toolchain_requirements_check.py
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
cargo test --manifest-path studio/Cargo.toml --all-targets
```

Then run applicable Java advanced/native/Bedrock/GameTest/client tests. L5-L8 changes additionally require exact target fingerprints, derived artifact/overlay verification and rollback checks.

## Compact handoff template

```text
OBJECTIVE:
BRANCH / COMMIT:
SCOPE / CR IDS:
AUTHORITATIVE FILES:
COMPLETED:
VERIFIED:
NOT VERIFIED:
DECISIONS:
OPEN ASSUMPTIONS:
DEPENDENCIES / TOOLCHAIN CHANGES:
BLOCKERS:
RECOVERY POINT:
NEXT ACTIONS:
```

A handoff is incomplete if another agent cannot distinguish what exists from what remains intended.
