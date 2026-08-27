# Advanced validation matrix

| Surface | Required evidence |
|---|---|
| Worker/sync pools | bounded queue tests, shutdown test, saturation benchmark |
| Dynamic data | concurrent update test, snapshot/version monotonicity |
| ASM/Instrumentation | bytecode verification, exact target fingerprint, rollback path |
| Mixin redirect | mapping/descriptor lock, conflict test with other mixins |
| Netty injection | event-loop test, ordering/backpressure/disconnect test |
| FFM/native | bounds/lifetime test, platform capability check |
| Shared-memory IPC | corruption/CRC test, process crash/restart test |
| LWJGL GPU | render-thread/context assertion, driver matrix, resource cleanup |
| Pose/IK | numerical edge cases, animation integration visual test |
| Codec worldgen | generated JSON/codec decode round trip, deterministic seed test |

Advanced features should not be enabled globally merely because they compile.
