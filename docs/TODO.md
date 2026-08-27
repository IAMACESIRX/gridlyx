# TODO and validation ledger

## Immediate

- [x] Add master `build.gradle` SHA-256 lock.
- [x] Add non-Java script gatekeeper to GitHub Actions.
- [x] Add JUnit/ArchUnit dependency locks.
- [x] Add headless GameTest orchestration facade.
- [x] Add deterministic auto-documentation manifest/generator.
- [x] Add bytecode disassembly/diff tooling.
- [x] Add CSV-to-recipe conversion pipeline.
- [ ] Compile the full advanced source set in CI on every platform change.
- [ ] Add first NeoForge 26.2 GameTest fixture and require it in nightly CI.
- [ ] Add client-side rendering smoke-world capture/benchmark.

## Hotload

- [ ] Validate script and data hotreload in a running 26.2 client.
- [ ] Add resource-reload adapter for generated textures/models.
- [ ] Add versioned URL/module classloader for schema-changing service implementations.
- [ ] Build class-schema comparator so compatible changes automatically choose `Instrumentation` and incompatible changes choose service replacement.
- [ ] Add rollback to previous known-good script/data/service version.
- [ ] Validate registry indirection under multiplayer reconnect and datapack reload.

## Sandbox construction

- [ ] Wire raycast selection to the actual client/server interaction packet.
- [ ] Add weld, hinge, slider, spring and rope constraints.
- [ ] Add authority model so the server owns physics/constraint truth.
- [ ] Add deterministic replication and prediction budgets.
- [ ] Add undo/redo transaction log for tool-gun actions.
- [ ] Add dynamic collision-to-`VoxelShape` NeoForge adapter.
- [ ] Add custom geometry render adapter and culling/batching benchmarks.

## Native/polyglot

- [ ] Compile Rust/C++ examples on Windows and Linux.
- [ ] Add ABI compatibility test vectors.
- [ ] Add Python/Go/C# bridge conformance tests.
- [ ] Add sandbox resource limits and script execution budgets.
- [ ] Benchmark GraalJS/GraalPy cold start, hot reload and steady-state execution.

## Operations

- [ ] Add JFR baseline profiles for client startup, world load and construction stress.
- [ ] Add chaos campaigns for packet delay/drop, worker saturation and bridge disconnect.
- [ ] Add OpenTelemetry/MCP trace propagation adapter.
- [ ] Add SBOM and dependency-license report.
- [ ] Add signed release and rollback playbook.
