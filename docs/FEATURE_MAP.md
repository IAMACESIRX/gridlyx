# Gridelyx feature and readiness map

Status uses R0-R6 from `PROJECT_PLAN.md`. This is an evidence snapshot, not a marketing promise. Complete retained scope lives in `CHAT_REQUIREMENTS_TRACEABILITY.md`; dependencies/tools live in `DEPENDENCIES_AND_TOOLCHAIN.md` and `CAPABILITY_DEPENDENCY_MATRIX.md`; feature analysis and execution topology live in `FEATURE_DECISION_FRAMEWORK.md` and `DEVELOPMENT_MAP.md`.

| Domain | Capability | Target | State | Notes |
|---|---|---|---:|---|
| Control | Whole-chat requirements traceability | Repository | R3 | Human + machine-readable **34-group** ledger and CI path checker are wired. |
| Control | Dependency/toolchain inventory | Repository | R3 | Human/machine tool inventory, capability matrix and CI evidence-path checker are wired. |
| Control | Advanced feature decision framework | Repository/project | R3 | W5x5x5, values, cost/horizons, risk, inversion, second-order, benchmarks, MVP, Pareto, critical path, Cynefin and Kanban are documented, templated, schema-backed and CI-gated. |
| Control | Development/critical-path map | Repository/project | R2-R3 | Parallel lanes, milestone predecessors, Kanban states and 10m→10y horizons are explicit. |
| Control | AI continuity/drift system | Repository | R3 | Handoff, AI roles, work state, decisions, assumptions, context routing and continuity CI. |
| Brand | Gridelyx canonical identity | Repository/product | R2-R3 | Identity frozen in `platform/brand.json`; canonical public entrypoints updated. |
| Brand | Requested GitHub repository slug/description | Repository metadata | R1 transition | Desired `IAMACESIRX/gridlyx` metadata is recorded; actual GitHub metadata mutation still pending. |
| Brand | Retired terminology compatibility migration | Repository/runtime | R1-R2 transition | Staged terminology manifest/checker exists; source/ABI/protocol/persisted migration remains. |
| Community | Contributor onboarding | Repository | R2 | Community, support, conduct, architecture and evidence guides are tracked. |
| Studio | GUI-independent core model | Desktop/shared | R3 | Rust instance/provider/provenance/resolver core has automated tests. |
| Launcher | Native desktop control plane | Desktop | R1 | Core contracts exist; GUI/runtime shell pending. |
| Launcher | Runs without preinstalled Java | Desktop | R1 | Product invariant; Java is per-instance managed state. |
| Minecraft | Dynamic version discovery | Java | R1 | Mojang provider contract defined; client implementation pending. |
| Java | Managed runtime selection/download | Java | R1 | Mojang-first/local/Adoptium policy defined; downloader pending. |
| Loader | Vanilla/Fabric/Quilt/Forge | Java | R1 | Provider/adapter contracts exist. |
| Loader | NeoForge | Java | R2+ | Existing build platform plus Studio adapter contract. |
| Loader | Legacy/future/custom | Java | R1 | External adapter/version-family bootstrap design. |
| Polyloader | UAL neutral operations | Java/neutral | R2-R3 | Loader-neutral advanced runtime substrate exists. |
| Polyloader | Prelaunch instrumentation bootstrap | Java | R1-R2 | Agent/ASM foundations exist; standalone cross-version packaging remains. |
| Polyloader | Foreign-loader sideload/emulation | Java | R1-R2 | Artifact analysis/classloader/capability model exists; real adapter pairs pending. |
| Polyloader | Broad historical/current version compatibility | Java | R1 | Version-family bootstrap/fingerprint architecture defined. |
| Content | Modrinth | Java | R1 | API/dependency strategy defined; network provider pending. |
| Content | CurseForge | Java | R1 | Authorized API/distribution policy defined; network provider pending. |
| Resolver | Required dependency ordering | Cross-platform | R3 | Deterministic Rust solver test passes. |
| Resolver | Optional/incompatibility/range/side solving | Cross-platform | R1-R2 | Core conflict/order logic exists; full solver pending. |
| Provenance | Source/hash/licence record | Cross-platform | R2 | Core model/policy and provider checks exist. |
| Instances | Isolated instance/content-lock model | Cross-platform | R2 | Typed model + schemas; filesystem implementation pending. |
| Packs | Modrinth/CurseForge/Prism/MultiMC interchange | Java | R0-R1 | Roadmap/contracts only. |
| Workspace | Independent multi-mod JAR workspaces | Java | R3 | `mods/<mod_id>` generation/build architecture validated. |
| Quality | Spotless/Checkstyle/build lock/script gatekeeper | Repository/Java | R2-R3 | Wired into platform workflows. |
| Security | CodeQL and provenance controls | Repository | R2-R3 | Workflow/policy present; results depend on target run. |
| Testing | JUnit/ArchUnit | Java | R2-R3 | Logic/architecture test foundations wired. |
| Testing | Headless GameTest | Java | R2 | Harness exists; representative fixture coverage still pending. |
| Testing | Profiling/telemetry/chaos | Cross-runtime | R1-R2 | JFR/telemetry/fault-injection frameworks exist; campaign evidence pending. |
| AI | MCP endpoint | Neutral | R2 | Stateless tool/resource routing framework. |
| AI | Local vector index | Neutral | R2 | In-process index framework; repo embedding integration pending. |
| AI | Deterministic repo index/context packs | Repository | R3 | CI checks and retrieval smoke exist. |
| AI | Auto-documentation | Repository | R2-R3 | Deterministic and provider-neutral AI-doc pipelines exist. |
| Runtime | External hotload core | Java/neutral | R2-R3 | NIO WatchService, service/classloader/redefine architecture; target validation pending. |
| Runtime | GraalJS/GraalPy polyglot | Java | R2-R3 | Sandboxed polyglot foundation; deeper performance/target validation pending. |
| Runtime | Direct Java compilation | Java | R2 | In-memory compiler/gateway framework. |
| Runtime | Non-Java external-tool SDK | Cross-language | R1-R2 | Bridge/native/script planes exist; unified extension contract pending. |
| Native | Panama/FFM bindings | Java/native | R2 | Project-owned native ABI boundary exists. |
| Native | Rust/C++ extensions | Native | R2-R3 | Cross-platform build foundations exist. |
| Native | Python/Go/C# bridges | Sidecar | R2 | Neutral framed protocol implementations/examples; conformance tests pending. |
| Network | Netty/IPC/web development endpoints | Java/native | R2 | Event-loop-safe/loopback-first foundations exist. |
| Rendering | Direct LWJGL/GPU buffer control | Java client | R2 | Infrastructure primitive; interactive driver/render evidence pending. |
| Rendering | Pose/matrix interception and IK | Java client | R2 | PoseStack/IK foundations; visual integration pending. |
| Rendering | Live model/mesh/texture editing | Cross-edition | R1-R2 | Dynamic asset models/architecture exist; full renderer adapters pending. |
| Rendering | Volumetric matrix preview | Java/Bedrock | R2 | Frame-stream architecture exists; target renderer binding pending. |
| Geometry | Dynamic collision / `VoxelShape` | Java | R2 | Adapter/composition boundary exists; complex target validation pending. |
| Geometry | Microgrid/circles/cylinders/curves/slopes | Cross-edition | R1 | Retained requirement with explicit implementation roadmap. |
| World | Live section-array editor | Java | R2-R3 | Async blitter/transaction architecture exists; exact Minecraft adapter pending. |
| World | Deferred lighting/reconciliation | Java | R2 | Bulk-edit contract exists; target integration validation pending. |
| World | NBT structure/live generated-world edits | Java | R2 | Blueprint/compiler architecture exists. |
| World | Dynamic Event and Structure Matrix | Java/neutral | R2 | Trigger/structure framework exists; target integration pending. |
| World | Paint/sub-voxel overlays | Cross-edition | R1-R2 | Overlay foundation exists; full arbitrary paint system pending. |
| World | Progressive transmutation | Cross-edition | R2 | State-machine foundation exists; progression/persistence adapters pending. |
| World | Dynamic liquid cells | Cross-edition | R1 | Explicit retained roadmap item. |
| World | Dynamic dimensions | Java/cross-edition | R1-R2 | Virtual lifecycle abstraction; exact target materialization pending. |
| World | Zero-entity teleport channels | Cross-edition | R2 | Neutral foundation; target integration pending. |
| Multiplayer | Edit revisions/consensus/ACK/culling | Java server | R2 | Protocol/framework exists; exact loader network integration pending. |
| Physics | Custom entity/part physics | Cross-edition | R2 | Deterministic body/force foundations. |
| Physics | Tool-gun/constraint engine | Cross-edition | R2 | Raycast/constraint architecture; full constraint catalog pending. |
| Scene | Hierarchical scene graph/properties/gizmos | Cross-edition | R1-R2 | Neutral foundations/architecture; full UI/runtime integration pending. |
| Bedrock | Stable Add-On runtime | Bedrock | R2-R3 | Pack/scripts and static CI contract exist. |
| Bedrock | Editor extension | Bedrock Preview | R2 | Preview entrypoint/static validation. |
| Bedrock | Native companion/shared memory | Bedrock native | R2-R3 | Windows/Linux native build foundations; engine adapters remain target-specific. |
| Bedrock | Full Java-feature parity target | Bedrock | R1 | Explicit parity requirement; capabilities remain evidence-gated per target. |
| Deep integration | L0-L8 escalation model | Cross-runtime | R1-R2 | Architecture/decision accepted. |
| Deep integration | Deterministic patch manager | Desktop/runtime | R1 | Manifest/materialization/rollback design; implementation pending. |
| Deep integration | Engine subsystem augmentation/replacement | Cross-runtime | R1 | Permitted design path when upstream surfaces are insufficient. |
| Production | Production project schema | Cross-edition | R2 | Source-lock/rational-frame foundation. |
| Production | Rational-time camera track | Java/neutral | R3 | Deterministic interpolation smoke evidence. |
| Production | Replay/event model | Cross-edition | R1 | Architecture defined; runtime logger pending. |
| Production | Camera/actor animation/editor | Cross-edition | R1 | Detailed design and TODO coverage. |
| Production | Real-time recording | Java/Bedrock | R0-R1 | Target adapters pending. |
| Production | Offline deterministic render | Java/Bedrock | R0-R1 | Renderer stepping/integration research required. |
| Production | Audio stems/export | Cross-edition/desktop | R1 | Architecture/provenance policy defined. |
| Toolchain | Java/Gradle/NeoForge/quality libraries | Java | R3 control evidence | Exact versions are locked and machine-documented. |
| Toolchain | Python/Rust/CMake/compiler release matrix | Cross-project | R1-R2 | Required uses documented; exact supported versions remain to be pinned. |
| Toolchain | Go/.NET external bridge lanes | Sidecar | R1-R2 | Example implementations exist; release conformance/version policy pending. |
| Toolchain | External encoder/decompiler adapters | Desktop/dev | R1 | Optional/provenance-gated; not implicit bundled dependencies. |
| Reference vault | Exact large binary payload on remote GitHub | Repository | R1 pending | Manifest/control layer exists; physical payload remains pending while marker file exists. |

