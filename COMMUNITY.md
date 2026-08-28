# Gridelyx community

**Gridelyx** is an experimental cross-edition Minecraft engineering platform; **Gridelyx Studio** is the integrated launcher/creator/production suite. The project spans launcher/runtime management, loader compatibility, live creator tooling, world editing, scripting/native extensions, multiplayer authoring, Bedrock adaptation and machinima/production.

## Start here

- New users and contributors: [`docs/community/GETTING_STARTED.md`](docs/community/GETTING_STARTED.md)
- Contributor workflow: [`docs/community/CONTRIBUTOR_ONBOARDING.md`](docs/community/CONTRIBUTOR_ONBOARDING.md)
- Architecture orientation: [`docs/community/ARCHITECTURE_TOUR.md`](docs/community/ARCHITECTURE_TOUR.md)
- Testing and evidence: [`docs/community/TESTING_AND_EVIDENCE.md`](docs/community/TESTING_AND_EVIDENCE.md)
- Feature/architecture decisions: [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md)
- Project values: [`docs/PROJECT_VALUES.md`](docs/PROJECT_VALUES.md)
- Development/critical-path map: [`docs/DEVELOPMENT_MAP.md`](docs/DEVELOPMENT_MAP.md)
- Benchmarking: [`docs/BENCHMARKING_MATRIX.md`](docs/BENCHMARKING_MATRIX.md)
- Terms and project vocabulary: [`docs/community/GLOSSARY.md`](docs/community/GLOSSARY.md)
- Dependencies/programs/toolchains: [`docs/DEPENDENCIES_AND_TOOLCHAIN.md`](docs/DEPENDENCIES_AND_TOOLCHAIN.md)
- Capability-to-prerequisite matrix: [`docs/CAPABILITY_DEPENDENCY_MATRIX.md`](docs/CAPABILITY_DEPENDENCY_MATRIX.md)
- Contribution rules: [`CONTRIBUTING.md`](CONTRIBUTING.md)
- Support routing: [`SUPPORT.md`](SUPPORT.md)
- Engineering/operational safety: [`SAFETY.md`](SAFETY.md)
- Security and vulnerability reporting: [`SECURITY.md`](SECURITY.md)
- Conduct expectations and moderation: [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md)

## Evidence-first rule

A feature appearing in architecture or source does not automatically mean it is release-ready. Gridelyx uses R0-R6 readiness and target-specific validation. Community-facing claims should link to concrete evidence and distinguish planned, framework, tested and target-validated states.

## Safety and security rule

Powerful integration is not treated as inherently safe merely because Gridelyx supports it. Privileged work involving world mutation, Java instrumentation, bytecode transformation, native/FFM memory, IPC, custom networking, generated code, GPU resources or deep Bedrock/runtime integration must follow [`SAFETY.md`](SAFETY.md) and [`SECURITY.md`](SECURITY.md), including bounded execution, explicit authority, appropriate isolation, target validation and recovery/rollback planning.

## Feature decision rule

Substantial new features and architecture changes should use the CR-034 Feature Decision Packet. That means mapping CR IDs, W5x5x5 positive/inverse questions, first principles, values, cost and time horizons, opportunity cost, reversibility, risk/inversion/pre-mortem, second-order effects, benchmarks, MVP, critical path, Cynefin, Kanban, evidence and rollback. See `.github/ISSUE_TEMPLATE/feature-evaluation.yml` for issue intake.

The framework decides **how to understand and sequence work**, not whether a retained capability silently disappears because it is difficult or costly.

## Scope preservation

The canonical conversation-derived requirements CR-001 through CR-034 are preserved in [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md) and `platform/chat-requirements.json`. Contributors must not silently remove or materially weaken those requirements. Scope changes require an explicit project decision and corresponding planning updates.

## Brand state

Gridelyx is the current and exclusive project-owned identity. Native and bridge compatibility now begins at the canonical Gridelyx v2 boundary documented in [`docs/GRIDELYX_BRIDGE_PROTOCOL.md`](docs/GRIDELYX_BRIDGE_PROTOCOL.md); current source should not introduce superseded product identifiers.

The requested GitHub repository target is `IAMACESIRX/gridlyx`; product/API identity remains Gridelyx/gridelyx. Actual repository metadata migration is tracked separately until verified.
