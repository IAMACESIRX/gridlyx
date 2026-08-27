# Minecraft Advanced Mod Development Kit

Private R&D platform for **AI-assisted, non-MCreator Minecraft mod engineering**.

The repository is intentionally two systems in one:

1. **Construction/test environment** — a reproducible NeoForge 26.2 + Java 25 development template, isolated mod workspaces, CI builds, static validation, optional GameTests and JAR inspection.
2. **Reference environment** — immutable version locks, upstream MDK snapshot, source/API indexes, and a vault format that can preserve the exact tool/reference archives supplied to the project without putting them on a mod's compile/runtime classpath.

## Canonical toolchain

| Component | Locked version |
|---|---:|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.67 |
| ModDevGradle | 2.0.144 |
| Gradle wrapper | 9.2.1 |
| Java | Eclipse Temurin / Adoptium 25.0.4+7 |
| LWJGL reference bundle | 3.4.1 |

The NeoForge MDK is the authoritative build layout. Vendor/reference archives are **not** automatically injected as `implementation` dependencies. This prevents duplicated Minecraft/LWJGL runtimes and classpath collisions.

## Create a mod workspace

```bash
python tools/new_mod.py spectral_tools "Spectral Tools" com.iamacesirx.mods.spectraltools
cd mods/spectral_tools
./gradlew build
```

Windows:

```powershell
py tools/new_mod.py spectral_tools "Spectral Tools" com.iamacesirx.mods.spectraltools
cd mods/spectral_tools
.\gradlew.bat build
```

## Validate the whole platform

```bash
python tools/validate_platform.py
python tools/build_all.py --include-template
```

## Reference vault

```bash
python tools/vault.py verify
python tools/vault.py reconstruct --all --output .reference-cache/raw
python tools/vault.py extract --all --output .reference-cache/extracted
```

`references/index/` is designed for fast AI lookup. The vault is for exact recovery and deep inspection.

## Repository zones

- `templates/` — canonical mod templates.
- `mods/` — AI/human-generated standalone mods.
- `experiments/` — throwaway or comparative R&D.
- `references/upstream/` — readable upstream snapshots.
- `references/index/` — compact searchable indexes and version locks.
- `references/proposals/` — supplied design/build proposals preserved verbatim.
- `vault/` — immutable supplied artifact bytes or Git-safe chunks.
- `tools/` — scaffolding, validation, build and vault utilities.
- `docs/` — architecture, AI workflow, testing and source policy.

See `AGENTS.md` before an AI changes code.
