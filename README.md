# Minecraft Advanced Mod Development Kit

Private R&D platform for **AI-assisted, non-MCreator Minecraft mod engineering**. It combines a reproducible NeoForge build/test environment with an on-demand reference vault and isolated advanced-engine research surfaces.

## Canonical toolchain

| Component | Locked version |
|---|---:|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.67 |
| ModDevGradle | 2.0.144 |
| Gradle | 9.2.1 |
| Java | Temurin 25.0.4+7 |
| Spotless | 8.10.0 |
| Checkstyle | 14.0.0 |
| ASM | 9.10.1 |
| LWJGL reference | 3.4.1 |

## Workspaces: multiple mods, multiple JARs

```bash
python tools/new_mod.py spectral_tools "Spectral Tools" com.iamacesirx.mods.spectraltools
python tools/new_mod.py world_lab "World Lab" com.iamacesirx.mods.worldlab
python tools/workspace.py list
python tools/workspace.py build spectral_tools
python tools/workspace.py build world_lab
```

Every `mods/<mod_id>` directory is an independent NeoForge project. Dependency graphs, generated resources, run directories and JARs remain isolated so multiple mods can be constructed and validated side by side.

## Quality, data and diagnostics

```bash
python tools/validate_platform.py
python tools/diagnose.py --static
python tools/workspace.py quality spectral_tools
python tools/workspace.py datagen spectral_tools
```

Inside a workspace, `./gradlew spotlessApply` formats Java and `./gradlew spotlessCheck check build` runs the normal quality/build gate. The template includes a modular main mod class, global registry controller, creative-tab anchor, automated `LanguageProvider`, generated-resource configuration, loader manifest template, localisation/asset blueprints and codec/worldgen bootstrap helper.

## Advanced engines

Advanced bytecode/native/render/network systems live in `src/advanced` and are disabled by default:

```bash
python tools/workspace.py build spectral_tools --advanced
```

An advanced build can produce the normal mod JAR, a full `-advanced.jar`, and a Java instrumentation `-agent.jar`. The current foundation includes bounded worker pools, multi-threaded state syncing, dynamic data snapshots, ASM generation, Instrumentation/hotswap, trusted dynamic Mixin config registration, Panama memory, shared-memory IPC, Netty pipeline injection, LWJGL GPU buffers, PoseStack interception and two-bone IK.

See `docs/CAPABILITY_MATRIX.md`: target-specific Mixin redirects, bytecode patches, render hooks and worldgen entries still require exact mapping/API validation rather than being guessed.

## AI / cloud development

- `AGENTS.md` defines repository rules for AI coding agents.
- `.github/workflows/copilot-setup-steps.yml` is GitHub's recognised Copilot cloud-agent setup workflow.
- `.github/copilot-setup-steps.toml` is the platform's additional agent-tuning manifest.
- `.devcontainer/devcontainer.json` provides the Codespaces/devcontainer environment.
- CodeQL, issue forms and PR templates are under `.github/`.

## Reference vault

`references/index/` is the fast lookup layer; `vault/` is exact recovery/deep-inspection storage. The large supplied binary payload remains represented by exact checksums/chunk manifests until its deterministic local import is pushed. While `vault/REMOTE_BINARY_IMPORT_PENDING.md` exists, do not assume those remote binary bytes are hydrated.

```bash
python tools/vault.py verify --all
python tools/reference_lookup.py search GLFW
```

## Architecture

Read `docs/ARCHITECTURE_WORKFLOW.md`, `docs/SECURITY_MODEL.md`, `docs/ADVANCED_ENGINES.md`, `docs/DATAGEN_AND_ASSETS.md`, and `docs/LICENSING_REQUIREMENTS.md` before changing cross-cutting platform behaviour.
