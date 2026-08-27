# Repository AI handoff

## Current identity state

This repository is the Minecraft Advanced Mod Development Platform project. The previous public product branding is **in transition** and must not be propagated into new architecture or naming decisions. A replacement brand will be selected before the full terminology migration is performed.

## Mission

Build a cross-edition Minecraft launcher, instance/content manager, cross-loader development environment, live creator/sandbox runtime and machinima/production suite with strong fault containment, provenance, hotload and AI-assisted engineering.

This is **not constrained to conventional modding extension points**. Where required capability cannot be provided through normal APIs/loaders, the platform may escalate into JVM/runtime instrumentation, native components, external services, launch/bootstrap changes, version-pinned executable/library patching or maintained project-owned runtime components. These changes remain additive, fingerprint-gated, attributable and reversible rather than turning the base game into an unknowable mutation.

## Current architecture

- Java advanced runtime: loader-neutral UAL, hotload, scripting, world/asset editing, scene/physics tooling and advanced native/polyglot mechanisms under `templates/neoforge-26.2/src/advanced`.
- Deep integration: `docs/DEEP_INTEGRATION_ARCHITECTURE.md` defines L0-L8 escalation from supported APIs through deterministic binary/runtime patching and project-owned components, with immutable-base, provenance and rollback rules.
- Bedrock: stable Add-On runtime, preview Editor extension and native companion under `bedrock/` and `native/bedrock/`.
- Native/IPC: project-owned C ABI, binary bridge framing, Panama/FFM, shared memory and language sidecars.
- Studio/launcher core: `studio/core` defines GUI-independent instance, provider, provenance and dependency-resolution contracts.
- Providers: `studio/providers/providers.json` and `loader-adapters.json`; official/authorized upstreams only.
- Product/project control: `docs/PROJECT_PLAN.md`, `docs/PROJECT_OVERVIEW.md`, `docs/ROADMAP.md`, `docs/FEATURE_MAP.md`, `docs/TODO.md`.
- AI continuity: `ai/AI_ORGANISATION.md`, `ai/DRIFT_MITIGATION.md`, `ai/work-state.json`, `ai/decision-ledger.json`, `ai/assumption-ledger.json`, `ai/context-map.json`.
- AI retrieval/indexing: `docs/AI_CONTEXT_SYSTEM.md`, `tools/repo_index.py`, `tools/ai_context_pack.py`.

## Source-of-truth order

When information conflicts:

1. explicit current human direction/corrections;
2. implementation, schemas and reproducible runtime evidence;
3. locked platform/version/provider manifests;
4. accepted architecture/decision records;
5. project plan/roadmap/feature/TODO;
6. handoff/context/index material;
7. generated summaries and speculative notes.

Never resolve a conflict by averaging incompatible summaries.

## Non-negotiable invariants

1. The desktop application must be able to start without Java installed; Java is resolved per Java Edition instance.
2. “Any loader” means an extensible loader-adapter contract. Never fabricate unknown loader metadata, coordinates or launch arguments.
3. Downloads use official/authorized channels and retain provenance/hashes.
4. Writable instance state is isolated; immutable cached artifacts may be deduplicated deliberately.
5. Simple and expert UX use the same resolver/lockfile truth.
6. Java and Bedrock share neutral capability contracts where useful but target adapters must report real differences.
7. Native/bytecode/preview/instrumentation/patch paths are version-gated and fail closed on drift.
8. Multiplayer live world mutation remains server-authoritative.
9. External scripts/tools do not gain authority merely because they connect through IPC, MCP, web, native or polyglot bridges.
10. A feature claim cannot exceed its R0-R6 evidence.
11. AI summaries/indexes point to authoritative source and never supersede it.
12. Destructive migrations require an explicit recovery route.
13. A requirement must not be rejected merely because normal Minecraft/loader/Bedrock APIs cannot express it. Use the shallowest reliable integration layer, but deeper additive engine/runtime/binary integration is allowed when justified and recoverable.
14. Deep integration must not bypass authentication, entitlement, DRM, anti-cheat or platform security controls as a means of obtaining capability.

## Active transition: rebrand

The old public brand is scheduled for complete current-tree removal after a replacement name is selected.

Until then:

- use neutral project terminology in newly created control-plane documentation;
- do not invent replacement protocol prefixes or package names;
- do not perform piecemeal renames that create mixed terminology;
- treat Git-history rewriting as a separate destructive operation requiring explicit approval.

After selection, follow `ai/DRIFT_MITIGATION.md` → **Rebrand protocol** and add a CI terminology ban for retired terms.

## Active engineering priorities

1. Preserve and validate the AI continuity/drift-control layer.
2. Complete the whole-project requirements reconciliation tracked in GitHub issues.
3. Implement the desktop launcher/runtime acquisition path end-to-end.
4. Extend loader/content resolution through the supported adapter/provider model.
5. Integrate the live creator runtime: world editing, assets/models, microgeometry, physics, scripting, AI/IDE and non-Java extensions.
6. Validate Java/Bedrock capability parity explicitly.
7. Build the additive deep-integration/patch-management path so capabilities can escalate beyond ordinary API/loader limits without sacrificing provenance or rollback.
8. Harden hotload, rollback and process-isolated fault containment, preserving editor state across broader supervised restart scopes.
9. Promote replay/timeline/camera foundations into the production suite.

## Session start protocol

For non-trivial work:

1. read `AGENTS.md`;
2. read this handoff;
3. read `ai/AI_ORGANISATION.md` and `ai/DRIFT_MITIGATION.md`;
4. inspect `ai/work-state.json`, decision ledger and assumption ledger;
5. use `ai/context-map.json` to locate task-specific canonical source;
6. inspect version/provider manifests and upstream references before guessing external APIs;
7. check GitHub issues/TODO when task scope overlaps tracked work.

## Session end protocol

A meaningful session should leave behind:

- exact changes made;
- committed versus uncommitted/staged status;
- tests/commands actually run;
- failures and validation not performed;
- readiness changes supported by evidence;
- new/closed assumptions;
- architecture decisions and rollback route where relevant;
- exact next work or blocker.

Update `ai/work-state.json` when the task crosses sessions or agents. Update `ai/decision-ledger.json` or `ai/assumption-ledger.json` when project truth changes.

## Capability-state vocabulary

Use both the evidence level and a plain state when helpful:

- planned;
- specified;
- scaffolded;
- implemented;
- tested;
- target-validated;
- blocked;
- regressed;
- superseded.

Do not use “supported” without naming the edition/version/loader/evidence scope when that distinction matters.

## Validation baseline

```bash
python tools/continuity_check.py
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
cargo test --manifest-path studio/Cargo.toml --all-targets
```

Then run applicable Java advanced/native/Bedrock/game/runtime tests for the actual subsystem changed. L5-L8 changes additionally require target fingerprints, derived-artifact/overlay verification and an explicit rollback check.

## Compact handoff template

```text
OBJECTIVE:
BRANCH / COMMIT:
SCOPE:
AUTHORITATIVE FILES:
COMPLETED:
VERIFIED:
NOT VERIFIED:
DECISIONS:
OPEN ASSUMPTIONS:
BLOCKERS:
RECOVERY POINT:
NEXT ACTIONS:
```

A handoff is incomplete if another agent cannot distinguish what exists from what merely remains intended.
