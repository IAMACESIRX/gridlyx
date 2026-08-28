# Gridelyx Studio Hotload Failure Engineering

Gridelyx treats failure analysis as an architecture input. The question is not only "how can this work?" but also "what design choices would make the public hotload goal fail?"

## Project-killing failure modes

| Failure mode | Why it kills the goal | Counter-design |
|---|---|---|
| Hotload is treated as dev-only | The public product never acquires the lifecycle, UX or safety machinery it needs | public hotload is a product requirement and CI contract |
| Hotload is equated with same-JVM redefine | JVM structural limits become product limits | H0-H6 activation bands + Runtime Epoch Handoff |
| Reloaded modules own anonymous global side effects | old hooks/timers/listeners survive replacement | Module Scope ownership and deterministic teardown |
| Static references root old revisions | classloaders cannot unload and stale state wins | versioned handles + leak auditor |
| Registries are assumed immutable forever | new/removable content cannot be first-class | registry virtualization + tombstones + migration |
| Registries are mutated without transaction boundaries | failed activation corrupts world/runtime state | staged activation + journal + rollback |
| Loader lifecycle is replayed without dependency ownership | dependent mods point at retired services | explicit dependency graph and topological rebind/reload |
| Server hotloads without client negotiation | protocol/assets/code diverge | revision handshake and compatibility negotiation |
| Patch surface sprawls across Minecraft | updates become unmaintainable | patch choke points, delegate defaults, fingerprint tests |
| Failure is swallowed | system appears live while state is partially invalid | fail closed at authority boundaries; quarantine/rollback |
| Every loader/version is attempted before one vertical slice | integration surface explodes without evidence | prove one end-to-end slice, then expand the matrix |
| World state has no migration model | content replacement breaks saves | logical IDs, schema versions, migrators and tombstones |
| Native/untrusted code shares unrestricted authority | one crash or exploit kills the runtime | process isolation and explicit trust bands |
| Runtime handoff has no rollback | a bad structural update strands the session | keep last-known-good epoch until successor passes health checks |
| Hotload latency is ignored | technically live becomes unusable | budget each H band; prewarm/cache next epoch |
| Observability is absent | leaks and half-applied transitions are impossible to diagnose | activation ledger, resource ownership report, epoch telemetry |

## Failure-injection validation

Gridelyx should deliberately test:

1. module throws halfway through activation;
2. module throws halfway through teardown;
3. event listener refuses to detach;
4. thread ignores interruption;
5. old classloader remains strongly reachable;
6. registry overlay conflicts with an existing logical ID;
7. client disconnects during revision negotiation;
8. world mutation succeeds then its caller throws;
9. state migrator produces an incompatible schema;
10. next JVM starts but fails health checks;
11. next JVM starts with a different mapping fingerprint;
12. native bridge process crashes during handoff;
13. asset revision races render upload;
14. dependency cycle appears during reload;
15. a package requests a higher trust band than the user granted.

Each test needs an expected containment boundary and recovery result, not merely "does not crash."

## Required invariants

- No new revision becomes authoritative before validation.
- Every owned side effect is discoverable from its Module Scope.
- Every world-affecting activation is journaled.
- A failed H0-H5 activation restores the prior authoritative revision or reports a hard containment failure.
- An H6 handoff keeps the prior epoch recoverable until the successor is healthy.
- A classloader/process that fails to retire is observable and cannot silently accumulate forever.
- Public UX reports the selected activation band and any capability escalation.

## Expansion discipline

Compatibility work grows from validated vertical slices:

`one Minecraft version + one loader + H0-H3`
→ `H4 registry virtualization`
→ `H5 loader lifecycle replay`
→ `H6 epoch handoff`
→ `second loader`
→ `legacy JVM lane`
→ `broad version matrix`.

This is not a constraint on ambition. It is the evidence order that prevents a wide compatibility claim from outrunning the mechanisms needed to make it real.
