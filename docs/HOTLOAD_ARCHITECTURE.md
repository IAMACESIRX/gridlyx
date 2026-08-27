# External hotload architecture

## Goal

Maximise restartless iteration while preserving JVM and Minecraft invariants **where doing so is the most reliable implementation**. Restartlessness is an optimization, not a ceiling on platform capability. When a safe live-reload path is insufficient, the platform may escalate through the additive deep-integration layers defined in `DEEP_INTEGRATION_ARCHITECTURE.md`.

## Reload classes

| Change | Preferred path |
|---|---|
| JavaScript/Python script | Replace GraalVM context/module |
| JSON/data/procedural definition | Parse, validate, atomically replace versioned runtime state |
| Generated client asset | Invalidate/rebuild the platform asset cache and request Minecraft resource reload |
| Method-body-only Java change | `Instrumentation.redefineClasses` when VM permits |
| New Java fields/method signatures | Load a new implementation behind a stable parent-loaded interface and swap the service slot |
| New dynamic gameplay definition | Put it in the platform virtual registry/codec layer |
| New vanilla/loader static registry capability | Prefer virtual/project-owned registry semantics; if native engine behavior is genuinely required, escalate to loader/JVM/native/patch integration rather than declaring the requirement impossible |
| Native/engine change that cannot be reloaded safely | Supervised native/sidecar/game-process restart with editor/session-state restoration |
| Version-pinned executable/library change | Rebuild or activate a verified derived runtime/overlay through the patch manager, then supervised restart |

## Core

`ExternalHotloadCore` owns an NIO.2 `WatchService`. It watches configured development roots and debounces filesystem bursts into typed reload events. Handlers perform parsing/compilation off-thread and publish only complete versions.

`HotSwapSlot<T>` keeps stable call sites while implementation instances can be replaced. Schema-changing Java should therefore target service implementations loaded through a replaceable classloader when that is sufficient, rather than classes already linked directly into Minecraft.

`AgentBootstrap` remains the fast path for compatible class redefinition. Failure to redefine must never corrupt the live implementation; replacement is transactional.

## Registry rule

The platform does not assume that frozen Minecraft registries can safely gain arbitrary entries through ordinary public APIs at runtime. Restartless construction content therefore prefers **virtual registries** that resolve stable runtime IDs to script/data-backed definitions.

However, a frozen registry is a normal-extension boundary, not automatically a project-scope boundary. If a required capability must exist in the native registry/engine representation, the implementation may escalate through bytecode transformation, JVM instrumentation, native augmentation, launch/bootstrap changes or a version-pinned additive patch layer. That deeper path carries a higher fingerprinting, validation and rollback burden.

## Reload-scope escalation

Prefer the smallest reliable restart/reload scope:

`data/script -> service implementation -> classloader -> native/sidecar process -> game process -> patched runtime rebuild`

The creator environment should persist source, workspace, selection, layout and recoverable runtime state across broader restart scopes where possible, so a required process restart does not destroy the development workflow.

## Safety

Hotload roots are development-only. Incoming files are size-limited, canonicalised beneath an approved root, parsed before activation and assigned monotonically increasing versions. Polyglot guest code gets no host-class lookup by default.

Native libraries cannot be unloaded reliably from a running JVM, so native ABI upgrades use process/version boundaries rather than pretending to hot-unload code. Binary/runtime patch layers must follow `DEEP_INTEGRATION_ARCHITECTURE.md`: exact target fingerprints, immutable/recoverable base artifacts, explicit provenance, verification and rollback.
