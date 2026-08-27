# Project structure

The public product identity is under rebrand. This is the target repository ownership map; new implementation should converge toward these boundaries instead of creating parallel subsystems.

```text
/
├─ README.md                              product entrypoint
├─ AGENTS.md                              mandatory AI/agent engineering rules
├─ AI_HANDOFF.md                          compact continuation state
├─ COMMUNITY.md                           community entrypoint
├─ CONTRIBUTING.md                        contribution policy
├─ CODE_OF_CONDUCT.md                     participation expectations
├─ SUPPORT.md                             support/triage routing
├─ SECURITY.md                            security reporting/policy
├─ ai/
│  ├─ README.md                           AI consumption guide
│  ├─ CONTEXT.md                          compressed architecture/context
│  ├─ AI_ORGANISATION.md                  capability-scoped AI roles
│  ├─ DRIFT_MITIGATION.md                 continuity/drift controls
│  ├─ work-state.json                     active machine-readable work state
│  ├─ decision-ledger.json                durable architecture decisions
│  ├─ assumption-ledger.json              unresolved assumptions
│  ├─ context-map.json                    task/domain -> canonical files
│  └─ generated/                          ignored/generated repo/chunk indexes
├─ studio/
│  ├─ Cargo.toml                          native desktop/control workspace
│  ├─ core/                               GUI-independent instance/resolver contracts
│  ├─ desktop/                            desktop shell/UI target
│  ├─ providers/                          source + loader adapter manifests
│  ├─ schemas/                            instance/lock/project schemas
│  └─ production/                         capture/export/project orchestration
├─ platform/
│  ├─ versions.json                       locked platform versions
│  ├─ capabilities.json                   compact capability manifest
│  ├─ chat-requirements.json              retained conversation requirements graph
│  ├─ polyloader-capabilities.json        loader-neutral capability state
│  ├─ world-editor-capabilities.json      world editor capability state
│  ├─ bedrock-capabilities.json           Bedrock capability state
│  └─ master-build.lock.json              canonical Gradle contract lock
├─ templates/                             canonical generated Java mod/runtime templates
├─ mods/                                  independent generated/distributable mod workspaces
├─ bedrock/                               Bedrock Add-On + Editor targets
├─ native/
│  ├─ cpp/                                project-owned native ABI
│  ├─ rust/                               native extension implementation/examples
│  └─ bedrock/                            Bedrock native companion/adapter boundary
├─ bridges/                               Python/Go/C#/AI/sidecar protocols
├─ scripts/                               live/procedural scripts
├─ tools/
│  ├─ repo_index.py                       deterministic AI file/chunk index
│  ├─ ai_context_pack.py                  task-scoped repository retrieval
│  ├─ continuity_check.py                 AI/project continuity validation
│  ├─ chat_requirements_check.py          retained-scope/evidence-path validation
│  ├─ studio_check.py                     launcher/provider architecture gate
│  └─ build/runtime/validation utilities
├─ docs/
│  ├─ PROJECT_PLAN.md                     durable program-control plan
│  ├─ PROJECT_OVERVIEW.md                 product architecture
│  ├─ PROJECT_STRUCTURE.md                this ownership map
│  ├─ CHAT_REQUIREMENTS_TRACEABILITY.md   complete retained conversation scope
│  ├─ ROADMAP.md                          staged delivery plan
│  ├─ FEATURE_MAP.md                      capability/evidence matrix
│  ├─ TODO.md                             live implementation ledger
│  ├─ DEEP_INTEGRATION_ARCHITECTURE.md    L0-L8 additive integration model
│  ├─ ACQUISITION_AND_RESOLUTION.md       acquisition/provider/dependency policy
│  ├─ POLYLOADER_ARCHITECTURE.md          loader-neutral runtime architecture
│  ├─ WORLD_EDIT_RUNTIME.md               live world-authoring architecture
│  ├─ BEDROCK_ARCHITECTURE.md             Bedrock target architecture
│  ├─ MACHINIMA_PRODUCTION.md             recording/animation/production architecture
│  ├─ AI_CONTEXT_SYSTEM.md                AI indexing/context rules
│  └─ community/                          onboarding, architecture, evidence, glossary
├─ references/index/                      compact indexed external/reference knowledge
└─ vault/                                 exact large recovery/reference material
```

## Ownership rules

### `studio/core`
Models Minecraft/loader/content/runtime concepts without depending on GUI frameworks. It owns deterministic instance/resolution/provenance contracts shared by desktop, CLI and AI tooling.

### `studio/desktop`
Owns UI, OS integration, authentication UX, credential-store bindings, downloads, settings, updater, process launching, patch/runtime composition and user-facing recovery.

### `studio/providers`
Contains declarative provider/loader descriptions. Network implementations remain provider-specific so terms, authentication, caching and rate-limit policy cannot bleed between providers.

### `templates/.../advanced`
Owns Java in-game creator/runtime mechanisms: UAL, scripting, bytecode/JVM/native bridges, world/asset editing, rendering, scene/physics and live-development infrastructure. It must not become the desktop launcher.

### `bedrock`
Owns supported Bedrock Script/Add-On/Editor assets and target adapters. Neutral project contracts should be reused where practical; target capability differences remain explicit.

### `native`
Owns trusted native process/in-process code and ABI boundaries. Deep binary/runtime patching is governed by `docs/DEEP_INTEGRATION_ARCHITECTURE.md`; it must use explicit version/fingerprint/provenance/rollback records rather than undocumented one-off mutations.

### `bridges`
Owns language-neutral and sidecar communication examples/protocol implementations. A connected bridge does not gain authority automatically; capabilities and server permissions govern operations.

### `platform`
Owns machine-readable project truth that multiple tools/CI lanes consume: versions, capabilities, build locks and retained requirement paths. Changes here should be reviewed as contract changes.

### `ai`
Contains compact context, navigation and project-control state rather than copied source trees. Context material points to authoritative source and never upgrades an assumption into a fact by repetition.

### `docs/community`
Owns newcomer/contributor orientation. Community documentation should teach the evidence/readiness model and must not turn planned architecture into support claims.

## Runtime data layout

The final data-root name will be chosen during the rebrand. Conceptually:

```text
<product-data-root>/
├─ config/
├─ credentials/              OS credential references only
├─ cache/
│  ├─ blobs/                 content-addressed immutable artifacts
│  ├─ metadata/              provider metadata under provider policy
│  ├─ java/                  managed runtimes
│  └─ derived-runtimes/      verified patched/augmented derivatives
├─ instances/<id>/
│  ├─ instance.json
│  ├─ content.lock.json
│  ├─ capability.lock.json   target/patch/toolkit graph (planned)
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

Downloaded immutable binaries may be hard-linked/reflinked into instances. Mutable configs, worlds and saves must be instance-owned unless the user explicitly establishes sharing. Patched/derived runtime artifacts must remain reproducible from verified base artifacts plus recorded patch/capability manifests.
