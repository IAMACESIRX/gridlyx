# AI entrypoint

For any substantial Gridelyx Studio task, read in this order:

1. `../AGENTS.md`
2. `../AI_HANDOFF.md`
3. `CONTEXT.md`
4. `context-map.json`
5. task-specific authoritative files named by the context map

Generate the deterministic repository index with:

```bash
python tools/repo_index.py
```

Generate a compact lexical context pack with:

```bash
python tools/ai_context_pack.py "your task description"
```

Do not ingest `vault/`, binaries, build outputs or every generated workspace unless the task specifically needs them. AI context files are navigation aids; implementation and validation evidence remain authoritative.
