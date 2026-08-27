# Security model

## Trust boundaries

- Generated mod source is untrusted until reviewed and validated.
- GitHub Actions run with least-privilege tokens.
- Vault/reference files are data, not executable dependencies.
- Native libraries, Java agents, ASM/Mixin transformers, Netty injections and shared-memory IPC are privileged experimental surfaces.

## Required controls

Advanced engines are disabled by default. Validate lengths, packet/state identifiers and native memory bounds. Never deserialize arbitrary Java objects from network or IPC channels. Keep worker pools bounded. Do not attach instrumentation agents to unrelated JVMs. GPU calls must remain on the correct render/context thread unless the API explicitly permits otherwise.

Network injection must preserve vanilla/NeoForge ordering, backpressure and disconnect semantics. Bytecode transforms must fingerprint target classes/methods and fail closed when mappings/signatures drift.

Secrets must never be committed, logged, placed in generated resources or passed to untrusted mod code.
