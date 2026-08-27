#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main() -> int:
    manifest = json.loads((ROOT / "platform/ecosystems.json").read_text(encoding="utf-8"))
    required = {
        "java": ROOT / "templates/neoforge-26.2/build.gradle",
        "bedrock": ROOT / "bedrock/addon/behavior_pack/manifest.json",
        "javascript": ROOT / "scripts/procedural/example_matrix.js",
        "python": ROOT / "bridges/python/bridge_frame.py",
        "rust": ROOT / "native/rust/Cargo.toml",
        "cpp": ROOT / "native/cpp/CMakeLists.txt",
        "go": ROOT / "bridges/go/main.go",
        "csharp": ROOT / "bridges/csharp/BridgeFrame.cs",
    }
    errors = []
    declared = set(manifest["ecosystems"])
    if declared != set(required):
        errors.append(f"ecosystem manifest mismatch: {declared} != {set(required)}")
    for ecosystem, path in required.items():
        if not path.exists():
            errors.append(f"{ecosystem}: missing {path.relative_to(ROOT)}")
    if errors:
        for error in errors:
            print("ERROR:", error)
        return 2
    print("PASS: Gridelyx multi-language and cross-edition ecosystem structure")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
