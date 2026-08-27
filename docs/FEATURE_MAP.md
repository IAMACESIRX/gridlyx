# Gridelyx Studio feature map

Status uses the repository readiness scale R0-R6. This table describes architectural/evidence state, not marketing promises.

| Domain | Capability | Target | State | Notes |
|---|---|---|---:|---|
| Launcher | Native desktop control plane | Desktop | R1 | Core contracts exist; GUI/runtime shell pending. |
| Launcher | Runs without preinstalled Java | Desktop | R1 | Product invariant; managed Java is instance dependency. |
| Minecraft | Dynamic version discovery | Java | R1 | Mojang provider contract defined. |
| Java | Managed runtime selection/download | Java | R1 | Mojang first, compatible local JVM, Adoptium fallback. |
| Loader | Vanilla | Java | R1 | Adapter contract defined. |
| Loader | Fabric | Java | R1 | Official Fabric Meta strategy defined. |
| Loader | Quilt | Java | R1 | Official Quilt Meta strategy defined. |
| Loader | Forge | Java | R1 | Official installer/Maven strategy defined. |
| Loader | NeoForge | Java | R2+ | Existing NeoForge build platform plus Studio adapter contract. |
| Loader | Legacy/future/custom | Java | R1 | External adapter / explicit launcher-profile import. |
| Content | Modrinth | Java | R1 | API/dependency strategy defined. |
| Content | CurseForge | Java | R1 | Approved-key/distribution-policy strategy defined. |
| Resolver | Required dependency graph | Cross-platform | R2 | Deterministic Rust solver with unit test. |
| Resolver | Optional dependency UX | Cross-platform | R1 | Contract defined. |
| Resolver | Incompatibility detection | Cross-platform | R2 | Core solver returns explicit conflicts. |
| Provenance | Source/hash/licence record | Cross-platform | R2 | Core model/policy exists. |
| Instances | Isolated instance model | Java | R1 | Model defined; filesystem implementation pending. |
| Instances | Content-addressed global cache | Desktop | R1 | Design defined. |
| Packs | Modrinth/CurseForge import | Java | R0-R1 | Contracts/roadmap only. |
| Packs | Prism/MultiMC import | Java | R0-R1 | Contracts/roadmap only. |
| Toolkit | Loader-neutral UAL | Java | R2-R3 | Existing advanced runtime substrate. |
| Toolkit | World editor | Java | R2-R3 | Existing architecture/static gates; runtime validation remains. |
| Toolkit | Mesh/texture live assets | Java | R2 | Existing registries/architecture. |
| Bedrock | Stable Add-On runtime | Bedrock | R2-R3 | Pack/scripts and CI contract exist. |
| Bedrock | Editor extension | Bedrock Preview | R2 | Preview API entrypoint/static validation. |
| Bedrock | Native VFSB companion | Bedrock native | R2 | Windows/Linux native builds pass; renderer adapter remains target-specific. |
| Production | Neutral timeline/replay model | Cross-edition | R1 | Architecture defined; runtime adapters pending. |
| Production | Camera director | Cross-edition | R1 | Track/mode design defined. |
| Production | Actor animation | Cross-edition | R1 | Track/pose design defined. |
| Production | Real-time capture | Java/Bedrock | R0-R1 | Adapter contract planned. |
| Production | Offline deterministic render | Java/Bedrock | R0-R1 | Requires renderer-specific validation. |
| Production | Audio stems | Java/Bedrock | R0-R1 | Capability-dependent. |
| Production | Encoder/export bridge | Desktop/native | R1 | External encoder provenance policy defined. |
| AI | AGENTS engineering contract | Repository | R2 | Existing and expanded context routing. |
| AI | Deterministic repo index | Repository | R2 | Generator included. |
| AI | Task-scoped context packs | Repository | R2 | Lightweight retrieval helper included. |
| AI | Handoff/context map | Repository | R2 | Canonical files included. |

## UX feature groups

### Library / home
Instances, recent worlds/projects, modpacks, update state, play/edit/create/produce actions, diagnostics and storage usage.

### New instance wizard
Minecraft version → loader → Java auto-resolution → content/modpack → toolkit profile → memory/performance → review resolution graph → create.

### Instance editor
Version components, mods/content, resource packs, shaders, worlds, servers, configs, Java/JVM, launch arguments, environment, logs, screenshots, saves, recordings, creator projects and snapshots.

### Content browser
Unified search with provider badges, compatibility filters, dependency preview, licence/source information, changelog/version chooser and one-click install queue.

### Expert resolution view
Graph/tree display, selected/rejected candidate reasons, required/optional/conflicting edges, hash/provenance, source URL, lockfile diff, manual pin/override and rollback.

### Creator workspace
World/structure/mesh/voxel/texture editors, in-game IDE, scripts, AI tools, scene hierarchy, asset browser, properties, hot reload state and target capability matrix.

### Production workspace
Replay library, scene/timeline, camera tracks, actor tracks, animation curves, cues, shot/take manager, render settings, audio routing, capture queue and export jobs.
