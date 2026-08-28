# Gridelyx Bedrock Architecture

## Objective

**Gridelyx Studio** targets Minecraft Java Edition and Minecraft Bedrock Edition through one neutral authoring/runtime model. Bedrock is not treated as a Java loader: it is a separate engine target behind shared Gridelyx operation, asset, scene, transaction, scripting and production contracts.

## Bedrock planes

```text
AI / IDE / scripts / Gridelyx UAL
                 |
        +--------+---------+
        |                  |
   supported API        native IPC
        |                  |
Behavior/Resource Pack   binary bridge
        |                  |
@minecraft/server       Java FFM/Panama
        |                  |
Bedrock runtime         native compatibility ABI
        |                  |
        |              named shared memory
        |                  |
        |              Bedrock companion
        |                  |
        +----------> versioned adapter
```

### Stable creator plane

The canonical stable baseline recorded by the project is Bedrock `1.26.40` with `@minecraft/server` `2.9.0`. `bedrock/addon` provides behavior/resource pack targets. `/scriptevent` provides a supported ingress path for Gridelyx Bedrock actions without assuming arbitrary native-memory access.

### Editor plane

`bedrock/editor-extension` is isolated as preview code. Gridelyx selection, transform, brush, world-edit, model/asset and creator tools should bind here as Editor APIs are validated. Preview API changes must not silently alter stable project or bridge contracts.

### Dedicated-server network plane

Server-only HTTP/WebSocket/network APIs may be used by validated Bedrock Dedicated Server adapters. They are not assumed to exist in ordinary game clients or Realms, so they cannot serve as the universal Bedrock connection.

### Native companion plane

Java 25 FFM/Panama can bind the project-owned native bridge. The current version-1 compatibility ABI still uses `gridelyx_native`, `gridelyx_*` symbols and `VFSB` magic. Those names are retained **only** as compatibility identifiers during the staged Gridelyx protocol/ABI migration in Issue #26.

Canonical future identity is `gridelyx_native`, `gridelyx_*` and `GLXB`, as recorded in `platform/brand.json` and [`GRIDELYX_BRIDGE_PROTOCOL.md`](GRIDELYX_BRIDGE_PROTOCOL.md).

The native library owns named shared-memory publication state. Producers write encoded bridge frames and publish metadata; consumers validate sequence/length/CRC before dispatch to a `BedrockAdapter`.

The default adapter is a logging/validation boundary. Deep renderer/world/executable integration is not treated as universally stable. If a retained capability requires a deeper target adapter, it must be exact-version/fingerprint gated, attributable and recoverable under [`DEEP_INTEGRATION_ARCHITECTURE.md`](DEEP_INTEGRATION_ARCHITECTURE.md).

## Shared neutral capabilities

The following Gridelyx domains are intended to remain engine-neutral above target adapters:

- lifecycle/control operations;
- UAL registry/event/network/resource/world/input intents;
- mesh and texture revisions;
- microgeometry authoring state;
- scene/transform/property state;
- world deltas and transaction metadata;
- liquid/paint/transmutation simulation state;
- physics/constraint intent;
- telemetry;
- script results and AI tool responses;
- replay/timeline/camera/animation/production state.

Minecraft Java objects and Bedrock C++ pointers must never become portable neutral-contract data.

## Live assets and geometry

Java and Bedrock can consume the same Gridelyx authoring/project model while each engine owns final rendering/collision resources. Target goals include:

- live models/meshes/voxels/textures;
- model/texture registry updates;
- microgrid placement;
- curved/sloped/cylindrical authoring representations;
- dynamic render geometry;
- target-specific collision representation;
- volumetric previews;
- live scene/property/gizmo updates.

Bedrock supported pack/Editor mechanisms are the default target. Native/deeper adapters are capability-gated rather than assumed.

## World systems

The Bedrock parity target includes adapters for:

- transactional live world edits;
- structures and generated-world events over existing chunks;
- Dynamic Liquid Simulation Cells;
- arbitrary paint/material overlay matrices;
- progression-locked/reversible world-transmutation state;
- server-authoritative multiplayer editing and replication culling.

Neutral workers may calculate deltas off-thread/process; authoritative target mutation still crosses the appropriate engine/server authority boundary.

## Fault model

- shared-memory payloads are length-bounded and integrity checked;
- publication-sequence changes invalidate concurrent reads;
- native ABI/protocol versions are checked before use;
- scripts/tools receive bounded capabilities;
- native companion failures must not corrupt authoritative world transaction state;
- unknown frame versions/types fail closed;
- process isolation is preferred for untrusted/crash-prone native or guest workloads;
- last-known-good state and transactional rollback are retained where technically feasible.

## Compatibility statement

Gridelyx aims for one authoring/runtime model across Java and Bedrock, not binary identity between different game engines. A feature is marked supported on Bedrock only when the relevant Script/pack, Editor, Dedicated Server, native or deeper versioned adapter path has matching evidence.

## References

- [`docs/GRIDELYX_BRIDGE_PROTOCOL.md`](GRIDELYX_BRIDGE_PROTOCOL.md)
- `platform/bedrock-capabilities.json`
- `platform/brand.json`
- [`docs/DEEP_INTEGRATION_ARCHITECTURE.md`](DEEP_INTEGRATION_ARCHITECTURE.md)
- [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](CHAT_REQUIREMENTS_TRACEABILITY.md)
- [`docs/FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md)
