# Gridelyx skill: NeoForge 26.2 mod development

Skill ID: `gridelyx.neoforge-26.2.mod-development`

Target state:

- Minecraft Java: `26.2`
- NeoForge: `26.2.0.67`
- ModDevGradle: `2.0.144`
- Java language/toolchain: `25`
- Gradle: `9.2.1`
- canonical project root: [`../../../templates/neoforge-26.2/`](../../../templates/neoforge-26.2/)

This is a **routing and engineering skill**, not a copy of the NeoForge MDK, Minecraft source, NeoForge binaries or dependency archives.

## Mandatory reads

Before generating version-specific mod code, read:

1. [`../../../AGENTS.md`](../../../AGENTS.md)
2. [`../../../docs/AI_MODDING_REFERENCE_CORPUS.md`](../../../docs/AI_MODDING_REFERENCE_CORPUS.md)
3. [`../../../platform/versions.json`](../../../platform/versions.json)
4. [`../../../platform/reference-sources.json`](../../../platform/reference-sources.json)
5. [`../../../platform/toolchain-requirements.json`](../../../platform/toolchain-requirements.json)
6. [`../../../templates/neoforge-26.2/build.gradle`](../../../templates/neoforge-26.2/build.gradle)
7. [`../../../templates/neoforge-26.2/gradle.properties`](../../../templates/neoforge-26.2/gradle.properties)
8. the relevant Gridelyx template source/resources under [`../../../templates/neoforge-26.2/`](../../../templates/neoforge-26.2/) for the requested feature.

If `.reference-cache/index/reference-corpus.jsonl` exists locally, use it to locate exact upstream source/doc files before broad filesystem scanning.

## Evidence order

Use evidence in this order:

