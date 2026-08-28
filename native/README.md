# Gridelyx Native Workspace

The native workspace contains Gridelyx's trusted native ABI/IPC implementation, cross-process shared-memory transport, Rust acceleration lane and Bedrock native companion.

## Canonical ABI state

The current native identity is defined in `platform/brand.json`:

- library: `gridelyx_native`;
- symbol prefix: `gridelyx_`;
- logical bridge magic: `GLXB`;
- shared-memory transport magic: `GLXM`;
- native ABI version: `2`;
- bridge protocol version: `2`.

Java 25 binds the C ABI through the Foreign Function & Memory API (Project Panama/FFM).

## Shared-memory contract

The `gridelyx_shm_create` / `gridelyx_shm_open` functions expose a named mapped region containing a publication header followed by a caller-writable payload region. A producer writes a complete Gridelyx binary bridge frame and publishes it. The native side advances publication state only after the payload is ready.

Consumers snapshot/copy the payload and verify publication sequence. If the sequence changes during the read, the consumer retries rather than accepting a torn revision.

Canonical logical protocol documentation: [`docs/GRIDELYX_BRIDGE_PROTOCOL.md`](../docs/GRIDELYX_BRIDGE_PROTOCOL.md).

## Bedrock companion

`native/bedrock` hosts a versioned `BedrockAdapter` boundary. The default adapter validates/logs frames and does not contain hard-coded Bedrock addresses, process injection or executable patching.

Platform/version-specific Bedrock adapters can be developed behind that boundary when a supported or explicitly validated deeper integration surface exists. Stable Add-On/Editor paths remain independent of those adapters.

## Other native use cases

Gridelyx native components may also support, behind explicit capability and version gates:

- high-throughput geometry/voxel/volumetric processing;
- shared-memory scene/world/telemetry streams;
- renderer buffer preparation and native acceleration;
- external simulation or AI workers;
- production/capture pipelines;
- exact target adapters when Java/Bedrock public surfaces are insufficient.

Native code must not silently become the authority for world/server state merely because it is faster.

## Safety

Native code is trusted process code. An invalid pointer, ABI mismatch, ownership error or incorrect FFM descriptor can terminate/corrupt a process. Therefore:

- keep ABI surfaces narrow and versioned;
- validate length, version, sequence and ownership;
- test Java/C++/Rust compatibility independently;
- isolate untrusted/generated/native extensions in a separate process when possible;
- fingerprint exact deeper-integration targets;
- retain last-known-good/rollback paths;
- never use native integration to bypass authentication, entitlement, DRM, anti-cheat or platform security controls.
