# Gridelyx project control plan

This document is the durable program-control layer for **Gridelyx / Gridelyx Studio**. Architecture, requirements, evidence, dependencies, feature decisions and migration state must remain reconstructable without relying on chat history.

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
- community onboarding and an evidence-first contribution model;
- explicit dependency/toolchain governance so no required program remains tribal knowledge;
- explicit feature-analysis/decision governance so complex work is decomposed, risk-assessed and sequenced without losing retained scope.

Consumer simplicity and expert transparency must use the same resolver, lockfiles, capability model and evidence state.

## Retained scope contract

`docs/CHAT_REQUIREMENTS_TRACEABILITY.md` is the human-readable retained-scope ledger. `platform/chat-requirements.json` is its machine-readable companion.

Every retained capability must remain represented by either:

1. implementation/evidence; or
2. explicit development planning.

A requirement does not disappear because it is difficult, expensive, version-fragile or unsupported by a conventional Minecraft/mod-loader API. Such findings may change integration level, schedule, readiness, target coverage or validation burden. Material removal/weakening requires an explicit human-approved superseding decision in `ai/decision-ledger.json`.

`tools/chat_requirements_check.py` and Gridelyx continuity CI structurally enforce this contract.

## Dependency and toolchain contract

`docs/DEPENDENCIES_AND_TOOLCHAIN.md`, `docs/CAPABILITY_DEPENDENCY_MATRIX.md` and `platform/toolchain-requirements.json` define what must be installed/acquired for each development or validation lane.

Rules:

- known versions are pinned rather than guessed;
- unpinned tools are explicitly marked and must receive a supported-version policy before release claims;
- optional language/native/production tools are capability-specific rather than universal requirements;
- external services and credentials are configuration, never committed dependencies;
- reference JDK/LWJGL/MDK artifacts remain separate from runtime classpaths;
- every new compiler, runtime, executable, provider or library updates the toolchain inventory and provenance/licensing surfaces;
- installing a dependency does not promote the corresponding feature's R0-R6 evidence.

`tools/toolchain_requirements_check.py` enforces structural consistency.

## Feature analysis and decision contract

CR-034 is governed by `docs/FEATURE_DECISION_FRAMEWORK.md`, `docs/PROJECT_VALUES.md`, `docs/DEVELOPMENT_MAP.md`, `docs/BENCHMARKING_MATRIX.md`, `docs/templates/FEATURE_EVALUATION_TEMPLATE.md` and `platform/feature-analysis.schema.json`.

For substantial features/architecture, analysis is proportionate to risk and reversibility and includes:

- W5x5x5 repeated positive/inverse Who/What/When/Where/How/Why interrogation;
- task decomposition and project-values alignment;
- time/money/energy/support/compatibility cost diagnostics;
- 10m/10h/10d/10mo/1y/5y/10y horizons and opportunity cost;
- regret minimisation and reversible-vs-difficult-to-reverse classification;
- risk register, inversion, pre-mortem and second-order effects;
- Eisenhower, Venn/overlap and structured brainstorming;
- first-principles modelling and current benchmark verification;
- Feynman explanation, MVP and 30/60-minute research timeboxes;
- asymmetric-risk assessment and working backward;
- Pareto/80-20, Critical Path Method, Cynefin and Kanban;
- evidence, rollback/migration and decision triggers.

Cost/priority diagnosis informs sequencing. It does not silently erase retained scope. `tools/feature_planning_check.py` enforces the structural planning contract.

## Gridelyx identity and migration

`platform/brand.json` is the canonical product identity source. Root brand: **Gridelyx**. Integrated suite: **Gridelyx Studio**. `platform/repository-metadata.json` separately records the explicitly requested GitHub repository slug `gridlyx` and desired combined description.

