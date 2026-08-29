# Gridelyx AI engineering contract

Scope: the entire repository.

Gridelyx is a cross-edition Minecraft launcher, instance/content manager, creator toolkit, advanced runtime R&D platform, world editor and machinima/production project. The integrated suite is **Gridelyx Studio**. AI-generated code or documentation is not trusted merely because it exists or compiles.

## Human authority

The human project owner has final authority over mission, product direction, brand, acceptable risk, publication and irreversible changes. AI/tool roles are replaceable engineering workers governed by repository evidence and explicit project controls.

## Mandatory context workflow

For non-trivial work:

1. Read [`AI_HANDOFF.md`](AI_HANDOFF.md).
2. Read [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md) and identify affected CR IDs.
3. Read [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md) for substantial feature/architecture work.
4. Read [`ai/AI_ORGANISATION.md`](ai/AI_ORGANISATION.md) and [`ai/DRIFT_MITIGATION.md`](ai/DRIFT_MITIGATION.md).
5. Inspect [`platform/brand.json`](platform/brand.json), [`platform/repository-metadata.json`](platform/repository-metadata.json), [`platform/chat-requirements.json`](platform/chat-requirements.json), [`platform/toolchain-requirements.json`](platform/toolchain-requirements.json), work state, decision ledger and assumption ledger.
6. Use the relevant domain in [`ai/context-map.json`](ai/context-map.json) instead of scanning unrelated trees.
7. Read [`platform/versions.json`](platform/versions.json), [`vault/manifest.json`](vault/manifest.json), provider manifests and relevant reference-policy docs before guessing external APIs or acquisition paths.
8. For broad tasks, use [`tools/repo_index.py`](tools/repo_index.py) and [`tools/ai_context_pack.py`](tools/ai_context_pack.py).
9. Treat implementation, schemas, CI/runtime evidence and explicit human corrections as stronger than AI summaries/generated indexes.
10. Update planning/readiness/decision/assumption/dependency state when a change alters project truth.

## Brand rule

**Gridelyx** is the canonical root brand; **Gridelyx Studio** is the integrated suite. New project-owned names use Gridelyx. Existing compatibility-boundary identifiers are governed by the current migration/compatibility contracts. Do not introduce retired-brand identifiers and do not blindly rename versioned compatibility boundaries without migration tests.

Product/API slug remains `gridelyx`; the requested GitHub repository slug is `gridlyx` and is recorded separately in [`platform/repository-metadata.json`](platform/repository-metadata.json).

## Requirements preservation

