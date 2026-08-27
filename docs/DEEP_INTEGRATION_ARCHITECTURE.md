# Additive deep integration architecture

## Principle

This project is not constrained to the extension surface exposed to conventional modders. The desired capability is the governing requirement; the implementation may extend across the launcher, game process, JVM/runtime, loader, native libraries, renderer, protocol adapters, storage adapters, helper processes and version-pinned executable/library patch layers when ordinary APIs are insufficient.

The governing constraint is **additive integration**:

- retain an immutable known-good upstream/base artifact whenever technically possible;
- express project-owned changes as overlays, patch sets, shims, injected services, adapters, sidecars or derived patched artifacts rather than losing the original;
- make every deep integration version/fingerprint gated;
- keep a deterministic record of what was changed and why;
- provide disable/rollback/recovery paths;
- never weaken authentication, licensing, DRM, anti-cheat or platform security controls as a means of gaining capability.

The question is therefore not "can a normal mod do this?" but "what is the shallowest reliable integration layer that can provide this capability without violating project invariants?"

## Capability escalation ladder

Use the lowest layer that can satisfy the requirement robustly, but escalation is explicitly allowed.

| Level | Integration surface | Typical mechanisms |
|---|---|---|
| L0 | Supported game/loader API | official APIs, datapacks, resource packs, Add-Ons, loader events |
| L1 | Loader transformation | Mixins, access transformers/wideners, bytecode transforms, loader hooks |
| L2 | JVM/runtime extension | Java agents, Instrumentation, replaceable classloaders, service indirection, Graal/polyglot runtime |
| L3 | Native in-process extension | Panama/FFM, JNI where required, project-owned native bridge, renderer/native adapters |
| L4 | External process extension | sidecars, helper daemons, compilers, asset processors, simulation workers, shared-memory/IPC services |
| L5 | Launch/bootstrap extension | custom launcher/bootstrap chain, pre-main initialization, environment/runtime composition, process supervision |
| L6 | Binary/library compatibility patch | deterministic version-pinned patch sets against known executable or shared-library hashes, producing a derived artifact or controlled runtime overlay |
| L7 | Engine subsystem augmentation | project-owned replacement/augmentation layer for registries, rendering, physics, world representation, networking or storage where the original subsystem cannot expose the required semantics |
| L8 | Forked/custom runtime component | a maintained project-owned component or fork when patching the upstream component becomes less reliable than owning the extension surface directly |

These are not maturity levels. A feature may legitimately use several levels at once.

## Immutable-base rule

Deep integration must not turn an installation into an unknowable mutated state.

Preferred model:

`verified base -> patch/overlay manifest -> derived runtime -> verification -> launch`

For every patched executable/library/runtime component, retain or recover:

- upstream/base identity and cryptographic hash;
- target version/platform/architecture;
- patch-set ID and version;
- ordered patch/overlay components;
- required dependencies and conflicts;
- output hash or runtime fingerprint;
- verification checks;
- disable/rollback procedure;
- provenance of project-owned patch material.

A clean base should be re-derivable from the legitimate upstream source rather than reconstructed from a patched binary.

## Patch manager

The platform should eventually expose a dedicated patch manager rather than scattering binary changes through ad-hoc scripts.

Conceptual responsibilities:

1. identify the exact target by version, platform, architecture and hash;
2. select only compatible patch modules;
3. resolve patch dependencies/conflicts;
4. materialise a derived artifact or runtime overlay in an isolated instance cache;
5. verify expected fingerprints and structural invariants;
6. launch only after verification;
7. record the active patch graph in the instance lock/provenance state;
8. support one-action disable/rollback to the verified base;
9. invalidate the patched derivative automatically when the upstream artifact changes.

Patch sets are capability modules, not undocumented permanent mutations.

## Runtime augmentation

Some capabilities are better implemented by adding a component than by rewriting the game:

- renderer/geometry service;
- physics service;
- virtual-registry service;
- asset/model compiler;
- world-edit transaction engine;
- replay/capture service;
- scripting VM;
- AI development service;
- native high-performance worker;
- Bedrock/Java bridge adapter;
- shared-memory transport;
- process supervisor.

The architecture should therefore permit in-process, out-of-process and hybrid implementations behind stable project-owned contracts.

## Frozen or hostile-to-extension boundaries

A frozen vanilla/loader registry, inaccessible engine function, non-public renderer structure or fixed protocol is a compatibility constraint, not automatically a product-scope constraint.

The response sequence is:

1. determine whether an existing supported extension point can express the capability;
2. try loader/JVM transformation where appropriate;
3. introduce a virtual/project-owned abstraction when native mutation is unnecessary;
4. add a native or external service when isolation/performance makes that cleaner;
5. patch or augment the target component when the capability fundamentally requires changing its behavior;
6. if repeated patching becomes structurally fragile, graduate the responsibility into a maintained project-owned component.

## Additive replacement pattern

"Additive" does not mean the original code must continue executing every operation. It means project capability is layered in a way that remains attributable and reversible.

A project-owned subsystem may supersede an upstream subsystem at runtime when necessary, provided:

- the upstream/base artifact remains identifiable and recoverable;
- interception/replacement is explicit in the active capability graph;
- compatibility is fingerprint/version gated;
- failure can fall back or fail closed rather than silently corrupting state;
- persistent data has a migration/recovery story.

## Version and drift policy

Deep hooks have higher compatibility risk than public APIs. Therefore each L2-L8 integration must declare its compatibility evidence.

Fingerprint inputs may include:

- executable/library hash;
- class/method descriptors;
- mappings/version IDs;
- native symbol/ABI version;
- renderer/backend version;
- protocol/schema version;
- expected bytecode or structural signatures.

A fingerprint mismatch must disable the affected deep capability until revalidated. "Probably similar" is not sufficient evidence for a binary/runtime patch.

## Hotload relationship

Hotload remains valuable but is not an absolute architectural boundary. If a capability cannot be made restartless safely, the platform may perform a supervised component, process or instance restart while preserving editor/session state.

The platform should prefer increasingly broad reload scopes:

`data/script -> service implementation -> classloader -> native/sidecar process -> game process -> patched runtime rebuild`

The user-facing development environment should preserve work state across these boundaries where possible.

## Security and legitimacy boundary

Deep integration is for extending legitimately obtained game/runtime components and project-owned instances. It must not be designed to:

- bypass account authentication or entitlement checks;
- circumvent DRM/licensing controls;
- defeat anti-cheat or conceal modifications from security systems;
- obtain or redistribute proprietary binaries without authorization;
- silently patch unrelated system software.

This restriction does not prevent invasive engineering of a local authorized instance for rendering, simulation, development, debugging, compatibility or creator capabilities.

## Architectural consequence

Future design reviews must not reject a requirement solely because Minecraft, a loader, Java, Bedrock or an exposed API does not currently support it. Such a finding changes the **integration level and validation burden**, not necessarily the requirement.

When a requirement crosses into L5-L8, record an explicit architectural decision identifying:

- why shallower layers are insufficient;
- exact target versions/fingerprints;
- additive component/patch model;
- blast radius;
- validation plan;
- rollback/recovery path;
- maintenance cost when upstream changes.
