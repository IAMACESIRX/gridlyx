# Gridelyx stakeholder dashboard

This page is the bird's-eye view. It summarizes the program without replacing the evidence-bearing sources: [`FEATURE_MAP.md`](FEATURE_MAP.md), [`ROADMAP.md`](ROADMAP.md), [`TODO.md`](TODO.md), [`DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md) and [`CHAT_REQUIREMENTS_TRACEABILITY.md`](CHAT_REQUIREMENTS_TRACEABILITY.md).

## 3-bullet value proposition

- **One cross-edition creation platform:** Gridelyx combines launcher/instance management, Polyloader/UAL interoperability, live world and asset authoring, scripting/native bridges, Java + Bedrock adaptation and production tooling behind one capability model.
- **Develop inside the living game:** the target workflow is edit → compile/script → hotload → inspect → test without unnecessary restarts, with in-game IDE/AI control, transactional world changes, multiplayer-aware authoring and last-known-good recovery.
- **Ambitious without hiding uncertainty:** every capability is tied to CR requirements, dependencies, R0-R6 evidence, target/version fingerprints, rollback and Feature Decision Packets so stakeholders can distinguish vision, framework, tested behavior and release readiness.

## Portfolio Kanban

> Kanban is a workflow view, not a substitute for dependency order or evidence. [`docs/DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md) owns the critical-path interpretation.

| Backlog | Ready / framed | Doing | Verifying | Done / control-plane complete |
|---|---|---|---|---|
| Terraria liquid cells | Vanilla launcher/runtime acquisition | Gridelyx terminology/ABI migration | Advanced Java/Bedrock target integration | CR-001–CR-035 retained-scope control |
| Full historical version-family matrix | First real Polyloader adapter pair | Toolchain reproducibility hardening | Target-specific world editor adapters | AI context/index/handoff control plane |
| Professional production passes | Modrinth + authorized CurseForge providers | Remote reference-vault completion | Multiplayer edit consensus/culling | Community onboarding baseline |
| Deep patch-manager implementation | Live creator runtime integration | Documentation/site/release-communication layer | Renderer/collision/microgeometry evidence | Feature Decision Framework |
| Bedrock parity extensions | Transactional Java world adapter | Repository metadata rename to `gridlyx` | Native bridge conformance | Core Studio provider/resolver contracts |

## Current critical path

```text
reproducible acquisition
        ↓
instance + content lock
        ↓
loader adapters + dependency solver
        ↓
UAL / capability negotiation
        ↓
real target adapters
        ↓
live creator/world/runtime integration
        ↓
multiplayer + fault containment
        ↓
cross-edition validation
        ↓
production + release hardening
```

## Executive health indicators

| Indicator | Current interpretation | Canonical source |
|---|---|---|
| Scope retention | CR ledger is machine-checked; implementation remains multi-phase | `platform/chat-requirements.json` |
| Product identity | Gridelyx / Gridelyx Studio frozen; legacy ABI migration remains | `platform/brand.json` |
| Java canonical lane | Minecraft 26.2 / NeoForge 26.2.0.67 / Java 25 | `platform/versions.json` |
| Cross-edition target | Java + Bedrock behind capability adapters | [`FEATURE_MAP.md`](FEATURE_MAP.md) |
| AI continuity | deterministic context map/index + handoff and decision ledgers | `ai/` |
| Large reference payload | manifest/control ready; remote binary import still pending while marker exists | [`vault/REMOTE_BINARY_IMPORT_PENDING.md`](../vault/REMOTE_BINARY_IMPORT_PENDING.md) |
| Repository rename | requested `IAMACESIRX/gridlyx`; GitHub metadata action tracked separately | `platform/repository-metadata.json` |

## Stakeholder filters

- **Vision:** [`PROJECT_OVERVIEW.md`](PROJECT_OVERVIEW.md), hero graphic, architecture diagrams.
- **Delivery:** [`ROADMAP.md`](ROADMAP.md), [`DEVELOPMENT_MAP.md`](DEVELOPMENT_MAP.md), this Kanban.
- **Evidence:** [`FEATURE_MAP.md`](FEATURE_MAP.md), CI, testing documents.
- **Cost/risk:** [`FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md), Feature Decision Packets, impact-effort matrix.
- **Technical depth:** architecture subsystem documents and source.
- **Community:** [`COMMUNITY.md`](../COMMUNITY.md) and `docs/community/`.
- **Release communication:** [`RELEASE_NOTES_AND_CHANGELOGS.md`](RELEASE_NOTES_AND_CHANGELOGS.md), [`CHANGELOG.md`](../CHANGELOG.md).
