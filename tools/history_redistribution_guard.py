#!/usr/bin/env python3
from __future__ import annotations

import argparse
import re
import subprocess
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

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
FORBIDDEN_UPSTREAM_PREFIXES = (
    "vault/mdk/",
    "vault/neoforge_installer/",
    "vault/jdk/",
    "vault/lwjgl/",
    "references/upstream/mdk-",
    "references/upstream/minecraft",
    "references/upstream/neoforge",
    "references/upstream/lwjgl",
    "references/upstream/jdk",
)
# Chunked payloads were historically designed around ~24 MiB pieces. A large-blob
# tripwire catches a renamed chunk even when its extension is intentionally hidden.
LARGE_BLOB_TRIPWIRE = 20 * 1024 * 1024
ALLOWED_LARGE_SUFFIXES = (
    ".png",
    ".jpg",
    ".jpeg",
    ".webp",
    ".gif",
    ".svg",
)


@dataclass(frozen=True)
class ObjectPath:
    oid: str
    path: str


def git(*args: str, input_text: str | None = None) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=ROOT,
        check=True,
        text=True,
        input=input_text,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    return result.stdout


def available_refs(include_pull_refs: bool) -> list[str]:
    prefixes = ["refs/heads", "refs/remotes/origin", "refs/tags"]
    if include_pull_refs:
        prefixes.append("refs/remotes/pull")
    raw = git("for-each-ref", "--format=%(refname)", *prefixes)
    refs = sorted({line.strip() for line in raw.splitlines() if line.strip()})
    # actions/checkout on a detached PR ref can leave the current commit outside
    # the enumerated namespaces; include HEAD explicitly as a final safety net.
    refs.append("HEAD")
    return refs


def enumerate_objects(refs: list[str]) -> list[ObjectPath]:
    raw = git("rev-list", "--objects", *refs)
    objects: list[ObjectPath] = []
    seen: set[tuple[str, str]] = set()
    for line in raw.splitlines():
        if not line:
            continue
        oid, sep, path = line.partition(" ")
        if not sep or not path:
            continue
        key = (oid, path)
        if key not in seen:
            seen.add(key)
            objects.append(ObjectPath(oid=oid, path=path))
    return objects


def blob_sizes(oids: list[str]) -> dict[str, tuple[str, int]]:
    if not oids:
        return {}
    request = "".join(f"{oid}\n" for oid in oids)
    raw = git("cat-file", "--batch-check=%(objectname) %(objecttype) %(objectsize)", input_text=request)
    result: dict[str, tuple[str, int]] = {}
    for line in raw.splitlines():
        parts = line.split()
        if len(parts) != 3:
            continue
        oid, object_type, size_text = parts
        try:
            result[oid] = (object_type, int(size_text))
        except ValueError:
            continue
    return result


def path_violation(path: str) -> str | None:
    lower = path.lower()
    if lower.endswith(FORBIDDEN_SUFFIXES) or FORBIDDEN_PART.search(lower):
        return "forbidden binary/archive extension"
    if any(lower.startswith(prefix) for prefix in FORBIDDEN_UPSTREAM_PREFIXES):
        return "forbidden upstream payload tree"
    return None


def scan(refs: list[str]) -> list[str]:
    failures: list[str] = []
    objects = enumerate_objects(refs)
    meta = blob_sizes(sorted({item.oid for item in objects}))

    for item in objects:
        reason = path_violation(item.path)
        if reason:
            failures.append(f"{reason}: {item.path} [{item.oid}]")
            continue

        object_type, size = meta.get(item.oid, ("", 0))
        lower = item.path.lower()
        if (
            object_type == "blob"
            and size >= LARGE_BLOB_TRIPWIRE
            and not lower.endswith(ALLOWED_LARGE_SUFFIXES)
        ):
            failures.append(
                f"large historical blob requires explicit review ({size} bytes): {item.path} [{item.oid}]"
            )

    return sorted(set(failures))


def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Reject prohibited upstream binary/archive payloads anywhere in Git history reachable from fetched public refs."
        )
    )
    parser.add_argument(
        "--include-pull-refs",
        action="store_true",
        help="also scan refs/remotes/pull when the workflow has fetched GitHub pull refs",
    )
    parser.add_argument(
        "refs",
        nargs="*",
        help="optional explicit refs; defaults to fetched heads/remotes/tags plus HEAD",
    )
    args = parser.parse_args()

    refs = args.refs or available_refs(args.include_pull_refs)
    failures = scan(refs)
    if failures:
        print("FAIL: reachable Git history contains prohibited or suspicious binary payloads")
        for failure in failures:
            print(f" - {failure}")
        print("Rewrite/purge the affected reachable refs before making the repository public.")
        return 2

    print(f"PASS: reachable history is payload-free across {len(refs)} scanned refs")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
