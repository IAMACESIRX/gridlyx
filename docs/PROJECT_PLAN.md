# Project control plan

This document is the durable program-control layer for the Minecraft Advanced Mod Development Platform repository. The public product name is currently **under rebrand**; architecture and requirements must not depend on a temporary brand.

## Mission

Build one coherent cross-edition Minecraft platform that combines:

- launcher and isolated instance management;
- legitimate Minecraft/runtime/loader/content acquisition;
- cross-loader and cross-version mod development;
- live creator tooling and in-game development;
- Java, Bedrock, native, scripting and external-tool extension planes;
- additive deep integration beyond conventional modding surfaces when required;
- transactional live world editing and multiplayer-safe authoring;
- advanced geometry, physics, assets and sandbox construction;
- replay, animation, camera, capture and virtual-production tooling;
- AI-assisted development with auditable, drift-resistant project continuity;
- community onboarding and an evidence-first contribution model.

Consumer simplicity and expert transparency must use the same resolver, lockfiles, capability model and evidence state.

## Retained scope contract

`docs/CHAT_REQUIREMENTS_TRACEABILITY.md` is the human-readable retained-scope ledger for the development conversation. `platform/chat-requirements.json` is its machine-readable companion.

Every retained capability must remain represented by either:

1. implementation/evidence; or
2. explicit development planning.

A requirement does not disappear because it is difficult, version-fragile or unsupported by a conventional Minecraft/mod-loader API. Such findings may change its integration level, schedule, readiness, target coverage or validation burden. Material removal/weakening requires an explicit human-approved superseding decision recorded in `ai/decision-ledger.json`.

`tools/chat_requirements_check.py` and Studio/project-continuity CI enforce structural traceability of this contract.

## Integration boundary and escalation

The platform is **not limited to what a normal mod, loader API or exposed game SDK can do**. Product requirements are evaluated against the full controllable execution stack: launcher/bootstrap, game process, JVM/runtime, bytecode, loader, native libraries, renderer, protocol/storage adapters, helper processes and project-owned components.

Use the shallowest reliable integration layer that satisfies the capability, but escalate when necessary. Permitted project-owned approaches include:

- supported game/loader APIs and data/resource systems;
- Mixins, access widening/transformers and bytecode transforms;
- Java agents, Instrumentation and replaceable classloader/service layers;
- Panama/FFM or JNI/native bridges where justified;
- external sidecars, workers, compilers, simulators and shared-memory/IPC services;
- custom launcher/bootstrap/process-supervision layers;
- deterministic version-pinned executable or shared-library patch sets;
- engine-subsystem augmentation/replacement behind project-owned contracts;
- maintained project-owned runtime components or forks when repeated patching is less reliable than owning the extension surface.

These mechanisms are governed by `docs/DEEP_INTEGRATION_ARCHITECTURE.md`.

Deep integration is additive: retain or recover a verified upstream/base artifact, express project changes as attributable patch/overlay/component graphs, fingerprint exact targets, verify derived runtime state and maintain rollback. A limitation in a public API changes the integration level and validation burden; it does **not** automatically delete or weaken the requirement.

Deep integration must not be used to bypass authentication, entitlement, DRM, anti-cheat or platform security controls.

## Authority and source-of-truth order

When sources conflict, use this order unless a newer explicit decision says otherwise:

1. user-approved project direction and explicit corrections;
2. executable code, schemas and reproducible runtime evidence;
3. locked platform/version/provider/requirements manifests;
4. accepted architecture and decision records;
5. retained requirements ledger, project plan, roadmap, feature map and TODO state;
6. AI handoff/context/index material;
7. generated summaries, experiments and speculative notes.

Generated AI material never upgrades itself into architectural truth.

## Readiness levels

| Level | Meaning |
|---|---|
| R0 | Idea only |
| R1 | Interface, contract or schema defined |
| R2 | Compiles or deterministic static tooling passes |
| R3 | Automated unit/integration test passes |
| R4 | Headless target integration validation passes |
| R5 | Interactive target validation passes |
| R6 | Release candidate with packaging, migration and rollback evidence |

Readiness is evidence-bound. A feature can be demoted when upstream APIs, mappings, loader behavior or runtime assumptions change.

## Program workstreams

