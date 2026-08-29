#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOCK_PATH = ROOT / "platform/master-build.lock.json"
VERSIONS = json.loads((ROOT / "platform/versions.json").read_text(encoding="utf-8"))


def git_blob_sha1(path: Path) -> str:
    content = path.read_bytes()
    header = f"blob {len(content)}\0".encode("ascii")
    return hashlib.sha1(header + content).hexdigest()


def projects() -> list[Path]:
    result = [ROOT / VERSIONS["template"]]
    mods = ROOT / "mods"
    if mods.exists():
        result.extend(
            path
            for path in sorted(mods.iterdir())
            if path.is_dir() and (path / "build.gradle").exists()
        )
    return result


def refresh() -> int:
    canonical = ROOT / VERSIONS["template"] / "build.gradle"
    data = {
        "schema_version": 2,
        "canonical_path": str(canonical.relative_to(ROOT)).replace("\\", "/"),
        "git_blob_sha1": git_blob_sha1(canonical),
        "policy": (
            "All generated mod workspaces inherit this build.gradle byte-for-byte. "
            "Change only by an explicit platform lock refresh."
        ),
    }
    LOCK_PATH.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
    print(f"REFRESHED: {data['git_blob_sha1']}")
    return 0


def check() -> int:
    lock = json.loads(LOCK_PATH.read_text(encoding="utf-8"))
    if lock.get("schema_version") != 2:
        print("ERROR: master build lock must use schema_version 2")
        return 2
    expected = lock["git_blob_sha1"]
    failures = []
    for project in projects():
        path = project / "build.gradle"
        actual = git_blob_sha1(path)
        if actual != expected:
            failures.append(
                f"{project.relative_to(ROOT)}: build.gradle blob {actual} != locked {expected}"
            )
    if failures:
        for failure in failures:
            print("ERROR:", failure)
        return 2
    print(f"PASS: master build.gradle Git blob lock {expected}")
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
