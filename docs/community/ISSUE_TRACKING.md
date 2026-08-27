# Gridelyx issue tracking

GitHub Issues are the operational work queue; repository ledgers remain the architectural source of truth.

## Every substantial issue should include

- affected `CR-*` requirement IDs;
- target edition/version/loader/platform;
- current R0-R6 evidence state;
- desired outcome;
- dependencies/toolchains required;
- authoritative source/docs;
- validation required to close the issue;
- rollback/migration notes where applicable.

## Issue vs documentation truth

An issue may track implementation status but must not silently redefine project scope. If an issue changes architecture, scope, dependency requirements or evidence state, update the corresponding canonical files in the same work:

- `docs/CHAT_REQUIREMENTS_TRACEABILITY.md` / `platform/chat-requirements.json`;
- `docs/TODO.md`, `ROADMAP.md`, `FEATURE_MAP.md`;
- `docs/DEPENDENCIES_AND_TOOLCHAIN.md` / `platform/toolchain-requirements.json`;
- decision/assumption ledgers when project truth changes.

## Closing an issue

Do not close a feature issue merely because code was written. Record the tests actually run and the highest evidence level they support. Interactive/render/native/Bedrock claims need target-specific evidence.
