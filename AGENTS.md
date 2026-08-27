# AI engineering contract

This repository is a private Minecraft R&D and advanced mod-development platform. AI agents may construct complete Java/NeoForge mods, but generated code is not trusted merely because it compiles.

## Required workflow

1. Identify the target `mods/<mod_id>` workspace or create a new one with `tools/new_mod.py`.
2. Read `platform/versions.json` and relevant `references/index/` entries before guessing external APIs.
3. Keep normal gameplay code in `src/main`; use `src/advanced` only for mechanisms that genuinely require bytecode/native/GPU/IPC/network interception.
4. Use registries and datagen rather than duplicated hard-coded resource state where appropriate.
5. Run `python tools/validate_platform.py` and `python tools/diagnose.py --static`.
6. Run Spotless, Checkstyle/check, build, and applicable GameTests.
7. Review generated resources and built JAR contents.
8. Record uncertainty and target-specific assumptions in code/docs/issues.

## Architecture rules

- `templates/` is canonical scaffolding; `mods/` contains independent distributable workspaces.
- Reference/vault material must not leak into normal `implementation` classpaths.
- Multiple mods must remain independently buildable and must not share writable runtime state by accident.
- Global registries own registration; feature modules consume registered holders rather than creating parallel registries.
- Generated resources go to `src/generated/resources` and must be deterministic.

## Advanced-engine rules

Advanced engines are disabled by default. Every native, bytecode, instrumentation, Mixin, Netty, IPC or direct GPU change needs an explicit lifecycle and failure path. Bytecode/Mixin targets must lock exact class/method descriptors and fail closed on mapping drift. Do not block Netty event loops or Minecraft render threads. Keep worker queues bounded. Validate all native-memory/IPC lengths. Do not attach agents to unrelated JVMs.

A target-specific Mixin redirect or ASM patch must be derived from the exact Minecraft/NeoForge mappings/reference source. Never invent descriptors from memory.

## Security and provenance

Never commit credentials, tokens or personal data. Treat generated source as untrusted until reviewed. Third-party code/assets require licence and provenance review even in a private repository. Preserve NeoForge MDK template licensing separately from the licence chosen for each generated mod.

## Definition of done

A change is done only when the relevant formatting/lint/build/test gates pass and the resulting architecture remains replaceable, bounded, diagnosable and version-aware.
