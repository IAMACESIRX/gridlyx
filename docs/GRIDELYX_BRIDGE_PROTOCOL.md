# Gridelyx Studio Binary Bridge Protocol (VFSB)

## Purpose

VFSB is the transport-neutral binary envelope used when Gridelyx Studio moves neutral operations or live assets across process/runtime boundaries. It is suitable for Java FFM/shared memory and can also be carried by sockets or files.

## Version 1 envelope

All multibyte envelope fields are big-endian.

| Offset | Size | Field |
|---:|---:|---|
| 0 | 4 | Magic `VFSB` (`0x56465342`) |
| 4 | 2 | Protocol version (`1`) |
| 6 | 2 | Frame type |
| 8 | 8 | Logical sequence |
| 16 | 4 | Payload length |
| 20 | 4 | CRC32 of payload |
| 24 | N | Payload |

Frame types:

1. control
2. UAL operation
3. mesh revision
4. texture patch
5. world delta / transaction message
6. telemetry
7. script result

Unknown versions and unknown frame types fail closed unless a negotiated compatibility adapter explicitly handles them.

## Native shared-memory publication

The native mapping has its own small transport header followed by a payload region. The VFSB envelope is copied into that payload region. This double layer is deliberate:

- VFSB remains portable between transports;
- the native header provides cross-process publication metadata;
- the native publication sequence advances only after the producer has finished writing;
- consumers copy a published payload and verify that the native sequence did not change during the copy;
- the native header stores a CRC32 for the complete encoded VFSB frame while VFSB itself checks its logical payload.

## Payload schemas

Version 1 defines the envelope and frame domains, not a frozen universal payload schema. Each frame type must carry a versioned schema identifier inside its payload before production use. Recommended encodings are deterministic binary records or a length-bounded canonical JSON/CBOR representation during early development.

Never put raw Java references, `net.minecraft` objects, Bedrock C++ pointers, process addresses or native ownership handles into a portable VFSB payload.

## Backpressure

A single shared region is a latest-frame transport, not an unbounded queue. Producers that need guaranteed delivery must layer an ACK/ring-buffer protocol above it or use the existing Gridelyx network/consensus mechanisms. Mesh and texture preview streams may intentionally drop superseded revisions; authoritative world transactions may not.

## Security

- Reject payload lengths beyond the negotiated capacity.
- Verify CRC before dispatch.
- Treat frame type and inner schema as untrusted input.
- Do not interpret payload bytes as executable native code.
- Native adapters must copy or retain data according to explicit ownership rules; payload memory becomes mutable again after the next publication.
