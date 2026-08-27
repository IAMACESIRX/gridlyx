# AI Mod Construction Workflow

## Input model

A mod request should be decomposed into:

- mod identifier and package;
- gameplay objective;
- common/client/server ownership;
- registries and data assets involved;
- persistent state/networking requirements;
- compatibility constraints;
- testable invariants;
- performance/security risks.

## Construction loop

`request -> API lookup -> scaffold -> smallest compiling slice -> tests -> feature slices -> GameTest -> package audit`

Do not generate an entire large mod before the first compilation. Compile after the first registry/event/network boundary is established; this catches mapping/API errors cheaply.

## Source lookup order

1. Current workspace and canonical template.
2. `references/index/` locks and archive/source indexes.
3. Hydrated supplied references in `.reference-cache/`.
4. NeoForge/Minecraft dependency sources resolved by Gradle/IDE.
5. Current official documentation when network access is available.

## Side safety

Do not load client-only classes from common/server initialization paths. Rendering, GLFW/window operations and client input belong behind client-only entry points.

## Networking

Treat packet payloads as untrusted input. Validate identifiers, ranges, entity/block ownership, permissions and server-authoritative state. Do not let a client packet directly grant items, teleport, mutate protected state or execute arbitrary commands without server checks.

## Persistence

Explicitly define where state lives, how it serializes, migration/version handling, failure recovery and whether state is world, dimension, chunk, entity, player or client-local.

## Performance

Avoid per-tick full-world scans, unbounded collections, synchronous disk/network work on the main thread, unnecessary allocations in hot render/tick paths and repeated registry/resource lookups that can be cached safely.
