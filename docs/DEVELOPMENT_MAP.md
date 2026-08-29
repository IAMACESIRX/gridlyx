# Gridelyx development map

This map complements [`ROADMAP.md`](ROADMAP.md), [`FEATURE_MAP.md`](FEATURE_MAP.md) and [`TODO.md`](TODO.md). It focuses on dependency order, parallel work, decision gates and Kanban state rather than repeating every feature description.

## Program topology

```text
Project control / CR traceability / toolchain / feature analysis
    |
    +--> Launcher + legitimate acquisition + instance model
    |       |
    |       +--> loader adapters + dependency solver + pack management
    |               |
    |               +--> Polyloader bootstrap / UAL / version fingerprints
    |
    +--> Neutral capability + permission + transaction contracts
    |       |
    |       +--> non-Java SDK / MCP / sidecars / native bridges
    |       +--> live IDE / AI automation / hotload supervisor
    |       +--> multiplayer authority / consensus / replication culling
    |
    +--> World mutation transaction engine
    |       |
    |       +--> async section blitter / live structures / ores / events
    |       +--> liquids / paint / transmutation
    |       +--> Bedrock world adapter
    |
    +--> Neutral scene + geometry + asset model
    |       |
    |       +--> model/texture editor
    |       +--> microgeometry / custom collision / rendering
    |       +--> physics / constraints / tool-gun / gizmos
    |       +--> Bedrock scene/render adapters
    |
    +--> Replay / timeline / camera / animation model
            |
            +--> capture / audio / render passes / machinima workflows
```

## Current critical path

The current program-level critical path is:

1. **Control plane remains trustworthy** — CR traceability, dependency inventory, feature-analysis framework, AI context and evidence rules.
2. **Launcher/runtime acquisition works end-to-end** — supported login, Mojang metadata, managed Java, assets/libraries and isolated vanilla launch.
3. **Loader/content resolver works end-to-end** — Fabric/Quilt/Forge/NeoForge adapters, provider APIs and complete dependency explanation graph.
4. **Polyloader/version abstraction gains one real cross-loader proof** — prelaunch bootstrap, fingerprints, UAL mapping and one validated source-loader→foreign-loader adapter pair.
5. **Capability/permission/transaction primitives become shared runtime contracts** — required before safe live AI, external tools and multiplayer mutation can converge.
6. **Exact world/render/scene target adapters connect the existing frameworks to real Java runtime behaviour**.
7. **Fault containment + hotload supervisor gains last-known-good recovery across script/service/process scopes**.
8. **Bedrock adapters consume the same neutral contracts and explicitly report parity gaps**.
9. **Production/replay/animation/capture consumes stable scene/runtime contracts**.
10. **Release hardening** — migration, SBOM/provenance, compatibility matrix, signed packages and strict terminology/toolchain enforcement.

A feature may proceed in parallel if it does not depend on an unfinished predecessor and its interface is intentionally experimental/versioned.

## Parallel work lanes

### Lane A — Control / AI / community
CR-002, CR-007, CR-009, CR-031, CR-032, CR-033, CR-034.

### Lane B — Launcher / resolver / ecosystem
CR-001, CR-003, CR-010, CR-011, CR-025, CR-026.

### Lane C — Runtime / hotload / non-Java
CR-004, CR-006, CR-012, CR-020, CR-021, CR-023.

### Lane D — World / multiplayer / simulation
CR-005, CR-013, CR-014, CR-015, CR-022, CR-028.

### Lane E — Geometry / assets / rendering / physics / scene
CR-016, CR-017, CR-018, CR-019, CR-029.

### Lane F — Bedrock
CR-024 plus parity work from C/D/E/F production.

### Lane G — Production
CR-027 with dependencies on scene, rendering, animation, replay and capture primitives.

### Lane H — Deep integration
CR-030, invoked when evidence demonstrates that shallower supported mechanisms cannot satisfy a retained requirement.

## Kanban model

Repository/project work uses:

- **Backlog** — retained but not shaped enough for execution.
- **Ready** — Feature Decision Packet sufficiently framed; prerequisites understood.
- **Doing** — active implementation/research.
- **Blocked** — waiting on external dependency, target evidence or unresolved architectural decision.
- **Verifying** — implementation exists and relevant validation is running/being collected.
- **Done** — required evidence for the claimed readiness exists and docs/manifests/issues are synchronized.

Never move an item to Done because a source file merely exists.

## Development horizon map

### 10 minutes
Clarify CR IDs, target, authority boundary, current evidence, risk and first reversible probe.

### 10 hours
Produce/refresh Feature Decision Packet, reference research, contract/schema or thin proof.

### 10 days
Deliver a reproducible vertical slice and automated evidence around the dominant risk.

### 10 months
Target integrated R4/R5 behaviour for the relevant workstream, not just framework code.

### 1 year
Maintain compatibility matrix, migrations, performance budgets, community docs and operational recovery.

### 5 years
Preserve neutral project data, adapters and capability contracts so game/loader/runtime implementations can be replaced.

### 10 years
Ensure archived Gridelyx projects remain understandable/recoverable and do not depend on vanished opaque services without migration/export paths.

## Milestone dependency summaries

| Milestone | Must have before exit | Parallel work allowed |
|---|---|---|
| Control-plane convergence | CR/checks/AI/toolchain/feature-analysis truth | all architecture research |
| Vanilla launcher | auth + metadata + Java + libraries/assets + isolated launch | editor/runtime prototyping |
| Modded launcher | loader adapters + provider resolver + dependency graph | Polyloader fingerprint research |
| Polyloader proof | prelaunch bootstrap + UAL + real adapter pair + failure classification | world/scene neutral contracts |
| Creator MVP | IDE/hotload + world transaction + model/scene + permission system | advanced Bedrock/native adapters |
| Multiplayer creator | server authority + revision consensus + culling + rollback | production authoring |
| Bedrock parity milestone | target adapters + capability matrix + recovery | Java optimization |
| Production MVP | replay/timeline/camera/animation/capture | release hardening |
| Release candidate | R6 evidence, migration/rollback, provenance, supported toolchain matrix | next-version experiments |

## Feature intake rule

Every new substantial feature must:

1. map to an existing CR or create a new CR;
2. use [`FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md);
3. state prerequisites and critical-path effect;
4. enter Kanban with a real state;
5. identify readiness/evidence target;
6. update [`FEATURE_MAP.md`](FEATURE_MAP.md), [`ROADMAP.md`](ROADMAP.md), [`TODO.md`](TODO.md) or target manifests when project truth changes.