1. target-validated Gridelyx code/tests;
2. Gridelyx pinned build/template configuration;
3. pinned official NeoForge MDK;
4. [official NeoForged documentation](https://docs.neoforged.net/) for the target line;
5. [NeoForge](https://github.com/neoforged/NeoForge) / [ModDevGradle](https://github.com/neoforged/ModDevGradle) source inspection;
6. resolved dependency source/Javadocs;
7. locally generated Minecraft development source for exact vanilla signatures/behaviour;
8. hypothesis requiring compile/GameTest/runtime validation.

Never invent a class, method, event, registry key, descriptor or mapping because a similar API existed in another Minecraft version.

## Workspace rule

For a normal mod, work in an independent Gradle root under:

```text
mods/<mod_id>/
```

Use the canonical template as the source of structure. Keep ordinary gameplay code in `src/main`; only shared advanced engine mechanisms belong in the advanced runtime lane.

## Feature routing

### Mod bootstrap / metadata

Consult, in order:

- canonical template main mod class and metadata templates under [`../../../templates/neoforge-26.2/`](../../../templates/neoforge-26.2/);
- pinned MDK example identified in [`../../../platform/reference-sources.json`](../../../platform/reference-sources.json);
- [NeoForged documentation](https://docs.neoforged.net/) for mod files/getting started.

Ensure `mod_id`, package/group and metadata substitutions remain internally consistent.

### Registries / content

Use deferred/official NeoForge registry mechanisms appropriate to 26.2. Consult [NeoForged documentation](https://docs.neoforged.net/) and the exact target APIs before generating code.

Prefer a single registry controller per logical mod domain over scattered static registration.

For each new block/item/entity/etc. consider:

- registry object;
- creative tab exposure when relevant;
- model/blockstate/item model generation;
- language keys;
- loot tables/recipes/tags;
- client-only registration boundaries;
- dedicated-server classloading safety.

### Data generation

Prefer data providers and generated resources over hand-duplicated JSON when the format is stable and supported.

Generated state belongs in `src/generated/resources`; source/authored assets remain separate.

Validate generated output for determinism and review the diff before committing it.

### Events / lifecycle

Do not assume an event bus or lifecycle stage from older Forge/NeoForge versions. Resolve the exact 26.2 event/API using [official documentation](https://docs.neoforged.net/) or source.

Keep client-only event handlers out of dedicated-server classloading paths.

### Networking

Resolve the exact payload/channel registration APIs from current [NeoForged documentation](https://docs.neoforged.net/) or [NeoForge source](https://github.com/neoforged/NeoForge).

Enforce:

- explicit packet/payload schema;
- bounded lengths/counts;
- thread handoff to authoritative game/server thread where required;
- server-side validation and authorization for world mutations;
- version/compatibility handling;
- no blocking work on Netty event loops.

### Worldgen / codecs / registries

Use data-driven/codec-backed systems where supported. For a target that crosses registry freeze/lifecycle boundaries, classify the operation as build-time/datapack reload/runtime virtualized/deep integration rather than pretending all registry changes are live-safe.

### Rendering / models / GPU

Start with supported NeoForge/Minecraft rendering hooks. Escalate to Gridelyx advanced rendering only when ordinary hooks cannot express the required behaviour.

When using LWJGL/direct GPU paths:

- consult resolved LWJGL sources/Javadocs and the [LWJGL source repository](https://github.com/LWJGL/lwjgl3);
- respect render-thread/context ownership;
- validate buffer lifetime and native memory;
- batch/cull before claiming performance;
- keep target/version fingerprints explicit for intercepted engine code.

### Bytecode / Mixins / Instrumentation

Use exact descriptors and structural fingerprints from the resolved target. Consult Gridelyx advanced runtime plus [ASM documentation](https://asm.ow2.io/) and source references in [`../../../platform/reference-sources.json`](../../../platform/reference-sources.json).

Do not transform classes from memory.

Selection rule:

```text
compatible method-body change -> Instrumentation redefine may be safe
schema/field/method structure change -> replaceable classloader/service epoch
loader/bootstrap transformation -> prelaunch transformation lane
unsafe/unknown target fingerprint -> fail closed
```

### Scripts / polyglot

GraalVM guest code is capability-gated. Use explicit host access, bounded execution and clear fault domains. External connection does not imply world/server authority. Consult the [GraalVM embedded-language documentation](https://www.graalvm.org/latest/reference-manual/embed-languages/) for exact host/guest APIs.

## Dependency lookup

Declared Java dependencies are resolved by Gradle, not copied into the repository.

Relevant coordinates include:

```text
org.ow2.asm:asm:9.10.1
org.ow2.asm:asm-commons:9.10.1
org.lwjgl:lwjgl:3.4.1
org.lwjgl:lwjgl-opengl:3.4.1
org.lwjgl:lwjgl-glfw:3.4.1
org.graalvm.polyglot:polyglot:25.3.4.1
org.graalvm.polyglot:js:25.3.4.1
org.graalvm.polyglot:python:25.3.4.1
org.junit:junit-bom:6.1.3
com.tngtech.archunit:archunit:1.4.2
```

Treat [`../../../platform/versions.json`](../../../platform/versions.json) and the canonical Gradle configuration under [`../../../templates/neoforge-26.2/`](../../../templates/neoforge-26.2/) as authoritative if versions change.

## Local reference commands

Validate acquisition policy with [`../../../tools/hydrate_references.py`](../../../tools/hydrate_references.py), [`../../../tools/redistribution_guard.py`](../../../tools/redistribution_guard.py), and [`../../../tools/history_redistribution_guard.py`](../../../tools/history_redistribution_guard.py):

```bash
python tools/hydrate_references.py --check
python tools/redistribution_guard.py
python tools/history_redistribution_guard.py
```

Hydrate the pinned MDK with [`../../../tools/hydrate_references.py`](../../../tools/hydrate_references.py):

```bash
python tools/hydrate_references.py --mdk
```

Hydrate the high-value AI corpus with [`../../../tools/hydrate_ai_references.py`](../../../tools/hydrate_ai_references.py):

```bash
python tools/hydrate_ai_references.py --core
```

Build indexes with [`../../../tools/build_reference_indexes.py`](../../../tools/build_reference_indexes.py):

```bash
python tools/build_reference_indexes.py --corpus
```

Resolve development dependencies from a clean/refreshing Gradle cache using the canonical template [`../../../templates/neoforge-26.2/`](../../../templates/neoforge-26.2/):

```bash
cd templates/neoforge-26.2
gradle --no-daemon --refresh-dependencies help
```

## Validation ladder

For generated mod code, use the strongest applicable sequence:

1. platform/static validators;
2. Spotless;
3. Checkstyle;
4. Java compilation;
5. unit tests;
6. ArchUnit rules;
7. data-generation validation;
8. JAR inspection;
9. NeoForge GameTest/server validation;
10. client/render/runtime validation when feature-specific;
11. multiplayer validation for network/authority changes.

A compiling implementation is not automatically a working Minecraft integration.

## AI output discipline

When a requested API is uncertain, return or record one of:

- `FACT` — directly supported by pinned project/upstream evidence;
- `DERIVED` — mechanically inferred from verified evidence;
- `ASSUMPTION` — plausible but not verified;
- `REQUIRES VALIDATION` — must be compiled/tested against target runtime;
- `UNKNOWN` — insufficient source evidence.

Do not silently replace an uncertain exact API with pseudocode while presenting it as target-ready code.

## Copyright / provenance discipline

The skill may quote only minimal source fragments needed for explanation and must preserve applicable attribution/licence requirements.

Do not copy generated/decompiled Minecraft source into project files, documentation, skill text or a public training dataset. Use local source retrieval to answer exact questions, then write original Gridelyx/mod code that interacts with the API.

Do not commit hydrated MDK/NeoForge/docs/library source trees. They remain `.reference-cache` state.

## Definition of success

A successful use of this skill produces code that:

- matches the pinned target versions;
- is grounded in current source/docs rather than cross-version memory;
- preserves public-repository acquisition/licensing boundaries;
- includes required resources/datagen/metadata, not Java-only scaffolding;
- compiles and passes relevant static tests;
- has explicit runtime validation status;
- leaves no upstream binary/source payload in Git or reachable Git history.