Gridelyx/VFSB source, protocol, ABI, persisted and path identifiers still present in the tree are classified migration compatibility debt, not current branding. `docs/REBRAND_PLAN.md` and `platform/terminology.json` govern the staged migration. Blind replacement across wire/ABI/persisted boundaries is prohibited.

## Integration boundary and escalation

Gridelyx is **not limited to what a normal mod, loader API or exposed game SDK can do**. Requirements are evaluated against the controllable execution stack: launcher/bootstrap, game process, JVM/runtime, bytecode, loaders, native libraries, renderer, protocol/storage adapters, helper processes and project-owned components.

Use the shallowest reliable layer that satisfies a capability, but escalate when necessary. Permitted project-owned approaches include:

- supported game/loader APIs and data/resource systems;
- Mixins, access widening/transformers and bytecode transforms;
- Java agents, Instrumentation and replaceable classloader/service layers;
- Panama/FFM or JNI/native bridges where justified;
- external sidecars, workers, compilers, simulators and shared-memory/IPC services;
- custom launcher/bootstrap/process-supervision layers;
- deterministic version-pinned executable or shared-library patch sets;
- engine-subsystem augmentation/replacement behind project-owned contracts;
- maintained project-owned runtime components/forks when repeated patching is less reliable than owning the extension surface.

`docs/DEEP_INTEGRATION_ARCHITECTURE.md` governs L0-L8 escalation.

Deep integration is additive: retain or recover a verified upstream/base artifact, express changes as attributable patch/overlay/component graphs, fingerprint exact targets, verify derived runtime state and maintain rollback. Lack of a public API changes the integration level and validation burden; it does **not** automatically delete or weaken the requirement.

Deep integration must not bypass authentication, entitlement, DRM, anti-cheat or platform security controls.

## Authority and source-of-truth order

When sources conflict, use this order unless a newer explicit decision says otherwise:

1. explicit current human direction/corrections;
2. executable code, schemas and reproducible runtime evidence;
3. locked brand/version/provider/requirements/toolchain/feature-analysis manifests;
4. accepted architecture and decision records;
5. retained requirements ledger, project plan, roadmap, development map, feature map and TODO state;
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

Readiness is evidence-bound and can be demoted when upstream APIs, mappings, loaders, executables, libraries or assumptions change.

## Program workstreams

1. **Desktop/product shell** — launcher, settings, credentials, updates, downloads, accounts and process lifecycle without Java merely to start the desktop app.
2. **Runtime acquisition** — Mojang metadata/libraries/assets, managed Java, caches, hashes, classifiers and provenance.
3. **Loader adaptation / Polyloader** — vanilla, Fabric, Quilt, Forge, NeoForge, legacy/future adapters, prelaunch bootstrap, UAL translation and capability-gated sideloading.
4. **Content/resolution** — Modrinth, authorized CurseForge, local import, dependency solving, content locks, pack import/export and transactional instance updates.
5. **Creator runtime** — UAL, in-game IDE, scripts, AI interface, assets, models, microgeometry, scene graph, physics, construction tools and hotload.
6. **Deep integration/patching** — runtime/JVM/native/bootstrap escalation, deterministic patch manifests, binary/library compatibility, derived-runtime verification, fingerprint gating and rollback.
7. **World systems** — live world editor, structures, ores/events, rollback/WAL, dynamic liquids, paint layers, microgeometry placement and progression-gated transmutation.
8. **Cross-edition adaptation** — Java and Bedrock capability adapters with explicit parity gaps rather than fabricated equivalence.
9. **Polyglot/native/external extensions** — JVM, Graal languages, Python/Go/C# sidecars, Rust/C++, shared memory, IPC, MCP and capability-gated external tools.
10. **Production** — replay, animation, cameras, timeline, shots/takes, real-time/offline capture, audio and export.
11. **AI/project intelligence** — context routing, agent roles, work-state handoff, decision/assumption tracking, indexing, requirement traceability and drift detection.
12. **Validation/security/operations** — CI, tests, provenance, archive safety, bounded execution, profiling, chaos/fault containment, signing and recovery.
13. **Community/contribution** — onboarding, architecture education, support routing, conduct expectations, evidence literacy and ownership/reviewer structures.
14. **Gridelyx migration** — terminology/source/path/protocol/ABI/persisted migration plus compatibility aliases where required.
15. **Dependency/toolchain hardening** — version policy, cross-platform compiler/runtime matrix, SBOM/licensing and external-tool provenance.
16. **Feature analysis/program optimisation** — Feature Decision Packets, critical path, Kanban, benchmark/evidence research, cost/risk/horizon analysis and cross-feature primitive discovery.

