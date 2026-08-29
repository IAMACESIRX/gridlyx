# Gridelyx advanced feature decision framework

This is the mandatory analysis framework for proposing, decomposing, prioritising and validating non-trivial Gridelyx features. It is diagnostic and planning infrastructure; it does **not** allow an agent to silently delete retained scope. A difficult or expensive feature remains retained until an explicit human-approved decision supersedes it.

## Purpose

Gridelyx combines launcher/runtime engineering, Minecraft/Bedrock integration, Polyloader compatibility, live world manipulation, rendering/physics, AI tooling, native code, production tooling and community-facing software. Large features therefore need analysis across technical feasibility, integration depth, cost, risk, reversibility, user value, compatibility, security, maintenance and long-term architectural effects.

Every major feature should produce a **Feature Decision Packet** before irreversible architecture is committed.

## 1. W5x5x5 interrogation model

The project uses the name **W5x5x5** for a repeated interrogation process. It is deliberately broader than conventional 5W1H.

### Positive questions

Ask each at least five times, going one causal/architectural level deeper on every pass:

- **Who?** Who uses it, owns it, operates it, validates it, maintains it and is affected by it?
- **What?** What capability, state, data, API, artifact, invariant and measurable outcome exists?
- **When?** When does it run, initialise, mutate, synchronize, reload, fail, recover and become obsolete?
- **Where?** Where does it live in repository/runtime/process/network/world/render/storage topology?
- **How?** How is it built, invoked, secured, synchronized, tested, rolled back and observed?
- **Why?** Why does the capability exist, why this layer, why now, why this design and why is the value worth its burden?

### Inverse questions

Ask each at least five times as well:

- **Who not?** Who must not control/use/receive/maintain it?
- **What isn't?** What is explicitly out of scope, not guaranteed, not authoritative or not interchangeable?
- **When isn't?** When must it not execute, mutate, hotload, replicate or claim support?
- **Where isn't?** Which process/thread/platform/version/loader/world/server/storage boundary must it not cross?
- **How not?** Which implementation approaches are unsafe, illegal, brittle, unverifiable or architecturally disallowed?
- **Why isn't?** Why are plausible alternatives not selected, why is a simpler/deeper approach insufficient, and why might the feature not deserve immediate execution?

### Five analysis perspectives

For each round, inspect at least these perspectives:

1. **User/product** — usability, discoverability, accessibility, workflow.
2. **Runtime/system** — performance, concurrency, memory, lifecycle, integration layer.
3. **Compatibility/ecosystem** — versions, loaders, Java/Bedrock, third-party providers/mods/tools.
4. **Safety/operations** — security, crash containment, rollback, observability, support burden.
5. **Project/evolution** — maintainability, extensibility, evidence, AI consumption, community contribution.

### Five evidence depths

Each conclusion should identify which evidence level supports it:

1. idea/assumption;
2. documented external fact or project contract;
3. static implementation/build evidence;
4. automated target/integration evidence;
5. interactive/release/operational evidence.

This W5x5x5 process is iterative, not arithmetic theatre: the objective is to expose hidden assumptions and second-order interactions before implementation hardens them.

## 2. Break the task down

Split the feature into small, independently testable work units. For every unit record:

- objective;
- inputs/outputs;
- owning subsystem;
- prerequisites/dependencies;
- target editions/versions/loaders/platforms;
- interfaces and data contracts;
- validation lane;
- rollback/recovery path;
- blocking assumptions;
- whether work can proceed in parallel.

Use a work-breakdown tree until leaf tasks can be implemented and verified without ambiguous ownership.

## 3. Check project values

Evaluate the feature against [`PROJECT_VALUES.md`](PROJECT_VALUES.md). Record alignment, tension and mitigation. Value mismatch is a diagnostic and design signal, not an automatic scope deletion mechanism.

## 4. Measure cost

Estimate, with ranges rather than false precision:

- engineering time;
- research time;
- compute/build/CI cost;
- hosting/network/storage cost;
- licensing/API/provider cost;
- hardware/test-device cost;
- contributor/maintainer energy;
- support/documentation burden;
- compatibility matrix expansion;
- long-term maintenance debt.

Cost analysis informs sequencing and architecture; it does not decide whether a retained capability exists.

## 5. Time-horizon decomposition — 10/10/10/10/1/5/10

Evaluate what can/should exist at each horizon:

- **10 minutes** — clarify problem, known constraints, first safe probe.
- **10 hours** — research/prototype/contract or first thin vertical slice.
- **10 days** — demonstrable subsystem or reproducible experiment.
- **10 months** — integrated product-quality capability target.
- **1 year** — maintenance, compatibility and ecosystem consequences.
- **5 years** — architectural durability, upstream evolution, migration burden.
- **10 years** — preservation, replacement, interoperability and whether data/projects remain recoverable.

