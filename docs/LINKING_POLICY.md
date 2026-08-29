# Gridelyx documentation linking policy

Status: **canonical documentation-navigation rule**

Gridelyx documentation must be navigable by humans and AI agents without forcing either to search manually for a referenced file, tool, manifest, schema, directory, upstream repository or documentation page.

## Core rule

When Markdown prose names a resolvable repository target, make the visible reference a clickable relative link.

Examples:

- [`../platform/versions.json`](../platform/versions.json)
- [`../tools/redistribution_guard.py`](../tools/redistribution_guard.py)
- [`../AGENTS.md`](../AGENTS.md)
- [`../templates/neoforge-26.2/`](../templates/neoforge-26.2/)
- [`../.github/workflows/markdown-linkify.yml`](../.github/workflows/markdown-linkify.yml)

When Markdown prose names an external source, provider, repository or documentation page, link to the authoritative destination rather than leaving a bare URL when practical.

Examples:

- [NeoForged documentation](https://docs.neoforged.net/)
- [NeoForge source](https://github.com/neoforged/NeoForge)
- [ModDevGradle source](https://github.com/neoforged/ModDevGradle)
- [Gradle documentation](https://docs.gradle.org/)

## File types covered

The rule is extension-agnostic. If a referenced target exists in the repository, documentation should make it navigable. Common examples include:

- `.md` documentation;
- `.py` tools;
- `.json` manifests, schemas and state;
- `.yml` / `.yaml` workflows and configuration;
- `.toml` configuration;
- `.java`, `.kt`, `.groovy` and Gradle source/build files;
- `.rs`, `.c`, `.cpp`, `.h`, `.hpp` native source;
- `.js` / `.ts` scripts;
- `.xml`, `.properties`, `.txt`, `.svg` and other tracked project files;
- tracked directories used as subsystem entrypoints.

## Executable and machine-readable files

Do **not** insert Markdown link syntax into Python, JSON, YAML, TOML, Java, Rust, C/C++, Gradle or other executable/machine-readable formats merely to make a path clickable. That would corrupt the file's semantics.

Inside those formats:

- keep URLs as valid URL strings;
- keep repository paths as valid path strings;
- use explicit fields such as `docs_url`, `source_url`, `repository_url`, `location`, `path`, or equivalent where the schema calls for them;
- make the corresponding human/AI documentation entry clickable in Markdown;
- preserve machine-readable provenance so tools can resolve the same destination programmatically.

## Code and command fences

Fenced code remains literal and copy/paste safe. Markdown links do not render as links inside a code fence.

When a command block references a repository tool or file, the surrounding prose should provide a clickable reference whenever practical.

Example:

Use [`../tools/history_redistribution_guard.py`](../tools/history_redistribution_guard.py):

```bash
python tools/history_redistribution_guard.py
```

## Generated tree diagrams

Tree diagrams and other preformatted code blocks may contain literal filenames because hyperlinks cannot render inside those blocks. Provide clickable navigation immediately before/after the diagram or in the ownership/navigation sections that explain it.

## Automation

Canonical enforcement tool: [`../tools/markdown_linkify.py`](../tools/markdown_linkify.py).

Canonical workflow: [`../.github/workflows/markdown-linkify.yml`](../.github/workflows/markdown-linkify.yml).

Check without mutation:

```bash
python tools/markdown_linkify.py --check
```

Apply deterministic fixes:

```bash
python tools/markdown_linkify.py --fix
```

The checker enforces two properties:

1. a resolvable repository/page target in Markdown prose should be represented as a link;
2. an existing relative Markdown link should resolve to a tracked/current repository file or directory.

## AI behavior

An AI agent reading a Markdown file should prefer the embedded destination over filename inference. If a link and visible label disagree, resolve the destination and report the inconsistency rather than guessing.

An AI editing documentation should preserve this policy and should not introduce a new bare path when a stable repository target exists.

A version-specific external API/source reference should point to the official/current or explicitly pinned source defined by [`../platform/reference-sources.json`](../platform/reference-sources.json) when available.
