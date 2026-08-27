# Contributor onboarding

## Before changing code

1. Read `CONTRIBUTING.md`.
2. Read the architecture document for the subsystem you are changing.
3. Check `docs/FEATURE_MAP.md`, `docs/TODO.md` and open GitHub issues.
4. If the change affects product scope, read `docs/CHAT_REQUIREMENTS_TRACEABILITY.md`.
5. If the work touches loaders, native code, bytecode, executable patches or engine replacement, read `docs/DEEP_INTEGRATION_ARCHITECTURE.md`.
6. If AI is being used materially, follow `AGENTS.md` and `AI_HANDOFF.md`.

## Contribution lifecycle

Use the project work states:

`PROPOSED -> FRAMED -> DESIGNED -> IMPLEMENTING -> VERIFYING -> ACCEPTED`

Do not call a capability complete because an interface or placeholder exists.

## Change boundaries

Prefer one coherent subsystem change per PR. A contribution should state:

- problem and intended capability;
- target edition/version/loader/platform;
- affected ownership plane;
- assumptions and compatibility risks;
- tests actually run;
- readiness change, if any;
- rollback/migration implications.

## Deep integration

Normal APIs are preferred because they carry lower compatibility cost, but they are not the project's capability ceiling. Escalation into bytecode, JVM agents, native extensions, launch bootstrap, executable/library patching or project-owned replacement subsystems is acceptable when justified by the requirement. Such changes require exact fingerprints, provenance, validation and rollback design.

## Review expectations

Reviewers should separate:

- design correctness;
- implementation correctness;
- target compatibility;
- performance evidence;
- security/provenance;
- documentation/readiness accuracy.

A successful compile is not enough evidence for rendering, world mutation, multiplayer, native or interactive-client claims.
