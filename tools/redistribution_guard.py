#!/usr/bin/env python3
from __future__ import annotations

import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "vault" / "manifest.json"

FORBIDDEN_SUFFIXES = (
    ".jar",
    ".class",
    ".zip",
    ".7z",
    ".rar",
    ".tgz",
    ".tar.gz",
    ".dll",
    ".so",
    ".dylib",
    ".exe",
    ".msi",
)
FORBIDDEN_PART = re.compile(r"\.part-\d+$", re.IGNORECASE)
FORBIDDEN_UPSTREAM_TREES = (
    "references/upstream/mdk-",
    "references/upstream/minecraft",
    "references/upstream/neoforge",
    "references/upstream/lwjgl",
    "references/upstream/jdk",
)


def tracked_paths() -> list[str]:
    raw = subprocess.check_output(["git", "ls-files", "-z"], cwd=ROOT)
    return [item.decode("utf-8") for item in raw.split(b"\0") if item]


def validate_manifest() -> list[str]:
    failures: list[str] = []
    data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    if data.get("schema_version") != 2:
        failures.append("vault/manifest.json must use acquisition schema_version 2")
    policy = data.get("policy", {})
    if policy.get("mode") != "acquire-at-build-or-run-time":
        failures.append("acquisition policy mode must be acquire-at-build-or-run-time")
    if policy.get("repository_must_not_redistribute_upstream_binaries") is not True:
        failures.append("repository_must_not_redistribute_upstream_binaries must be true")
    for artifact in data.get("artifacts", []):
        if artifact.get("repository_storage") != "prohibited":
            failures.append(f"{artifact.get('id', '<unknown>')}: repository_storage must be prohibited")
    return failures


def validate_tracked_files() -> list[str]:
    failures: list[str] = []
    for path in tracked_paths():
        lower = path.lower()
        if lower.endswith(FORBIDDEN_SUFFIXES) or FORBIDDEN_PART.search(lower):
            failures.append(f"tracked binary/archive is prohibited: {path}")
        if any(lower.startswith(prefix) for prefix in FORBIDDEN_UPSTREAM_TREES):
            failures.append(f"tracked upstream reference tree is prohibited: {path}")
    return failures


def main() -> int:
    failures = validate_manifest() + validate_tracked_files()
    if failures:
        print("FAIL: public-repository redistribution guard")
        for failure in failures:
            print(f" - {failure}")
        return 2
    print("PASS: no tracked upstream binary/archive payloads; dependencies are acquisition-only")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
