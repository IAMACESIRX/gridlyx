# Drift mitigation and continuity control

This document prevents the repository from slowly changing meaning across AI sessions, contributors, versions and rebrands without an explicit decision.

## What counts as drift

### Requirement drift
A requested capability disappears, is weakened, or is reinterpreted without a recorded decision.

### Architecture drift
A new subsystem duplicates an existing responsibility, bypasses a canonical abstraction, or changes ownership boundaries accidentally.

### Terminology drift
Different files use competing names for the same concept, an obsolete brand survives a rename, or a protocol/component name changes inconsistently.

### Evidence drift
Documentation says a feature is implemented or compatible after the evidence that supported the claim has become stale or invalid.

### Version drift
Minecraft, loaders, mappings, Bedrock APIs, Java, Gradle, native dependencies or provider APIs change while repository assumptions remain pinned to old behavior.

### Scope drift
A task expands into unrelated cleanup or redesign and loses the user's original objective.

### Assumption drift
An uncertain statement is repeated until later agents treat it as fact.

### Handoff drift
A later session reconstructs project state from summaries and misses corrections, blockers, unfinished changes or failed validation.

### Brand drift
A retired public identity remains in source, docs, protocols, package labels, workflows, issues or generated metadata after a rebrand.

## Canonical anchors

The following anchors are checked before material changes:

1. explicit current user direction;
2. `docs/PROJECT_PLAN.md`;
3. `docs/PROJECT_OVERVIEW.md` and subsystem architecture docs;
4. `docs/FEATURE_MAP.md` and readiness evidence;
5. `docs/TODO.md` and active GitHub issues;
6. `AI_HANDOFF.md` and `ai/work-state.json`;
7. `ai/decision-ledger.json` and `ai/assumption-ledger.json`;
8. target-specific implementation, tests and upstream version evidence.

No single generated summary is an anchor by itself.

## Drift-control cycle

### 1. Orient
Identify the exact task, target subsystem, target versions and authoritative files.

### 2. Compare
Check current implementation against plan, handoff, decisions, assumptions and readiness claims.

### 3. Classify differences
For every meaningful mismatch classify it as:

- intentional change;
- stale documentation;
- implementation regression;
- unresolved assumption;
- version incompatibility;
- terminology/rebrand residue;
- duplicated architecture;
- unknown requiring investigation.

### 4. Decide
If the mismatch changes project truth, add/update a decision or assumption record before normalizing the repository.

### 5. Implement minimally
Change the smallest coherent set of canonical files. Avoid copying the same architectural truth into many documents.

### 6. Verify
Use the strongest applicable evidence: static -> automated -> headless target -> interactive target -> release/recovery evidence.

### 7. Reconcile
Update planning, readiness and handoff state only after the evidence exists.

## Hard invariants

- A capability is never promoted because an interface or TODO exists.
- Unknown loader/API details are never fabricated.
- A compatibility statement names the version/evidence scope that supports it.
- Java and Bedrock are capability-adapted; neither is declared equivalent merely because the neutral contract has a method for it.
- Native, bytecode, preview and instrumentation paths fail closed when fingerprints or versions drift.
- World mutation remains server-authoritative in multiplayer.
- Untrusted or crash-prone external execution does not gain authority merely by connecting through IPC/MCP/web/native bridges.
- Destructive operations require recovery state.
- AI summaries never override implementation evidence or explicit corrections.

## Decision freshness

A decision should define a review trigger when it depends on external behavior. Typical triggers:

- Minecraft/loader/Bedrock version changes;
- upstream provider/API changes;
- protocol schema changes;
- new renderer/network/world-storage behavior;
- a failed test contradicts the decision's assumptions;
- product rename changes public identifiers.

## Assumption ageing

Assumptions have one of these states:

- `open` — unresolved and relevant;
- `validated` — evidence established;
- `falsified` — evidence disproved it;
- `superseded` — newer decision replaced it;
- `expired` — context/version changed enough that it must be re-evaluated.

Open assumptions with high blast radius should block readiness promotion.

## Handoff anti-drift rules

A session handoff must:

- report the exact branch/commit when known;
- separate committed changes from staged/generated/uncommitted work;
- identify commands/tests actually run;
- identify validation that was not run;
- list open blockers and assumptions;
- preserve explicit corrections and superseded terminology;
- never claim background work will finish later.

## Rebrand protocol

A brand change is a controlled migration, not a search-and-replace performed piecemeal.

After the replacement brand is selected:

1. freeze the canonical spelling, capitalization, slug and protocol prefix;
2. inventory all old-brand occurrences in tracked files, paths, code identifiers, workflow names, schemas, documentation and generated templates;
3. classify each occurrence as public brand, internal identifier, protocol magic/ABI, historical provenance, or external reference;
4. rename public and internal project-owned identifiers coherently;
5. provide migrations/compatibility aliases only where required by persistent data or external consumers;
6. add a repository terminology check that fails CI on forbidden retired terms;
7. rescan the complete tracked tree and generated artifacts;
8. separately decide whether Git history should be rewritten. History rewriting is destructive and is not implied by a current-tree scrub.

Until a replacement name is approved, new control-plane documents use neutral project terminology.

## Review cadence

Perform a formal drift review:

- at milestone boundaries;
- before readiness promotion to R4+;
- after large AI-generated change sets;
- after upstream/game/loader version updates;
- after architecture or brand changes;
- when documentation conflict is discovered;
- before a release candidate.

## Machine check

`python tools/continuity_check.py`

The checker validates the existence and structure of the continuity control files and verifies context-map paths. It is intentionally conservative: passing the checker proves structural continuity metadata is coherent, not that runtime behavior is correct.