[`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md) and [`platform/chat-requirements.json`](platform/chat-requirements.json) are the retained whole-chat scope. A future agent may not remove or materially weaken a requirement because it is difficult, outside normal Minecraft APIs, expensive, lower priority or not currently target-validated. Such discoveries change integration level, schedule and evidence burden. Scope removal needs explicit human approval recorded in the decision ledger.

## Feature decision protocol

Substantial features and architecture changes use [`docs/FEATURE_DECISION_FRAMEWORK.md`](docs/FEATURE_DECISION_FRAMEWORK.md) and [`docs/templates/FEATURE_EVALUATION_TEMPLATE.md`](docs/templates/FEATURE_EVALUATION_TEMPLATE.md).

Required analysis includes:

- W5x5x5 repeated Who/What/When/Where/How/Why and inverse Who-not/What-isn't/When-isn't/Where-isn't/How-not/Why-isn't interrogation;
- task decomposition and project-values alignment;
- cost/resource diagnostics and 10m/10h/10d/10mo/1y/5y/10y horizon analysis;
- opportunity cost, regret minimisation and reversible/irreversible classification;
- risk register, inversion, second-order effects and pre-mortem;
- Eisenhower, overlap/Venn, brainstorming and first-principles analysis;
- benchmarking with current verification before relying on external behaviour;
- Feynman explanation, MVP and timeboxing;
- asymmetric risk, working backward and Pareto/80-20;
- Critical Path Method, Cynefin and Kanban state;
- required evidence, rollback/migration and unresolved assumptions.

This is diagnostic/planning machinery. It must not be used to silently discard a retained feature because its cost is high. Machine contract: [`platform/feature-analysis.schema.json`](platform/feature-analysis.schema.json).

## Source-of-truth discipline

When sources conflict, follow [`docs/PROJECT_PLAN.md`](docs/PROJECT_PLAN.md) and [`AI_HANDOFF.md`](AI_HANDOFF.md). Do not choose the most convenient summary.

Use FACT / DERIVED / ASSUMPTION / HYPOTHESIS / DESIGN CHOICE / UNKNOWN / REQUIRES VALIDATION when useful. Capability maturity cannot exceed its recorded R0-R6 evidence.

## Repository ownership

- [`studio/`](studio/) — launcher/instance/provider/resolver contracts and desktop orchestration boundaries.
- [`templates/`](templates/) — canonical Java scaffolding/advanced runtime; [`mods/`](mods/) — independent distributable/generated workspaces.
- [`bedrock/`](bedrock/) — supported Bedrock Add-On/Editor targets.
- [`native/`](native/) — trusted native ABI/companion code.
- [`bridges/`](bridges/) — neutral sidecar/language bridge examples and protocols.
- [`ai/`](ai/) — compact navigation/handoff/work state/continuity controls, not duplicated source truth.
- [`references/`](references/) — Gridelyx-authored policy/navigation only; hydrated upstream checkouts and local indexes belong under ignored `.reference-cache/`.
- [`vault/`](vault/) — metadata-only upstream acquisition manifest; it is **not** a binary vault.
- [`platform/`](platform/) — versions, brand/repository metadata, capabilities, requirements, feature-analysis and toolchain manifests.

## Dependency/toolchain and public-repository rules

[`docs/DEPENDENCIES_AND_TOOLCHAIN.md`](docs/DEPENDENCIES_AND_TOOLCHAIN.md), [`platform/toolchain-requirements.json`](platform/toolchain-requirements.json) and [`vault/manifest.json`](vault/manifest.json) are canonical.

- Never invent a version for an unpinned tool.
- Tool installation is not proof that a feature works.
- Optional language/native/production tooling is capability-specific, not universally required.
- **Never commit upstream JARs, class files, distribution archives, installers, native binaries, decompiled Minecraft source, Maven caches, JDK/Gradle distributions or hydrated upstream source trees.**
- Minecraft/NeoForge development artifacts are resolved by the supported NeoForge/Gradle toolchain into local or runner caches.
- JDK and Gradle are dynamically installed in CI through [`/.github/actions/gridelyx-toolchain/action.yml`](.github/actions/gridelyx-toolchain/action.yml); local developers may use compatible installed toolchains.
- LWJGL, ASM, GraalVM, JUnit, ArchUnit and other Java libraries are package-manager dependencies, not vendored reference bundles.
- Optional NeoForge MDK comparison material is hydrated only to `.reference-cache/upstream/mdk-26.2` from the pinned official revision.
- `.gitignore` is not the only control: [`tools/redistribution_guard.py`](tools/redistribution_guard.py) must pass against the actual Git index.
- Do not recreate the deleted `tools/vault.py`, `tools/import_binary_vault.py`, binary chunk scheme or `REMOTE_BINARY_IMPORT_PENDING.md` design unless the human owner explicitly reverses the public-repository policy.
- Before release claims, presently unpinned Python/Rust/CMake/compiler/Go/.NET/encoder policies need supported-version evidence.

## Launcher / acquisition rules

- The desktop application must start without Java merely to open the UI. Java is resolved per Java Edition instance.
- “Any loader/version” means extensible versioned adapters. Never invent loader versions, Maven coordinates, launch arguments or metadata.
- Prefer [Mojang/Piston metadata](https://piston-meta.mojang.com/mc/game/version_manifest_v2.json) for Minecraft/version/library/runtime truth; official loader metadata/Mavens for loaders; [Modrinth](https://docs.modrinth.com/api/) and authorized [CurseForge APIs](https://docs.curseforge.com/rest-api/) for content.
- CurseForge access must respect approved API/current terms and author third-party-distribution controls.
- Never scrape around a provider outage/auth failure where a supported API/channel is required.
- Every acquired artifact gets local SHA-256 where appropriate; verify upstream hashes/signatures when available and retain provenance.
- Imported local files are not assumed redistributable.
- Dependency resolution preserves required/optional/incompatible/embedded semantics and explains decisions.
- Writable instance state stays isolated; immutable hash-addressed artifacts may be deliberately deduplicated in user-local Gridelyx storage.
- Acquiring an artifact for an authorized user/runtime does not grant permission to publish that artifact from the Gridelyx source repository.

## Java/mod engineering workflow

1. Identify/create the target `mods/<mod_id>` workspace using the conventions under [`mods/`](mods/) and [`templates/`](templates/).
2. Keep ordinary gameplay code in `src/main`; use `src/advanced` for shared advanced mechanisms such as bytecode/native/GPU/IPC/network interception/runtime infrastructure.
3. Use registries/datagen rather than duplicate hard-coded resource state where appropriate.
4. Run static/platform diagnostics.
5. Run formatting, Checkstyle/check, build and applicable GameTests.
6. Review generated resources and built JAR contents.
7. Record target/version assumptions and unresolved API behavior.

## Advanced-engine rules

Advanced engines remain opt-in and version/capability gated.

- Bytecode/Mixin targets lock exact descriptors/fingerprints and fail closed on mapping drift.
- Never invent descriptors from memory.
- Do not block Netty event loops or Minecraft render threads.
- Worker queues are bounded.
- Native-memory/IPC lengths are validated.
- Do not attach agents to unrelated JVMs.
- Crash-prone/non-cooperative execution moves to process isolation when same-process containment is insufficient.
- Deep changes preserve a verified base, patch/overlay graph and rollback.

## Non-Java modification rules

Gridelyx intentionally supports external modification through GraalJS/GraalPy, Python, Go, C#, Rust/C++, MCP, Netty/web/IPC/shared-memory/native and Bedrock adapters. Each uses explicit capabilities/permissions. A bridge connection never grants implicit server/world authority.

## Multiplayer/world rules

- Live world mutation is server-authoritative.
- Async workers may compute section deltas/geometry/structures/simulations but commits cross controlled server-thread/transaction boundaries.
- Bulk section mutation must reconcile lighting, heightmaps, POI, block entities, persistence and client state.
- World editing preserves rollback/recovery where feasible.
- Multiplayer edit channels require authorization, bounded transactions, revision/consensus handling and replication culling.

## Geometry/render/physics rules

Microgeometry, curved/slanted shapes and custom hitboxes must keep authoring geometry, render geometry and collision representation explicit. Use `VoxelShape` where sufficient; deeper collision/renderer augmentation is allowed when justified. Client rendering work must respect render-thread/context ownership and measure batching/culling/performance before support claims. Multiplayer physics truth remains server-side.

## Bedrock rules

Use supported Script/Add-On/Editor APIs when sufficient. Preview APIs are version-pinned and isolated. Native companions consume neutral Gridelyx bridge operations behind explicit adapters. Unsupported required capabilities may escalate to deeper additive integration under exact fingerprints and rollback; do not present undocumented native/executable behavior as stable across Bedrock versions.

## Machinima / production rules

- Production timeline/project data is neutral and non-destructive.
- Exact timing uses rational frame/tick time, not only floating-point seconds.
- Projects reference exact source instance/content locks when compatibility matters.
- Renderer/audio passes are advertised only after target evidence.
- External encoders are replaceable/provenance-recorded executables and are invoked with structured, bounded arguments.

## Security and provenance

Never commit credentials, tokens or personal data. Treat generated source, mods and imported archives as untrusted until reviewed. Sanitize archive paths before extraction. Third-party code/assets require licence/provenance review. Preserve upstream/template licensing separately from project licensing. Deep integration must not bypass authentication, entitlement, DRM, anti-cheat or platform security controls.

For public-repository dependency handling, provenance records may point at upstream artifacts but must not copy prohibited payloads into the source tree. Runtime/download caches stay outside Git.

## Required continuity gates

The executable gates are [`tools/continuity_check.py`](tools/continuity_check.py), [`tools/chat_requirements_check.py`](tools/chat_requirements_check.py), [`tools/toolchain_requirements_check.py`](tools/toolchain_requirements_check.py), [`tools/feature_planning_check.py`](tools/feature_planning_check.py), [`tools/terminology_check.py`](tools/terminology_check.py), [`tools/hydrate_references.py`](tools/hydrate_references.py), and [`tools/redistribution_guard.py`](tools/redistribution_guard.py).

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/toolchain_requirements_check.py
python tools/feature_planning_check.py
python tools/terminology_check.py
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
```

These prove control-plane consistency, not Minecraft runtime compatibility.

## Definition of done

A change is done only when relevant formatting/lint/build/test gates pass, evidence/readiness and requirements/dependency/planning paths are synchronized, the no-redistribution acquisition policy remains intact, and the architecture remains replaceable, bounded, diagnosable, version-aware, recoverable and provenance-aware. Interface presence alone is not runtime support.
