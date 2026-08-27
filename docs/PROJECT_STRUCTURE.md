# Gridelyx Studio project structure

This is the target repository map. Existing paths remain valid; new implementation should converge toward these ownership boundaries instead of creating parallel subsystems.

```text
/
├─ README.md                         product entrypoint
├─ AGENTS.md                         mandatory AI/agent engineering rules
├─ AI_HANDOFF.md                     compact continuation state for future agents
├─ ai/
│  ├─ README.md                      how AI should consume the repository
│  ├─ CONTEXT.md                     compressed architecture/context
│  ├─ context-map.json               task/domain -> canonical files
│  └─ generated/                     ignored/generated repo and chunk indexes
├─ studio/
│  ├─ Cargo.toml                     native Studio workspace
│  ├─ core/                          GUI-independent launcher/instance/resolver contracts
│  ├─ desktop/                       future desktop shell/UI
│  ├─ providers/                     source + loader adapter manifests
│  ├─ schemas/                       instance/lock/project schemas
│  └─ production/                    launcher-side capture/export/project orchestration
├─ platform/                         locked ecosystem/product/version manifests
├─ templates/                        canonical generated Java mod/runtime templates
├─ mods/                             independent generated/distributable mod workspaces
├─ bedrock/                          Bedrock Add-On + Editor targets
├─ native/
│  ├─ cpp/                           Gridelyx native ABI
│  ├─ rust/                          native extension implementation/examples
│  └─ bedrock/                       Bedrock VFSB companion and adapter boundary
├─ bridges/                          Python/Go/C#/AI/sidecar protocols
├─ scripts/                          live/procedural scripts
├─ tools/
│  ├─ repo_index.py                  deterministic AI file/chunk index generator
│  ├─ ai_context_pack.py             task-scoped lightweight repository retrieval
│  ├─ studio_check.py                Studio architecture/provenance static gate
│  └─ existing build/validation tools
├─ docs/
│  ├─ PROJECT_OVERVIEW.md            canonical product architecture
│  ├─ PROJECT_STRUCTURE.md           this ownership map
│  ├─ ROADMAP.md                     staged delivery plan
│  ├─ FEATURE_MAP.md                 capability/evidence matrix
│  ├─ ACQUISITION_AND_RESOLUTION.md  download/provider/dependency policy
│  ├─ MACHINIMA_PRODUCTION.md        replay/animation/capture architecture
│  ├─ AI_CONTEXT_SYSTEM.md           AI indexing/handoff rules
│  ├─ PROJECT_PLAN.md                readiness/workstream planning
│  ├─ TODO.md                        live implementation ledger
│  └─ existing subsystem docs
├─ references/index/                 compact indexed external/reference knowledge
└─ vault/                            exact large recovery/reference material
```

## Ownership rules

### `studio/core`
May model Minecraft/loader/content/runtime concepts but must not depend on GUI frameworks. It owns deterministic instance/resolution/provenance contracts shared by desktop, CLI and AI tooling.

### `studio/desktop`
Owns UI, OS integration, authentication UX, credential-store bindings, download progress, user settings, updater and process launching. It consumes `studio/core`; it does not duplicate resolution logic.

### `studio/providers`
Contains declarative provider/loader descriptions. Network implementations must be separated per provider so terms, authentication, caching and rate-limit policy cannot bleed between providers.

### `templates/.../advanced`
Owns Java in-game creator/runtime mechanisms. It must not become the desktop launcher. Stable neutral protocols may be shared through generated schemas or bridge contracts.

### `bedrock`
Owns supported Bedrock Script/Add-On/Editor assets. It should consume Gridelyx-neutral operations, not duplicate creator logic unnecessarily.

### `native`
Owns trusted native process code and ABI boundaries. It must never become an undocumented catch-all for game executable patching.

### `ai`
Contains small canonical context and generated indexes only. Do not duplicate entire source files into AI context. Context files point to authoritative implementation/docs and record why they matter.

## Runtime data layout (outside the source checkout)

Recommended product data root:

```text
Gridelyx/
├─ config/
├─ credentials/          OS credential references only; never plaintext secrets
├─ cache/
│  ├─ blobs/             content-addressed immutable artifacts
│  ├─ metadata/          provider metadata within provider policy
│  └─ java/              managed runtimes
├─ instances/<id>/
│  ├─ instance.json
│  ├─ content.lock.json
│  ├─ game/
│  ├─ mods/
│  ├─ config/
│  ├─ saves/
│  ├─ resourcepacks/
│  ├─ shaderpacks/
│  ├─ logs/
│  ├─ screenshots/
│  ├─ recordings/
│  └─ creator-projects/
├─ production/<project-id>/
│  ├─ project.json
│  ├─ scenes/
│  ├─ timelines/
│  ├─ replays/
│  ├─ audio/
│  ├─ renders/
│  └─ exports/
└─ diagnostics/
```

Downloaded immutable binaries may be hard-linked/reflinked into instances. Mutable configs, worlds and saves must be instance-owned unless the user explicitly establishes sharing.
