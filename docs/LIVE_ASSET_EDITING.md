# Gridelyx Live Asset Editing

## Goal

The live asset plane provides Hytale-style authoring ergonomics inside a running Minecraft client without
pretending vanilla's baked model/atlas lifecycle is dynamically mutable by default.

The architecture keeps authoring state independent from the target renderer and adds a target-specific GPU
adapter at the final boundary.

## Runtime model flow

```text
Voxel editor / AI tool
  -> MeshAsset
  -> DynamicModelRegistry
  -> VertexOverridePipeline
  -> Minecraft render adapter
  -> render-thread GPU upload
  -> live world preview
```

`MeshAsset` stores arbitrary interleaved vertex data plus an index buffer. `DynamicModelRegistry` assigns
monotonic revisions and emits bounded dirty notifications. `VertexOverridePipeline` resolves an authored mesh
before the target renderer consumes geometry.

The renderer adapter remains version-specific because Minecraft's model baking, render state and buffer APIs
change across versions. That adapter must never make the core model registry depend on a concrete
`net.minecraft` class.

## Runtime texture flow

```text
Paint tool / AI texture operation
  -> RGBA patch
  -> DynamicTextureRegistry
  -> revisioned texture surface
  -> render-thread upload queue
  -> dynamic texture / atlas indirection
```

The core supports whole-surface replacement and rectangular patches. A target adapter should prefer a dynamic
texture indirection or dedicated Gridelyx atlas over mutating vanilla's baked atlas in place. This makes
rollback, hot replacement and resource ownership substantially easier.

## Editor workspace

`VoxelEditorWorkspace` stores the version-neutral selection/tool state for:

- selection;
- sculpting;
- per-vertex movement;
- texture painting;
- UV editing;
- transform operations.

The existing scene graph and translate/rotate/scale gizmo layer can own object-level transforms while this
workspace owns mesh-level edits.

## Required target integration

The following remain renderer-specific:

- native Minecraft `Screen` implementation;
- ray picking against live authored meshes;
- render-thread upload and deletion;
- dynamic texture object allocation;
- material/shader binding;
- render culling and LOD;
- replacement of baked block/item/model lookup;
- resource reload survival.

All GPU resources must have explicit lifetime ownership. Replacing an asset revision must not leak old vertex
buffers or texture objects.