## Work item lifecycle

Project-level Kanban uses `Backlog -> Ready -> Doing -> Blocked -> Verifying -> Done`, while engineering maturity may also be described as `PROPOSED -> FRAMED -> DESIGNED -> IMPLEMENTING -> VERIFYING -> ACCEPTED`.

Exceptional states: `DEFERRED`, `REJECTED`, `SUPERSEDED`, `REGRESSED`.

A work item is not complete merely because source files exist. `Done` requires the evidence appropriate to the claimed readiness and synchronized project-control state.

## Decision protocol

For architecture/compatibility decisions record:

- problem and desired outcome;
- affected subsystem/versions/editions/loaders;
- known constraints and unknowns;
- candidate approaches;
- chosen approach and why;
- evidence;
- falsification/review trigger;
- rollback/migration route;
- files/issues/tests affected.

Use `ai/decision-ledger.json` and `docs/DECISIONS.md`. Substantial features should additionally carry a Feature Decision Packet.

L5-L8 deep-integration decisions additionally record why shallower mechanisms are insufficient, exact target fingerprints, blast radius, derived-runtime/overlay model, upstream maintenance burden and recovery behavior.

Any decision removing/materially weakening a retained `CR-*` requirement must name that CR and be human-approved.

## Assumption protocol

Unverified assumptions belong in `ai/assumption-ledger.json` with scope, confidence, reason/evidence, validation route, review trigger and impact if false. Repetition by multiple AI sessions never upgrades an assumption into fact.

## Session and handoff protocol

For substantial AI-assisted work:

1. read `AGENTS.md` and `AI_HANDOFF.md`;
2. identify affected CR IDs in the retained requirements ledger;
3. use the Feature Decision Packet when the work is substantial/architectural;
4. inspect brand, repository metadata, requirements, feature-analysis and toolchain manifests;
5. read AI organisation/drift controls and task context map;
6. inspect authoritative implementation/evidence before maturity claims;
7. update work state when work spans sessions/agents;
8. make the smallest coherent change set;
9. run the relevant verification lane;
10. update requirements/dependencies/decisions/assumptions/readiness/TODO/Kanban state when truth changes;
11. leave a handoff distinguishing completed, verified, unverified, blocked and next work.

## Drift review cadence

Run a drift review after:

- product/protocol/repository rename;
- loader/Minecraft/Bedrock/API version change;
- executable/library fingerprint or patch target change;
- major architecture change;
- new runtime/language bridge/tool dependency;
- new/materially changed retained requirement;
- feature-analysis/critical-path change that materially alters sequencing;
- readiness promotion/demotion;
- milestone completion;
- long-running branch/AI handoff;
- conflicting documentation;
- human correction superseding terminology/design.

## Recovery and reversibility

- instance updates use locks/snapshots;
- world edits use transactions/WAL/rollback;
- hotload keeps last-known-good artifacts;
- target adapters fail closed on fingerprint/mapping drift;
- deep binary/runtime changes preserve a recoverable base and patch/overlay graph;
- patched derivatives invalidate when upstream fingerprints change;
- destructive migrations use backups/versioned schemas;
- project-control changes retain enough provenance to reconstruct decisions.

## Milestones

