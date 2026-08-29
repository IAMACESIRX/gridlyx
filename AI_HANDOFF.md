# Gridelyx AI handoff

## Current identity state

The canonical root brand is **Gridelyx** and the integrated suite is **Gridelyx Studio**. `platform/brand.json` is authoritative. Product/API slug remains `gridelyx`; the requested GitHub repository slug is `gridlyx`, tracked in `platform/repository-metadata.json`.

## Mission

Build a cross-edition Minecraft launcher, instance/content manager, cross-loader development environment, live creator/sandbox runtime, world editor and machinima/production suite with strong fault containment, provenance, hotload and AI-assisted engineering.

The project is **not constrained to conventional modding extension points**. Requirements may escalate into JVM/runtime instrumentation, native components, external services, launch/bootstrap changes, deterministic executable/library patching or project-owned runtime components when shallower layers cannot provide the capability. Deeper changes remain additive, fingerprint-gated, attributable and reversible.

## Canonical retained scope

Read [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md) and `platform/chat-requirements.json` before broad design work. They preserve the retained Gridelyx capability scope, including:

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
- live asset editing;
- physics/construction;
- scene/properties/gizmos/live execution;
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
- Gridelyx identity/protocol migration;
- complete dependency/toolchain inventory;
- W5x5x5/decision/risk/cost/long-horizon feature-planning system.

Never silently drop one of these because it is difficult, expensive or outside normal mod APIs.

## Feature-planning protocol

For a substantial feature or architecture decision read:

- [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md);
- [`docs/PROJECT_VALUES.md`](docs/PROJECT_VALUES.md);
- [`docs/DEVELOPMENT_MAP.md`](docs/DEVELOPMENT_MAP.md);
- [`docs/BENCHMARKING_MATRIX.md`](docs/BENCHMARKING_MATRIX.md);
- [`docs/templates/FEATURE_EVALUATION_TEMPLATE.md`](docs/templates/FEATURE_EVALUATION_TEMPLATE.md);
- `platform/feature-analysis.schema.json`.

The packet must cover W5x5x5 positive/inverse questions, task decomposition, values, cost, 10m/10h/10d/10mo/1y/5y/10y horizons, opportunity cost, regret/reversibility, risk/inversion/pre-mortem, second-order effects, Eisenhower, overlap/Venn analysis, brainstorming, first principles, verified benchmarks, Feynman explanation, MVP/timebox, asymmetric risk, working backward, Pareto, Critical Path, Cynefin, Kanban, validation and rollback. This framework guides sequencing; it is not an automatic scope veto.

## Current architecture

- **Java advanced runtime:** `templates/neoforge-26.2/src/advanced` — UAL/Polyloader, public hotload orchestration, bytecode, scripting, MCP/indexing, world editing, assets, scene/physics tooling, rendering, native/IPC and production foundations.
- **Hotload target integration:** concrete NeoForge H0/H1 resource reload, H3 versioned module epochs, Instrumentation redefine, and H6 external runtime-supervisor boundary; H6 fails closed without a real supervisor.
- **Deep integration:** [`docs/DEEP_INTEGRATION_ARCHITECTURE.md`](docs/DEEP_INTEGRATION_ARCHITECTURE.md) — escalation from supported APIs through additive patch/runtime ownership.
- **Bedrock:** `bedrock/` + `native/bedrock/` + `platform/bedrock-capabilities.json`.
- **Native/IPC:** `native/`, `bridges/`, FFM/Panama/shared-memory framing.
- **Studio/launcher core:** `studio/core` plus provider/loader manifests and schemas.
- **Public upstream acquisition:** `vault/manifest.json`, `.github/actions/gridelyx-toolchain/action.yml`, `tools/hydrate_references.py`, `tools/redistribution_guard.py`. Upstream payloads are acquired into ignored local/runner/package-manager caches, not committed.
- **Project control:** [`docs/PROJECT_PLAN.md`](docs/PROJECT_PLAN.md), `ROADMAP.md`, `DEVELOPMENT_MAP.md`, `FEATURE_MAP.md`, `TODO.md`, requirements ledger.
- **AI continuity:** [`ai/AI_ORGANISATION.md`](ai/AI_ORGANISATION.md), drift controls, work/decision/assumption ledgers, context map.
- **Dependencies/toolchain:** [`docs/DEPENDENCIES_AND_TOOLCHAIN.md`](docs/DEPENDENCIES_AND_TOOLCHAIN.md), `platform/toolchain-requirements.json`, `vault/manifest.json`.
- **Feature analysis:** [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md), `platform/feature-analysis.schema.json`.
- **Community:** [`COMMUNITY.md`](COMMUNITY.md), [`CONTRIBUTING.md`](CONTRIBUTING.md), [`SUPPORT.md`](SUPPORT.md), [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md), `docs/community/`.

## Source-of-truth order

When information conflicts:

1. explicit current human direction/corrections;
2. implementation, schemas and reproducible runtime evidence;
3. locked platform/version/brand/provider/toolchain/acquisition/feature-analysis manifests;
4. accepted architecture/decision records;
5. requirements ledger, project plan, roadmap, development map, feature map and TODO;
6. handoff/context/index material;
7. generated summaries and speculative notes.

Never resolve contradictions by averaging incompatible summaries.

## Non-negotiable invariants

