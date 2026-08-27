# Gridelyx rebrand migration plan

Status: **identity selected; migration in progress**

The replacement root brand is **Gridelyx**. The integrated product suite is **Gridelyx Studio**.

Ordinary web/GitHub collision screening performed during selection found no obvious current software/game/company collision for the exact name. That is an engineering naming screen only, **not legal trademark clearance**. Public/commercial release still requires appropriate trademark, business-name, domain and social-handle checks.

## Canonical identity record

The authoritative machine-readable record is `../platform/brand.json`.

```text
ROOT_BRAND=Gridelyx
DISPLAY_NAME=Gridelyx Studio
CANONICAL_CASE=Gridelyx
REPOSITORY_SLUG=gridelyx              # desired future public slug; current GitHub repo path remains unchanged until migration step
SHORT_NAME=gridelyx
PROTOCOL_PREFIX=GLYX
PACKAGE_NAMESPACE=dev.gridelyx        # engineering target namespace; domain/trademark ownership is a separate legal concern
EXECUTABLE_NAME=gridelyx
DATA_ROOT_NAME=Gridelyx
NATIVE_LIBRARY=gridelyx_native
NATIVE_SYMBOL_PREFIX=gridelyx_
```

### Compatibility identifiers

The current bridge magic `VFSB` and existing `gridelyx_*`/`Gridelyx*` ABI/source/persisted identifiers are **legacy compatibility state**, not current branding. They must not be blindly replaced because native symbols, serialized data, bridge magic and class/file names can be compatibility boundaries.

The target future bridge magic is recorded as `GLXB`, but changing it requires a versioned bridge migration and compatibility tests.

## Why migration is staged

Retired terminology exists across:

- README and architecture documentation;
- workflow/job names;
- Java/Rust/C/C++ symbols;
- schemas and serialized project files;
- protocol names, magic values and ABI functions;
- package/module identifiers;
- generated templates/examples;
- Bedrock script filenames/content;
- issue/PR templates;
- AI context, indexes and handoffs;
- filenames/directories;
- tests that assert terminology;
- release/package metadata.

A blind search-and-replace can make the tree look renamed while breaking wire compatibility, native linkage or persisted projects. Gridelyx therefore migrates by identifier class.

## Inventory classes

Every retired-brand occurrence is classified as:

- **A — public terminology:** docs, UI, workflow/display names, examples;
- **B — project-owned source identifier:** classes, functions, modules, package metadata, tests;
- **C — persistent compatibility identifier:** schemas, config keys, instance/project data, protocol identifiers;
- **D — binary/ABI identifier:** C ABI symbols, bridge magic, IPC/native names;
- **E — filename/path:** tracked project-owned paths;
- **F — historical/external provenance:** factual historical/upstream references that must not be falsified;
- **G — generated/cache artifact:** regenerated after source migration.

A/B/E normally migrate directly after references are updated. C/D require migration/versioning analysis. F remains only where historically necessary. G is regenerated.

## Migration phases

### Phase 0 — Select — COMPLETE

- [x] Screen candidate.
- [x] Select Gridelyx.
- [x] Freeze canonical identity in `platform/brand.json`.
- [x] Record human decision in the decision ledger.

### Phase 1 — Inventory — IN PROGRESS

- [x] Identify major legacy public/source/protocol/native path families from the recursive repository tree.
- [ ] Produce a complete machine-readable retired-term occurrence inventory.
- [ ] Classify every occurrence A-G.
- [ ] Identify persisted consumers of VFSB/native names before changing them.

### Phase 2 — Public/current-tree terminology

- [ ] Rename root README/product docs to Gridelyx.
- [ ] Rename AI handoff/context/control material.
- [ ] Rename workflow/job/check display names.
- [ ] Rename community docs and issue/PR user-facing strings.
- [ ] Rename Studio/Creator/Production UI-facing strings.
- [ ] Update repository description/topics where supported.

New project-owned documentation should use Gridelyx immediately even while compatibility migration remains unfinished.

### Phase 3 — Source identifiers

Migrate project-owned classes/types/functions/constants/modules, including legacy `Gridelyx*` Java classes, Rust crate/package names, Bedrock script names, C/C++ headers/sources and tests. Compile/static/native/Bedrock checks must run after each compatibility boundary rather than one giant replacement.

### Phase 4 — Persistent/protocol/ABI migration

For every persisted/wire/ABI identifier choose one:

- direct rename because there is provably no consumer;
- versioned migration reader/writer;
- temporary compatibility alias;
- intentionally retained legacy wire token with explicit removal policy.

`VFSB` is in this class. It remains active until a Gridelyx bridge-protocol migration proves safe interoperability.

### Phase 5 — Terminology enforcement

Add and maintain:

- `platform/terminology.json` containing canonical and retired terms;
- a terminology checker;
- CI enforcement that becomes stricter as migration phases complete.

During migration, only explicitly classified compatibility/provenance occurrences may remain. After migration, public/current project terminology must contain no unexplained retired brand usage.

### Phase 6 — Regenerate and validate

- regenerate AI repository indexes/docs;
- rebuild Java/Studio/native/Bedrock targets;
- run continuity, requirements, toolchain, platform, Bedrock, native and security checks;
- re-run full-tree terminology inventory;
- inspect package/release metadata;
- test persisted/protocol migration paths.

### Phase 7 — Repository metadata

Where supported and approved:

- rename repository slug to the chosen Gridelyx slug;
- update description/topics;
- update release/package names;
- update external/community links;
- verify GitHub redirects instead of assuming them.

## Git history

A clean Gridelyx current tree and a rewritten Git history are separate operations. Removing Gridelyx from historical commits would rewrite history, invalidate commit SHAs and disrupt clones/forks. It is not part of ordinary rebranding without separate explicit approval.

## Exit criteria

The migration is complete when:

- Gridelyx identity values are canonical;
- current tracked public terminology uses Gridelyx consistently;
- every persistent/ABI legacy identifier has migrated or has an explicit compatibility exception;
- terminology CI passes at strict mode;
- AI/context indexes contain no accidental retired branding;
- Java/Studio/native/Bedrock builds and relevant runtime validation pass;
- repository metadata is updated;
- a final tracked-tree scan produces no unexplained Gridelyx occurrence.
