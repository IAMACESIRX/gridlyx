# Gridelyx Studio desktop shell

The desktop shell is the user-facing launcher/manager application. It must start on a clean machine without Java installed and consume `studio/core` for instance/provider/resolution truth.

## UX goals

- **Simple mode:** instance cards, one-click create/install/update/play, automatic compatible Java/loader/dependency choices, understandable conflict repair.
- **Advanced mode:** exact component graph, Java runtime, loader version, JVM/game args, memory, environment, hashes, provenance, dependency explanation, lockfile diff and toolkit module controls.

## Planned surfaces

1. Home/library
2. New instance wizard
3. Instance details/editor
4. Unified content browser
5. Modpack/import/export manager
6. Accounts
7. Downloads/cache/storage
8. Creator workspace launcher
9. Production/machinima workspace
10. Logs/diagnostics
11. Settings/API credentials

## Service boundaries

The UI must call services for acquisition, resolver, instance store, auth, process launch, toolkit and production jobs. Do not place provider-specific HTTP or dependency-solving rules in UI components.

## Framework constraint

The implementation should use a native bootstrap (Rust-backed desktop shell is the current architectural direction) so Java can be downloaded/managed after Gridelyx starts. Final framework selection remains a deliberate implementation decision and should be recorded in an ADR before UI-specific code becomes large.