1. Gridelyx is the canonical root brand; compatibility identifiers do not restore retired branding.
2. The desktop application must start without Java installed; Java is resolved per Java Edition instance.
3. “Any loader/version” means extensible versioned adapters and evidence, not fabricated universal compatibility.
4. Downloads use official/authorized channels and retain hashes/provenance where appropriate.
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
17. **The public source repository must not redistribute upstream Minecraft, NeoForge, JDK, Gradle, LWJGL, Maven-cache, installer, native-bundle or hydrated reference payloads.** Store provider/version/revision metadata and acquisition logic instead.
18. The deleted binary-vault importer/reconstructor/chunk model must not be restored unless the human owner explicitly reverses the public-repository policy.
19. Optional NeoForge MDK reference material belongs only in ignored `.reference-cache/`; Minecraft/NeoForge runtime artifacts belong in toolchain/package-manager/runtime caches.
20. Cost/priority analysis is diagnostic; it cannot silently erase retained CR scope.
21. Difficult-to-reverse decisions require stronger evidence and explicit migration/recovery analysis.

## Active engineering priorities

1. Preserve Gridelyx identity, requirements, toolchain, acquisition and feature-planning continuity gates.
2. Keep the repository safe to publish: acquisition metadata/code in Git, third-party payloads outside Git, redistribution guard mandatory.
3. Validate clean-machine/empty-cache Java builds where JDK/Gradle are installed dynamically and ModDevGradle/Maven resolve remaining dependencies.
4. Implement the desktop launcher/runtime acquisition path end-to-end through legitimate/authorized providers.
5. Extend loader/content resolution through the provider/adapter model.
6. Produce the first real cross-loader Polyloader/UAL compatibility proof.
7. Integrate live creator runtime: world editing, assets/models, microgeometry, physics, scripting, AI/IDE and non-Java extensions.
8. Implement/validate dynamic liquids, paint layers and progression transmutation.
9. Validate Java/Bedrock parity explicitly.
10. Build the managed deep-integration/patch path with immutable base/provenance/rollback.
11. Harden hotload, rollback and process-isolated fault containment, including a real H6 Runtime Epoch supervisor.
12. Promote replay/timeline/camera foundations into the complete production suite.
13. Pin presently-unpinned Python/Rust/CMake/compiler/Go/.NET release toolchain policies.
14. Apply Feature Decision Packets to major implementation issues as they move from Backlog to Ready.

## Public acquisition architecture

The source repository must remain payload-free for upstream runtime/toolchain dependencies:

- `actions/setup-java` installs the locked Temurin JDK in CI.
- `gradle/actions/setup-gradle` installs locked Gradle in CI.
- `net.neoforged.moddev` resolves the Minecraft/NeoForge development runtime and mappings into Gradle caches.
- Gradle/Maven resolves LWJGL, ASM, GraalVM, JUnit, ArchUnit and other declared libraries.
- `tools/hydrate_references.py --mdk` optionally clones the pinned official NeoForge MDK revision to `.reference-cache/upstream/mdk-26.2`.
- `tools/redistribution_guard.py` scans `git ls-files` and rejects tracked JAR/class/archive/native/installer/chunk payloads and upstream reference trees.
- `vault/manifest.json` is acquisition metadata only; the historical directory name does not mean binary storage.

A local launcher/runtime may cache legitimately acquired game/runtime artifacts for the user, subject to provider terms. That local cache is not permission to publish those bytes from the Gridelyx source repository.

## Session start protocol

For non-trivial work:

1. read [`AGENTS.md`](AGENTS.md);
2. read this handoff;
3. read [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md);
4. for substantial feature/architecture work, read [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md) and [`docs/DEVELOPMENT_MAP.md`](docs/DEVELOPMENT_MAP.md);
5. read [`ai/AI_ORGANISATION.md`](ai/AI_ORGANISATION.md) and [`ai/DRIFT_MITIGATION.md`](ai/DRIFT_MITIGATION.md);
6. inspect `platform/brand.json`, `platform/repository-metadata.json`, `platform/chat-requirements.json`, `platform/feature-analysis.schema.json`, `platform/toolchain-requirements.json` and `vault/manifest.json`;
7. inspect work-state, decision and assumption ledgers;
8. use `ai/context-map.json` for task-specific canonical source;
9. inspect versions/providers/acquisition metadata before guessing external APIs or distribution behavior;
10. check GitHub issues/TODO for overlapping tracked work.

## Session end protocol

A meaningful session leaves:

- exact changes made;
- committed versus uncommitted/staged state;
- tests/commands actually run;
- failures and validation not performed;
- readiness changes supported by evidence;
- new/closed assumptions;
- architecture/brand/protocol decisions and rollback routes;
- dependency/toolchain/acquisition changes;
- Feature Decision Packet/critical-path/Kanban changes when relevant;
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
python tools/feature_planning_check.py
python tools/terminology_check.py
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
cargo test --manifest-path studio/Cargo.toml --all-targets
```

Then run applicable Java advanced/native/Bedrock/GameTest/client tests. Clean public-build validation should start with no checked-in upstream payloads and, where practical, empty dependency caches. L5-L8 changes additionally require exact target fingerprints, derived artifact/overlay verification and rollback checks.

## Compact handoff template

```text
OBJECTIVE:
BRANCH / COMMIT:
SCOPE / CR IDS:
KANBAN STATE:
FEATURE DECISION PACKET:
CRITICAL-PATH EFFECT:
AUTHORITATIVE FILES:
COMPLETED:
VERIFIED:
NOT VERIFIED:
DECISIONS:
OPEN ASSUMPTIONS:
DEPENDENCIES / TOOLCHAIN / ACQUISITION CHANGES:
RISKS / RECOVERY:
BLOCKERS:
RECOVERY POINT:
NEXT ACTIONS:
```

A handoff is incomplete if another agent cannot distinguish what exists from what remains intended.
