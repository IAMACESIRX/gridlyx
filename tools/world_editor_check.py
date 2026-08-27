#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CAPABILITIES = ROOT / "platform" / "world-editor-capabilities.json"
REQUIRED = [
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/ParallelArrayBlitter.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/AsyncSubChunkBlitter.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/NbtStructureBlueprintLoader.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/BlueprintSectionCompiler.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/OverlayBuffer.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/render/VolumetricMatrixStream.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/scene/SceneGraph.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/clientdev/LiveCompilationGateway.java",
    "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/network/NettyEditChannel.java",
    "docs/WORLD_EDIT_RUNTIME.md",
    "docs/INGAME_DEVELOPMENT_ENVIRONMENT.md",
    "docs/MULTIPLAYER_WORLD_EDIT.md",
]


def main() -> int:
    errors: list[str] = []
    for relative in REQUIRED:
        if not (ROOT / relative).is_file():
            errors.append(f"missing required world-editor file: {relative}")
    data = json.loads(CAPABILITIES.read_text(encoding="utf-8"))
    if data.get("plane") != "live-world-authoring":
        errors.append("world editor capability manifest has unexpected plane")
    capabilities = data.get("capabilities", {})
    required_keys = {
        "parallel_array_blitting",
        "async_subchunk_delta_preparation",
        "server_thread_commit_queue",
        "deferred_lighting_reconciliation",
        "nbt_structure_blueprint_loader",
        "generated_chunk_overwrite_compiler",
        "netty_edit_channel",
        "replication_culling",
        "revision_consensus",
    }
    missing = required_keys - set(capabilities)
    if missing:
        errors.append(f"missing capability entries: {sorted(missing)}")
    if errors:
        for error in errors:
            print("ERROR:", error)
        return 2
    print("PASS: live world-authoring platform invariants")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
