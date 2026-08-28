# Feature Decision Packet — <feature>

## Identity

- Feature:
- CR IDs:
- Issue/PR:
- Owner/workstream:
- Kanban state: Backlog / Ready / Doing / Blocked / Verifying / Done
- Readiness now / target:
- Target editions/versions/loaders/platforms:

## Desired outcome

### Working-backward success statement

<Describe the finished user experience, compatibility, evidence and recovery behaviour.>

### Feynman explanation

<Explain what this does, how state moves, and what happens on failure in plain language.>

## W5x5x5

For each prompt, perform at least five progressively deeper passes.

### Who / Who not

1.
2.
3.
4.
5.

### What / What isn't

1.
2.
3.
4.
5.

### When / When isn't

1.
2.
3.
4.
5.

### Where / Where isn't

1.
2.
3.
4.
5.

### How / How not

1.
2.
3.
4.
5.

### Why / Why isn't

1.
2.
3.
4.
5.

## Perspectives

- User/product:
- Runtime/system:
- Compatibility/ecosystem:
- Safety/operations:
- Project/evolution:

## Facts / assumptions / unknowns

- FACT:
- DERIVED:
- ASSUMPTION:
- HYPOTHESIS:
- UNKNOWN:
- REQUIRES VALIDATION:

## Values alignment

Use `docs/PROJECT_VALUES.md`.

- Strengthens:
- Tensions:
- Mitigations:

## First principles

- Undeniable system truths:
- Neutral state/data model:
- Authority/lifecycle model:

## Task breakdown

| Work unit | Prerequisites | Parallel? | Validation | Rollback |
|---|---|---:|---|---|
| | | | | |

## Architecture brainstorm

### Candidate A

### Candidate B

### Candidate C

### Rejected approaches

## Benchmarking

Reference `docs/BENCHMARKING_MATRIX.md` and current evidence.

- Benchmarks:
- Emulate:
- Exceed:
- Reject:
- Generalize:

## Pros / cons / trade-offs

- Pros:
- Cons:
- Debt introduced:
- Debt removed:
- Compatibility gained/lost:
- UX complexity gained/lost:

## Cost diagnostic

- Engineering:
- Research:
- CI/compute:
- Storage/network:
- API/licensing:
- Hardware/devices:
- Maintainer/support energy:
- Compatibility maintenance:

## 10/10/10/10/1/5/10 horizon

| Horizon | Target/effect | Opportunity cost |
|---|---|---|
| 10 minutes | | |
| 10 hours | | |
| 10 days | | |
| 10 months | | |
| 1 year | | |
| 5 years | | |
| 10 years | | |

## Opportunity cost

- Delays:
- Unlocks:
- Shared primitives:
- Duplication avoided:

## Regret minimisation

- Regret if not built:
- Regret if locked in too early:
- Future option value:

## Reversibility

- Classification: reversible / difficult-to-reverse
- Recovery point:
- Migration path:
- Persisted/public compatibility impact:

## Risk register

| Risk | Probability | Impact | Detectability | Blast radius | Mitigation | Recovery |
|---|---|---|---|---|---|---|
| | | | | | | |

## Inversion

How would we deliberately make this fail spectacularly?

1.
2.
3.
4.
5.

Controls that prevent those paths:

1.
2.
3.
4.
5.

## Second-order effects

- Immediate effect -> secondary effect -> tertiary effect:
- Feedback loops:

## Eisenhower

- Urgency:
- Importance:
- Quadrant:
- Sequencing consequence:

## Overlap / Venn analysis

- Shared CRs/subsystems:
- Shared platform primitives:
- Boundaries that must remain separate:

## MVP

- Smallest vertical slice:
- Riskiest assumption tested:
- Success/failure measurement:

## Timebox

- First research/probe box: 30 / 60 minutes
- Question to resolve:
- Stop/expand condition:

## Pre-mortem

Assume it shipped and failed. Causes:

1.
2.
3.
4.
5.

Preventive tests/controls:

## Asymmetric risk

- Capped downside experiment:
- Potential upside:
- Hard safety limit:

## Pareto / 80-20

- Highest-leverage 20%:
- Remaining retained work:

## Critical path

- Predecessors:
- Blocking sequence:
- Successors:
- Parallelizable tasks:

## Cynefin

- Clear / complicated / complex / chaotic / confused:
- Appropriate operating mode:

## Validation and evidence

- Static/build:
- Unit/architecture:
- Headless target:
- Interactive target:
- Performance/chaos/security:
- Release/migration/rollback:

## Final decision / next trigger

- Current decision:
- Evidence basis:
- Unresolved assumptions:
- Next decision trigger:
