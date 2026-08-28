# Gridelyx identity migration completion record

Status: **current-tree migration complete; protocol/native boundary advanced to v2**

The canonical root brand is **Gridelyx** and the integrated product suite is **Gridelyx Studio**. `platform/brand.json` is authoritative for machine-readable identity.

## Canonical identity

```text
ROOT_BRAND=Gridelyx
DISPLAY_NAME=Gridelyx Studio
CANONICAL_CASE=Gridelyx
REPOSITORY_SLUG=gridelyx
SHORT_NAME=gridelyx
PROTOCOL_PREFIX=GLYX
PACKAGE_NAMESPACE=dev.gridelyx
EXECUTABLE_NAME=gridelyx
DATA_ROOT_NAME=Gridelyx
NATIVE_LIBRARY=gridelyx_native
NATIVE_SYMBOL_PREFIX=gridelyx_
BRIDGE_MAGIC=GLXB
BRIDGE_PROTOCOL_VERSION=2
NATIVE_TRANSPORT_MAGIC=GLXM
NATIVE_ABI_VERSION=2
```

## Migration result

The current project-owned tree has been migrated across:

- public documentation and community entrypoints;
- Java/Rust/C/C++ symbols and package metadata;
- native library and executable targets;
- Bedrock scripts, manifests and runtime identifiers;
- schemas and serialized project metadata;
- binary bridge and shared-memory protocol identities;
- workflow/job names;
- AI context, handoff, decision and assumption state;
- tests and validation tooling;
- tracked filenames and paths.

The protocol/native migration is deliberately represented as a breaking version boundary rather than an ambiguous in-place rename. Gridelyx bridge protocol v2 uses `GLXB`; the native shared-memory transport uses `GLXM`; the native ABI exports `gridelyx_*` symbols at ABI version 2.

## Enforcement

`platform/terminology.json` and `tools/terminology_check.py` enforce Gridelyx identity across the whole current project-owned tree, including tracked path names and textual content. Build/cache/binary locations are excluded from source terminology scanning because they are generated or non-text artefacts.

Any future compatibility adapter for a pre-v2 consumer must be explicit, versioned, isolated from the canonical API and covered by interoperability/rollback evidence. It must not redefine the current project identity.

## Validation requirements

Before release, run:

```bash
python tools/terminology_check.py
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/toolchain_requirements_check.py
python tools/feature_planning_check.py
python tools/bedrock_check.py
python tools/validate_platform.py
cargo test --manifest-path studio/Cargo.toml --all-targets
cargo test --manifest-path native/rust/Cargo.toml
cmake -S native -B build/native -DCMAKE_BUILD_TYPE=Release
cmake --build build/native --config Release
cd templates/neoforge-26.2 && ./gradlew --no-daemon -Penable_advanced_engines=true spotlessCheck check build
```

The advanced polyglot smoke lane additionally exercises the Gridelyx runtime smoke suite, bridge round trip, reload orchestrator routing and production timeline primitives.

## Repository metadata

The source tree is now Gridelyx-first. Repository slug/description/topics are independent GitHub metadata operations and may be changed separately without altering source compatibility.

## Git history

This migration applies to the current tree. Historical commits are not rewritten; rewriting history would invalidate commit SHAs and disrupt existing clones/forks. Historical identity is therefore a Git-history concern rather than current-source terminology.

## Exit criteria

The migration is considered complete when:

- `platform/brand.json` exposes only canonical Gridelyx identity;
- native ABI v2 and GLXB/GLXM protocol identities are built and validated;
- Bedrock and Java bridges use the v2 identifiers;
- strict terminology scanning passes;
- Java, Studio, native and Bedrock validation passes;
- no current project-owned path or text reintroduces the retired identifier.