1. **Desktop/product shell** — launcher shell, settings, credentials, updates, downloads, accounts and process lifecycle without requiring Java merely to start the desktop app.
2. **Runtime acquisition** — Mojang metadata/libraries/assets, managed Java, caches, hashes, classifiers and provenance.
3. **Loader adaptation / Polyloader** — vanilla, Fabric, Quilt, Forge, NeoForge, legacy/future adapters, prelaunch bootstrap, UAL translation and capability-gated sideloading.
4. **Content/resolution** — Modrinth, authorized CurseForge, local import, dependency solving, content locks, pack import/export and transactional instance updates.
5. **Creator runtime** — UAL, in-game IDE, scripts, AI interface, assets, models, microgeometry, scene graph, physics, construction tools and hotload.
6. **Deep integration and patching** — runtime/JVM/native/bootstrap escalation, deterministic patch manifests, binary/library compatibility layers, derived-runtime verification, fingerprint gating and rollback.
7. **World systems** — live world editor, structures, ores/events, rollback/WAL, dynamic liquids, paint layers, microgeometry placement and progression-gated transmutation.
8. **Cross-edition adaptation** — Java and Bedrock capability adapters with explicit parity gaps rather than fabricated equivalence.
9. **Polyglot/native/external extensions** — JVM, Graal languages, Python/Go/C# sidecars, Rust/C++, shared memory, IPC, MCP and capability-gated external tools.
10. **Production** — replay, animation, cameras, timeline, shots/takes, real-time/offline capture, audio and export.
11. **AI/project intelligence** — context routing, agent roles, work-state handoff, decision/assumption tracking, deterministic indexing, requirements traceability and drift detection.
12. **Validation/security/operations** — CI, tests, provenance, archive safety, bounded execution, profiling, chaos/fault containment, signing and recovery.
13. **Community and contribution** — onboarding, architecture education, support routing, conduct expectations, evidence literacy and future ownership/reviewer structures.
14. **Rebrand/migration** — replacement identity selection, current-tree terminology migration, compatibility aliases where required and terminology CI.

## Work item lifecycle

Every non-trivial work item should move through explicit states:

`PROPOSED -> FRAMED -> DESIGNED -> IMPLEMENTING -> VERIFYING -> ACCEPTED`

Exceptional terminal/holding states:

- `BLOCKED` — external dependency or unresolved prerequisite;
- `DEFERRED` — deliberately postponed;
- `REJECTED` — evaluated and intentionally not pursued;
- `SUPERSEDED` — replaced by a newer decision;
- `REGRESSED` — previously working evidence no longer holds.

A handoff must not call an item complete merely because source files exist.

## Decision protocol

For an architectural or compatibility decision, record:

- problem and desired outcome;
- affected subsystem and versions/editions/loaders;
- known constraints and unknowns;
- candidate approaches;
- chosen approach and why;
- evidence supporting the choice;
- falsification/review trigger;
- rollback or migration route;
- files/issues/tests affected.

Use `ai/decision-ledger.json` for compact durable records and `docs/DECISIONS.md` for human-readable architectural decisions.

Any L5-L8 deep-integration decision must additionally record why shallower mechanisms are insufficient, exact target fingerprints/versions, blast radius, derived-runtime/overlay model, upstream-change maintenance burden and recovery behavior.

Any decision that removes or materially weakens a retained `CR-*` requirement must identify that requirement explicitly and must be human-approved.

## Assumption protocol

Unverified assumptions belong in `ai/assumption-ledger.json`. Each assumption must identify:

- scope;
- confidence;
- evidence or reason;
- validation route;
- expiry/review trigger;
- what breaks if it is false.

An assumption must never silently become a fact because multiple AI sessions repeat it.

## Session and handoff protocol

For substantial AI-assisted work:

1. read `AGENTS.md` and `AI_HANDOFF.md`;
2. for broad/scope-impacting work, read `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` and `platform/chat-requirements.json`;
3. read `ai/AI_ORGANISATION.md`, `ai/DRIFT_MITIGATION.md` and the relevant `ai/context-map.json` domain;
4. inspect authoritative implementation/evidence before making maturity claims;
5. record the active objective and boundaries in `ai/work-state.json` when work spans sessions or agents;
6. make the smallest coherent change set;
7. run the relevant verification lane, including requirements traceability when scope/evidence paths change;
8. update decision/assumption/readiness/TODO state if the result changes project truth;
9. leave a handoff that distinguishes completed, verified, unverified, blocked and next work.

## Drift review cadence

