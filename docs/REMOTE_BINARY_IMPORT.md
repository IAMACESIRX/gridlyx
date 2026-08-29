# Dynamic Upstream Acquisition

Gridelyx no longer uses a private binary-import workflow. The repository is intended to be publishable without embedding third-party development/runtime payloads.

## Normal GitHub Actions flow

After checkout, use the repository-local dynamic toolchain action:

```yaml
- uses: actions/checkout@v7
- uses: ./.github/actions/gridelyx-toolchain
```

That action installs the locked Eclipse Temurin JDK and Gradle release into GitHub's runner/tool caches and validates the no-redistribution policy.

The subsequent Gradle build performs the remaining dependency acquisition:

```bash
cd templates/neoforge-26.2
./gradlew --no-daemon build
```

`net.neoforged.moddev` resolves the Minecraft/NeoForge development runtime and mappings. Normal Gradle dependency resolution obtains the declared Java libraries. None of those upstream payloads are copied into tracked repository paths.

## Local developer flow

A developer may use a compatible local JDK/Gradle installation or install the versions recorded in `platform/versions.json`. The template's launcher shim invokes the system Gradle executable, which then populates the developer's normal Gradle caches.

Validate the repository policy at any time:

```bash
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
```

For an optional, pinned NeoForge MDK comparison checkout:

```bash
python tools/hydrate_references.py --mdk
```

That clone is stored beneath `.reference-cache/` and is intentionally not copied into the tracked template or `references/upstream/` tree.

## What must never be committed

Do not commit:

- Minecraft client/server JARs or decompiled source trees;
- NeoForge installer/runtime JARs;
- JDK or Gradle distribution archives;
- LWJGL distribution/native bundles;
- Maven-resolved dependency JARs;
- binary chunks or reconstructed upstream archives;
- hydrated upstream reference checkouts.

The `.gitignore` blocks these normal cases, and `tools/redistribution_guard.py` independently scans the Git index so a force-added prohibited payload still fails validation.

## Why this model

This keeps Gridelyx source reproducible while making the distinction between **dependency acquisition** and **dependency redistribution** explicit. The repository records where a dependency comes from and which version is expected; the upstream provider or package resolver supplies the actual bytes to each developer/CI runner.
