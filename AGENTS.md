# AI engineering contract

Scope: the entire repository.

This is a cross-edition Minecraft launcher, instance/content manager, creator toolkit, advanced runtime R&D platform and machinima/production project. Its public product brand is currently under rebrand. AI-generated code or documentation is not trusted merely because it exists or compiles.

## Human authority

The human project owner has final authority over mission, product direction, brand, acceptable risk, publication and irreversible changes. AI/tool roles are replaceable engineering workers governed by repository evidence and explicit project controls.

## Mandatory context workflow

For non-trivial work:

1. Read `AI_HANDOFF.md`.
2. For broad, architectural or scope-affecting work, read `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` and `platform/chat-requirements.json`.
3. Read `ai/AI_ORGANISATION.md` and `ai/DRIFT_MITIGATION.md`.
4. Inspect `ai/work-state.json`, `ai/decision-ledger.json` and `ai/assumption-ledger.json`.
5. Use the relevant domain in `ai/context-map.json` rather than scanning unrelated trees.
6. Read `platform/versions.json` and relevant `references/index/` entries before guessing external APIs.
7. For broad tasks, use `tools/repo_index.py` and `tools/ai_context_pack.py` to narrow source context.
8. Treat authoritative source, schemas, CI/runtime evidence and explicit corrections as stronger than AI summaries or generated indexes.
9. Update planning/readiness/decision/assumption state when a change materially changes project truth.

## Source-of-truth discipline

When sources conflict, follow the hierarchy in `docs/PROJECT_PLAN.md` and `AI_HANDOFF.md`. Do not resolve contradictions by silently choosing the most convenient summary.

`docs/CHAT_REQUIREMENTS_TRACEABILITY.md` preserves requested project scope. A requirement may be planned or low-readiness, but it may not be silently removed or materially weakened because a normal Minecraft/loader API cannot implement it. Such a limitation changes integration depth, schedule and validation burden. Scope removal requires an explicit human-approved superseding decision.

Use FACT / DERIVED / ASSUMPTION / HYPOTHESIS / DESIGN CHOICE / UNKNOWN / REQUIRES VALIDATION when useful. A capability is only as mature as its recorded R0-R6 evidence.

## Repository ownership

- `studio/` owns desktop-independent launcher/instance/provider/resolver contracts and desktop orchestration boundaries.
- `templates/` is canonical Java scaffolding; `mods/` contains independent distributable/generated workspaces.
- `bedrock/` owns supported Bedrock Add-On/Editor targets.
- `native/` owns trusted native ABI/companion code.
- `bridges/` owns neutral sidecar/language bridge examples and protocols.
- `ai/` owns compact navigation, handoff/work state and AI continuity controls; it must not duplicate full source truth.
- `platform/chat-requirements.json` is the machine-readable retained-scope graph; its paths must remain valid.
- `references/index/` is compact reference knowledge; `vault/` is exact recovery/deep-inspection storage and should not be scanned by default.

## Launcher / acquisition rules

- The desktop application must start without requiring Java merely to launch the UI. Java is resolved per Java Edition instance.
- “Any loader” means an extensible adapter contract. Never invent unknown loader versions, Maven coordinates, launch arguments or metadata.
- Prefer Mojang launcher metadata for Minecraft/version/library/runtime truth; official loader metadata/Mavens for loaders; Modrinth and authorized CurseForge APIs for content.
- CurseForge access must respect approved API/current terms and author third-party-distribution controls.
- Never scrape around a provider outage/authentication failure when a supported API/channel is required.
- Every downloaded artifact gets a local SHA-256; verify upstream hashes/signatures when available and retain provenance.
- Imported local files are not assumed redistributable.
- Dependency resolution preserves required/optional/incompatible/embedded semantics and explains selection/rejection decisions.
- Writable instance state remains isolated; immutable hash-addressed artifacts may be deliberately deduplicated.

## Java/mod engineering workflow

