# Rebrand migration plan

Status: **candidate selection**

The project is replacing its previous public product identity. The full current-tree terminology scrub begins only after the replacement name and identifiers are approved.

## Why this is staged

This repository contains more than user-facing prose. Product-owned terminology can appear in:

- README and architecture documentation;
- workflow/job names;
- Java/Rust/C/C++ symbols;
- schemas and serialized project files;
- protocol names, magic values and ABI functions;
- package/module identifiers;
- generated templates and examples;
- issue/PR templates;
- AI context, indexes and handoffs;
- filenames and directory names;
- tests that assert terminology;
- release/package metadata;
- persisted worlds, instance locks or creator projects.

Blind search-and-replace could break compatibility or leave mixed terminology. The migration therefore inventories and classifies first.

## Brand selection requirements

A preferred replacement should:

1. have no obvious collision in current software/game/company and GitHub screening;
2. be distinctive enough for search and documentation;
3. work as a one-word root brand without requiring “Studio” to distinguish it;
4. support module names such as `<Brand> Launcher`, `<Brand> Creator`, `<Brand> Runtime`, `<Brand> Bridge` and `<Brand> Production`;
5. have a usable repository slug and short internal/protocol namespace;
6. avoid implying official Mojang/Minecraft ownership or compatibility guarantees;
7. remain pronounceable and visually recognizable;
8. not lock the product to voxels, Java, one loader or one edition because the architecture extends beyond those boundaries.

Ordinary web/GitHub screening is collision discovery, not legal trademark clearance. Final commercial/public branding should receive jurisdiction-appropriate trademark, business-name and domain/social-handle checks.

## Canonical identity record

Once selected, freeze these values before migration:

```text
DISPLAY_NAME=
CANONICAL_CASE=
REPOSITORY_SLUG=
SHORT_NAME=
PROTOCOL_PREFIX=
PACKAGE_NAMESPACE=
EXECUTABLE_NAME=
DATA_ROOT_NAME=
```

If a protocol/package identifier cannot safely change immediately, document its compatibility alias and removal version separately.

## Inventory classes

Every retired-brand occurrence is classified as one of:

- **A — public terminology:** documentation, UI, workflow/display names, screenshots/examples;
- **B — project-owned source identifier:** classes, functions, modules, package metadata, test names;
- **C — persistent compatibility identifier:** serialized schemas, config keys, instance/project data, protocol identifiers;
- **D — binary/ABI identifier:** C ABI symbols, frame magic, IPC/native names;
- **E — filename/path:** project-owned tracked path that should be renamed;
- **F — historical/external provenance:** upstream citations, historical records or third-party names that must not be falsified;
- **G — generated/cache artifact:** regenerated from canonical source after the migration.

A/B/E normally rename directly. C/D require migration analysis. F remains only when factual provenance requires it and should be clearly marked historical/external. G is deleted/regenerated.

## Migration phases

### Phase 0 — Select

- screen candidates;
- choose replacement name;
- freeze canonical identity record;
- create a brand decision record.

### Phase 1 — Inventory

Perform a complete tracked-tree scan for:

- exact old display name;
- root word in any casing;
- abbreviations and protocol prefixes;
- derived package/module/function names;
- workflow/release names;
- old filenames/paths.

Produce a machine-readable occurrence inventory before editing.

### Phase 2 — Public/current-tree rename

Rename:

- root README and docs;
- AI handoff/context/control material;
- workflow/job/check display names;
- issue/PR templates;
- Studio/Creator/Production UI-facing strings;
- new-module descriptions and examples;
- repository description where supported.

### Phase 3 — Source identifiers

Rename project-owned:

- classes/types/functions/constants;
- native namespaces and ABI wrappers where safe;
- Rust crates/modules where safe;
- bridge examples;
- schema titles and package metadata;
- tests and validation messages.

Run compile/static tests after each compatibility boundary rather than one giant blind replacement.

### Phase 4 — Persistent/protocol migration

For each persistent or ABI identifier choose one:

- direct rename because no external/persisted consumer exists;
- versioned migration reader/writer;
- temporary compatibility alias;
- intentionally retained legacy wire identifier with documentation explaining why.

The public project name may be completely scrubbed even if a private wire-format compatibility token must temporarily remain; such a token is technical compatibility state, not branding, and requires an explicit migration issue.

### Phase 5 — Terminology enforcement

Add:

- `platform/terminology.json` containing canonical and forbidden project-owned terms;
- `tools/terminology_check.py` scanning tracked text paths;
- CI step that fails on retired terminology outside an explicit provenance/migration allowlist.

The allowlist must be narrow and documented.

### Phase 6 — Regenerate and validate

- regenerate AI repository indexes/docs;
- rebuild Java/Studio/native/Bedrock targets;
- run continuity, platform, Bedrock, native and security checks;
- rescan the tracked tree;
- inspect release/package metadata;
- test migration paths for persisted data/protocols.

### Phase 7 — Repository metadata

Where supported and desired:

- rename repository slug;
- update description/topics;
- update release/package names;
- update external links and community references.

Repository redirects should be verified rather than assumed.

## Git history

A clean current tree and a rewritten Git history are different operations.

Normal rebrand target: **zero retired project-brand terminology in current project-owned terminology**, except explicit compatibility/provenance exceptions.

Removing the name from historical commits requires history rewriting, force-push, invalidation of commit SHAs and coordination with every clone/fork. That destructive operation is not part of the normal current-tree scrub unless separately approved.

## Exit criteria

The rebrand is complete when:

- canonical identity values are frozen;
- current tracked project-owned terminology uses the new brand consistently;
- every persistent/ABI legacy identifier has either migrated or has an explicit compatibility exception;
- terminology CI passes;
- AI/context indexes contain no accidental retired branding;
- build/runtime validation appropriate to changed identifiers passes;
- repository metadata is updated;
- a final full-tree scan produces no unexplained occurrence.
