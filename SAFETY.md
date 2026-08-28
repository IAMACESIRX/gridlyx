# Gridelyx Engineering Safety Policy

## Purpose

Gridelyx deliberately works close to engine, runtime, memory, networking and world-state boundaries. This document defines the engineering safety rules for developing, testing and operating those capabilities.

`SAFETY.md` is about preventing accidental harm, corruption, data loss, unsafe execution and uncontrolled failure. Security vulnerabilities and hostile abuse are covered by `SECURITY.md`; contributor behaviour is covered by `CODE_OF_CONDUCT.md`.

## Safety model

Gridelyx uses four principles:

1. **Bound the blast radius.** Experimental work should affect the smallest possible process, world, instance, file set or network scope.
2. **Preserve recovery.** Consequential changes should have rollback, checkpoint, snapshot, journal or last-known-good paths when technically feasible.
3. **Separate preparation from authority.** Expensive or speculative work may happen off-thread/off-process, but authoritative Minecraft mutation occurs only through the correct target boundary.
4. **Escalate intentionally.** When a shallow integration cannot safely realise a change, Gridelyx may escalate to instrumentation, classloader replacement, native integration or runtime epoch handoff, but the stronger mechanism must be explicit and evidence-gated.

## Safety classifications

Gridelyx changes should be thought of in practical risk bands:

- **S0 — Passive:** documentation, inspection, read-only analysis, metadata queries.
- **S1 — Reversible local:** temporary UI/assets/scripts/configuration with deterministic unload/reset.
- **S2 — Stateful:** instance configuration, content locks, live world edits, persistent project data, mod graph changes.
- **S3 — Privileged runtime:** Java agents, bytecode transformation, native memory, shared IPC, GPU/native buffers, custom networking, executable/runtime patching.
- **S4 — Destructive or externally consequential:** irreversible world/file deletion, credential changes, broad network exposure, production migration, destructive patching or actions affecting systems/users outside the local test boundary.

Higher-risk work requires proportionally stronger validation, isolation and recovery planning.

## General engineering rules

- Never treat "compiled" as equivalent to "safe" or "works."
- Never treat a class, API stub, adapter or architecture document as proof of runtime readiness.
- Prefer deterministic/reproducible operations over hidden mutable state.
- Preserve immutable originals for patchable/downloaded artifacts whenever possible.
- Do not perform destructive migration in place when copy/verify/switch is feasible.
- Fail closed at ambiguous compatibility, authority or integrity boundaries.
- Make dangerous/privileged modes explicit rather than silently activating them.
- Surface meaningful diagnostics when a safety mechanism blocks an operation.

## World and save safety

Live world mutation can permanently corrupt or semantically damage a save even when the JVM remains healthy.

Required practices for consequential world edits include:

- calculate/prepare changes away from the authoritative live mutation path when possible;
- perform live chunk/world mutation on the correct server/engine thread;
- preflight expected revisions before committing multi-section transactions;
- prepare inverse deltas or equivalent rollback state before mutation;
- reconcile lighting, heightmaps, block entities, POI, save state and client synchronisation as required by the target version;
- bound the number of chunks/sections changed by one transaction;
- journal or snapshot persistent operations where rollback must survive a crash;
- test rollback after failures that occur **mid-commit**, not only before the first mutation;
- never assume an unloaded or partially generated chunk is equivalent to a fully materialised one.

Experimental world-edit logic should be tested on disposable worlds before valuable saves.

## Runtime reload and hotload safety

Hotload means continuity of the user workflow, not that every change must mutate the same JVM in place.

The reload orchestrator should choose the smallest safe activation mechanism:

- data reload for declarative state;
- revisioned asset swap for resources;
- scoped script epoch for scripts;
- Java redefinition only for compatible class changes;
- classloader/service replacement for module revisions;
- registry/lifecycle virtualization where supported;
- runtime epoch handoff for structural or bootstrap changes.

A failed new revision must not silently become authoritative. Stage, validate, switch, then retire the old revision.

Old script/module scopes must release owned listeners, commands, timers, threads, network registrations, resources and native handles before being considered retired.

## Java instrumentation and bytecode safety

Instrumentation can destabilise an entire JVM.

- Transform only expected targets.
- Fingerprint target classes/versions where practical.
- Treat class-shape changes as structural unless proven supported.
- Do not assume HotSwap can add/remove fields, methods, inheritance or arbitrary early transforms.
- Preserve original bytes or an equivalent known-good recovery route for reversible transforms.
- Avoid swallowing transformation failures without telemetry.
- Never attach the Gridelyx agent to unrelated JVMs without explicit operator intent.
- Early/bootstrap transforms should be validated against the exact runtime fingerprint they target.

