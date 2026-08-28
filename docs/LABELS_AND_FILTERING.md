# Gridelyx Studio Labels, Bands and Scales

Gridelyx labels are an operational coordinate system. A work item can carry several independent dimensions instead of being compressed into one vague status label.

## Why scales

A structural hotload feature may simultaneously be:

- `hotload:H6-epoch` because it needs a fresh runtime epoch;
- `trust:T4-engine` because it patches Minecraft/bootstrap;
- `risk:R4` because the failure blast radius is high;
- `impact:I5` because it affects the platform architecture;
- `effort:F4` because it spans several subsystems;
- `confidence:C2` because a prototype exists;
- `evidence:Q3` because integration tests pass;
- `reversible:V2` because epoch rollback is demonstrated.

None of those labels means "do not do it." They tell us what engineering and evidence are required.

## Core dimensions

- `type:*` — nature of work: feature, bug, architecture, research, test, tooling, performance, breaking change.
- `status:*` — workflow state from research/design through verification/hardening/done.
- `area:*` — subsystem ownership.
- `priority:P0-P4` — scheduling priority.
- `impact:I0-I5` — scope of product/ecosystem effect.
- `risk:R0-R5` — consequence if the implementation is wrong.
- `effort:F0-F5` — implementation breadth.
- `confidence:C0-C4` — confidence in the current model.
- `evidence:Q0-Q6` — strongest evidence achieved.
- `reversible:V0-V4` — demonstrated recovery strength.
- `surface:*` — client/server/launcher/studio/native/protocol surface.
- `audience:*` — player, creator, mod author, server owner or maintainer.
- `compat:*` — loader/version target.
- `change:*` — compatibility surface being changed.
- `failure:*` — explicit failure mode being contained.

## Public hotload bands

The `hotload:H*` scale is the minimum activation machinery required:

| Band | Meaning |
|---|---|
| `H0-data` | data/config/tag/recipe reload |
| `H1-assets` | model/texture/audio/shader/UI asset swap |
| `H2-behavior` | sandboxed behavior/script epoch |
| `H3-module` | Java/service replacement behind stable handles |
| `H4-registry` | dynamic logical content through registry virtualization |
| `H5-lifecycle` | loader lifecycle replay and dependency-aware reload |
| `H6-epoch` | structural/bootstrap change through patched runtime or Runtime Epoch Handoff |

H6 is not a rejection band. It is the strongest public hotload mechanism.

## Trust bands

Trust describes authority, not audience:

| Band | Authority |
|---|---|
| `T0-data` | declarative only |
| `T1-sandboxed` | sandboxed executable behavior |
| `T2-capabilities` | declared Gridelyx capabilities |
| `T3-privileged` | privileged runtime integration |
| `T4-engine` | engine/bootstrap/native patch authority |

A public package can request any band; higher bands require clearer consent, provenance and containment.

## Evidence scale

`Q0` idea → `Q1` contract → `Q2` static/build proof → `Q3` unit/integration → `Q4` headless target → `Q5` interactive target → `Q6` release/migration/rollback proof.

This is deliberately separate from confidence. A design can be intellectually convincing (`C3`) while still lacking real target evidence (`Q2`).

## Reversibility scale

`V0` no demonstrated rollback → `V1` process fallback → `V2` restart/epoch rollback → `V3` transactional rollback → `V4` instant in-process reversibility.

## Failure labels

Failure labels are intentionally specific:

- state leak;
- registration leak;
- classloader leak;
- world corruption;
- protocol desync;
- render desync;
- bootstrap-order conflict;
- mapping drift;
- privilege escape;
- migration failure;
- epoch handoff failure;
- dependency cycle.

They make chaos/failure testing searchable instead of burying failure assumptions in issue prose.

## Minimum useful issue labeling

A normal feature should have at least:

`type:* + status:* + area:* + priority:* + evidence:*`

Hotload work should additionally carry:

`hotload:* + trust:* + risk:* + reversible:*`

Compatibility work should add:

`compat:* + surface:*`

## Automation

`platform/label-taxonomy.json` is canonical. `tools/sync_labels.py` materializes it into GitHub. The label workflow runs automatically when the taxonomy or sync tool changes and remains manually dispatchable for reconciliation.

Labels do not replace requirements, design records, tests, milestones or release evidence. They make those artifacts filterable.