For every horizon, list opportunity cost and what becomes easier/harder if the decision is made now.

## 6. Pros / cons / trade-off matrix

Record:

- direct benefits;
- indirect benefits;
- direct costs;
- indirect costs;
- technical debt introduced;
- technical debt removed;
- user complexity added/removed;
- ecosystem compatibility gained/lost;
- failure modes created/removed;
- alternatives and their trade-offs.

Do not use a simple vote count. Weight evidence, reversibility and project values.

## 7. Opportunity cost

For each significant feature, identify:

- work delayed by choosing it;
- shared infrastructure it unlocks;
- duplicated future work avoided;
- compatibility burden created;
- whether a platform primitive can serve multiple CR groups;
- whether the same outcome can be obtained through a cheaper reusable abstraction.

## 8. Regret minimisation

Ask:

- What would Gridelyx regret **not** having built if the project succeeds?
- What would it regret locking itself into too early?
- Which decision preserves future option value?
- Which data/protocol/project formats need durable forward migration even if implementation is replaced?

## 9. Reversible vs irreversible decisions

Classify each decision:

- **Type 2 / reversible** — prototype rapidly, instrument heavily, keep rollback.
- **Type 1 / difficult-to-reverse** — require stronger evidence, design review and migration/recovery plan.

Examples of higher irreversibility include persisted project schemas, public protocols, package namespaces, published APIs, world formats, native ABI, account/security architecture, repository history rewriting and destructive migrations.

## 10. Risk register

Record probability, impact, detectability, blast radius, mitigation, recovery and owner for:

- world/save corruption;
- client/server crash or deadlock;
- native memory corruption;
- performance regression;
- incompatibility/version drift;
- provider/API/legal/licensing failure;
- security/authority escalation;
- data-loss/migration failure;
- network desynchronization;
- user-experience failure;
- maintenance abandonment.

## 11. Inversion

Assume the task fails spectacularly. List exactly how to produce that failure, for example:

- mutate authoritative chunks from uncontrolled worker threads;
- trust arbitrary bridge clients;
- promise universal loader compatibility without adapters/evidence;
- bake one Minecraft mapping/version into every subsystem;
- hotload unbounded scripts in-process;
- patch binaries without fingerprints/rollback;
- tie project files to one renderer implementation;
- make UI and resolver use different truth.

Then design controls that prevent each failure path.

## 12. Second-order thinking

Map immediate and secondary effects. Example chain:

`microgeometry -> more geometry instances -> render/collision/network pressure -> chunk persistence changes -> editor tooling changes -> Bedrock parity burden -> project-format migration requirements`

For major decisions map at least two consequence hops and identify reinforcing/limiting feedback loops.

## 13. Eisenhower matrix

Classify execution, not retained scope:

- urgent + important — act/mitigate now;
- important + not urgent — schedule deliberately;
- urgent + lower importance — automate/delegate/contain;
- neither urgent nor important — keep in backlog if retained, or explicitly supersede if human-approved.

## 14. Venn diagrams and overlap analysis

Use set/overlap reasoning to identify shared primitives among features. Represent overlaps in text/graphs when a literal visual is unnecessary.

Typical Gridelyx overlaps:

- live world editor ∩ multiplayer ∩ rollback;
- microgeometry ∩ renderer ∩ collision ∩ physics ∩ network replication;
- Polyloader ∩ hotload ∩ bytecode ∩ version mapping;
- in-game IDE ∩ AI ∩ scripting ∩ permission system;
- Bedrock parity ∩ shared-memory bridge ∩ neutral scene/world/project formats;
- production ∩ replay ∩ animation ∩ rendering ∩ audio.

Prefer shared platform primitives over duplicated feature-specific engines.

## 15. Brainstorming

Generate multiple architecture candidates before convergence. Each brainstorm must separate:

- facts;
- assumptions;
- speculative ideas;
- rejected options;
- experiments needed to discriminate between options.

Novelty is not evidence.

## 16. First-principles thinking

Reduce the feature to truths that remain valid independent of Minecraft APIs. Examples:

- a world edit is a state transition over spatial data;
- multiplayer authoring is replicated authoritative state plus permissions and conflict handling;
- a model is geometry/material/transform/metadata, not necessarily a vanilla JSON model;
- a loader compatibility problem is lifecycle/API/bytecode/resource semantic translation;
- hotload is replacement of runtime state with bounded consistency and rollback.

Build neutral contracts upward, then adapt to Minecraft/Bedrock/loader implementations.

## 17. Benchmarking

Use [`BENCHMARKING_MATRIX.md`](BENCHMARKING_MATRIX.md) as the starting comparison set. For any feature:

