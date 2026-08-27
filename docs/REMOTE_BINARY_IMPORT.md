# Completing the Exact Private Reference Vault

The GitHub API connection used to initialise this repository can write source/text files but cannot stream the supplied ~632 MB binary payload. The repository therefore records the exact expected bytes in `vault/manifest.json` and keeps `vault/REMOTE_BINARY_IMPORT_PENDING.md` until the binary import is complete.

The JDK and LWJGL archives are intentionally split into 24 MiB ordinary Git blobs. This stays below GitHub's single-file hard limit while keeping the private repository self-contained and independent of a third-party download remaining available forever.

## Windows / PowerShell

Clone the private repository and point the importer at the folder containing the four original files supplied to the platform:

```powershell
git clone https://github.com/IAMACESIRX/minecraft-advanced-mod-development-kit.git
cd minecraft-advanced-mod-development-kit
py tools/import_binary_vault.py "C:\path\to\the\four\original\files"
py tools/hydrate_references.py
py tools/vault.py verify --all
py tools/validate_platform.py

git add vault references/upstream references/index templates/neoforge-26.2/gradle/wrapper/gradle-wrapper.jar templates/neoforge-26.2/gradlew templates/neoforge-26.2/gradlew.bat
git add -u vault/REMOTE_BINARY_IMPORT_PENDING.md
git commit -m "Import exact supplied Minecraft R&D reference vault"
git push origin main
```

The importer identifies artifacts by exact SHA-256 and byte length, not merely by filename. It therefore tolerates harmless filename differences such as browser-added `(1)` suffixes but rejects changed bytes.

`hydrate_references.py` then:

1. reconstructs and verifies the exact supplied MDK ZIP;
2. extracts it to `references/upstream/mdk-26.2/` as an immutable readable snapshot;
3. restores the official Gradle wrapper JAR and launch scripts into the live mod template;
4. regenerates archive, JDK-source and LWJGL-source/type indexes.

Once the pending marker is removed, the `Reference Vault Integrity` workflow verifies every artifact and every chunk by SHA-256.

## Storage trade-off

This deliberately makes the repository relatively large. That is a design choice for permanent private ownership of the supplied R&D references. If clone weight later becomes a problem, the same manifest can be migrated to Git LFS or private release/package storage without changing the AI-facing reference indexes or mod workspace architecture.
