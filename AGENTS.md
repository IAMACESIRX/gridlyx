# Gridelyx Studio AI engineering contract

Gridelyx Studio is a private cross-edition Minecraft launcher, instance/content manager, creator toolkit, advanced runtime R&D platform and machinima/production suite. AI-generated code is not trusted merely because it compiles.

## Mandatory context workflow

1. Read `AI_HANDOFF.md`, `ai/CONTEXT.md` and the relevant domain in `ai/context-map.json`.
2. Read `platform/versions.json` and relevant `references/index/` entries before guessing external APIs.
3. For broad tasks, generate `build/ai/repo-index.json` with `python tools/repo_index.py`; use `tools/ai_context_pack.py` to identify task-relevant source before scanning unrelated trees.
4. Treat authoritative source/CI/runtime evidence as stronger than AI summaries or generated indexes.
5. Update `docs/TODO.md`, feature/readiness docs or decisions when an architectural change materially changes future work.

## Repository ownership

- `studio/` owns desktop-independent launcher/instance/provider/resolver contracts and future desktop orchestration.
- `templates/` is canonical Java scaffolding; `mods/` contains independent distributable/generated workspaces.
- `bedrock/` owns supported Bedrock Add-On/Editor targets.
- `native/` owns trusted native ABI/companion code.
- `ai/` contains compact navigation/context, never duplicated full repository dumps.
- `references/index/` is the compact reference layer; `vault/` is exact recovery/deep-inspection storage and should not be scanned by default.

## Launcher / acquisition rules

- The Gridelyx desktop application must be able to start without Java installed. Java is resolved per Java Edition instance.
- “Any loader” means an extensible adapter contract. Never invent unknown loader versions, Maven coordinates, launcher arguments or metadata.
- Prefer Mojang launcher metadata for Minecraft/version/library/runtime truth; official loader metadata/Mavens for loaders; Modrinth and authorized CurseForge APIs for content.
- CurseForge access must use an approved API key/current API terms and must never bypass author third-party-distribution opt-out.
- Never scrape around a provider outage/authentication failure when a supported API/channel is required.
- Every downloaded artifact gets a local SHA-256; verify upstream hashes/signatures when available and retain provenance.
- Imported local files are not assumed redistributable.
- Dependency resolution must preserve required/optional/incompatible/embedded semantics and explain selection/rejection decisions.
- Writable instance state must remain isolated; immutable hash-addressed artifacts may be deduplicated deliberately.

## Java/mod engineering workflow

1. Identify target `mods/<mod_id>` workspace or create one with `tools/new_mod.py`.
2. Keep normal gameplay code in `src/main`; use `src/advanced` only for mechanisms genuinely requiring bytecode/native/GPU/IPC/network interception or Studio runtime infrastructure.
3. Use registries/datagen instead of duplicated hard-coded resource state where appropriate.
4. Run `python tools/validate_platform.py` and `python tools/diagnose.py --static`.
5. Run Spotless, Checkstyle/check, build and applicable GameTests.
6. Review generated resources and built JAR contents.
7. Record uncertainty and target-specific assumptions in code/docs/issues.

## Advanced-engine rules

Advanced engines are disabled by default. Every native, bytecode, instrumentation, Mixin, Netty, IPC or direct GPU change needs explicit lifecycle/failure paths. Bytecode/Mixin targets must lock exact descriptors and fail closed on mapping drift. Do not block Netty event loops or Minecraft render threads. Keep worker queues bounded. Validate native-memory/IPC lengths. Do not attach agents to unrelated JVMs.

A target-specific Mixin redirect or ASM patch must be derived from exact target mappings/reference source. Never invent descriptors from memory.

## Bedrock rules

Use supported Script/Add-On/Editor APIs whenever they can express the capability. Preview APIs are version-pinned and isolated. Native companions consume Gridelyx-neutral VFSB frames behind explicit adapter boundaries. Do not make undocumented Bedrock executable patching a universal product dependency or present it as stable across versions.

## Machinima / production rules

- Production timeline/project data is neutral and non-destructive; Java/Bedrock adapters declare concrete capabilities.
- Store exact timing as rational frame/tick time, not only floating-point seconds.
- Replay/production projects reference exact source instance/content locks when compatibility matters.
- Renderer/audio passes are only advertised after target-specific evidence.
- External encoders such as FFmpeg are replaceable/provenance-recorded executables; never shell-concatenate untrusted arguments.
- Multiplayer production controls must respect server authority/permissions and must not bypass visibility/anti-cheat boundaries.

## Security and provenance

Never commit credentials, tokens or personal data. Treat generated source, mods and imported archives as untrusted until reviewed. Sanitize archive paths before extraction. Third-party code/assets require licence/provenance review even in a private repository. Preserve upstream/template licensing separately from Gridelyx/project licensing.

## Fact/status discipline

Use FACT / DERIVED / ASSUMPTION / HYPOTHESIS / DESIGN CHOICE / UNKNOWN / REQUIRES VALIDATION when useful. A capability is only as mature as its recorded R0-R6 evidence in project docs/tests.

## Definition of done

A change is done only when relevant formatting/lint/build/test gates pass and the architecture remains replaceable, bounded, diagnosable, version-aware and provenance-aware. Interface presence alone is not runtime support.