1. choose relevant top-tier reference products/projects;
2. verify their **current** behaviour before relying on it;
3. identify workflow primitives rather than copying branding/UI;
4. reverse-engineer operational strengths, constraints and failure handling;
5. record what Gridelyx should emulate, exceed, reject or make platform-neutral.

Benchmarks are inspiration/evidence inputs, not architecture authority.

## 18. Feynman technique

Write a plain-language explanation that a non-specialist can follow:

- what problem exists;
- what Gridelyx does;
- what data moves where;
- what can go wrong;
- what happens when it fails.

If the explanation requires hiding critical lifecycle, authority or state transitions behind jargon, the design probably has unresolved gaps.

## 19. Minimum Viable Product

Define the smallest vertical slice that proves the riskiest assumption. The MVP should:

- use real target boundaries where risk lives;
- be reversible;
- produce measurable evidence;
- avoid pretending scaffolding equals product readiness;
- preserve a path toward the full retained requirement.

## 20. Timeboxing

For bounded research/probes, define a 30- or 60-minute investigation box before expanding scope. Timeboxing is used to force evidence acquisition, not to rush irreversible implementation.

## 21. Pre-mortem

Assume the feature shipped and failed. Work backward from:

- crashes/data loss;
- incompatibility;
- poor UX;
- unusable performance;
- impossible maintenance;
- security abuse;
- provider/licensing failure;
- community inability to contribute.

Convert the causes into tests, monitoring, architecture constraints and rollout gates.

## 22. Asymmetric risk assessment

Prefer experiments/architectures where failure is bounded but upside is large: isolated sidecars, feature flags, capability gates, transactional world mutations, generated derived runtimes, adapters and reversible project schemas.

Do not confuse high upside with permission to expose uncapped world/security/native failure domains.

## 23. Working backward

Write a concise future success statement describing the completed user experience and evidence. Then map backward:

`success -> release evidence -> integrated subsystems -> platform primitives -> experiments -> research questions`

The success statement must include compatibility and recovery behaviour, not only happy-path UX.

## 24. 80/20 / Pareto analysis

Identify the small set of primitives that unlock multiple retained requirements. Likely high-leverage Gridelyx primitives include:

- capability/permission model;
- neutral scene/world/edit transaction formats;
- version/fingerprint adapter registry;
- process/IPC bridge;
- hotload/recovery supervisor;
- dependency/provenance resolver;
- renderer/collision abstraction;
- deterministic project/replay timeline model.

Use Pareto analysis for sequencing, never to erase the remaining 80% of retained scope.

## 25. Critical Path Method

Model dependencies explicitly and identify the sequence that controls a milestone. [`DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md) records the current program-level path. Feature packets must record predecessor/successor dependencies and parallelizable work.

## 26. Cynefin classification

Classify the problem:

- **clear** — known procedure; execute and verify;
- **complicated** — expert analysis can determine a solution;
- **complex** — probe/sense/respond; use safe-to-fail experiments;
- **chaotic** — contain blast radius first, then diagnose;
- **confused** — decompose until subproblems fit other domains.

Most deep cross-version loader/Bedrock/hotload work is complex or complicated, not clear.

## 27. Kanban

Every implementation issue has at minimum:

- **To Do**
- **Doing**
- **Done**

Recommended expanded states:

`Backlog -> Ready -> Doing -> Blocked -> Verifying -> Done`

`Done` requires matching evidence. A source file existing is not enough.

## 28. Feature Decision Packet output

A complete packet includes:

1. CR IDs and feature name;
2. desired user outcome;
3. W5x5x5 analysis;
4. positive/inverse scope boundaries;
5. values alignment;
6. first-principles model;
7. architecture candidates + brainstorm;
8. benchmark references;
9. pros/cons/trade-offs;
10. cost/resource estimate;
11. 10m/10h/10d/10mo/1y/5y/10y horizons;
12. opportunity cost;
13. regret-minimisation analysis;
14. reversible/irreversible classification;
15. risk register;
16. inversion/pre-mortem;
17. second-order effects;
18. Eisenhower class;
19. overlap/Venn/shared-primitives analysis;
20. MVP/experiment;
21. timebox for first evidence;
22. asymmetric-risk strategy;
23. working-backward success statement;
24. Pareto/high-leverage primitives;
25. critical path/dependencies;
26. Cynefin class;
27. Kanban state;
28. tests/evidence required;
29. rollback/migration path;
30. unresolved assumptions and next decision trigger.

Use [`docs/templates/FEATURE_EVALUATION_TEMPLATE.md`](templates/FEATURE_EVALUATION_TEMPLATE.md) and the feature issue form. Machine contract: `platform/feature-analysis.schema.json`.
