# Capability matrix

| Capability | Platform state | Activation boundary |
|---|---|---|
| Spotless / Checkstyle | wired | every workspace build |
| Issue / PR templates | wired | repository UI |
| Localization provider | compiling template implementation | normal datagen |
| Asset/data blueprints | wired | generator input |
| Codec worldgen extension | bootstrap helper + blueprint | mod supplies registry-specific codecs/entries |
| Multiple mod JAR workspaces | wired | `mods/<mod_id>` |
| Worker pools / sync pipeline | implementation foundation | advanced source set |
| Dynamic data engine | implementation foundation | advanced source set |
| Java Instrumentation / hotswap | implementation foundation + agent JAR | explicit `-javaagent`/attach environment |
| ASM generation | implementation foundation | advanced source set |
| Mixin redirectors | trusted dynamic config loader + target blueprint | target-specific mixin must be generated/validated per mapping |
| Netty injection | typed event-loop-safe insertion/removal primitive | resolved Minecraft channel only |
| FFM / Panama | native-memory primitive | advanced source set |
| Shared-memory IPC | single-channel memory-mapped prototype | explicit multi-process integration |
| Native LWJGL GPU | buffer ownership primitive | active render/OpenGL context only |
| PoseStack / IK | interception hooks + solver | target render injection required |
| CodeQL | workflow present | requires GitHub code-scanning entitlement/configuration for private repo |
| Copilot tuning | official setup workflow + platform TOML | GitHub Copilot / platform tooling |
| Codespaces | devcontainer present | GitHub Codespaces |

A foundation means the reusable mechanism exists, not that every possible Minecraft target has already been patched. Target-specific bytecode, rendering and network hooks remain version-sensitive and must be validated against the exact mappings/runtime.
