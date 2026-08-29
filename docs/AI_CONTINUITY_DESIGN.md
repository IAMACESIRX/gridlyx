# AI continuity design rationale

This repository's AI continuity system was informed by structural lessons observed in Project Athena, then redesigned for a software-engineering/mod-development platform. It is intentionally an **equivalent solution to the same continuity problems**, not a copy of Athena's identity, organisation or governance model.

## Problems being solved

Long-lived AI-assisted repositories tend to accumulate several failure modes:

- later agents do not know where authoritative truth lives;
- a summary is mistaken for implementation evidence;
- architecture decisions lose their original constraints;
- assumptions are repeated until they become folklore;
- roles blur between researcher, implementer and validator;
- handoffs omit unverified or unfinished work;
- renamed concepts survive in disconnected files;
- current state cannot be reconstructed without rereading the whole repository.

The continuity layer exists to make those failure modes observable and recoverable.

## Useful structural equivalents

### Universal entrypoint -> deterministic entry chain

Project Athena demonstrates the value of one obvious AI entrypoint. This repository uses a short ordered chain instead:

1. [`AGENTS.md`](../AGENTS.md) — mandatory engineering rules;
2. [`AI_HANDOFF.md`](../AI_HANDOFF.md) — current state and active transitions;
3. [`ai/AI_ORGANISATION.md`](../ai/AI_ORGANISATION.md) — role/capability boundaries;
4. [`ai/DRIFT_MITIGATION.md`](../ai/DRIFT_MITIGATION.md) — consistency controls;
5. `ai/work-state.json` — machine-readable current work;
6. decision/assumption ledgers;
7. `ai/context-map.json` — task-specific routing.

The result is equivalent discoverability without requiring a monolithic portal file.

### Agent registry -> capability-scoped engineering roles

Athena's registry shows that explicit role/capability boundaries reduce ambiguity. Here the equivalent is deliberately lighter:

- architecture steward;
- implementation engineer;
- compatibility researcher;
- runtime validator;
- continuity steward;
- security/provenance reviewer;
- documentation/community steward;
- human direction owner.

These are task functions, not permanent artificial personalities or an autonomous hierarchy. One model may execute several roles sequentially, but validation remains conceptually separate from implementation.

### Organisational hierarchy -> repository ownership graph

Instead of mirroring an institution, this project maps authority to technical ownership boundaries:

- launcher/product orchestration;
- Java runtime/creator plane;
- Bedrock adapters;
- native/IPC bridges;
- world systems;
- production systems;
- AI/project-control layer;
- validation/security/provenance.

Technical ownership answers “which subsystem owns this truth?” rather than “which artificial department outranks another?”

### Context manifest -> context map + deterministic index

Athena's context/audit pattern demonstrates the value of knowing what has been retrieved and what has not. This repository uses:

- `ai/context-map.json` for canonical task routing;
- `tools/repo_index.py` for deterministic file/chunk indexing;
- `tools/ai_context_pack.py` for bounded task-specific retrieval;
- source hashes/paths as stable evidence anchors where appropriate.

A context pack is navigation assistance, never a substitute for authoritative source.

### Decision framework -> decision + assumption ledgers

The useful equivalence is not Athena's exact schema; it is preserving why a decision was made and how it can be disproved.

`ai/decision-ledger.json` records:

- scope;
- decision;
- reason/evidence;
- review trigger;
- rollback route.

`ai/assumption-ledger.json` keeps unresolved claims separate so repetition cannot silently convert them into facts.

### Retrieval audit -> evidence/readiness discipline

A key continuity distinction is:

> “I retrieved and checked these sources” is not the same as “I proved the entire system is complete.”

This repository generalizes that distinction through R0-R6 readiness and explicit validation lanes. A source tree may be comprehensively indexed while runtime support remains unverified.

### Session writeback -> durable work state and handoff

A substantial session should end by writing verified state back into the repository:

- what changed;
- what was actually tested;
- what remains unverified;
- current assumptions/blockers;
- recovery point;
- next action.

`ai/work-state.json` carries compact cross-session state while [`AI_HANDOFF.md`](../AI_HANDOFF.md) gives a readable summary.

## Deliberate non-equivalences

The following Athena characteristics are **not** imported because they do not improve this project's engineering correctness:

- identity/personality persistence as architectural authority;
- artificial institutional titles for their own sake;
- a simulated organisation where a simpler repository ownership boundary works;
- duplicated memory prose when machine-readable state or source evidence is sufficient;
- governance rules unrelated to software/runtime risk.

## Continuity invariants

1. Observation is not authority.
2. Implementation is not validation.
3. Retrieval completeness is not runtime completeness.
4. Repetition is not evidence.
5. A generated summary never supersedes canonical source.
6. External/version-dependent decisions carry review triggers.
7. Handoffs distinguish committed, verified and merely intended work.
8. Corrections supersede obsolete terminology explicitly.
9. Project control remains lightweight enough that developers can actually maintain it.

## Verification

`tools/continuity_check.py` checks structural coherence of the continuity layer. Studio CI runs it before the broader Studio architecture checks.

This structural test does not claim that game/runtime capabilities are working; those remain under their target-specific validation lanes.
