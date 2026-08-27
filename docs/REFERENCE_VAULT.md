# Reference Vault

The vault exists to preserve supplied R&D inputs independently of build dependencies.

## Supplied artifacts

- NeoForge 26.2 MDK archive.
- NeoForge `26.2.0.67` installer.
- Eclipse Temurin/Adoptium JDK `25.0.4+7` Windows HotSpot archive.
- LWJGL `3.4.1` distribution bundle.

Every artifact is recorded in `vault/manifest.json` by size and SHA-256. Large archives are split into deterministic parts so no Git object has to exceed GitHub's normal single-file limit.

## Exact reconstruction

```bash
python tools/vault.py verify
python tools/vault.py reconstruct --all --output .reference-cache/raw
```

The reconstructed file must match the original SHA-256 before it is accepted.

## Extraction

```bash
python tools/vault.py extract --all --output .reference-cache/extracted
```

Extraction is local and ignored by Git. This keeps hundreds of megabytes of binaries/natives out of normal AI edits while retaining exact recoverability.

## Minecraft source policy

Do not commit decompiled Mojang game source into this repository. Use the licensed development toolchain/mappings to materialise development sources locally when needed. Commit original mod code, public API references, mappings/metadata permitted for redistribution, and your own derived documentation—not a mirrored proprietary game codebase.
