# Gridelyx Studio Public Hotload Architecture

## Product requirement

Hotloading is a public-facing Gridelyx Studio capability. It is not restricted to development mode.

The user experience target is simple:

1. install or enable Gridelyx once;
2. add, remove, edit, or replace compatible content while Minecraft is running;
3. Gridelyx chooses the smallest activation mechanism that can safely realize the change;
4. if the current JVM cannot absorb a structural change, Gridelyx performs a Runtime Epoch Handoff rather than exposing a manual restart as the normal workflow.

"Hotload" therefore describes continuity of the user session and creative workflow, not a promise that every change mutates one JVM in place.

## Lessons from live-modding systems

### Garry's Mod

Garry's Mod demonstrates the value of a reloadable gameplay layer: Lua files can be refreshed while the game is running. Its well-known failure mode is equally useful to us: executing a changed script again does not automatically remove hooks, callbacks, timers, globals, or other side effects installed by the old revision.

Gridelyx takes the immediacy but changes the ownership model. Every revision receives a Module Scope. Side effects must be owned by that scope and are deterministically detached before the next revision becomes authoritative.

### Hytale

Hytale demonstrates a broader authoring model: mod discovery/management is integrated into the game, content and tools are designed around modding, and plugin reload has explicit lifecycle cleanup. Current Hytale updates also emphasize unloading dependents, releasing plugin classloaders, removing registered asset types during shutdown, and avoiding stale static references across reloads.

Gridelyx takes that lifecycle discipline and extends it across Minecraft loaders and, where necessary, patched Minecraft choke points.

## Activation bands

Gridelyx classifies a change by the minimum mechanism required, not by whether the feature is "allowed."

| Band | Name | Typical changes | Preferred activation |
|---|---|---|---|
| H0 | Data | recipes, tags, language, config, declarative rules | transactional data reload |
| H1 | Assets | textures, models, audio, shaders, UI assets | revisioned asset swap |
| H2 | Behaviour | sandbox scripts, events, commands, procedural rules | scoped script epoch |
| H3 | Module | Java/service implementation behind stable interfaces | versioned classloader + swappable handles |
| H4 | Registry | items, blocks, entities, components, registrable types | Gridelyx registry virtualization |
| H5 | Lifecycle | loader entrypoints, dependency graphs, lifecycle callbacks | UAL lifecycle replay/emulation |
| H6 | Engine | class-shape changes, early transforms, bootstrap changes | patched runtime or Runtime Epoch Handoff |

Higher bands are not "bad" or hidden. They simply require stronger machinery.

## Minimal-install patch strategy

Gridelyx should patch a small set of high-leverage Minecraft/loader choke points once rather than permanently forking broad areas of the game.

Target patch points:

1. registry lookup/freeze boundaries;
2. resource/model/texture invalidation;
3. event registration and dispatch ownership;
4. command and network registration;
5. scheduler/tick ownership;
6. render/model indirection;
7. loader lifecycle dispatch;
8. service/class resolution;
9. save-world content identity/tombstone resolution;
10. client/server content negotiation.

The patched call sites delegate to Gridelyx indirection tables. Normal vanilla/loader behaviour remains the default delegate, which limits patch surface and makes bypass/fallback testing possible.

## Module Scope: make reload side effects reversible

Every public hotloaded module is activated inside a Module Scope.

A scope owns:

- event listeners and hooks;
- commands;
- packet/channel registrations;
- scheduled work;
- threads/executors;
- resource handles;
- asset registrations;
- registry overlays;
- service handles;
- native/process handles;
- state migration callbacks.

Activation is staged. The new revision becomes authoritative only after validation succeeds. Teardown is LIFO and idempotent. Failure during teardown is recorded and escalated to an epoch rollback rather than silently leaking state.

This directly addresses the classic "script re-executed on top of old state" hot-reload failure.

## Versioned references instead of static ownership

Reloadable code must not become permanently rooted through static global references.