## UX feature groups

### Library / home
Instances, recent worlds/projects, modpacks, update state, play/edit/create/produce actions, diagnostics and storage usage.

### New instance wizard
Minecraft version → loader → Java auto-resolution → content/modpack → Gridelyx toolkit profile → memory/performance → review resolution graph → create.

### Instance editor
Version components, mods/content, resource packs, shaders, worlds, servers, configs, Java/JVM, launch arguments, environment, logs, screenshots, saves, recordings, creator projects, patch graph and snapshots.

### Content browser
Unified search with provider badges, compatibility filters, dependency preview, licence/source information, changelog/version chooser and one-click install queue.

### Expert resolution view
Graph/tree display, selected/rejected candidate reasons, required/optional/conflicting edges, hashes/provenance, target fingerprints, loader/runtime state, lockfile diff, manual pin/override and rollback.

### Creator workspace
World/structure/liquid/paint/microgeometry/mesh/voxel/texture editors, in-game IDE, scripts, AI tools, scene hierarchy, asset browser, properties, physics/tool-gun, hotload state and target capability matrix.

### Production workspace
Replay library, scene/timeline, camera tracks, actor/animation curves, cues, shot/take manager, render settings, audio routing, capture queue and export jobs.

### Planning / architecture workspace
Feature Decision Packets, W5x5x5 analysis, benchmark evidence, cost/horizon/risk diagnostics, dependency/critical-path graphs, Cynefin classification and Kanban state for substantial work.
