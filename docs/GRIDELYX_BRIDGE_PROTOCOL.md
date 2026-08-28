# Gridelyx Binary Bridge Protocol (GLXB)

## Purpose

GLXB is the transport-neutral binary envelope used when Gridelyx moves neutral operations, live assets and control messages across process/runtime boundaries. It is suitable for Java FFM/shared memory and may also be carried by sockets or files.

## Canonical identity

The Gridelyx bridge migration is complete for the current tree:

- logical envelope magic: **`GLXB`** (`0x474C5842`);
- protocol version: **2**;
- native shared-memory transport magic: **`GLXM`** (`0x474C584D`);
- native ABI version: **2**;
- native symbol prefix: **`gridelyx_`**;
- native library target: **`gridelyx_native`**.

Protocol/ABI v2 is intentionally incompatible with the retired pre-Gridelyx identity. Peers must negotiate protocol version before exchanging authoritative frames.

## Version 2 envelope

All multibyte envelope fields are big-endian.

| Offset | Size | Field |
|---:|---:|---|
| 0 | 4 | Magic `GLXB` (`0x474C5842`) |
| 4 | 2 | Protocol version (`2`) |
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

Unknown versions and frame types fail closed unless an explicitly negotiated compatibility adapter handles them.

## Native shared-memory publication

The native mapping has its own transport header followed by a payload region. A GLXB envelope is copied into that region. The double layer is deliberate:

- GLXB remains transport-neutral;
- the native header provides cross-process publication metadata;
- publication sequence advances only after the producer finishes writing;
- consumers snapshot the sequence, copy the frame, then verify the sequence did not change;
- the native header stores CRC32 for the complete encoded frame while GLXB verifies its logical payload.

The current native mapping uses `GLXM` and protocol version 2. A mapping with another magic/version is rejected rather than interpreted heuristically.

## Payload schemas

Version 2 defines the envelope and frame domains, not a frozen universal payload schema. Each frame type must carry a versioned inner schema identifier before production use. Recommended encodings are deterministic binary records or length-bounded canonical JSON/CBOR during early development.

Never put raw Java references, `net.minecraft` objects, Bedrock C++ pointers, process addresses or native ownership handles into portable Gridelyx bridge payloads.

## Backpressure

A single shared region is a latest-frame transport, not an unbounded queue. Guaranteed delivery requires an ACK/ring-buffer protocol above it or Gridelyx network/consensus mechanisms. Mesh and texture preview streams may intentionally drop superseded revisions; authoritative world transactions may not.

## Security

- Reject payload lengths beyond negotiated capacity.
- Verify CRC before dispatch.
- Treat frame type and inner schema as untrusted input.
- Never interpret payload bytes as executable native code.
- Native adapters copy or retain data according to explicit ownership rules.
- Bridge connectivity alone never grants world/server authority.
- Protocol/ABI mismatches fail closed.

## Validation

`tools/bedrock_check.py`, native CI and advanced Java smoke tests jointly verify the current Gridelyx names, GLXB envelope, native ABI symbols and Bedrock bridge round trip.
