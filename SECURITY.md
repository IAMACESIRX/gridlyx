# Security policy

Do not report exploitable security issues in public issue threads. Use the repository owner's private security-reporting channel when available.

High-risk surfaces include Java instrumentation, bytecode transformation, native libraries, FFM memory, dynamic scripts, filesystem watchers, Netty development endpoints, IPC bridges, imported/decompiled mods and GPU/native buffers.

Security requirements:
- development control ports bind loopback unless authentication/authorisation is explicitly configured;
- polyglot scripts receive no unrestricted host access;
- hotload roots are canonicalised and bounded;
- bridge frames are size-limited and versioned;
- native code is never considered sandboxed;
- imported binaries retain hashes/provenance and are not executed merely because they were imported;
- chaos engineering is development/test only;
- all privileged runtime features remain opt-in and recoverable.
