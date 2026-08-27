# Gridelyx project entrypoint

Use this page when you know what you want to work on but do not yet know where it lives.

## Before coding

1. Read `../../README.md`.
2. Read `../../docs/CHAT_REQUIREMENTS_TRACEABILITY.md` and identify the relevant `CR-*` requirement(s).
3. Read `../../docs/DEPENDENCIES_AND_TOOLCHAIN.md` and `../../docs/CAPABILITY_DEPENDENCY_MATRIX.md`.
4. Check `../../docs/FEATURE_MAP.md` for current evidence state.
5. Check `../../docs/TODO.md` and GitHub Issues for active work.
6. Read `../../AGENTS.md` if using AI assistance.

## Capability navigation

- Launcher, Java/runtime acquisition, providers, instances: `../../studio/`, `../ACQUISITION_AND_RESOLUTION.md`
- Polyloader/UAL/version adaptation: `../POLYLOADER_ARCHITECTURE.md`, `../../templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/`
- Hotload/in-game IDE/AI: `../HOTLOAD_ARCHITECTURE.md`, `../INGAME_DEVELOPMENT_ENVIRONMENT.md`
- Non-Java tools/bridges: `../POLYGLOT_AND_BRIDGES.md`, `../../bridges/`, `../../native/`
- Live world editor/events: `../WORLD_EDIT_RUNTIME.md`, `../WORLD_EDITOR_ROADMAP.md`
- Multiplayer authoring: `../MULTIPLAYER_WORLD_EDIT.md`
- Models/textures/rendering/collision: `../LIVE_ASSET_EDITING.md`, `../ADVANCED_ENGINES.md`
- Physics/scene/tool-gun: advanced runtime `physics/`, `scene/`, `sandbox/`
- Bedrock: `../BEDROCK_ARCHITECTURE.md`, `../../bedrock/`, `../../native/bedrock/`
- Recording/animation/machinima: `../MACHINIMA_PRODUCTION.md`, `../../studio/production/`
- Deep patch/runtime integration: `../DEEP_INTEGRATION_ARCHITECTURE.md`
- AI/project intelligence: `../../AI_HANDOFF.md`, `../../ai/`, `../AI_CONTEXT_SYSTEM.md`

## What counts as done

An interface or source file is not enough. Use the R0-R6 evidence model and run the relevant tests from `TESTING_AND_EVIDENCE.md`. Update requirements/dependencies/planning when a change alters project truth.

## Naming

Use **Gridelyx** as the root brand and **Gridelyx Studio** for the integrated suite. Legacy Gridelyx/VFSB identifiers are compatibility migration state; do not copy them into new code unless implementing an explicit migration/alias boundary.
