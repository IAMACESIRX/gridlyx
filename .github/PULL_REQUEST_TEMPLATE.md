## Change

Describe the mechanism and why it belongs in this layer.

## Scope

- Mod workspace(s):
- Platform/template files:
- Advanced engines touched:

## Validation

- [ ] `python tools/validate_platform.py`
- [ ] `python tools/diagnose.py --static`
- [ ] Relevant `./gradlew spotlessCheck check build`
- [ ] Datagen output reviewed if changed
- [ ] GameTests run when gameplay behaviour changed
- [ ] Generated JAR inspected

## Security / trust boundaries

- [ ] No secrets added
- [ ] New network/native/bytecode code is feature-gated
- [ ] Inputs are bounded/validated
- [ ] Failure and rollback behaviour documented

## Licensing / provenance

- [ ] New third-party code/assets have compatible licences and provenance recorded
- [ ] Generated assets/data identify their generator where practical
