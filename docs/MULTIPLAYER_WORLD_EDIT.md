# Multiplayer World Edit Protocol

## Authority

The server is authoritative. Clients submit intentions or editor commands; the server validates permissions,
expected section revisions, bounds and resource budgets before accepting a mutation.

## Concurrency consensus

Every edited `SectionKey` has a monotonically increasing revision. `EditConsensusLog.reserve` uses compare-and-set on
the expected base revision. Conflicting edits are rejected/rebased rather than racing writes into a live section.

## Replication

`EditPacket` carries transaction ID, section coordinates, base/new revision and bounded payload bytes.
`EditPacketCodec` defines the Netty wire frame. `NettyEditChannel` installs/removes the decoder on the channel event
loop to preserve Netty thread safety.

`ReplicationCuller` filters recipients by chunk distance and the endpoint's view distance. `EditorStateSynchronizer`
tracks per-client transaction acknowledgements so already-applied edits are not streamed repeatedly.

## Integration requirements

The NeoForge adapter must authenticate edit requests, enforce server permissions, schedule commit work through
`ServerEditScheduler`, encode actual section deltas, and hand outgoing packets only to players in the replication
interest set. Large events should be partitioned into bounded transactions to avoid tick starvation.
