# Gridelyx Studio

![Gridelyx Studio concept map](assets/gridelyx-hero.svg)

[![Product](https://img.shields.io/badge/Product-Gridelyx%20Studio-5865F2?style=for-the-badge)](PROJECT_OVERVIEW.md)
[![Scope](https://img.shields.io/badge/Scope-Java%20%2B%20Bedrock-2563EB?style=for-the-badge)](FEATURE_MAP.md)
[![Requirements](https://img.shields.io/badge/Requirements-CR--001%E2%80%93CR--035-7C3AED?style=for-the-badge)](CHAT_REQUIREMENTS_TRACEABILITY.md)
[![Evidence](https://img.shields.io/badge/Evidence-R0%E2%80%93R6-0F766E?style=for-the-badge)](community/TESTING_AND_EVIDENCE.md)
[![Java](https://img.shields.io/badge/Java-25.0.4%2B7-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](DEPENDENCIES_AND_TOOLCHAIN.md)
[![NeoForge](https://img.shields.io/badge/NeoForge-26.2.0.67-8B5CF6?style=for-the-badge)](DEPENDENCIES_AND_TOOLCHAIN.md)
[![AI](https://img.shields.io/badge/AI-MCP%20%2B%20Context%20Index-111827?style=for-the-badge)](AI_CONTEXT_SYSTEM.md)
[![Native](https://img.shields.io/badge/Native-Rust%20%2B%20C%2B%2B%20%2B%20Panama-B7410E?style=for-the-badge)](ADVANCED_ENGINES.md)

## Value proposition

- **One cross-edition creation platform:** launcher, dependency resolver, Polyloader/UAL, world editing, live assets, scripts/native bridges, Java/Bedrock adapters and production tooling use one capability/evidence model.
- **Develop inside the living game:** Gridelyx targets in-game editing, IDE/AI control, hotload, transactional world mutation and multiplayer-aware authoring so iteration does not default to restart-and-alt-tab loops.
- **Evidence-first ambition:** requirements, versions, dependencies, risks, rollback and R0-R6 validation remain visible so vision is never presented as shipped support without proof.

## Choose your view

| You are… | Start here |
|---|---|
| Stakeholder / collaborator | [Stakeholder Dashboard](STAKEHOLDER_DASHBOARD.md) |
| Player / evaluator | [Project Overview](PROJECT_OVERVIEW.md) |
| Contributor | [Contributor Onboarding](community/CONTRIBUTOR_ONBOARDING.md) |
| Architect | [Architecture Diagrams](ARCHITECTURE_DIAGRAMS.md) |
| Feature owner | [Feature Decision Framework](FEATURE_DECISION_FRAMEWORK.md) |
| API/tool developer | [Interactive API Documentation](api/index.md) |
| Maintainer | [Roadmap](ROADMAP.md), [TODO](TODO.md), [Release Communications](RELEASE_NOTES_AND_CHANGELOGS.md) |
| AI agent | root [`AGENTS.md`](../AGENTS.md), [`AI_HANDOFF.md`](../AI_HANDOFF.md), then [`ai/context-map.json`](../ai/context-map.json) |

## Current visual map

```mermaid
flowchart LR
  Discover[Discover / Install] --> Create[Create / Modify Live]
  Create --> Validate[Test / Evidence]
  Validate --> Collaborate[Multiplayer / Share]
  Collaborate --> Produce[Record / Animate / Render]
  Produce --> Release[Docs / Changelog / Release Notes]
  Release -. feedback .-> Discover
```

The detailed system diagram is in [Architecture Diagrams](ARCHITECTURE_DIAGRAMS.md). Current readiness is in [Feature Map](FEATURE_MAP.md); retained scope is in [Chat Requirements Traceability](CHAT_REQUIREMENTS_TRACEABILITY.md).
