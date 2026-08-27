# MADK bridge protocol 1.0

All integer fields are network byte order (big endian).

```
int32  protocol_version
int64  request_id
int32  operation_utf8_length
bytes  operation_utf8
int32  payload_length
bytes  payload
```

Limits: operation <= 256 bytes; payload <= 16 MiB. The same frame can travel over loopback TCP, Unix/domain sockets where available, named-pipe adapters, or the existing shared-memory IPC transport.

Production/external listeners require authentication, authorisation, bounded message sizes, timeouts and provenance. The supplied examples are codecs/conformance fixtures, not exposed network daemons.
