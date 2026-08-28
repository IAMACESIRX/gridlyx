# Gridelyx documentation-driven marketing

Gridelyx should market **what can be understood and evidenced**, not what can merely be imagined. Documentation is therefore part of the product surface: every externally communicated claim should lead to architecture, readiness, evidence, a demo, a roadmap item or an explicit experimental limitation.

## Core positioning

### One-line description

**Gridelyx Studio is a cross-edition Minecraft creation and development platform combining launcher/runtime management, Polyloader interoperability, live world and asset authoring, AI-assisted development, sandbox construction and production tooling.**

### Three value pillars

1. **Build across the ecosystem** — Java + Bedrock targets, loader adapters, scripts, native bridges and external tools behind capability contracts.
2. **Create in context** — in-game IDE/AI, live world editing, assets, geometry, physics, multiplayer authoring and restart-minimized iteration.
3. **Prove what works** — R0-R6 evidence, target fingerprints, rollback, dependency provenance and visible readiness rather than unsupported universal claims.

## Documentation funnel

```text
Hero / badges / 3 bullets
        ↓
Stakeholder dashboard
        ↓
Use-case / journey page
        ↓
Architecture diagram
        ↓
Feature map + readiness evidence
        ↓
Getting started / API / contributor guide
        ↓
Release notes / changelog / demos
```

Different audiences should enter at different depths without receiving contradictory descriptions.

## Claim-to-proof rule

| Marketing claim | Required proof surface |
|---|---|
| Supports a target | target capability manifest + R4/R5 evidence |
| Live reloads a feature | reload-scope documentation + integration test/demo |
| Safe world editing | transaction/reconciliation/rollback evidence |
| Cross-loader compatibility | named adapter pair + fixture/runtime evidence |
| Bedrock parity | parity matrix + exact API/native adapter evidence |
| High-performance editing | benchmark method + reproducible result |
| AI integration | capability/permission contract + observable tool flow |
| Production-ready | R6 packaging/migration/rollback evidence |

Words such as **planned**, **framework**, **experimental**, **validated** and **release-ready** must preserve their technical meaning.

## Reusable communication assets

- `docs/assets/gridelyx-hero.svg` — hero / concept map.
- `docs/STAKEHOLDER_DASHBOARD.md` — executive program status.
- `docs/ARCHITECTURE_DIAGRAMS.md` — diagrams as code.
- `docs/USER_JOURNEYS.md` — outcome-oriented journeys.
- `docs/FEATURE_MAP.md` — readiness/evidence matrix.
- `docs/api/` — interactive development API contract.
- `CHANGELOG.md` and generated release notes — visible project momentum.

## Release-content recipe

For a substantial release or milestone:

1. summarize the user-visible outcome;
2. list validated targets and known gaps;
3. show one architecture or workflow diagram if it improves comprehension;
4. link changed CR IDs and evidence;
5. show migration/rollback notes where relevant;
6. include a short developer/API section;
7. generate a concise social/community version from the same evidence packet rather than writing disconnected marketing copy.

## Documentation as demand discovery

Questions repeatedly asked by users/community should become navigation, glossary, examples or API recipes. High-traffic unanswered topics indicate product or documentation gaps and should feed Feature Decision Packets and the roadmap rather than remaining support-only knowledge.

## Anti-hype constraints

- Do not call planned cross-version compatibility universal support.
- Do not describe interfaces/framework classes as finished gameplay features.
- Do not claim crash-proof behavior; describe isolation/recovery boundaries.
- Do not imply affiliation with Mojang/Microsoft or comparison products.
- Do not publish private credentials, proprietary game source or restricted provider content.
