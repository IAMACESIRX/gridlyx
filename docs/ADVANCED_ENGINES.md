# Advanced engine layer

The template contains an opt-in `src/advanced` source set. Normal builds keep these mechanisms isolated. Enable them only in a workspace that needs them:

```bash
./gradlew -Penable_advanced_engines=true build
```

This produces the normal mod JAR plus an `-advanced.jar` and an optional `-agent.jar`.

## Included foundations

- bounded custom worker pools and coalescing multi-threaded state syncing;
- versioned dynamic data engine;
- Java Instrumentation transformer/hotswap bridge;
- direct ASM class generation (ASM 9.10.1);
- runtime registration of trusted Mixin configuration files;
- Foreign Function & Memory API native-memory bridge;
- memory-mapped multi-process IPC channel;
- event-loop-safe Netty pipeline insertion/removal;
- direct LWJGL/OpenGL buffer ownership primitive;
- PoseStack interception hooks and a two-bone IK solver;
- codec/worldgen datagen bootstrap helper.

These are infrastructure primitives, not permission to patch arbitrary game internals. Target-specific Mixin/ASM transforms must fingerprint class/method descriptors and fail closed on mapping drift. Do not perform blocking work on Netty event loops or Minecraft render threads.
