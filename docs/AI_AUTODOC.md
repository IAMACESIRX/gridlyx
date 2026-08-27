# AI-driven auto-documentation

The repository has two documentation layers:

1. `tools/autodoc.py` is deterministic. It renders the capability manifest exactly and is safe to require in CI.
2. `tools/ai_autodoc.py` creates a model-neutral, provenance-aware documentation request and can pass it to an explicitly configured local/sidecar AI adapter.

The AI layer is intentionally **provider-independent**. It does not embed an API key, cloud vendor or unrestricted network call into the repository. A local model, Python AI sidecar, MCP-connected service or other trusted adapter can accept JSON on stdin and return Markdown on stdout.

Example:

```bash
python tools/ai_autodoc.py --context build/ai-doc-context.json
python tools/ai_autodoc.py --output build/AI_PLATFORM_REPORT.md \
  --provider-command python bridges/ai/my_documentation_adapter.py
```

The request explicitly instructs the model not to invent runtime validation, benchmarks, compatibility or API claims. Model output remains generated evidence that requires review; it does not supersede CI, GameTest, client testing or measurements.