When the current JVM cannot safely absorb the requested change, prefer runtime epoch handoff over forcing an unsupported mutation.

## Native, FFM and memory safety

Native code can corrupt process memory or terminate the process without Java-level recovery.

- Every FFM function descriptor must exactly match the native ABI.
- Validate pointer/segment lifetime and ownership.
- Validate capacity before copying or publishing data.
- Keep memory regions bounded and versioned.
- Do not retain a native pointer beyond the lifetime contract that created it.
- Avoid sharing mutable memory between threads/processes without an explicit consistency protocol.
- Treat ABI mismatch as a hard failure.
- Keep native symbols narrow and stable within a declared ABI version.
- Prefer separate worker processes for untrusted or failure-prone native extensions.

A native crash is a process failure. Do not design recovery logic that assumes Java finally-blocks will run after arbitrary memory corruption.

## Shared memory and IPC safety

Shared memory is fast but bypasses many safety properties of higher-level transports.

- Use explicit headers, version/magic, capacity, sequence/publication state and integrity checks.
- Readers must reject torn/changed publications.
- Writers must publish only after payload completion.
- Do not use a single latest-frame region where guaranteed delivery is required unless ACK/ring-buffer semantics are layered above it.
- Never encode raw process pointers or JVM object references into portable messages.
- Define ownership and lifetime for every buffer crossing a process boundary.
- Authenticate or otherwise constrain any IPC path that can cause privileged operations.

## Concurrency and worker safety

Parallelism is used for preparation and computation, not as permission to mutate non-thread-safe engine state.

- Bound worker-pool size and queue depth.
- Provide cancellation/deadline semantics where tasks may stall.
- Assume Java interruption is cooperative, not a hard kill mechanism.
- Avoid blocking filesystem watcher, render, network or server threads with heavy callbacks.
- Preserve ordering where operations are causally dependent.
- Use revision/epoch identifiers to reject stale asynchronous work.
- Treat race-condition fixes as requiring stress/repetition tests, not one successful run.

## Network safety

Custom Netty, HTTP, WebSocket or other control surfaces can expand Gridelyx's blast radius beyond the local machine.

- Bind loopback by default.
- Require authentication/authorisation before remote privileged control.
- Preserve Netty ordering, backpressure and disconnect semantics when injecting handlers.
- Bound packet/frame/message sizes.
- Validate schemas before dispatch.
- Rate-limit or otherwise bound remotely triggerable expensive work.
- Never trust a client to enforce server-authoritative world permissions.

## Multiplayer safety

Shared-state edits can affect every connected player.

- The server remains authoritative for shared world state.
- Require compatible revision/protocol negotiation where an edit depends on client content.
- Do not commit an authoritative migration merely because one client loaded successfully.
- Make rollback/mixed-revision behaviour explicit.
- Avoid protocol experiments on public servers without administrator consent and a recovery plan.
- Keep test/chaos traffic isolated from production multiplayer services.

## AI-generated and dynamically generated code

AI and procedural generation can accelerate development but can also create plausible-looking unsafe code.

Generated implementation must be reviewed before privileged execution, especially when it touches:

- native pointers or FFM;
- authentication/credentials;
- filesystem deletion or migration;
- network listeners;
- bytecode/instrumentation;
- thread synchronisation;
- world/save mutation;
- serialization/deserialization;
- process execution;
- updater/download logic.

AI-generated test claims, citations and API/version claims require verification. The model's confidence is not runtime evidence.

Do not feed secrets, private keys, account tokens, sensitive user data or unapproved proprietary material into external AI systems.

## Imported mods, packs and external artifacts

Third-party content is not trusted solely because it is popular or packaged for Minecraft.

- Preserve hashes and provider/source provenance.
- Do not execute an artifact simply because it was imported or indexed.
- Parse archives defensively and reject path traversal.
- Treat embedded native binaries and executable scripts as elevated-risk content.
- Distinguish metadata inspection from activation.
- Keep decompilation/analysis outputs separate from authoritative originals.
- Respect licences and redistribution restrictions.

## GPU and rendering safety

Direct GPU APIs can crash drivers/processes, exhaust VRAM or corrupt rendering state.

- Perform graphics calls on the correct context/thread.
- Validate buffer sizes, offsets, formats and object lifetime.
- Bound dynamic allocations and staging queues.
- Release replaced GPU resources deterministically.
- Avoid assuming a successful OpenGL/Vulkan/driver call proves semantic correctness.
- Test resource reload under repeated create/destroy cycles to detect leaks.

## Bedrock integration safety

Gridelyx's Bedrock plane includes stable Add-On/Script API paths and may include deeper target-specific companion adapters.

