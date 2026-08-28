# Gridelyx documentation and presentation toolchain

This lane is project/developer infrastructure, not a Minecraft runtime dependency.

## Pinned documentation stack

| Component | Version | Purpose | Acquisition |
|---|---:|---|---|
| MkDocs | `1.6.1` | Static technical documentation site | PyPI via `requirements-docs.txt` |
| Material for MkDocs | `9.7.7` | Documentation theme/navigation/search | PyPI via `requirements-docs.txt` |
| mkdocs-swagger-ui-tag | `0.8.0` | Embedded Swagger UI for OpenAPI | PyPI via `requirements-docs.txt` |
| Mermaid | `11.17.2` | Browser rendering for diagrams-as-code | pinned jsDelivr URL in `mkdocs.yml` |
| Shields.io | service | README/site parameter badges | `img.shields.io` static/dynamic badge URLs |
| GitHub CLI (`gh`) | runner/developer supplied, version not frozen | Manual label-taxonomy synchronization | GitHub-hosted runner or developer install |

Version evidence for the Python packages lives in `requirements-docs.txt`; Mermaid is pinned in `mkdocs.yml`.

## Local site build

```bash
python -m venv .venv-docs
# activate the environment for your platform
python -m pip install -r requirements-docs.txt
mkdocs build --strict
```

Preview:

```bash
mkdocs serve
```

## Interactive API documentation

`docs/api/gridelyx-development-api.openapi.yaml` is OpenAPI 3.1. `mkdocs-swagger-ui-tag` renders it inside [`docs/api/index.md`](api/index.md). The API spec is a capability contract and must not be interpreted as proof that every route exists on every runtime target.

## Diagrams as code

Canonical diagram sources are under `docs/diagrams/*.mmd`. GitHub can render Mermaid code fences directly; the documentation site renders Mermaid using the pinned browser library.

Do not paste screenshots as the only source for architectural diagrams. If a raster/export is useful for social or presentation use, retain the corresponding text diagram source.

## Hero graphic

`docs/assets/gridelyx-hero.svg` is a project-owned vector concept map. SVG is used so it remains source-controlled, scalable and inspectable without a binary design-tool dependency.

## Shields badges

Badges should expose concise parameters or real external state. Static badges may describe stable facts such as product identity or pinned toolchain versions. Dynamic badges should be backed by an actual workflow/API; never hard-code a green status badge for an evidence state that can fail.

## GitHub label tooling

`tools/sync_labels.py` requires the GitHub CLI only when actually applying the taxonomy. `--dry-run` validates/generates commands without `gh`. The manual workflow carries `issues: write`; normal documentation/build workflows do not.

## Supply-chain rule

Documentation dependencies are pinned separately from game/runtime dependencies. They must not leak into Gradle/Minecraft classpaths, native bundles or launcher instance manifests.
