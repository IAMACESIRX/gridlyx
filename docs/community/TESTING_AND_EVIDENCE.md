# Testing and evidence

The project uses R0-R6 readiness because Minecraft compatibility cannot be established by compilation alone.

## Readiness

- **R0** — idea only.
- **R1** — contract/schema/interface defined.
- **R2** — compiles or deterministic static validation passes.
- **R3** — automated unit/integration tests pass.
- **R4** — headless target integration passes.
- **R5** — interactive target validation passes.
- **R6** — release-candidate packaging, migration and rollback evidence.

## Validation layers

1. Static structure, versions, provenance, requirements and build locks.
2. Formatting/lint/security scanning.
3. Unit and architecture tests.
4. Gradle/native/polyglot compilation and packaging.
5. Headless GameTest/server integration where applicable.
6. Interactive rendering/input/world/client validation.
7. Performance, chaos, migration and recovery evidence.

## Claims

A PR should not promote a feature beyond its evidence. Examples:

- A Java class compiling does not prove its Minecraft hook is correct.
- A GameTest does not prove a GPU/render path works visually.
- A Bedrock Script API implementation does not prove a native renderer adapter works.
- A compatibility abstraction does not prove arbitrary third-party mods can be cross-loaded.

## Useful commands

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/studio_check.py
python tools/repo_index.py --check
python tools/validate_platform.py
python tools/diagnose.py --static
cargo test --manifest-path studio/Cargo.toml --all-targets
```

Run subsystem-specific Java, native, Bedrock and GameTest lanes in addition to these checks when relevant.

## Failed tests

A failure is evidence, not an inconvenience to hide. Preserve the diagnostic, identify the smallest responsible layer, and update readiness if a previously supported path regresses.
