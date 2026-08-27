# Contributing

1. Keep `templates/neoforge-26.2/build.gradle` locked. If the platform contract intentionally changes, update it and run `python tools/build_lock.py --refresh` in the same reviewed change.
2. Run `python tools/script_gatekeeper.py`, `python tools/validate_platform.py` and `python tools/autodoc.py --check` before submitting.
3. Ordinary gameplay code belongs in `src/main`; experimental runtime/native/bytecode/polyglot work belongs in isolated advanced surfaces.
4. Add the lowest-cost meaningful test first, then promote significant Minecraft behaviour to GameTest and rendering/client validation.
5. Imported/forked code and assets require provenance and licensing notes. Never assume decompilable means redistributable.
6. Security-sensitive capabilities need explicit enablement, bounded inputs, rollback and failure containment.
