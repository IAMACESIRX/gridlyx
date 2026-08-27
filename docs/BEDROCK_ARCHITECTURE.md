# Gridelyx Studio Bedrock Architecture

## Objective

Gridelyx Studio targets Minecraft Java Edition and Minecraft Bedrock Edition through one neutral authoring/runtime model. Bedrock is not treated as a Java loader: it is a separate engine target behind the same Gridelyx operation, asset, transaction and scripting contracts.

## Bedrock planes

```text
AI / IDE / scripts / Gridelyx UAL
                 |
        +--------+---------+
        |                  |
   supported API        native IPC
        |                  |
Behavior/Resource Pack   VFSB codec
        |                  |
@minecraft/server       Java FFM/Panama
        |                  |
Bedrock runtime         gridelyx_native
        |                  |
        |              named shared memory
        |                  |
        |              Bedrock companion
        |                  |
        +----------> versioned adapter
```

### Stable creator plane

The canonical stable baseline is Bedrock 1.26.40 with `@minecraft/server` 2.9.0. `bedrock/addon` provides a behavior pack and resource pack. `/scriptevent gridelyx:<action> <payload>` enters the Gridelyx Bedrock dispatcher.

This plane is portable across normal Bedrock environments that support the referenced Creator APIs. It does not have arbitrary native-memory access.

### Editor plane

`bedrock/editor-extension` is isolated as preview code. Gridelyx selection, transform, brush, world-edit and asset-authoring tools should bind here as Editor APIs are validated. Preview API changes must not alter the stable bridge protocol.

### Dedicated-server network plane

`@minecraft/server-net` is useful for a Bedrock Dedicated Server sidecar because it exposes HTTP/WebSocket facilities there. It is not a client or Realm transport, so it is optional and may not be used as the universal Gridelyx Bedrock connection.

### Native companion plane

The Java process can use Java 25 FFM to bind `gridelyx_native`. This native library owns a named shared-memory region and publication sequence. The producer writes an encoded VFSB frame, then publishes metadata. A companion process validates sequence and CRC before passing the frame to a `BedrockAdapter`.

The default adapter is a logging/validation adapter. The repository intentionally does not include hard-coded Bedrock addresses, signature scanners, process injection or executable patching as the platform baseline.

## Shared capabilities

The following Gridelyx domains are intended to remain engine-neutral above the adapter boundary:

- lifecycle/control operations;
- UAL registry/event/network/resource/world/input intents;
- mesh revisions;
- texture patches;
- scene/transform state;
- world deltas and transaction metadata;
- telemetry;
- script results and AI tool responses.

Minecraft Java classes and Bedrock C++ object pointers must never be serialized into these neutral contracts.

## Live assets

Java and Bedrock can consume the same Gridelyx mesh/texture authoring model, but each engine owns final GPU resources. Bedrock's supported resource-pack and Editor mechanisms are the default target. A native adapter may provide a lower-latency rendering path only after version-specific validation.

## Fault model

- Shared-memory payloads are length-bounded and CRC-protected.
- Publication sequence changes invalidate concurrent reads.
- Native ABI/protocol versions are checked before use.
- Script failures remain inside the existing Gridelyx fault boundary.
- Native companion failures must not corrupt authoritative world transaction state.
- Unknown frame versions/types fail closed.

## Compatibility statement

Gridelyx Studio's goal is one authoring/runtime abstraction across Java and Bedrock, not binary identity between two different game engines. A feature is marked supported on Bedrock only when its stable Script/pack, Editor, Dedicated Server, or native adapter path is validated for that target.
