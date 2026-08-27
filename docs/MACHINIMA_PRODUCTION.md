# Machinima, animation and production architecture

## Objective

Gridelyx Studio should let a creator treat Minecraft as a programmable virtual production stage: record gameplay/replays, direct cameras, animate actors and objects, stage scenes, edit timelines, render shots and export production assets without destructively rewriting the source world.

## Architecture

```text
live game / replay / scripted scene
        |
        v
Production Event Bus
        |
        +-- world/entity state samples
        +-- camera state
        +-- animation/pose cues
        +-- particles/commands/dialogue markers
        +-- audio routing markers
        |
        v
Neutral Production Timeline
        |
        +-- Java adapter
        +-- Bedrock adapter
        +-- offline simulator where deterministic state is available
        |
        v
Capture Pipeline
        |
        +-- image sequence
        +-- real-time video frames
        +-- optional render passes
        +-- audio stems
        |
        v
Encoder / Export Bridge
```

The neutral timeline is edition-independent. Game-specific adapters negotiate what can actually be driven or captured.

## Project model

A production project contains:

- source instance + exact content lock reference;
- world/save/replay identity;
- scene definitions;
- nested sequences;
- shot/take records;
- timeline tracks;
- camera rigs;
- actor bindings;
- animation clips;
- cues/markers;
- render presets;
- audio-routing metadata;
- generated renders and export manifests.

Projects should reference content by stable IDs/hashes rather than copying every dependency.

## Time model

Use two related clocks:

1. **game tick/time** for deterministic simulation/event placement;
2. **production time** represented as rational frame time for exact frame rates.

Time-remap tracks map production time to source tick/time. This enables pause frames, slow motion, speed ramps and reverse playback where the underlying replay/runtime adapter supports it.

Do not store keyframe time solely as floating-point seconds.

## Timeline track families

### Camera

- position/rotation;
- FOV/zoom;
- target/look-at;
- roll;
- focus distance/aperture metadata;
- exposure/colour metadata where adapter permits;
- camera shake/noise layers;
- cut/blend transitions.

### Transform

For entities, props, scene nodes and editor objects: translation, rotation, scale (when supported), visibility and parenting/binding changes.

### Actor animation

- body/limb pose;
- head/eye/look direction;
- held item/equipment;
- locomotion state;
- emote/custom animation clip;
- facial/morph tracks only when the target model/runtime exposes them;
- additive animation layers and procedural look-at/IK where implemented.

### Gameplay/world

- block/world edit transactions;
- command/script cues;
- particles;
- weather/time;
- lights or Gridelyx custom render properties;
- entity spawn/despawn/teleport;
- dialogue/subtitle markers.

### Audio

- game mix markers;
- voice/dialogue clips;
- music;
- ambient/SFX stems;
- automation metadata for level/pan/ducking.

Audio capture capabilities depend on target hooks. The project format can carry stems even when a given target only provides a mixed game output.

## Replay/event recording

Recording mode should capture enough information to reconstruct a scene without simply screen-recording it:

- session/instance/content-lock identity;
- tick-stamped player/entity/world events;
- camera/input samples when desired;
- RNG seeds or deterministic-state markers when available;
- Gridelyx world-edit transactions;
- script/production cues;
- compatibility metadata.

Replay files are versioned. If exact deterministic replay cannot be guaranteed across game/mod versions, Gridelyx marks the replay as requiring the original instance lock.

## Camera director

Camera rigs:

- free camera;
- first/third-person binding;
- target/look-at;
- orbit;
- dolly/rail;
- Catmull-Rom/Bezier/spline path;
- crane/jib abstraction;
- handheld/noise layer;
- vehicle/entity mount.

Camera evaluation should be deterministic per production time. Interactive editor smoothing must not change baked/rendered camera motion unless committed.

## Capture modes

### Real-time capture

Captures at gameplay rate. Useful for streaming, quick recordings and performance-accurate footage. Dropped-frame policy must be visible.

### Offline deterministic capture

When the target adapter can control simulation/render stepping:

1. set production frame time;
2. resolve source tick/time;
3. evaluate scene/timeline;
4. settle required world/render state;
5. render one frame/pass;
6. write frame atomically;
7. advance exactly one output frame.

This allows high resolutions, supersampling and frame rates independent of real-time game speed.

## Render passes

Capability-negotiated optional passes:

- beauty/colour;
- depth;
- normals;
- object/entity ID masks;
- motion vectors;
- albedo/material IDs;
- transparent background where technically feasible.

Do not claim a pass on Bedrock/Java until a target-specific renderer adapter can actually provide it.

## Encoding/export

Canonical lossless interchange is an image sequence plus timing/audio/project metadata. Video encoding is a replaceable bridge.

If FFmpeg is used:

- Gridelyx does not silently bundle an unknown binary;
- detect a user-provided executable or obtain a build through an explicitly configured legal/provenanced provider;
- record executable version/hash/licence source;
- invoke with an argument array, not shell-concatenated user text;
- surface codec/container availability rather than assuming every FFmpeg build is identical.

## Java implementation path

- production-neutral Java records/classes in the advanced runtime;
- NeoForge/Fabric/Forge/Quilt client adapter hooks;
- render-thread capture queue with bounded buffers;
- server-authoritative replay/world events where applicable;
- client camera/animation adapter;
- optional native encoder/shared-memory output.

## Bedrock implementation path

- stable Script API for supported entity/world/camera control;
- Editor APIs for authoring tools where preview/stable status permits;
- VFSB neutral production commands;
- optional versioned native companion for capture capabilities unavailable to Script API;
- fail closed when a Bedrock update invalidates a native adapter.

## Multiplayer and permissions

Production tools must distinguish local visual direction from authoritative world/entity changes. Multiplayer actor/world control requires server permission/role checks. Replay/camera capture must not become a mechanism for bypassing server visibility or anti-cheat boundaries.

## Minimum viable machinima milestone

R5 MVP:

- record/replay one controlled local scene;
- free/spline camera;
- camera + entity transform keyframes;
- shot/take timeline;
- 1080p/60 image-sequence or video capture;
- save/reopen project with identical keyframe timing;
- preserve exact source instance/content lock;
- export without changing the source world.
