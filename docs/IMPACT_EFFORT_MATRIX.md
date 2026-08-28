# Gridelyx impact-effort matrix

Impact-effort analysis is a **diagnostic for sequencing**, not a scope-deletion mechanism. Retained CR requirements remain until explicitly superseded.

Source: `diagrams/impact-effort.mmd`.

```mermaid
quadrantChart
  title Gridelyx portfolio impact vs effort
  x-axis Low effort --> High effort
  y-axis Low impact --> High impact
  quadrant-1 Strategic bets
  quadrant-2 Quick wins
  quadrant-3 Defer / validate first
  quadrant-4 Platform investments
  "README + docs portal": [0.18, 0.82]
  "Stakeholder dashboard": [0.22, 0.74]
  "Automated changelog": [0.34, 0.68]
  "Interactive API docs": [0.40, 0.72]
  "Launcher MVP": [0.62, 0.94]
  "Live world editor target adapter": [0.72, 0.90]
  "Polyloader adapter pair": [0.76, 0.88]
  "Bedrock parity adapters": [0.88, 0.86]
  "Microgeometry renderer/collision": [0.81, 0.82]
  "Professional machinima pipeline": [0.78, 0.70]
  "Full historical version matrix": [0.95, 0.68]
```

## Scoring model

Use 0.0–1.0 scores and record assumptions in the Feature Decision Packet.

**Impact** considers user reach, critical-path unlocks, risk reduction, evidence gained, architectural reuse and strategic differentiation.

**Effort** considers engineering time, maintenance burden, target/version fragmentation, external dependencies, testing matrix, security blast radius and migration/rollback cost.

Scores are deliberately approximate until measured. A high-effort/high-impact feature is a strategic bet, not a rejection candidate.

## Review cadence

Re-score after major target-version changes, MVP evidence, new loader/Bedrock APIs, benchmark discoveries, critical-path changes or a Feature Decision Packet that materially changes cost/risk assumptions.
