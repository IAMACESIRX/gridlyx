# Support and triage

This repository contains both validated foundations and experimental R&D. When asking for help, identify the target precisely.

## Include

- Minecraft edition and exact version;
- loader and exact loader version, if applicable;
- Java/runtime version for Java Edition;
- operating system and architecture;
- project commit or release;
- relevant instance/content lock when available;
- whether advanced/native/bytecode/hotload/deep-integration capabilities are enabled;
- exact reproduction steps;
- logs and diagnostics with credentials/tokens removed;
- expected and observed behavior.

## Where to report

- Functional bug: use the GitHub bug issue form.
- Loader/version compatibility: use the compatibility issue form.
- New capability: use the feature request form.
- Live world editor issue: use the world-editor issue form.
- Security vulnerability: follow [`SECURITY.md`](SECURITY.md); do not disclose exploitable details publicly before triage.

## Experimental features

For framework-level or planned features, support means architecture/design triage rather than a promise that the target capability already works. Check [`docs/FEATURE_MAP.md`](docs/FEATURE_MAP.md), [`docs/TODO.md`](docs/TODO.md) and [`docs/CHAT_REQUIREMENTS_TRACEABILITY.md`](docs/CHAT_REQUIREMENTS_TRACEABILITY.md) before assuming a requested feature is at interactive validation maturity.

## Diagnostics

Relevant starting commands include:

```bash
python tools/continuity_check.py
python tools/chat_requirements_check.py
python tools/studio_check.py
python tools/validate_platform.py
python tools/diagnose.py --static
```

Then run the applicable Java, Bedrock, native, GameTest or Studio validation lane for the subsystem involved.
