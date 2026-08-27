# Immutable Artifact Vault

Do not hand-edit files in this directory.

`manifest.json` records the exact supplied bytes. Artifacts above the repository-safe threshold are split into deterministic 24 MiB parts. Reconstruction is a byte concatenation followed by SHA-256 verification.

```bash
python tools/vault.py verify --all
python tools/vault.py reconstruct --all --output .reference-cache/raw
```