Gridelyx-owned engine call sites resolve through:

- versioned handles;
- stable interfaces;
- MethodHandles;
- proxy/dispatch slots;
- generation/epoch IDs.

A classloader epoch can therefore become collectible once its Module Scope, dependents, threads and external registrations are released.

A leak auditor should track old epochs with weak references and report the concrete root category when an epoch cannot retire.

## Registry virtualization

Minecraft registries are one of the largest barriers to public hotloading. Gridelyx should not depend exclusively on mutating a frozen registry in place.

The H4 design is an indirection layer:

`logical Gridelyx content id -> active revision -> physical Minecraft representation`

The patch layer intercepts lookup and serialization at selected choke points. A removed revision can become a tombstone rather than corrupting a save. A replacement revision can migrate data while preserving the logical identity visible to worlds and network peers.

Where a specific Minecraft version allows safe dynamic registry mutation, the adapter may use it. Where it does not, the virtual registry remains authoritative.

## Lifecycle replay

Loader APIs are translated into owned UAL operations:

`discover -> resolve dependencies -> stage registrations -> activate -> run -> quiesce -> detach -> migrate -> replace`

A loader adapter records the side effects produced during lifecycle execution so they can be reversed or rebound. Dependents form an explicit graph. Reloading a provider either rebinds compatible dependents or reloads them in topological order.

Mixins, transformation services and other early transforms are therefore not dismissed. They are classified as structural work and routed to the H6 path when same-process transformation cannot express the requested change.

## Runtime Epoch Handoff

Some JVM changes require a new process: adding/removing fields or methods on already-loaded classes, changing inheritance, changing early transformation history, swapping native libraries that cannot be safely unloaded, or rebuilding a loader graph.

Gridelyx preserves the hotload experience with a Runtime Epoch Handoff:

1. quiesce authoritative mutation;
2. checkpoint world/session/editor/module state;
3. resolve the new runtime graph;
4. launch the next JVM/runtime epoch automatically;
5. establish authenticated local IPC/shared memory;
6. restore transferable state and reconnect client/server surfaces;
7. switch authority to the new epoch;
8. retire the previous epoch only after health checks pass;
9. fall back to the last-known-good epoch if activation fails.

For a player or creator this is still one hotload action. The implementation is free to use a fresh JVM when physics says that is the safer or more capable mechanism.

## Trust and capability bands

Public-facing does not mean every package receives unrestricted host access.

Gridelyx separates *availability* from *authority*:

- T0: declarative/data only;
- T1: sandboxed behaviour;
- T2: declared Gridelyx capabilities;
- T3: privileged runtime integration;
- T4: signed/explicit engine-patch authority.

A package can be public and H6 while still requiring explicit T4 permission. The UI should explain requested capabilities and activation mechanics before installation.

## Multiplayer

The server remains authoritative for shared state.

Hotload negotiation includes:

- package/content identity;
- revision and dependency graph;
- required client assets/code;
- protocol schema versions;
- trust/capability requirements;
- migration epoch;
- rollback epoch.

A server may stream or instruct acquisition of permitted client-side content. World mutation is committed only after required peers reach a compatible revision or an adapter explicitly supports mixed revisions.

## Observability

Every activation produces an immutable record:

- requested package/revision;
- detected loader/game/JVM fingerprint;
- selected H band and trust band;
- selected activation strategy;
- registrations/resources acquired;
- state migration result;
- health checks;
- rollback/fallback result;
- retired classloader/process status.

The UI should show "what Gridelyx is doing" rather than reduce complex transitions to a generic loading spinner.

## Success criterion

The breakthrough target is not "every bytecode edit happens in the same JVM."

The target is stronger:

> A creator or player can change the active mod/content graph through Gridelyx Studio with minimal install interaction, while Gridelyx automatically chooses in-process replacement, virtualization, lifecycle replay, engine patching, or a state-preserving runtime epoch handoff and can recover to a known-good state when the new revision fails.
