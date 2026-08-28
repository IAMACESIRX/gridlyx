# Gridelyx Bedrock Runtime

This directory is the Bedrock target plane for **Gridelyx Studio**. It shares neutral operation, asset, scene, transaction and scripting concepts with the Java Polyloader while adapting them to APIs and target-specific integration surfaces that exist in Minecraft: Bedrock Edition.

## Integration tiers

### 1. Stable Add-On runtime

`addon/behavior_pack` and `addon/resource_pack` target the stable Bedrock creator stack. The behavior pack uses `@minecraft/server` and accepts Gridelyx actions through `/scriptevent` without assuming native process-memory access.

### 2. Editor extension (preview)

`editor-extension` is deliberately isolated because `@minecraft/server-editor` is pre-release. It is the Bedrock home for Gridelyx authoring tools such as selection, transform, asset/model editing and world-edit UI as required Editor surfaces are validated.

### 3. Native companion bridge

Java and native tools exchange versioned Gridelyx binary frames through the shared-memory ABI in `native/cpp`. The canonical logical bridge uses **GLXB protocol v2**, the shared-memory transport uses **GLXM**, and the native ABI exports `gridelyx_*` symbols from `gridelyx_native` at ABI version 2.

`native/bedrock` consumes frames behind a `BedrockAdapter` boundary. The default companion does not patch or inject into the closed-source Bedrock executable.

A version-specific native renderer/world integration may implement that adapter only when a supported or explicitly validated integration surface exists. Gridelyx must continue to function through stable Add-On/Editor paths when a deeper adapter is unavailable.

## Capability routing

```text
Gridelyx UAL / AI / scripts
          |
          +--> Java loader adapters (Java Edition)
          |
          +--> Bedrock Script Adapter --> behavior/resource packs
          |
          +--> GLXB v2 bridge --> Panama/FFM --> Gridelyx native ABI v2
                                             |
                                             +--> Bedrock companion
                                                  |
                                                  +--> versioned adapter
```

The bridge is a transport, not a claim that Java objects or Minecraft Java Edition classes can be projected directly into Bedrock's C++ object model.

## Target capability goals

The Bedrock plane participates in the same retained Gridelyx capability graph, including target-specific implementations for:

- live world editing, structures, generated-chunk events and transactional rollback;
- model/voxel/mesh/texture authoring;
- microgeometry and custom render/collision representations where technically achievable;
- liquids, paint layers and world-transmutation state;
- scene graph, transform gizmos and sandbox physics;
- scripts, AI/IDE automation and external-tool bridges;
- multiplayer-authoritative editing and replication;
- replay, animation, camera and production/capture systems.

Parity is evidence-gated. A Java feature is not automatically marked supported on Bedrock just because a neutral contract exists.

## Current baselines

- Stable creator target: Bedrock `1.26.40` / `@minecraft/server` `2.9.0`.
- Editor extension target: separately versioned preview scaffold.
- Dedicated-server network integration is optional because server-only networking APIs are not a universal client/Realm transport.

## Validation

Run:

```bash
python tools/bedrock_check.py
```

Canonical bridge documentation: `docs/GRIDELYX_BRIDGE_PROTOCOL.md`.

Deep-integration policy: `docs/DEEP_INTEGRATION_ARCHITECTURE.md`.
