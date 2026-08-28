# Gridelyx labels and filtering

Labels are for **operational filtering**, not architectural truth. The retained CR ledger, R0-R6 evidence, Feature Decision Packets, roadmap and decision ledger remain authoritative.

Canonical taxonomy: `../platform/label-taxonomy.json`.

## Label dimensions

- `type:*` — feature, bug, architecture, docs, research, security.
- `status:*` — backlog, ready, doing, blocked, verifying, done.
- `edition:*` — Java or Bedrock target.
- `area:*` — launcher, Polyloader, creator, world, rendering, physics, multiplayer, AI, native, production, project-control.
- `risk:*` — high-risk or experimental work.
- `evidence:*` — coarse R0-R1, R2-R3, R4-R5 or R6 evidence band.

A substantial feature issue should normally have one type, one status, at least one area, an evidence band and the relevant edition/target labels.

## Saved-query patterns

```text
is:open label:status:doing
is:open label:status:blocked
is:open label:status:ready label:edition:java
is:open label:status:ready label:edition:bedrock
is:open label:risk:high
is:open label:status:verifying
is:open label:type:docs
is:open label:area:ai
```

## Synchronizing the taxonomy

`tools/sync_labels.py` reconciles the machine taxonomy through the GitHub CLI. The manual `Sync Gridelyx Labels` workflow has `issues: write` permission and exists specifically to avoid giving ordinary build workflows mutation authority.

Local usage:

```bash
python tools/sync_labels.py --repo IAMACESIRX/minecraft-advanced-mod-development-kit
```

After the repository is renamed, use `IAMACESIRX/gridlyx`.

## Filtering discipline

Do not use labels to claim target compatibility. A `edition:bedrock` issue may still be R0/R1. Likewise, `status:done` means the issue's declared acceptance target is complete; it does not automatically make the broader CR release-ready.
