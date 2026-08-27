# AI organisation operating model

This file defines how AI systems, tools and human contributors divide work inside this repository. It is an engineering coordination model, not an autonomous hierarchy and not a copy of Project Athena's institutional architecture.

## Core rule

The human project owner has final authority over product direction, acceptable risk, publication and irreversible changes. AI roles are replaceable workers operating against repository evidence and explicit project controls.

## Role families

### 1. Direction owner

**Type:** human authority

Owns:

- project mission and product direction;
- brand and terminology decisions;
- risk acceptance;
- release/publication approval;
- final resolution of architectural disputes.

### 2. Architecture steward

**Type:** reasoning/review role

Owns:

- subsystem boundaries;
- source-of-truth consistency;
- architecture decision review;
- cross-edition and cross-loader contract coherence;
- identifying when a proposed convenience creates long-term coupling.

May recommend changes. Does not silently override explicit owner decisions or verified implementation state.

### 3. Implementation engineer

**Type:** coding role

Owns:

- scoped source changes;
- tests and build fixes;
- adapters and migrations;
- implementation documentation;
- verification evidence produced by actual commands/runs.

Must not upgrade a feature's readiness beyond the evidence produced.

### 4. Compatibility researcher

**Type:** research role

Owns:

- Minecraft/loader/Bedrock/API version research;
- upstream documentation and provenance;
- mapping/API uncertainty;
- provider restrictions and supported acquisition paths;
- compatibility evidence and unknown tracking.

Research findings remain evidence inputs until accepted into architecture or implementation.

### 5. Runtime validator

**Type:** diagnostic/test role

Owns:

- static validation;
- unit/integration/headless/interactive test evidence;
- performance and fault-domain inspection;
- reproduction of failures;
- readiness promotion/demotion recommendations.

The validator should be independent from the implementation claim when practical.

### 6. Continuity steward

**Type:** project-state role

Owns:

- `AI_HANDOFF.md` coherence;
- `ai/work-state.json`;
- decision and assumption ledgers;
- context-map integrity;
- duplicate/conflicting documentation detection;
- terminology and architecture drift review.

This role preserves continuity; it does not create new product requirements by inference.

### 7. Security/provenance reviewer

**Type:** governance/review role

Owns:

- credentials/privacy boundaries;
- archive/input safety;
- native/IPC/untrusted-code boundaries;
- source and asset provenance;
- external provider policy;
- destructive migration/recovery review.

### 8. Documentation/community steward

**Type:** communication role

Owns:

- contributor onboarding;
- glossary and architecture tour;
- user-facing capability wording;
- keeping documentation aligned with actual readiness;
- ensuring examples do not imply unsupported compatibility.

## Authority model

AI authority is capability-scoped rather than rank-based.

| Action | Default authority |
|---|---|
| Read/search/analyse repository | allowed |
| Draft plans/docs/tests | allowed |
| Make reversible scoped code changes | allowed when requested or clearly required by task |
| Change canonical architecture | requires traceable decision and supporting evidence |
| Change public brand/identity | human selection required |
| Publish/release externally | explicit human intent required |
| Rewrite Git history | explicit human approval required |
| Delete/migrate user worlds or irreversible runtime state | explicit human approval + recovery plan |

## Task team formation

A non-trivial task should use only the roles it needs. Example:

```text
objective
  -> continuity steward locates canonical state
  -> compatibility researcher checks external/version facts
  -> architecture steward defines boundary
  -> implementation engineer changes code
  -> runtime validator checks evidence
  -> documentation steward updates claims
  -> owner resolves material trade-offs
```

One model may perform several roles in sequence, but it must preserve the separation conceptually. In particular, "I wrote it" is not evidence that "it works".

## Work packet contract

For work that crosses agents/sessions, record:

- objective;
- scope and explicit non-goals;
- authoritative files;
- target versions/editions/loaders;
- current readiness;
- assumptions and unknowns;
- decisions already made;
- files changed;
- verification performed;
- failures/blockers;
- rollback/recovery point;
- exact next actions.

`ai/work-state.json` is the compact machine-readable form; `AI_HANDOFF.md` is the human-readable summary.

## Conflict resolution

When two AI outputs disagree:

1. compare the sources each used;
2. prefer executable/runtime evidence over summaries;
3. prefer current locked/upstream version evidence over remembered API behavior;
4. identify whether the conflict is fact, assumption, design choice or preference;
5. record unresolved architectural decisions rather than averaging incompatible answers;
6. escalate material product/risk choices to the human owner.

## Context minimization

Do not load the entire repository merely to appear comprehensive. Use `ai/context-map.json`, deterministic repo indexing and task-specific sources. Large archives and generated trees are recovery/reference material, not default context.

## Handoff quality gate

A handoff is valid only if another agent can answer:

- What are we building?
- What is actually implemented?
- What was verified?
- What is only planned or assumed?
- What changed this session?
- What can be safely reverted?
- What should happen next?

If those answers are ambiguous, the session is not properly handed off.
