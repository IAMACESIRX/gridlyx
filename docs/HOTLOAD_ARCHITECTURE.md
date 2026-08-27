# External hotload architecture

## Goal

Maximise restartless iteration while preserving JVM and Minecraft invariants.

## Reload classes

| Change | Restartless path |
|---|---|
| JavaScript/Python script | Replace GraalVM context/module |
| JSON/data/procedural definition | Parse, validate, atomically replace versioned runtime state |
| Generated client asset | Invalidate/rebuild the platform asset cache and request Minecraft resource reload |
| Method-body-only Java change | `Instrumentation.redefineClasses` when VM permits |
| New Java fields/method signatures | Load a new implementation behind a stable parent-loaded interface and swap the service slot |
| New dynamic gameplay definition | Put it in the platform virtual registry/codec layer |
| New vanilla/NeoForge static registry entry | **Not guaranteed restartless**; frozen registries/network IDs are a hard boundary |

## Core

`ExternalHotloadCore` owns an NIO.2 `WatchService`. It watches configured development roots and debounces filesystem bursts into typed reload events. Handlers perform parsing/compilation off-thread and publish only complete versions.

`HotSwapSlot<T>` keeps stable call sites while implementation instances can be replaced. Schema-changing Java should therefore target service implementations loaded through a replaceable classloader, not classes already linked directly into Minecraft.

`AgentBootstrap` remains the fast path for compatible class redefinition. Failure to redefine must never corrupt the live implementation; replacement is transactional.

## Registry rule

The platform does not claim that frozen Minecraft registries can safely gain arbitrary entries at runtime. Restartless construction content uses **virtual registries** that resolve stable runtime IDs to script/data-backed definitions. A later engine adapter may materialise them into native registry objects only where NeoForge explicitly supports it.

## Safety

Hotload roots are development-only. Incoming files are size-limited, canonicalised beneath an approved root, parsed before activation and assigned monotonically increasing versions. Polyglot guest code gets no host-class lookup by default. Native libraries cannot be unloaded reliably from a running JVM, so native ABI upgrades use process/version boundaries rather than pretending to hot-unload code.
