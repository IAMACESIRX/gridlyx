# Live World Editor Roadmap

## R0 - Framework present

Palette-indexed section arrays, parallel blitting, NBT blueprints, dynamic event matrix, overlays, transmutation state,
scene graph, embedded IDE models, direct compiler, AI bridge, Netty framing, replication culling and revision consensus.

## R1 - Target integration

Implement exact NeoForge 26.2 adapters for loaded-section snapshots, palette resolution, direct section mutation,
heightmap/POI/block-entity handling, lighting reconciliation, resource reloads, native client screens, key mappings and
render-event hooks.

## R2 - Editor operations

Selection volumes, copy/cut/paste, rotate/mirror, replace/filter, undo/redo journals, brush tools, sub-voxel paint,
structure library and procedural event authoring.

## R3 - Multiplayer

Permissions, server validation, per-section revision conflicts, bounded transaction fragmentation, ACK/resend,
near-player replication culling, late-join snapshots and rollback after rejected edits.

## R4 - Performance

Benchmarks for 4K/64K/1M block edits, worker scaling, main-thread commit budgets, lighting reconciliation cost, packet
compression, preview backpressure and renderer upload limits.

## R5 - Durability

Crash recovery journal, save/reload verification, relight correctness, block-entity preservation, world backup hooks and
cross-version migration tests.

## Definition of done

A capability is not production-ready until it passes compilation, deterministic unit checks, headless GameTests,
interactive client validation, multiplayer validation and save/reload verification where applicable.
