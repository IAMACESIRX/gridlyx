# Gridelyx Security Policy

## Security posture

Gridelyx is an experimental cross-edition development/runtime platform with intentionally powerful integration surfaces. It can interact with JVM instrumentation, bytecode transformation, native code, shared memory, scripting runtimes, filesystem watchers, networking, graphics APIs, imported third-party content and live Minecraft state.

Those capabilities are not treated as inherently safe merely because they are part of Gridelyx. Privileged surfaces must be explicit, reviewable, bounded and recoverable.

Operational engineering hazards that are not primarily security vulnerabilities are documented separately in [`SAFETY.md`](SAFETY.md).

## Supported security state

Gridelyx is under active development and does not currently claim a hardened production security boundary across every retained capability.

Security support is evidence-based. A subsystem that exists in source code is not automatically considered hardened, sandboxed or safe for hostile input.

When evaluating a report, maintainers will consider the exact commit, component, platform, Minecraft version, loader/runtime version and activation mode involved.

## Reporting a vulnerability

Do **not** publish an exploitable vulnerability, working credential, private user data, or immediately actionable exploit chain in a public issue.

Use GitHub private vulnerability reporting / the repository owner's private security-reporting channel when available. If no private channel is available, contact a maintainer privately before publishing technical details.

A useful report should include:

- affected component and exact revision/commit;
- platform, Minecraft/loader/runtime versions and configuration;
- preconditions and required privileges;
- clear reproduction steps or a minimal proof of concept;
- expected versus observed behaviour;
- likely impact;
- whether the issue crosses a trust, authentication, process, filesystem, memory, multiplayer-authority or persistence boundary;
- suggested containment or remediation if known.

Please minimise exposure. Demonstrate the problem with the least destructive proof necessary.

## High-risk security surfaces

The following receive elevated scrutiny:

- Java Instrumentation, agents, HotSwap/redefinition and retransformation;
- ASM/Mixin and other bytecode transformation paths;
- custom classloaders, service replacement and runtime epoch handoff;
- FFM/Panama memory access and native libraries;
- C/C++ and Rust FFI boundaries;
- named shared memory and cross-process IPC;
- Netty channels, development HTTP/WebSocket/control endpoints and custom network pipelines;
- GraalJS/GraalPy and other polyglot execution;
- dynamically compiled Java;
- filesystem hotload/watch roots;
- imported, decompiled or externally supplied mod binaries;
- native GPU buffers and direct LWJGL calls;
- Bedrock native companion/deeper integration adapters;
- world-edit and multiplayer-authority operations;
- authentication credentials, provider API keys and account/session tokens;
- update, download, provenance and content-addressed cache paths.

## Core security requirements

### Least privilege

Components receive only the authority they require. A transport connection, loaded module, script context or native process does not automatically gain authority over world state, accounts, filesystem, network or other processes.

Privileged runtime features must be explicit and capability-scoped.

### Local control endpoints

Development/control endpoints bind to loopback by default unless authenticated and authorised remote access is deliberately configured.

A network listener must not become remotely reachable merely because a development feature is enabled.

### Authentication and authorisation

Authentication answers who the caller is; authorisation answers what the caller may do. Gridelyx must not substitute one for the other.

Sensitive operations should be checked at the authoritative execution boundary, not only in UI code.

### Scripts and generated code

Polyglot scripts receive no unrestricted host access by default. Host class lookup, filesystem access, process execution and network access should be denied unless explicitly granted.

AI-generated or user-generated code is untrusted until reviewed and validated. Generated code must not be automatically promoted to privileged execution merely because compilation succeeds.

### Filesystem safety

Hotload/import roots must be canonicalised and constrained to approved directories. Path traversal, symlink escape and ambiguous normalisation must be rejected.

Imported binaries and archives retain provenance and hashes and are not executed merely because they were discovered or imported.

### Native and FFM safety

Native code is trusted process code, **not a sandbox**. FFM descriptors, buffer sizes, lifetimes, ownership and alignment must be exact.

Cross-boundary lengths, versions, sequence values and memory regions must be validated before use. Native crashes and memory corruption must be treated as process-integrity failures, not ordinary recoverable script exceptions.

### IPC and bridge protocols

