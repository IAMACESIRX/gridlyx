# Changelog

All notable Gridelyx changes should be summarized here or generated into a release candidate from Git history. This file follows an evidence-first variant of Keep a Changelog: entries should describe observable project changes and link to detailed evidence when the distinction between framework and target support matters.

## [Unreleased]

### Added

- Gridelyx stakeholder/documentation presentation layer: hero SVG, 3-bullet value proposition, Shields badges, architecture/user-journey/impact-effort diagrams as code, portfolio Kanban, documentation site scaffold and interactive OpenAPI documentation.
- Machine-readable issue/PR label taxonomy and manual least-privilege label synchronization workflow.
- Documentation-driven marketing and release-communication architecture.
- Acquisition-only public-repository dependency policy in [`vault/manifest.json`](vault/manifest.json): Minecraft, NeoForge, JDK, Gradle, LWJGL and Java libraries are resolved dynamically from official/package-manager channels instead of being mirrored in Git.
- Repository-local dynamic GitHub Actions toolchain bootstrap for the locked Temurin JDK and Gradle release.
- Git-index redistribution guard that rejects tracked upstream JARs, class files, archives, native binaries, installers, chunk payloads and hydrated upstream reference trees.
- Optional pinned NeoForge MDK hydration into ignored `.reference-cache/` for comparison/provenance without redistribution.

### Changed

- Documentation and stakeholder communication are treated as maintained project infrastructure rather than disconnected promotional copy.
- The former binary-reference-vault/import design is superseded by provider/version/revision metadata plus on-demand acquisition and local caches.
- Reference lookup/index generation operates on ignored dynamically hydrated material rather than committed upstream archives.

### Removed

- Binary vault importer/reconstructor tooling and the remote-binary-import pending marker.

### Known incomplete work

- GitHub repository metadata rename to `IAMACESIRX/gridlyx` remains tracked separately.
- Clean-machine/empty-cache public CI validation remains required once GitHub-hosted Actions execution is available.
- Many Gridelyx runtime capabilities remain at planned/framework/validation stages as recorded in [`docs/FEATURE_MAP.md`](docs/FEATURE_MAP.md).
