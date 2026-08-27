# Gridelyx Studio Bedrock Runtime

This directory is the Bedrock target plane for Gridelyx Studio. It shares the platform's neutral operation and asset concepts with the Java polyloader while adapting them to APIs that actually exist in Minecraft: Bedrock Edition.

## Integration tiers

### 1. Stable Add-On runtime

`addon/behavior_pack` and `addon/resource_pack` target the stable Bedrock creator stack. The behavior pack uses `@minecraft/server` and accepts Gridelyx commands through `/scriptevent` without assuming access to native process memory.

### 2. Editor extension (preview)

`editor-extension` is deliberately isolated because `@minecraft/server-editor` is pre-release. It is the natural Bedrock home for Gridelyx authoring tools, selection, transform, asset and world-edit UI as Microsoft exposes the required Editor extension surfaces.

### 3. Native companion bridge

Java and native tools exchange versioned Gridelyx binary frames through the named shared-memory ABI in `native/cpp`. `native/bedrock` consumes those frames behind a `BedrockAdapter` interface. The default companion does not patch or inject into the closed-source Bedrock executable.

A version-specific native renderer integration may implement that adapter only when a supported or explicitly validated integration surface exists. Gridelyx Studio must continue to function through the stable Add-On/Editor route when such a native adapter is unavailable.

## Capability routing

```text
Gridelyx UAL / AI / scripts
          |
          +--> Java loader adapters (Java Edition)
          |
          +--> Bedrock Script Adapter --> behavior/resource packs
          |
          +--> VFSB binary codec --> Panama --> gridelyx_native
                                           |
                                           +--> Bedrock companion
                                                |
                                                +--> versioned adapter
```

The binary bridge is a transport, not a claim that Java objects or Minecraft Java Edition classes can be projected directly into Bedrock's C++ object model.

## Current baselines

- Stable creator target: Bedrock 1.26.40 / `@minecraft/server` 2.9.0.
- Editor extension target: separately versioned preview scaffold.
- Dedicated-server network integration is optional because `@minecraft/server-net` is not available in the normal game client or Realms.

Run `python tools/bedrock_check.py` from the repository root to validate the Bedrock plane.
