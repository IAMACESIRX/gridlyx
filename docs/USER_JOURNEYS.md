# Gridelyx user journey mapping

User journeys are product-planning tools. They describe intended experience and handoffs; they do not promote runtime readiness.

## Primary journey

Source: `diagrams/user-journey.mmd`.

```mermaid
journey
  title Gridelyx end-to-end user journey
  section Discover
    Understand the value proposition: 5: User
    Inspect readiness and evidence: 4: User, Stakeholder
  section Install
    Create an instance: 5: User
    Resolve Java, Minecraft and loader: 5: Gridelyx
    Resolve mods and dependencies: 4: Gridelyx
  section Create
    Open creator workspace: 5: Creator
    Edit assets, world and geometry live: 5: Creator
    Use in-game IDE or AI tools: 5: Creator, AI
  section Validate
    Run static, unit and GameTest lanes: 4: Developer, AI
    Inspect telemetry and rollback state: 4: Developer, Operator
  section Collaborate
    Connect players to live server: 5: Operator, Player
    Apply permissioned synchronized edits: 4: Creator, Server
  section Produce
    Record replay and animation: 4: Creator
    Build shots and export production: 4: Creator
  section Share
    Generate changelog and release notes: 4: Maintainer, AI
    Publish documentation and evidence: 5: Maintainer, Community
```

## Personas and success criteria

| Persona | Wants | Failure mode to avoid | Success evidence |
|---|---|---|---|
| Player | Install and play without dependency archaeology | broken instance / unexplained conflict | deterministic resolution + clean launch |
| Mod creator | fast code/content iteration | restart-heavy workflow and opaque errors | live reload where valid + targeted diagnostics |
| World builder | edit generated worlds while players are connected | corruption, desync, lighting/chunk breakage | transactional mutation + reconciliation + rollback |
| Technical artist | create custom geometry/assets in context | static-export loop with poor preview fidelity | live asset/mesh preview + renderer evidence |
| Server operator | safe collaborative editing | client authority or tick stalls | permissioned server authority + culling/budgets |
| AI/tool developer | control the environment through stable contracts | raw unrestricted memory/input automation | MCP/capability APIs + bounded permissions |
| Filmmaker | animate, record and reproduce scenes | nondeterministic takes and weak timing | rational timeline + replay/capture metadata |
| Maintainer | know what is actually ready | architecture docs mistaken for shipped support | R0-R6 evidence + release-note provenance |

## Journey instrumentation targets

Future telemetry should measure completion/error time for instance creation, dependency resolution, first creator edit, hotload, transaction rollback, multiplayer synchronization, capture/export and documentation discovery. Metrics must be privacy-aware and opt-in where user telemetry leaves the local machine.
