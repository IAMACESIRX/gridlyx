# Native extension workspace

Rust and C++ implementations expose the same minimal C ABI. Java binds to this ABI through the Java 25 Foreign Function & Memory API.

The ABI begins with `madk_abi_version()` and deliberately uses primitive C-compatible values. Complex native services should use opaque handles plus explicit create/destroy functions rather than sharing Java object pointers.

Native extensions are opt-in, trusted process code. A crash or memory-safety error can terminate or corrupt the Minecraft client, so this layer has a separate CI lane and must not be treated as equivalent to sandboxed scripts.
