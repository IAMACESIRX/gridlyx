# Live World Edit Runtime

## Data plane

The editor works in palette-resolved 16x16x16 section arrays. `BlueprintSectionCompiler` slices an arbitrary
`StructureBlueprint` across chunk/section boundaries and overlays it onto snapshots of already-generated chunks.
This is the MCEdit-style path for modifying existing terrain without requiring regeneration.

`NbtStructureBlueprintLoader` reads standard compressed or uncompressed structure NBT into a neutral blueprint.
Palette entries are canonicalised as block-state strings. Block NBT compounds are preserved as metadata for the
server integration adapter.

## Parallel blitting

Workers may read immutable snapshots, copy arrays, build procedural matrices and compute `SectionDelta` objects.
Workers must not mutate a live Minecraft chunk. `ServerEditScheduler` is drained from the authoritative server tick
and is the only commit path.

## Lighting policy

Bulk structural manipulation calls `WorldMutationSink.applyWithoutLighting`. This intentionally avoids per-block
lighting churn during the edit. The transaction records dirty sections and either calls one explicit lighting
reconciliation after the batch or leaves the sections in `MANUAL_RECONCILE` state for a later controlled pass.

`MANUAL_RECONCILE` is a development/performance option, not permission to save a permanently inconsistent world.
The Minecraft adapter must reconcile light, heightmaps, POI/block entities, save flags and client chunk state before
considering an edit durable.

## Dynamic Event and Structure Matrix

`DynamicEventStructureMatrix` binds a structure blueprint to a real-time predicate and activation anchor. Meteor,
citadel, corruption, restoration and scripted environment events can therefore produce normal section plans and use
the same transactional path as interactive edits.

## Overlays and transmutation

`OverlayBuffer` is a sparse sub-voxel paint/material grid independent of block storage. It can drive decals, editor
masks, density fields or future micro-voxel rendering without manufacturing thousands of Minecraft block states.

`ProgressiveTransmutationStateMachine` provides a global generation/progress state for staged world conversion and
rollback.

## Volumetric preview

`VolumetricMatrixStream` is the real-time density/material frame bus. `ClientVolumetricBridge` retains only the newest
frame so rendering backpressure cannot indefinitely queue obsolete previews. The exact 26.2 renderer adapter remains
a target-integration task.
