# Gridelyx bridge protocol

Gridelyx Studio uses a versioned logical bridge protocol and a separate native shared-memory transport. These identifiers are part of the Gridelyx v2 compatibility boundary and must not be changed without a protocol/ABI version transition.

## Logical bridge — GLXB/2

- **Magic:** `GLXB`
- **Protocol version:** `2`
- **Purpose:** cross-runtime framed messages between Gridelyx Java, Bedrock and companion/runtime adapters.
- **Rule:** decoders fail closed on invalid magic, unsupported version, malformed lengths or invalid frame structure.

The Java reference implementation is `BedrockBridgeCodec` / `BedrockBridgeFrame`. Bedrock runtime capability reporting identifies the bridge as `GLXB/2`.

## Native shared-memory transport — GLXM/2

- **Magic:** `GLXM`
- **Transport protocol version:** `2`
- **Native ABI version:** `2`
- **Native library:** `gridelyx_native`
- **Native symbol prefix:** `gridelyx_`

The shared-memory transport is a bounded latest-frame transport with sequence publication and CRC-backed snapshot validation. Transport memory is not a substitute for logical message validation; consumers must validate both the native transport envelope and the logical GLXB frame.

## ABI contract

The canonical native API exports Gridelyx-owned symbols including:

- `gridelyx_abi_version`
- `gridelyx_protocol_version`
- `gridelyx_add`
- `gridelyx_shm_create`
- `gridelyx_shm_open`
- `gridelyx_shm_payload`
- `gridelyx_shm_capacity`
- `gridelyx_shm_sequence`
- `gridelyx_shm_publish`
- `gridelyx_shm_snapshot`
- `gridelyx_shm_close`
- `gridelyx_shm_unlink`

Java FFM bindings are provided by `GridelyxNativeBridge`. Rust and C/C++ implementations must expose the same ABI/version semantics before they are considered interoperable.

## Compatibility policy

Gridelyx v2 is an explicit breaking identity/protocol boundary. Current Gridelyx code does not retain project-owned compatibility aliases for pre-Gridelyx product identifiers. Persisted or external consumers must negotiate the Gridelyx versioned protocol rather than relying on retired naming.

Future protocol changes must:

1. increment the relevant protocol or ABI version;
2. document the wire/ABI delta;
3. add positive and negative interoperability fixtures;
4. fail closed on unsupported versions;
5. preserve rollback/migration evidence before release.

## Validation

The following checks form the minimum protocol evidence set:

- Java codec smoke tests for GLXB/2;
- native C/C++ and Rust ABI/version tests;
- shared-memory create/open/publish/snapshot validation;
- CRC/sequence rejection tests;
- Bedrock companion integration checks;
- FFM symbol-resolution tests;
- malformed-frame and unsupported-version negative tests.

A source file, symbol or interface existing is not sufficient evidence of production readiness; target-specific integration must still pass the project readiness gates.
