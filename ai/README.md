# AI entrypoint

For any substantial Gridelyx Studio task, read in this order:

1. [`../AGENTS.md`](../AGENTS.md)
2. [`../AI_HANDOFF.md`](../AI_HANDOFF.md)
3. [`../docs/LINKING_POLICY.md`](../docs/LINKING_POLICY.md)
4. [`CONTEXT.md`](CONTEXT.md)
5. [`context-map.json`](context-map.json)
6. task-specific authoritative files named by the context map

## Mod-development reference surface

For Minecraft/NeoForge mod-development work, also read:

1. [`../docs/AI_MODDING_REFERENCE_CORPUS.md`](../docs/AI_MODDING_REFERENCE_CORPUS.md)
2. [`../platform/versions.json`](../platform/versions.json)
3. [`../platform/toolchain-requirements.json`](../platform/toolchain-requirements.json)
4. [`../platform/reference-sources.json`](../platform/reference-sources.json)
5. [`../vault/manifest.json`](../vault/manifest.json)
6. [`skills/neoforge-26.2-mod-development/SKILL.md`](skills/neoforge-26.2-mod-development/SKILL.md) for the pinned NeoForge 26.2 target
7. the relevant canonical Gridelyx template files under [`../templates/neoforge-26.2/`](../templates/neoforge-26.2/)

The public repository intentionally contains **routing, versions, coordinates, provenance, skills and Gridelyx-owned source instead of vendored upstream payloads**.

Hydrate the high-value official/open-source reference corpus locally with [`../tools/hydrate_ai_references.py`](../tools/hydrate_ai_references.py) and build its searchable index with [`../tools/build_reference_indexes.py`](../tools/build_reference_indexes.py):

```bash
python tools/hydrate_ai_references.py --core
python tools/build_reference_indexes.py --corpus
```

This produces an ignored local searchable corpus at `.reference-cache/index/reference-corpus.jsonl`.

Each indexed chunk carries source ID, version, resolved revision where available, roles, file hash, original path and local provenance. Agents should search this corpus before guessing a version-specific API.

Minecraft development sources produced/resolved by ModDevGradle are **local reference material only**. They may be used for exact local API/signature/behaviour lookup under the applicable terms, but must not be copied into Git, public documentation, skills or a public training corpus merely because the development toolchain can generate them.

## General project context

Generate the deterministic repository index with [`../tools/repo_index.py`](../tools/repo_index.py):

```bash
python tools/repo_index.py
```

Generate a compact lexical context pack with [`../tools/ai_context_pack.py`](../tools/ai_context_pack.py):

```bash
python tools/ai_context_pack.py "your task description"
```

Do not ingest [`../vault/`](../vault/) as though it contains binaries: it is acquisition metadata only. Do not ingest binaries, build outputs, Gradle/Maven caches or every generated workspace unless the task specifically needs local runtime evidence.

AI context files are navigation aids; implementation, schemas, official/pinned references and validation evidence remain authoritative.

## Documentation navigation discipline

Follow [`../docs/LINKING_POLICY.md`](../docs/LINKING_POLICY.md). Markdown references to resolvable files, directories and official pages should be clickable. Machine-readable/code files must remain valid syntax: do not inject Markdown links into JSON, Python, YAML, Java, Rust, C/C++ or similar formats. Keep command fences literal and provide clickable references in adjacent Markdown prose.

## Publication guardrails

Before treating a branch as publication-ready, use [`../tools/redistribution_guard.py`](../tools/redistribution_guard.py), [`../tools/history_redistribution_guard.py`](../tools/history_redistribution_guard.py), and [`../tools/reference_sources_check.py`](../tools/reference_sources_check.py):

```bash
python tools/redistribution_guard.py
python tools/history_redistribution_guard.py
python tools/reference_sources_check.py
```

The GitHub history workflow fetches public branches, tags and pull-request heads and checks reachable historical objects for prohibited archives, JAR/class/native/executable payloads and suspicious large blobs. Hydrated reference material belongs only under ignored `.reference-cache/` paths.
