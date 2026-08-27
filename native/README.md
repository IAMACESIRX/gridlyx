# Gridelyx Studio Native Workspace

The native workspace contains Gridelyx Studio's versioned C ABI, cross-process shared-memory transport, Rust acceleration lane and Bedrock native companion.

Java 25 binds the C ABI through the Foreign Function & Memory API. The canonical exported prefix is `gridelyx_`; the native library artifact is `gridelyx_native`.

## Shared-memory contract

`gridelyx_shm_create` and `gridelyx_shm_open` expose a named mapped region containing an internal publication header followed by a caller-writable payload region. Java or another producer writes a complete `VFSB` frame into the payload and calls `gridelyx_shm_publish`. The native side updates frame metadata and advances an atomic publication sequence only after the payload has been written.

Consumers call `gridelyx_shm_snapshot`, copy the payload, then re-check `gridelyx_shm_sequence`. A changed sequence means the producer published a newer revision during the copy and the consumer must retry.

## Bedrock companion

`native/bedrock` is a consumer host with a versioned `BedrockAdapter` interface. Its default adapter only validates and logs Gridelyx frames. It deliberately contains no hard-coded Bedrock addresses, signatures, process injection or binary patching.

A platform/version-specific Bedrock adapter can be developed behind that boundary when an appropriate supported or explicitly validated integration surface is available. The stable Add-On and Editor integrations remain independent of that adapter.

## Safety

Native code is trusted process code. An invalid pointer, ABI mismatch or incorrect FFM descriptor can terminate or corrupt the JVM or native process. Keep the ABI narrow, versioned and independently tested. Untrusted AI-generated/native code belongs in a separate process fault domain.
