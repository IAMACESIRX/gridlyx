# Test strategy

Testing is layered because no single harness can validate JVM bytecode, Minecraft world semantics, rendering, native code and cross-process behaviour at once.

1. **Static gates** — build lock, script gatekeeper, version/provenance checks, Spotless and Checkstyle.
2. **Logic tests** — JUnit and deterministic test doubles/mocks for clocks, registries, bridge endpoints and procedural rules.
3. **Architecture tests** — ArchUnit prevents ordinary mod code from depending on isolated advanced internals and can grow into package/layer constraints.
4. **Bytecode tests** — ASM verification, `javap` structural diffs and redefine compatibility tests.
5. **Native/polyglot tests** — ABI conformance, Rust/C++ builds and GraalJS/GraalPy smoke tests.
6. **Headless Minecraft tests** — NeoForge GameTestServer for registry/data/world logic. MCTester can be attached as an optional adapter only when its target version is confirmed.
7. **Interactive client tests** — rendering, input, resource hotload, GPU pipeline, tool-gun interaction and visual/collision correctness.
8. **Chaos/performance tests** — JFR baselines, worker saturation, network delay/drop, bridge disconnect, reload storms and rollback.

A lower layer passing never implies that higher-layer Minecraft behaviour is correct.