Run a drift review when any of these occurs:

- product or protocol rename;
- loader/Minecraft/Bedrock/API version update;
- executable/library fingerprint or patch target update;
- major architecture change;
- new runtime or language bridge;
- new or materially changed retained requirement;
- readiness promotion/demotion;
- milestone completion;
- long-running branch or AI handoff;
- conflicting documentation discovered;
- user correction supersedes prior terminology or design.

The detailed controls are in `ai/DRIFT_MITIGATION.md`.

## Recovery and reversibility

Changes should preserve a known-good recovery path whenever feasible:

- instance updates use locks/snapshots;
- world edits use transactions/WAL/rollback boundaries;
- hotload keeps last-known-good artifacts;
- target adapters fail closed when fingerprints/mappings drift;
- deep binary/runtime changes preserve an immutable/recoverable base and a recorded patch/overlay graph;
- patched derivatives are invalidated when their upstream fingerprints change;
- destructive migrations require backups and versioned schemas;
- AI/project-control changes retain enough decision provenance to reconstruct why a choice was made.

## Milestones

### M0 — Repository and control-plane convergence
Canonical architecture, complete retained-scope ledger, AI operating model, drift controls, evidence/readiness model, provider policy, community onboarding and synchronized planning surfaces.

### M1 — Vanilla launcher
Desktop shell, supported authentication, Mojang metadata, managed Java, cache, instance lock and reliable vanilla launch.

### M2 — Modded launcher and Polyloader foundations
Fabric/Quilt/Forge/NeoForge adapters, Modrinth and authorized CurseForge providers, dependency solver, simple/advanced instance UX, prelaunch/bootstrap foundations and rollback.

### M3 — Instance/modpack ecosystem
Pack import/export, Prism/MultiMC migration, server profiles, snapshots, portable bundles, mod-fork/analysis workflows and diagnostics.

### M4 — Creator integration
Desktop workspace plus in-game creator runtime, world editing/events, liquids/paint/transmutation, microgeometry, assets/models, scene/physics construction, AI/IDE, non-Java extension gateway, safe hotload and first managed deep-integration paths.

### M5 — Cross-edition creator validation
Java/Bedrock capability parity manifest, target-specific asset/world/IDE/extension/render/physics adapters and documented escalation routes where native target constraints require deeper integration.

### M6 — Machinima MVP
Replay/event log, rational-time timeline, camera rigs, actor tracks, in-game animation editing, shot/take editor and reproducible capture/reopen/render.

### M7 — Professional production
Offline render, advanced passes, audio stems, multi-camera/take tooling, queues, collaboration and interchange research.

### M8 — Hardening/rebrand/release
Cross-platform packaging, security/provenance reports, fuzz/chaos/performance evidence, signed updates, migrations, patch compatibility/rollback evidence, replacement-brand terminology enforcement and stable/beta/nightly channels.

## Release gates

A milestone advances only when its claimed readiness is backed by matching evidence. Compile success alone is insufficient for interactive/runtime claims. Provider/API/game-version/executable/library drift can reopen completed milestones for revalidation.

A release/rebrand milestone also requires `tools/chat_requirements_check.py` to pass so retained project scope remains accounted for.

## Planning surfaces

- `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` — complete retained conversation scope;
- `platform/chat-requirements.json` — machine-readable requirement/evidence/planning paths;
- `tools/chat_requirements_check.py` — traceability CI gate;
- `docs/PROJECT_OVERVIEW.md` — product architecture;
- `docs/PROJECT_STRUCTURE.md` — ownership boundaries;
- `docs/DEEP_INTEGRATION_ARCHITECTURE.md` — additive runtime/JVM/native/bootstrap/binary integration model;
- `docs/ROADMAP.md` — staged delivery details;
- `docs/FEATURE_MAP.md` — capability/readiness matrix;
- `docs/TODO.md` — live implementation ledger;
- `COMMUNITY.md` and `docs/community/` — community/contributor onboarding;
- `AI_HANDOFF.md` — compact continuation state;
- `ai/AI_ORGANISATION.md` — AI roles and authority;
- `ai/DRIFT_MITIGATION.md` — continuity/drift controls;
- `ai/work-state.json` — machine-readable active work state;
- `ai/decision-ledger.json` — compact decision trace;
- `ai/assumption-ledger.json` — unresolved assumptions;
- `ai/context-map.json` — task/domain navigation.
