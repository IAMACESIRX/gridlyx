# Studio production plane

This directory owns launcher-side machinima/project orchestration: production project storage, render/capture queues, external encoder integration, export manifests and desktop timeline services. Game-specific camera/replay/render hooks belong in their Java/Bedrock runtime adapters rather than here.

Canonical architecture: `docs/MACHINIMA_PRODUCTION.md`.

Planned modules:

- `project` — production project/scene/shot/take persistence;
- `timeline` — rational-time tracks and keyframe evaluation;
- `capture` — capture jobs, frame manifests, dropped-frame policy;
- `encoder` — replaceable FFmpeg/image-sequence exporters with provenance;
- `replay` — replay compatibility/instance-lock linkage;
- `render_queue` — local/remote job scheduling and resumable outputs.

The production plane must remain non-destructive: scene/timeline edits do not modify the source world unless a user explicitly commits a world-edit transaction.
