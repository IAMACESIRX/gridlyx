# Acquisition, dependency resolution and provenance

## Principle

Gridelyx Studio is a resolver and client, not an unofficial mirror. It obtains metadata and files through official or explicitly authorized channels, records provenance, verifies integrity and refuses to bypass provider/author distribution restrictions.

## Acquisition pipeline

```text
user intent
   -> compatibility query
   -> provider discovery
   -> candidate versions
   -> policy filter
   -> dependency graph expansion
   -> conflict/constraint solve
   -> download plan
   -> download to temporary blob
   -> hash/signature verification
   -> immutable cache commit
   -> instance materialization
   -> lockfile + provenance evidence
```

A failed verification never promotes a temporary blob into the shared cache.

## Minecraft versions and libraries

The Java Edition version catalogue is discovered dynamically from Mojang launcher metadata. The selected version JSON is authoritative for client/server artifact URLs, libraries, assets, launch arguments and Java requirements when those fields are present. Snapshots, historical releases and unusual versions are therefore data-driven rather than maintained in a hand-edited list.

Minecraft binaries are downloaded from Mojang/Microsoft upstream URLs referenced by authoritative metadata. Gridelyx does not redistribute them as project-owned artifacts.

## Java runtime resolution

1. Read the selected version metadata.
2. If a Mojang-managed Java component/runtime is provided and valid for the host platform, prefer it for default/simple mode.
3. Detect compatible local runtimes.
4. Offer a managed Adoptium Temurin runtime as a transparent fallback/advanced choice.
5. Validate architecture and major version before launch.
6. Record exact runtime provenance in the instance lock.

Do not silently use whichever `java` happens to be first on `PATH` when a managed runtime has been resolved.

## Loader adapters

A loader adapter must provide:

- loader ID and exact version;
- supported Minecraft-version relationship;
- Java requirements/constraints if they differ;
- source/provenance endpoints;
- launcher profile or deterministic installer materialization procedure;
- required libraries and arguments;
- client/server applicability;
- validation rules and failure modes.

Built-ins use official Fabric Meta, Quilt Meta, Forge distribution/Maven and NeoForge Maven/installer sources. A future or legacy loader is added through the same adapter interface. Gridelyx may import an explicit existing launcher profile, but must not invent loader coordinates from filenames alone.

## Content providers

### Modrinth

Use the production API for project search, compatible version filtering and dependency metadata. Required, optional, incompatible and embedded relationships are retained in Gridelyx's graph. Authentication is only requested when the selected API operation requires it.

### CurseForge

Use only the supported third-party API with an approved API key. Provider data and files must be handled under the current CurseForge API terms. If an author has disabled third-party distribution, Gridelyx does not construct a bypass URL or scrape the website; it explains that the artifact must be obtained through an allowed channel/imported by the user if legally permitted.

### Local/user import

Local JARs/archives can be imported without a network provider. Gridelyx records SHA-256, source=`local-import`, detected metadata and unknown licence state. It must not automatically republish those files when exporting packs.

## Dependency solver

The solver works over explicit artifact candidates and directed edges:

- `required`: dependency must be present;
- `embedded`: treated as required for ordering but may already reside inside the parent artifact;
- `optional`: presented to policy/UI for selection;
- `incompatible`: cannot coexist in the same resolved graph.

Additional constraints include Minecraft version, loader, side, release channel, pinned versions, user exclusions, Java/runtime requirements and provider availability.

The final plan must be explainable: every chosen version has a reason and every rejected candidate can expose at least one failed constraint.

## Lockfile requirements

A content lock should contain, at minimum:

- Gridelyx lock schema version;
- Minecraft edition/version;
- loader ID/version and adapter version;
- Java runtime vendor/version/architecture/source;
- each artifact ID/display name/version/type/provider;
- canonical upstream URI or provider file/version ID;
- cryptographic hash;
- licence when known;
- dependency edges;
- user pin/override decisions;
- toolkit module set;
- resolver version and timestamp.

Secrets/API keys are never written to the lockfile.

## Downloads and cache

- bounded concurrency;
- resumable downloads when upstream supports ranges;
- per-provider rate-limit/backoff policy;
- temporary `.part` staging;
- maximum-size and disk-space checks;
- content-addressed final cache key;
- atomic rename/commit after verification;
- no writable instance file shares the immutable cache inode if the game/mod is expected to mutate it.

## Archive safety

Before extracting packs/installers:

- reject absolute paths;
- reject `..` traversal after normalization;
- reject device/special-file entries;
- enforce expanded-size/file-count limits;
- sanitize symbolic links/hard links;
- stage extraction outside the live instance;
- validate manifest before commit.

## Provider failure behavior

A provider outage or authentication failure is not permission to switch to an unofficial scrape. The resolver may use another provider only when the same artifact is legitimately published there and compatibility/provenance can be independently established.
