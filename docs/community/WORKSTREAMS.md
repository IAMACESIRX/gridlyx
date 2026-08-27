# Gridelyx community workstreams

Use these workstreams to choose a contribution area without needing to understand the whole repository first.

| Workstream | Primary CRs | Starting points |
|---|---|---|
| Launcher / instances / acquisition | CR-025, CR-033 | `studio/`, `docs/ACQUISITION_AND_RESOLUTION.md` |
| Polyloader / version compatibility | CR-004, CR-010, CR-011, CR-012 | `docs/POLYLOADER_ARCHITECTURE.md`, advanced `polyloader/` + `runtime/` |
| AI / MCP / project intelligence | CR-007, CR-008, CR-009 | `ai/`, advanced `ai/`, `tools/repo_index.py` |
| Live world editor / events | CR-013, CR-014, CR-022 | advanced `worldedit/`, world-edit docs |
| Terraria world systems | CR-015 | `docs/TODO.md`, world-edit/transmutation foundations |
| Microgeometry / rendering / collision | CR-016, CR-017, CR-029 | advanced `assets/`, `collision/`, `render/` |
| Sandbox physics / scene tools | CR-018, CR-019 | advanced `physics/`, `sandbox/`, `scene/` |
| In-game IDE / non-Java SDK | CR-020, CR-021, CR-023 | advanced `clientdev/`, `scripting/`, `bridges/`, `native/` |
| Bedrock | CR-024 | `bedrock/`, `native/bedrock/`, Bedrock docs |
| Mod analysis / forking | CR-026 | `tools/fork_mod.py`, `tools/bytecode_diff.py` |
| Machinima / production | CR-027 | `docs/MACHINIMA_PRODUCTION.md`, advanced `production/`, `studio/production/` |
| Dynamic worlds / teleport | CR-028 | advanced `world/`, worldgen/datagen foundations |
| Deep integration / patching | CR-030 | `docs/DEEP_INTEGRATION_ARCHITECTURE.md` |
| Community / documentation | CR-031 | `COMMUNITY.md`, `docs/community/` |
| Gridelyx migration | CR-032 | `platform/brand.json`, `platform/terminology.json`, `docs/REBRAND_PLAN.md` |
| Toolchain / reproducibility | CR-033 | `docs/DEPENDENCIES_AND_TOOLCHAIN.md`, `platform/toolchain-requirements.json` |

Every contribution must preserve the retained CR scope, declare new prerequisites, and avoid promoting evidence state beyond what was actually tested.
