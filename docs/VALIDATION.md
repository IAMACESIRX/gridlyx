# Validation Model

A successful `gradlew build` proves compilation and packaging, not gameplay correctness.

## Gates

| Gate | What it catches |
|---|---|
| Static platform validator | version drift, malformed mod IDs, missing metadata, forbidden reference coupling |
| Gradle build | Java/API errors, resource processing, packaging failures |
| Unit tests | pure deterministic logic |
| Dependency report | conflicting/duplicated dependency choices |
| NeoForge GameTest | in-world behavioural invariants |
| JAR audit | missing metadata/resources or empty output |
| Manual client/server run | rendering, UX, integration and behaviour not covered by tests |

## GameTest

NeoForge exposes the `gameTestServer` run type; when a workspace actually contains registered tests it can be run with `./gradlew runGameTestServer`. Do not make it an unconditional gate for empty starter mods.

## Failure policy

A failed build/test is evidence. Preserve the error and fix the smallest responsible layer. Do not work around API mismatches with reflection, raw mixins or native hooks until the ordinary supported API path has been ruled out.
