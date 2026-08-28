# Gridelyx release notes and changelogs

Release communication follows an **evidence-first two-stage pipeline**:

1. generate deterministic change evidence from Git history;
2. optionally ask an AI adapter to rewrite that evidence for readability under a strict no-invention prompt.

AI is not the source of truth. If the deterministic input does not support a claim, the release note must not contain it.

## Deterministic changelog

```bash
python tools/generate_changelog.py --base <tag-or-commit> --head HEAD --output dist/release/changelog-candidate.md
```

The generator groups common conventional-commit prefixes but retains the original commit subject, abbreviated SHA and author. Unknown styles go to **Other** rather than being discarded.

## Release-note candidate

```bash
python tools/generate_release_notes.py dist/release/changelog-candidate.md \
  --output dist/release/release-notes-candidate.md
```

This creates a deterministic review candidate with explicit evidence/limitation sections.

## AI-assisted synthesis

AI can be enabled only when an adapter is explicitly configured:

```bash
GRIDELYX_AI_RELEASE_NOTES_COMMAND="<command>" \
python tools/generate_release_notes.py dist/release/changelog-candidate.md \
  --output dist/release/release-notes-candidate.md --ai
```

or configure:

- `GRIDELYX_AI_RELEASE_NOTES_ENDPOINT`
- optional `GRIDELYX_AI_RELEASE_NOTES_TOKEN`

The HTTP adapter contract is a POST JSON object with `system` and `evidence` strings, returning `{ "text": "...markdown..." }`.

The prompt forbids invention of features, support matrices, performance figures, issue numbers or readiness. It requires known limitations and migration notes to survive the rewrite.

## GitHub Actions

`Gridelyx Release Notes Candidate` is manual and read-only. It generates artifacts rather than publishing a release automatically. This keeps a human review gate between generated communication and public release metadata.

Repository variables/secrets used by the optional AI lane:

- variable `GRIDELYX_AI_RELEASE_NOTES_COMMAND`, or
- variable `GRIDELYX_AI_RELEASE_NOTES_ENDPOINT`, plus
- secret `GRIDELYX_AI_RELEASE_NOTES_TOKEN` when the endpoint requires authentication.

No secret is committed to the repository.

## Publishing checklist

Before promoting a candidate into a GitHub release or [`CHANGELOG.md`](../CHANGELOG.md):

1. verify commit/PR range;
2. inspect CI and target tests;
3. compare claims with [`FEATURE_MAP.md`](FEATURE_MAP.md) and capability manifests;
4. include migrations/rollback when relevant;
5. name exact Java/Bedrock/version/loader targets for runtime support claims;
6. preserve experimental/planned/framework labels;
7. remove internal/private details not intended for release.

## Future automation

When release maturity justifies it, a separate explicitly authorized workflow may open/update a release PR or GitHub release draft. It must never auto-publish AI-generated text without review.