Gridelyx bridge/shared-memory frames are versioned and length-bounded. Receivers must validate:

- protocol/ABI version;
- frame type;
- declared payload length;
- capacity bounds;
- checksum/integrity metadata;
- sequence/publication state;
- inner schema before dispatch.

Portable frames must not contain raw JVM references, native pointers, process addresses or unscoped ownership handles.

A successful bridge connection does not itself confer authority.

### Bytecode transformation

Instrumentation and transformation targets must be fingerprinted or otherwise constrained to expected classes/versions where appropriate. Unexpected layouts should fail closed or escalate to a safer activation path.

Silent transformation failure that leaves the system in an ambiguous partially modified state is not acceptable for privileged paths.

### Dependency and supply-chain security

Dependencies, external tools and downloaded artifacts should use pinned/declared versions where reproducibility requires them. Hashes, provenance and source/provider identity should be retained for acquired artifacts.

CI and dependency scanning are evidence sources, not proof that a build is vulnerability-free.

### Secrets

Secrets must not be committed to source, examples, generated assets, logs, crash reports or AI context packs.

Credentials should use an appropriate credential store or environment/secret mechanism and should be scoped, revocable and minimally privileged.

If a secret is exposed, assume compromise and rotate/revoke it rather than relying on Git history deletion alone.

### Multiplayer authority

The server remains authoritative for shared game/world state unless a deliberately designed protocol specifies otherwise.

Clients must not be able to obtain world mutation, administrative, filesystem or privileged runtime authority by crafting reload/edit/network messages outside their granted capability.

### Denial of service and resource bounds

Untrusted or externally controlled work must be bounded where possible by:

- payload/file size;
- queue depth;
- thread/worker count;
- execution deadline;
- memory allocation;
- recursion/dependency depth;
- retry/backoff limits;
- rate limits for remotely triggerable work.

A bounded executor is not a hard isolation boundary for code that can ignore interruption or use unrestricted native/process access.

### Recovery and rollback

Privileged updates, transformations and world mutations should preserve a last-known-good or rollback path where technically possible.

Crash-safe persistence and restart recovery are preferred over attempting unsafe continuation after VM-fatal or process-integrity failure.

## Prohibited project behaviour

Gridelyx security/integration work must not intentionally be used to:

- bypass account authentication, licensing or entitlement checks;
- defeat DRM or anti-cheat systems;
- steal credentials or session material;
- inject into unrelated processes without explicit authorisation;
- conceal persistence, backdoors or remote-control behaviour;
- access third-party systems without permission.

Deep runtime integration is an engineering capability, not permission to violate platform or third-party security boundaries.

## Security testing

Appropriate testing may include:

- static analysis and CodeQL;
- dependency/provenance checks;
- fuzzing parsers and bridge boundaries;
- malformed/truncated frame tests;
- path traversal and root-escape tests;
- permission/authority negative tests;
- race/concurrency tests;
- native ABI/version mismatch tests;
- rollback/failure-injection tests;
- resource exhaustion tests with explicit bounds;
- target fingerprint mismatch tests.

Chaos/failure injection belongs in development/test environments and must not be silently enabled for ordinary users.

## Vulnerability handling

When a credible vulnerability is received, maintainers should, as appropriate:

1. reproduce or establish the affected boundary;
2. identify immediately exposed users/configurations;
3. contain or disable the vulnerable capability if necessary;
4. prepare and validate a fix;
5. check adjacent implementations for the same failure class;
6. document affected versions/commits when known;
7. publish remediation information after unnecessary exploit exposure has been reduced.

No bug bounty, guaranteed response time or legal safe-harbour programme is promised by this document. Good-faith security research and responsible reporting are nevertheless strongly preferred over public exploitation.

## Public disclosure

After remediation or reasonable coordination, security findings may be documented publicly to improve engineering quality. Public write-ups should avoid exposing active credentials, personal data or unnecessarily weaponised details for still-vulnerable deployments.

## Related policies

- [`SAFETY.md`](SAFETY.md) — operational and engineering safety.
- [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md) — contributor/community conduct.
- [`docs/SECURITY_MODEL.md`](docs/SECURITY_MODEL.md) — detailed architecture-level security model.
