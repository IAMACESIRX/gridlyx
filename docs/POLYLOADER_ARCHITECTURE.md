# Gridelyx Studio Polyloader Architecture

## Objective

Gridelyx Studio is a loader-neutral runtime engineering plane. The long-term target is to place a small
prelaunch bootstrap below the normal Minecraft/mod-loader lifecycle and expose a stable Unified Abstraction
Layer (UAL) above it.

The architecture is capability-driven rather than based on a claim that arbitrary mods are universally
portable. Fabric, Quilt, Forge, NeoForge, Liteloader and vanilla environments differ in registration timing,
class transformation, mappings, dependency resolution and frozen runtime state.

## Layer model

```text
JVM launch
  -> -javaagent Gridelyx bootstrap
  -> Instrumentation/ClassFileTransformer
  -> runtime environment fingerprint
  -> loader adapters and mapping probes
  -> ASM translation rules
  -> Unified Abstraction Layer
  -> Minecraft / active loader

AI / IDE / server script
  -> compatibility analyser
  -> isolated sideload classloader
  -> loader adapter or UAL activation
  -> capability decision
       LIVE_SAFE
       EMULATED
       PRELAUNCH_REQUIRED
       UNSUPPORTED
```

## Prelaunch bootstrap

`AgentBootstrap` is the JVM instrumentation entrypoint. `premain` executes before application `main` when the
launcher supplies the agent with `-javaagent`. It installs the shared transform engine and then attempts to
install `PolyloaderBootstrap`.

The bootstrap deliberately detects whether ASM is visible. A production standalone agent must bundle or
otherwise provide its own compatible ASM classes; it must not silently depend on a particular mod loader to
supply ASM after boot.

Attaching with `agentmain` is useful for development, but it is not equivalent to prelaunch injection. Classes
that have already passed through early loader/Mixin transformation may require retransformation or a restart.

## Unified Abstraction Layer

`UnifiedAbstractionLayer` carries neutral operations across eight domains:

- registry;
- event;
- network;
- resource;
- render;
- world;
- input;
- lifecycle.

Loader adapters translate a supported source-loader operation into one of these contracts. UAL payloads avoid
Minecraft classes so the core package remains compile-time blind to `net.minecraft` and loader APIs.

## ASM translation

`AsmInvocationTranslator` performs exact invocation replacement through `CallTranslationRule` objects. The
current safe rule requires an identical JVM descriptor. This avoids corrupting the operand stack while the
adapter system is still being built.

Future adapters may add richer bytecode lowering, but each lowering must have verifier tests and explicit stack
semantics. "Strip loader code" must never mean deleting unknown instructions and hoping verification succeeds.

## Sideloading

`ModArtifactAnalyzer` inspects a JAR without executing it and classifies obvious loader metadata, Mixins, access
wideners, transformation services and native libraries.

`SideloadContainer` only opens a child-first isolated classloader for artifacts classified as `LIVE_SAFE` or
`EMULATED`. Activation is delegated to a loader adapter.

Early-transform artifacts are `PRELAUNCH_REQUIRED` because loading the JAR late cannot recreate every action
that its original loader would have performed before Minecraft classes were defined.

Closing an isolated classloader detaches Gridelyx's reference to it. Java does not guarantee immediate class
unloading, and a mod that leaked threads, statics or callbacks into parent-owned objects can keep the loader
reachable.

## Reflection and MethodHandles

`DynamicHandleScanner` searches already-loaded classes by structural method shape rather than a hard-coded
Minecraft symbol. This is a discovery primitive, not a magical obfuscation solver.

Production symbol binding should combine:

1. runtime class and module inventory;
2. loader-provided mappings when available;
3. bytecode fingerprints and descriptors;
4. semantic probes against known behaviour;
5. cached, version-scoped binding manifests;
6. confidence thresholds and fail-closed behaviour.

A low-confidence match must never be invoked merely because its parameter count happens to match.

## Version independence

The existing template is a Java 25 / Minecraft 26.2 development target. That is not itself a binary that can
run on every historical Minecraft JVM.

Supporting the 1.7.10 era through current snapshots requires a split bootstrap family:

- a legacy low-bytecode agent/launcher lane for Java 8-era clients;
- transitional lanes for later JVM generations where required;
- the current Java 25 kernel;
- a version-neutral bridge protocol between stage-0 bootstrap and stage-1 Gridelyx services.

The UAL and compatibility manifests should stay stable while JVM-specific launch shims change.

## Non-negotiable compatibility rules

- No arbitrary claim that a Fabric JAR can always be injected live into NeoForge or vice versa.
- Mixins, access wideners, coremods and transformation services default to prelaunch.
- Frozen registries and lifecycle-only registration points may require restart.
- Native code is never remapped by Java bytecode translation.
- Unknown mappings fail closed.
- Server authority and existing world-edit transaction rules remain intact.
- Loader adapters are separately tested by Minecraft version family.
