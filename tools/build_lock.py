#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOCK_PATH = ROOT / "platform/master-build.lock.json"
VERSIONS = json.loads((ROOT / "platform/versions.json").read_text(encoding="utf-8"))


def digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def projects() -> list[Path]:
    result = [ROOT / VERSIONS["template"]]
    mods = ROOT / "mods"
    if mods.exists():
        result.extend(
            path for path in sorted(mods.iterdir())
            if path.is_dir() and (path / "build.gradle").exists()
        )
    return result


def refresh() -> int:
    canonical = ROOT / VERSIONS["template"] / "build.gradle"
    data = {
        "schema_version": 1,
        "canonical_path": str(canonical.relative_to(ROOT)).replace("\\", "/"),
        "sha256": digest(canonical),
        "policy": (
            "All generated mod workspaces inherit this build.gradle byte-for-byte. "
            "Change only by an explicit platform lock refresh."
        ),
    }
    LOCK_PATH.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"REFRESHED: {data['sha256']}")
    return 0


def check() -> int:
    lock = json.loads(LOCK_PATH.read_text(encoding="utf-8"))
    expected = lock["sha256"]
    failures = []
    for project in projects():
        path = project / "build.gradle"
        actual = digest(path)
        if actual != expected:
            failures.append(
                f"{project.relative_to(ROOT)}: build.gradle {actual} != locked {expected}"
            )
    if failures:
        for failure in failures:
            print("ERROR:", failure)
        return 2
    print(f"PASS: master build.gradle lock {expected}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--check", action="store_true")
    mode.add_argument("--refresh", action="store_true")
    args = parser.parse_args()
    return refresh() if args.refresh else check()


if __name__ == "__main__":
    raise SystemExit(main())