### M0 — Repository/control-plane convergence
Canonical Gridelyx identity, complete CR-001..CR-034 retained-scope ledger, feature-analysis/critical-path system, toolchain inventory, AI operating model, drift controls, evidence/readiness model, provider policy, community onboarding and synchronized planning surfaces.

### M1 — Vanilla launcher
Desktop shell, supported authentication, Mojang metadata, managed Java, cache, instance lock and reliable vanilla launch.

### M2 — Modded launcher and Polyloader foundations
Fabric/Quilt/Forge/NeoForge adapters, Modrinth/authorized CurseForge providers, dependency solver, simple/advanced UX, prelaunch/bootstrap foundations and rollback.

### M3 — Instance/modpack ecosystem
Pack import/export, Prism/MultiMC migration, server profiles, snapshots, portable bundles, mod-fork/analysis and diagnostics.

### M4 — Creator integration
Desktop + in-game creator runtime, world editing/events, liquids/paint/transmutation, microgeometry, assets/models, scene/physics construction, AI/IDE, non-Java gateway, safe hotload and managed deep-integration paths.

### M5 — Cross-edition creator validation
Java/Bedrock parity manifest, target-specific asset/world/IDE/extension/render/physics adapters and documented deeper integration where necessary.

### M6 — Machinima MVP
Replay/event log, rational-time timeline, cameras, actor tracks, animation editing, shot/take editor and reproducible capture/reopen/render.

### M7 — Professional production
Offline render, advanced passes, audio stems, multi-camera/take tooling, queues, collaboration and interchange research.

### M8 — Hardening/Gridelyx migration/release
Cross-platform packaging, security/provenance/SBOM, fuzz/chaos/performance evidence, signed updates, migrations, patch rollback evidence, completed terminology compatibility migration and stable/beta/nightly channels.

## Release gates

A milestone advances only with evidence matching claimed readiness. Compile success alone is insufficient for interactive/runtime claims. Provider/API/game/executable/library drift can reopen completed work.

Release/control-plane changes require:

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/toolchain_requirements_check.py
python tools/feature_planning_check.py
python tools/terminology_check.py
```

plus subsystem-specific tests.

## Planning surfaces

- `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` — complete retained conversation scope;
- `platform/chat-requirements.json` — machine-readable requirement/evidence paths;
- `docs/FEATURE_DECISION_FRAMEWORK.md` — substantial-feature analysis method;
- `docs/PROJECT_VALUES.md` — values/invariants used by feature analysis;
- `docs/DEVELOPMENT_MAP.md` — critical path, parallel lanes, horizons and Kanban;
- `docs/BENCHMARKING_MATRIX.md` — benchmark targets/process;
- `docs/templates/FEATURE_EVALUATION_TEMPLATE.md` — reusable decision packet;
- `platform/feature-analysis.schema.json` — machine-readable feature packet contract;
- `docs/DEPENDENCIES_AND_TOOLCHAIN.md` — dependency/tool documentation;
- `docs/CAPABILITY_DEPENDENCY_MATRIX.md` — capability-to-prerequisite/validation mapping;
- `platform/toolchain-requirements.json` — machine-readable tools/libraries;
- `platform/brand.json` — canonical Gridelyx product identity;
- `platform/repository-metadata.json` — requested GitHub slug/description state;
- `platform/terminology.json` / `docs/REBRAND_PLAN.md` — migration state;
- `docs/PROJECT_OVERVIEW.md` — product architecture;
- `docs/PROJECT_STRUCTURE.md` — ownership boundaries;
- `docs/DEEP_INTEGRATION_ARCHITECTURE.md` — additive integration model;
- `docs/ROADMAP.md` — staged delivery;
- `docs/FEATURE_MAP.md` — readiness/evidence matrix;
- `docs/TODO.md` — live implementation ledger;
- `COMMUNITY.md` and `docs/community/` — onboarding;
- `AI_HANDOFF.md`, `ai/` — AI continuity/control/navigation.
