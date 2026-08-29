# Gridelyx project values

These values guide architecture, sequencing and trade-offs. They are diagnostic constraints, not a mechanism for silently removing retained requirements.

## Core values

1. **Capability without fabricated support** — pursue ambitious capabilities, but distinguish planned/framework/tested/target-validated/release-ready states.
2. **User control** — expose simple workflows and expert detail from the same underlying truth; do not hide destructive or authority-changing operations.
3. **Legitimate acquisition and provenance** — use supported/authorized channels, verify artifacts and retain hashes/source/licensing state.
4. **Reversibility and recoverability** — design world edits, instance changes, hotload, patches, project formats and migrations with recovery paths.
5. **Fault containment** — isolate failures by thread/process/transaction/capability boundary; do not claim impossible crash immunity.
6. **Version and loader adaptability** — prefer neutral contracts, adapter registries, fingerprints and capability negotiation over hard-coded single-version assumptions.
7. **Cross-edition reuse without fake parity** — share neutral models between Java and Bedrock where useful while exposing real target-specific gaps.
8. **Security and least authority** — external tools, scripts, AI, network clients and native plugins receive explicit capabilities only.
9. **Performance as an architectural property** — measure render, network, world-edit, scripting, native and memory costs at the layer where they occur.
10. **Open engineering comprehension** — humans and AI should be able to understand why a subsystem exists, how it is validated and how to recover it.
11. **Community accessibility** — onboarding, issue structure, documentation and contribution boundaries should make advanced work approachable without flattening technical depth.
12. **Long-lived project data** — project/world/edit/replay/scene formats should remain migratable even if implementations are replaced.
13. **Shared primitives over duplicated engines** — favour reusable transaction, permission, scene, resolver, adapter, bridge and recovery infrastructure.
14. **First-principles architecture** — separate the underlying system problem from a particular Minecraft/loader API before selecting integration depth.
15. **Evidence-driven escalation** — use the shallowest reliable integration layer, but permit instrumentation/native/patch/project-owned components when evidence shows shallower paths cannot meet the retained requirement.
16. **No hidden dependencies** — every required compiler/runtime/program/provider/library belongs in the toolchain/dependency inventory.
17. **No silent scope drift** — CR requirements remain until explicitly superseded by human-approved decision.

## Decision questions

For every significant feature ask:

- Which values does this strengthen?
- Which values does it put in tension?
- Is the tension inherent or an implementation artifact?
- What guardrail reduces the tension?
- What evidence would show the chosen balance is wrong?

Use these results inside the Feature Decision Packet defined in [`FEATURE_DECISION_FRAMEWORK.md`](FEATURE_DECISION_FRAMEWORK.md).
