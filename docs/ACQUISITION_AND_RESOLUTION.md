# Acquisition, dependency resolution and provenance

## Principle

Gridelyx Studio is a resolver and client, not an unofficial mirror. It obtains metadata and files through official or explicitly authorized channels, records provenance, verifies integrity and refuses to bypass provider/author distribution restrictions.

This principle applies at **two distinct layers**:

1. **Source/build layer:** the public Gridelyx Git repository contains project-owned source, dependency coordinates/version locks, provider metadata and acquisition logic. It does not contain upstream Minecraft/NeoForge/JDK/Gradle/LWJGL/Maven payloads merely to make builds self-contained.
2. **End-user runtime layer:** Gridelyx may acquire and cache artifacts locally for an authorized user's selected instance when the provider's supported channel and terms permit that acquisition. Those local cache bytes remain runtime/user state and are not republished from the Gridelyx source repository.

Canonical source-repository acquisition policy is recorded in [`../vault/manifest.json`](../vault/manifest.json) and enforced by [`../tools/redistribution_guard.py`](../tools/redistribution_guard.py).

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
   -> immutable local cache commit
   -> instance materialization
   -> lockfile + provenance evidence
```

A failed verification never promotes a temporary blob into the shared local cache.

## Build/development acquisition

For the canonical NeoForge development lane, Gridelyx does not reimplement dependency downloads that supported tooling already handles:

- GitHub Actions installs the locked Temurin JDK and Gradle release dynamically through [`.github/actions/gridelyx-toolchain/action.yml`](../.github/actions/gridelyx-toolchain/action.yml).
- NeoForge ModDevGradle resolves the Minecraft/NeoForge development runtime and mappings into local/runner Gradle caches.
- Gradle/Maven resolves LWJGL, ASM, GraalVM Polyglot, JUnit, ArchUnit and other declared libraries.
- An optional pinned NeoForge MDK source/reference checkout may be hydrated into ignored `.reference-cache/` for comparison/provenance; it is not copied into tracked source.

The repository records provider/version/revision/resolver truth so clean environments can reproduce acquisition without redistributing the acquired payloads from Git.

## Minecraft versions and libraries

The Java Edition version catalogue is discovered dynamically from Mojang launcher metadata. The selected version JSON is authoritative for client/server artifact URLs, libraries, assets, launch arguments and Java requirements when those fields are present. Snapshots, historical releases and unusual versions are therefore data-driven rather than maintained in a hand-edited list.

Minecraft binaries are downloaded from Mojang/Microsoft upstream URLs referenced by authoritative metadata for legitimate local use. Gridelyx does not redistribute them as project-owned artifacts or commit them to the source repository.

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

Built-ins use official Fabric Meta, Quilt Meta, Forge distribution/Maven and NeoForge Maven/services. A future or legacy loader is added through the same adapter interface. Gridelyx may import an explicit existing launcher profile, but must not invent loader coordinates from filenames alone.

A loader installer/runtime being downloaded locally for a supported operation does not imply that installer/runtime may be copied into this source repository or a Gridelyx release artifact.

## Content providers

### Modrinth

Use the production API for project search, compatible version filtering and dependency metadata. Required, optional, incompatible and embedded relationships are retained in Gridelyx's graph. Authentication is only requested when the selected API operation requires it.

### CurseForge

Use only the supported third-party API with an approved API key. Provider data and files must be handled under the current CurseForge API terms. If an author has disabled third-party distribution, Gridelyx does not construct a bypass URL or scrape the website; it explains that the artifact must be obtained through an allowed channel/imported by the user if legally permitted.

### Local/user import

Local JARs/archives can be imported without a network provider. Gridelyx records SHA-256, source=`local-import`, detected metadata and unknown licence state. It must not automatically republish those files when exporting packs or copy them into the Gridelyx source repository.

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
- no writable instance file shares the immutable cache inode if the game/mod is expected to mutate it;
- cache roots remain outside the tracked Git repository.

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

A public source build should fail visibly when an official dependency cannot be resolved; it must not fall back to a checked-in shadow copy of that dependency.
