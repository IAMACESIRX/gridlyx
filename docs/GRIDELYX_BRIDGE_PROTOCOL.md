# Gridelyx Binary Bridge Protocol

## Purpose

The Gridelyx binary bridge is the transport-neutral envelope used when Gridelyx moves neutral operations or live assets across process/runtime boundaries. It is suitable for Java FFM/shared memory and can also be carried by sockets or files.

## Protocol identity and migration

The current deployed/version-1 compatibility envelope still uses legacy magic **`VFSB`** (`0x56465342`) and the existing `gridelyx_*` native ABI. Those values are **compatibility identifiers, not current product branding**.

Canonical future Gridelyx protocol identity is recorded in `platform/brand.json`:

- protocol prefix: `GLYX`;
- future bridge magic: `GLXB`;
- future native symbol prefix: `gridelyx_`;
- future native library: `gridelyx_native`.

Issue #26 governs a versioned old↔new protocol/ABI migration. Do not rewrite the deployed magic/symbols until interoperability, persisted-state and rollback tests exist.

## Version 1 compatibility envelope

All multibyte envelope fields are big-endian.

| Offset | Size | Field |
|---:|---:|---|
| 0 | 4 | legacy compatibility magic `VFSB` (`0x56465342`) |
| 4 | 2 | Protocol version (`1`) |
| 6 | 2 | Frame type |
| 8 | 8 | Logical sequence |
| 16 | 4 | Payload length |
| 20 | 4 | CRC32 of payload |
| 24 | N | Payload |

Frame types:

1. control;
2. UAL operation;
3. mesh revision;
4. texture patch;
5. world delta / transaction message;
6. telemetry;
7. script result.

Unknown versions and frame types fail closed unless a negotiated compatibility adapter explicitly handles them.

## Native shared-memory publication

The native mapping has its own small transport header followed by a payload region. The binary bridge envelope is copied into that region. This double layer is deliberate:

- the Gridelyx logical envelope remains portable between transports;
- the native header provides cross-process publication metadata;
- publication sequence advances only after the producer finishes writing;
- consumers copy a published payload and verify the native sequence did not change during the copy;
- the native header stores CRC32 for the complete encoded frame while the logical envelope checks its payload.

## Payload schemas

Version 1 defines envelope/frame domains, not a frozen universal payload schema. Each frame type must carry a versioned inner schema identifier before production use. Recommended early-development encodings are deterministic binary records or length-bounded canonical JSON/CBOR.

Never put raw Java references, `net.minecraft` objects, Bedrock C++ pointers, process addresses or native ownership handles into portable Gridelyx bridge payloads.

## Backpressure

A single shared region is a latest-frame transport, not an unbounded queue. Guaranteed delivery requires an ACK/ring-buffer protocol above it or Gridelyx network/consensus mechanisms. Mesh/texture preview streams may intentionally drop superseded revisions; authoritative world transactions may not.

## Security

- reject payload lengths beyond negotiated capacity;
- verify CRC before dispatch;
- treat frame type and inner schema as untrusted input;
- never interpret payload bytes as executable native code;
- native adapters copy/retain data according to explicit ownership rules;
- bridge connectivity alone never grants world/server authority.

## Migration exit criteria

The legacy version-1 naming can be removed only when:

1. new Gridelyx protocol/native identifiers are implemented;
2. old↔new interoperability tests pass;
3. persisted/shared-memory/network compatibility is understood;
4. Java/C++/Rust/Bedrock consumers are updated;
5. rollback to last-known-good protocol is documented;
6. `platform/brand.json`, terminology controls and Issue #26 are updated together.
