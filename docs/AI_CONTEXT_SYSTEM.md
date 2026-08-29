# AI context and repository-intelligence system

## Goal

An AI should not need to ingest the entire repository blindly for every task. Gridelyx uses layered context: a tiny orientation layer, task/domain maps, deterministic file/chunk indexes, then authoritative source files fetched only when relevant.

## Canonical AI entry order

1. [`AGENTS.md`](../AGENTS.md) — safety, engineering and validation contract.
2. [`AI_HANDOFF.md`](../AI_HANDOFF.md) — current product state, active workstreams and continuation rules.
3. [`ai/CONTEXT.md`](../ai/CONTEXT.md) — compact architecture vocabulary and invariants.
4. `ai/context-map.json` — maps task domains to canonical files.
5. Generated `build/ai/repo-index.json` — file metadata/hash/area index.
6. Generated task context pack from `tools/ai_context_pack.py`.
7. Authoritative implementation/docs referenced by the pack.

Never treat generated indexes or summaries as more authoritative than the source paths they reference.

## Deterministic repository index

`tools/repo_index.py` walks text/code files while excluding `.git`, build outputs, caches, binaries and large vault payloads. It records:

- path;
- byte size;
- SHA-256;
- extension;
- inferred area;
- first Markdown heading when applicable;
- chunk line ranges and normalized search terms for text files.

This provides a cheap navigation surface and change detector. It is not a semantic embedding database.

## Task-scoped context packs

`tools/ai_context_pack.py <query>` performs lightweight lexical scoring over canonical docs/code and emits the highest-scoring file/line chunks. The goal is fast triage before a stronger semantic/vector retrieval system is used.

Example queries:

```bash
python tools/repo_index.py
python tools/ai_context_pack.py "launcher Java Fabric dependency resolution"
python tools/ai_context_pack.py "machinima camera timeline offline render"
python tools/ai_context_pack.py "Bedrock VFSB native bridge"
```

## Context map

`ai/context-map.json` is deliberately small and curated. It answers: “For this kind of task, what should I read first?” It should contain stable domains such as launcher, acquisition, loaders, creator, Bedrock, production, native, world editor, AI and validation.

## Handoff discipline

[`AI_HANDOFF.md`](../AI_HANDOFF.md) should contain only durable continuation information:

- product definition;
- current architecture boundaries;
- active/next milestones;
- important exact protocol names;
- known incomplete areas;
- validation commands;
- source/provenance constraints.

Do not dump conversation transcripts into handoff files. Decisions belong in [`docs/DECISIONS.md`](DECISIONS.md)/ADRs; tasks belong in [`docs/TODO.md`](TODO.md); capabilities belong in [`docs/FEATURE_MAP.md`](FEATURE_MAP.md).

## Fact/status vocabulary

AI-generated engineering notes should distinguish:

- **FACT** — directly evidenced in source/tool output.
- **DERIVED** — follows mechanically from facts.
- **ASSUMPTION** — intentionally adopted but not validated.
- **HYPOTHESIS** — proposed mechanism requiring test.
- **DESIGN CHOICE** — selected architecture among alternatives.
- **UNKNOWN** — missing information.
- **REQUIRES VALIDATION** — implementation exists but runtime evidence is insufficient.

## Index freshness

Indexes are build artifacts. They should be regenerated when files change and should carry the current Git commit SHA when available. CI should eventually fail if an explicitly committed index is stale; until then the preferred approach is to generate indexes on demand rather than commit large duplicates.

## Vector/semantic extension

The existing local vector-index/AI-autodoc work can consume the deterministic index as its manifest. A future semantic index should store chunk IDs keyed by `(commit_sha, path, start_line, end_line, sha256)` so embeddings can be reused for unchanged chunks and invalidated precisely when source changes.

Recommended retrieval pipeline:

```text
query
 -> curated context-map domains
 -> deterministic lexical candidates
 -> semantic/vector rerank (optional)
 -> source fetch
 -> answer/change plan
 -> validation evidence
 -> handoff/TODO update if architecture changed
```
