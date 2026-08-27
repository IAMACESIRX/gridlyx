# Architecture workflow

The platform uses a gated path rather than allowing generated code to flow directly into a release JAR.

`request -> decompose -> select workspace -> consult references -> implement -> format/lint -> static scan -> datagen -> compile -> tests/GameTests -> runtime diagnostics -> JAR audit -> review -> release`

## Layers

1. **Platform control**: version locks, policies, CI, workspace tooling and reference indexes.
2. **Template**: conservative NeoForge starter architecture and generators.
3. **Mod workspaces**: independent `mods/<mod_id>` builds so multiple JARs can be developed side by side.
4. **Advanced engines**: bytecode, native, GPU, IPC and network interception code. Disabled by default and opt-in per workspace.
5. **Generated resources**: deterministic output under `src/generated/resources`; generated files are never treated as hand-authored truth.
6. **Validation**: Spotless, Checkstyle, CodeQL, Gradle checks, GameTests, diagnostics and JAR inspection.

## Advanced-engine rule

Low-level mechanisms must expose a narrow interface from normal gameplay code. A mod should remain buildable with `enable_advanced_engines=false`. Native calls, agents, class transformers and network injections need explicit lifecycle, capability checks, rollback/failure behaviour and bounded resource use.

## Evidence classes

- **Compile evidence** proves only type/build compatibility.
- **GameTest evidence** proves specified in-game behaviour under the test fixture.
- **Benchmark evidence** characterises performance under stated conditions.
- **Runtime/manual evidence** is required for rendering, driver/native and interaction paths that GameTest cannot exercise.
