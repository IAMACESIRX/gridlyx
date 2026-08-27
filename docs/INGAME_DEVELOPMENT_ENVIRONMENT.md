# Alt-Tabless In-Game Development Environment

The client development plane is intentionally split between a stable core and a Minecraft UI adapter.

- `DeveloperFeatureRegistry`: global on/off state for editor, console, AI, overlays and automation.
- `KeyBindingRouter`: action IDs that an exact NeoForge key-registration adapter can bind to real keys.
- `DeveloperScreenInjectionPipeline`: screen/menu model registry used by the native client screen adapter.
- `EmbeddedIdeConsole`: bounded in-game log/compile history.
- `DirectJavaCompiler`: JDK compiler API producing in-memory class bytes from source strings.
- `LiveCompilationGateway`: method-body-compatible classes can use Instrumentation redefinition; new classes use an
  isolated classloader. It does not claim JVM schema changes are universally redefinable.
- `ClientAutomationController`: bounded high-level in-game action queue. It does not synthesize operating-system input.
- `AiDevelopmentBridge`: bounded correlation-ID request/response channel for a local MCP/AI sidecar or approved server
  endpoint.

## Security boundary

Direct compilation, automation and AI passthrough are developer-mode capabilities. Multiplayer servers must decide
whether a player may request world edits; the client console never becomes authoritative simply because it can compile
or send commands.