- Stable supported APIs are preferred where they satisfy the capability.
- Deeper adapters must be exact-version/fingerprint gated.
- Do not hard-code guessed closed-source addresses as if they were validated interfaces.
- Keep the stable Add-On/Editor plane functional when deeper integration is unavailable.
- Do not use native integration to bypass platform authentication, entitlement, DRM or anti-cheat boundaries.

## Filesystem, update and migration safety

- Resolve canonical paths before destructive operations.
- Refuse traversal outside approved roots.
- Download to staging paths, verify, then atomically promote where possible.
- Preserve a previous known-good executable/runtime/configuration during updates.
- Never delete the only copy of user state as part of a migration before validation succeeds.
- Use explicit schema versions for persistent project/instance formats.
- Prefer copy-migrate-verify-switch over mutate-in-place for high-value data.

## Secrets and privacy safety

- Keep credentials out of logs, crash dumps, screenshots, generated context packs and support bundles.
- Redact account/session tokens from diagnostics.
- Collect only diagnostic data required for the task.
- Do not silently enable telemetry that exports private project/world/user data.
- Local-first processing is preferred for sensitive authoring state when practical.

## Failure containment

Gridelyx distinguishes recoverable failures from process-integrity failures.

Recoverable failures may be handled through:

- exception boundaries;
- cancellation;
- module/script epoch replacement;
- transactional rollback;
- client resynchronisation;
- worker-process restart.

Process-integrity or VM-fatal failures should use crash-safe persistence and restart/epoch recovery rather than pretending execution can safely continue.

Examples include severe native memory corruption, unrecoverable JVM failure or inconsistent bootstrap state.

## Runtime Epoch Handoff safety

A runtime epoch handoff must not switch authority merely because the new process launched.

The safe sequence is:

1. quiesce authoritative mutation;
2. checkpoint transferable state;
3. resolve and verify the new runtime graph;
4. launch the new epoch;
5. authenticate the local bridge;
6. restore transferable state;
7. run health/compatibility checks;
8. switch authority;
9. retire the old epoch only after the new one is healthy;
10. fall back to the last-known-good epoch if activation fails.

## Destructive operations

Operations that can permanently remove or corrupt user data should require explicit intent and clear scope.

Examples:

- deleting instances/worlds/projects;
- overwriting saves;
- irreversible schema migration;
- removing rollback history;
- destructive runtime patching;
- clearing credential or account stores;
- deleting content-addressed blobs still referenced by active locks.

Where practical, use recycle/quarantine/archive semantics before permanent deletion.

## Test environment rules

High-risk experimental work should begin in disposable, isolated environments.

Recommended layers include:

- unit/property tests;
- static/architecture checks;
- synthetic fixtures;
- temporary files/directories;
- disposable Minecraft instances;
- disposable worlds;
- isolated worker processes;
- local-only test servers;
- target-version smoke tests;
- failure/rollback injection;
- long-run leak/stress tests.

Do not use a valuable world, production server or primary account as the first validation target for privileged experimental code.

## Release and readiness gates

A safety-sensitive capability should not be promoted to a stronger readiness claim without evidence appropriate to the risk.

At minimum, consider:

- happy-path test;
- malformed/negative inputs;
- cancellation/timeouts;
- partial failure;
- rollback;
- restart/recovery;
- version mismatch;
- resource leak behaviour;
- concurrency behaviour;
- permission/authority denial;
- target-specific runtime validation.

## Incident response

If Gridelyx causes unexpected destructive or unsafe behaviour:

1. stop further authoritative mutation;
2. preserve logs, revision IDs and relevant crash/transaction evidence;
3. avoid repeatedly reopening/mutating the affected data;
4. switch to a last-known-good runtime/configuration where possible;
5. restore from journal/snapshot/rollback state if validated;
6. isolate the triggering module/content revision;
7. reproduce on disposable state before attempting a fix on valuable data;
8. document the failure class and add regression coverage.

Security incidents should additionally follow `SECURITY.md`.

## Safety is not capability removal

A difficult capability is not automatically removed from Gridelyx because it carries risk. The project instead seeks the strongest architecture that can make the capability bounded, explicit, testable, recoverable and appropriately permissioned.

Where physics or platform constraints make an in-place mechanism unsafe, Gridelyx may use isolation, virtualization, process replacement or another implementation strategy while preserving the user-facing capability goal.

## Related documents

- `SECURITY.md` — security policy and vulnerability reporting.
- `CODE_OF_CONDUCT.md` — community and contributor behaviour.
- `docs/SECURITY_MODEL.md` — architecture-level threat/security model.
- `docs/FAULT_TOLERANCE.md` — failure containment and recovery hierarchy.
- `docs/HOTLOAD_ARCHITECTURE.md` — public hotload and runtime epoch design.