1. Identify the target `mods/<mod_id>` workspace or create one with `tools/new_mod.py`.
2. Keep normal gameplay code in `src/main`; use `src/advanced` only for mechanisms genuinely requiring bytecode/native/GPU/IPC/network interception or shared runtime infrastructure.
3. Use registries/datagen instead of duplicated hard-coded resource state where appropriate.
4. Run relevant static/platform diagnostics.
5. Run formatting, Checkstyle/check, build and applicable GameTests.
6. Review generated resources and built JAR contents.
7. Record uncertainty and target-specific assumptions in code/docs/issues or `ai/assumption-ledger.json`.

## Advanced-engine rules

Advanced engines are disabled by default. Every native, bytecode, instrumentation, Mixin, Netty, IPC or direct GPU change needs explicit lifecycle and failure paths.

- Bytecode/Mixin targets lock exact descriptors and fail closed on mapping drift.
- Never invent descriptors from memory.
- Do not block Netty event loops or Minecraft render threads.
- Keep worker queues bounded.
- Validate native-memory/IPC lengths.
- Do not attach agents to unrelated JVMs.
- External scripts/tools do not gain world/server authority merely by connecting through a bridge.
- Crash-prone or untrusted execution should move to process isolation when the same-process fault domain cannot safely contain it.
- When ordinary APIs are insufficient, follow `docs/DEEP_INTEGRATION_ARCHITECTURE.md`: escalate deliberately through loader/JVM/native/bootstrap/patch/project-owned component layers with exact fingerprints, provenance, validation and rollback.

## Multiplayer/world rules

- Live world mutation is server-authoritative.
- Asynchronous workers may compute deltas, geometry, structures or simulations but commit through controlled server-thread/transaction boundaries.
- World editing must preserve rollback/recovery semantics where feasible.
- Client tools do not bypass server permissions, visibility boundaries or anti-cheat expectations.

## Bedrock rules

Use supported Script/Add-On/Editor APIs whenever they can express the capability because they are lower-maintenance integration surfaces. Preview APIs are version-pinned and isolated. Native companions consume neutral project bridge frames behind explicit adapter boundaries. If a required Bedrock capability cannot be expressed through supported APIs, deeper additive native/bootstrap/executable integration may be researched and version-gated under `docs/DEEP_INTEGRATION_ARCHITECTURE.md`; it must not be presented as universally stable across Bedrock versions.

## Machinima / production rules

- Production timeline/project data is neutral and non-destructive; Java/Bedrock adapters declare concrete capabilities.
- Store exact timing as rational frame/tick time, not only floating-point seconds.
- Replay/production projects reference exact source instance/content locks when compatibility matters.
- Renderer/audio passes are advertised only after target-specific evidence.
- External encoders are replaceable/provenance-recorded executables; never shell-concatenate untrusted arguments.

## Rebrand rules

The replacement product brand has not yet been selected.

- Use neutral project terminology in newly created control-plane material.
- Do not perform piecemeal protocol/package/ABI renames.
- After the human selects the replacement brand, follow the rebrand protocol in `ai/DRIFT_MITIGATION.md` and add a CI ban for retired terminology.
- Rewriting Git history is destructive and requires separate explicit approval; a current-tree scrub does not imply history rewriting.

## Security and provenance

Never commit credentials, tokens or personal data. Treat generated source, mods and imported archives as untrusted until reviewed. Sanitize archive paths before extraction. Third-party code/assets require licence/provenance review even in a private repository. Preserve upstream/template licensing separately from project licensing.

## Required continuity checks

Run:

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
```

These prove only that the AI/project-control and retained-scope structures are internally coherent. They do not prove Minecraft/runtime compatibility.

## Definition of done

A change is done only when relevant formatting/lint/build/test gates pass, evidence/readiness claims are synchronized, retained requirements remain accounted for, and the architecture remains replaceable, bounded, diagnosable, version-aware, recoverable and provenance-aware. Interface presence alone is not runtime support.
