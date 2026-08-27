# Getting started

## What this repository is

The project is building a cross-edition Minecraft platform that combines launcher/instance management, loader/content resolution, live creator tooling, world editing, scripting/native extension planes, multiplayer authoring and machinima/production.

It is not one monolithic mod. Different capabilities live in desktop, Java, Bedrock, native, bridge and project-control planes.

## Choose your path

### I want to build a normal Java mod

1. Read `AGENTS.md` and `docs/AI_MOD_WORKFLOW.md`.
2. Create or select `mods/<mod_id>`.
3. Use the canonical template and generated-resource workflow.
4. Run format/lint/static/build tests.
5. Add GameTests or client validation when behavior requires them.

### I want to work on launcher/instance management

Start with `docs/PROJECT_OVERVIEW.md`, `docs/ACQUISITION_AND_RESOLUTION.md`, `studio/core` and `studio/providers`.

### I want to work on live creator/world systems

Start with `docs/WORLD_EDIT_RUNTIME.md`, `docs/INGAME_DEVELOPMENT_ENVIRONMENT.md`, `docs/MULTIPLAYER_WORLD_EDIT.md`, `docs/HOTLOAD_ARCHITECTURE.md` and the advanced Java source set.

### I want to work on Bedrock

Start with `docs/BEDROCK_ARCHITECTURE.md`, `bedrock/`, `native/bedrock/` and the target capability manifest. Do not assume Java and Bedrock APIs are equivalent.

### I want to work on loaders/runtime internals

Read `docs/POLYLOADER_ARCHITECTURE.md`, `docs/DEEP_INTEGRATION_ARCHITECTURE.md` and `docs/ADVANCED_VALIDATION.md` before modifying bootstrap, bytecode or native paths.

### I want to work on recording/animation/machinima

Read `docs/MACHINIMA_PRODUCTION.md`, the production schema/source and roadmap.

## Project status

Use `docs/FEATURE_MAP.md` for evidence/readiness, `docs/TODO.md` for live work and `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` for retained founding scope. Architecture prose alone does not prove that a feature currently works in-game.

## AI-assisted contribution

AI contributors follow the same evidence rules as human contributors. Start with `AGENTS.md` and `AI_HANDOFF.md`; use `ai/context-map.json` and repository indexing rather than ingesting unrelated large trees.
