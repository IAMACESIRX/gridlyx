# Binary Vault Import Pending

The repository control/source layer has been written through the connected GitHub API. The exact supplied binary vault is prepared separately because this connector accepts UTF-8 repository writes but cannot stream the ~632 MB local binary payload.

The prepared complete repository contains the exact MDK ZIP, NeoForge installer JAR, JDK archive and LWJGL archive. Large inputs are split into 24 MiB Git-safe parts and are described by `vault/manifest.json` with per-part and reconstructed SHA-256 values.

Remove this marker only after those vault files have been pushed. Then `Reference Vault Integrity` will enforce byte-for-byte verification in CI.
