# Polyglot, native and bridge architecture

## Embedded scripting

The advanced source set uses `org.graalvm.polyglot:polyglot` with GraalJS and GraalPy runtime dependencies. Scripts execute in explicitly constructed contexts with host access denied by default. Minecraft services must be exposed through narrow capability objects rather than `HostAccess.ALL`.

The platform deliberately uses the Polyglot `Context` API instead of legacy JSR-223. Script contexts are disposable, making script hotloading a replace-context operation rather than mutation of an unknown interpreter state.

## Native extensions

Rust and C++ examples implement the same tiny C ABI. Java binds to that ABI through the Java 25 Foreign Function & Memory API. Real extensions should expose coarse, versioned calls instead of sharing arbitrary Java object pointers.

Native code is treated as trusted process code: a memory-safety failure can crash or corrupt Minecraft. It therefore has a separate CI path and is never enabled by default.

## Cross-process bridges

Python AI, Go and C# sidecars use a language-neutral framed protocol. Each frame contains:

- protocol version
- correlation/request ID
- operation name
- payload length
- payload bytes
- optional trace metadata

Transports can be shared memory, local sockets or a loopback Netty channel. Authentication/authorisation is required before exposing a non-loopback endpoint.

## MCP

The MCP adapter targets protocol revision `2026-07-28`, whose core is stateless. The local endpoint therefore does not depend on sticky sessions. MCP tools are adapters over platform services, not direct unrestricted access to the filesystem, JVM instrumentation, native memory or Minecraft internals.
