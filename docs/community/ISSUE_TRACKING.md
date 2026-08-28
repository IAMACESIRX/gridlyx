# Gridelyx issue tracking

GitHub Issues are the operational work queue; repository ledgers remain the architectural source of truth.

## Every substantial issue should include

- affected `CR-*` requirement IDs;
- target edition/version/loader/platform;
- current R0-R6 evidence state;
- Kanban state (`Backlog`, `Ready`, `Doing`, `Blocked`, `Verifying`, `Done`);
- desired outcome / working-backward success statement;
- dependencies/toolchains required;
- critical-path effect and prerequisites;
- authoritative source/docs;
- validation required to close the issue;
- rollback/migration notes where applicable.

## Feature Decision Packet

Substantial feature or architecture work uses [`../FEATURE_DECISION_FRAMEWORK.md`](../FEATURE_DECISION_FRAMEWORK.md) and [`../templates/FEATURE_EVALUATION_TEMPLATE.md`](../templates/FEATURE_EVALUATION_TEMPLATE.md). The repository issue form `.github/ISSUE_TEMPLATE/feature-evaluation.yml` provides the intake surface.

Before an item moves from Backlog toward Ready, analyse it at a depth proportionate to risk/reversibility, including W5x5x5, first principles, values, cost/horizons, opportunity cost, regret/reversibility, risks/inversion/pre-mortem, second-order effects, benchmarks, MVP/timebox, Pareto, Critical Path, Cynefin and evidence/rollback.

Cost or priority analysis controls sequencing; it does not silently delete retained CR scope.

## Issue vs documentation truth

An issue may track implementation status but must not silently redefine project scope. If an issue changes architecture, scope, dependency requirements, planning state or evidence state, update the corresponding canonical files in the same work:

- [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](../CHAT_REQUIREMENTS_TRACEABILITY.md) / `platform/chat-requirements.json`;
- [`docs/FEATURE_DECISION_FRAMEWORK.md`](../FEATURE_DECISION_FRAMEWORK.md) / `platform/feature-analysis.schema.json` when decision methodology changes;
- [`docs/TODO.md`](../TODO.md), `ROADMAP.md`, `DEVELOPMENT_MAP.md`, `FEATURE_MAP.md`;
- [`docs/DEPENDENCIES_AND_TOOLCHAIN.md`](../DEPENDENCIES_AND_TOOLCHAIN.md) / `platform/toolchain-requirements.json`;
- decision/assumption/work-state ledgers when project truth changes.

## Closing an issue

Do not close a feature issue merely because code was written. Record the tests actually run and the highest evidence level they support. Interactive/render/native/Bedrock claims need target-specific evidence. `Done` means evidence and project-control state agree.
