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
# tripwire catches renamed chunks even if an unknown binary signature is used.
LARGE_BLOB_TRIPWIRE = 20 * 1024 * 1024
ALLOWED_LARGE_SUFFIXES = (
    ".png",
    ".jpg",
    ".jpeg",
    ".webp",
    ".gif",
    ".svg",
)

# Magic numbers detect renamed archives/executables/classes regardless of filename.
# ZIP covers JAR/JMOD/ordinary ZIP containers; all of those are forbidden as
# upstream/vendored payloads by the public-source policy.
FORBIDDEN_MAGIC: tuple[tuple[bytes, str], ...] = (
    (b"PK\x03\x04", "ZIP/JAR/JMOD archive"),
    (b"PK\x05\x06", "empty ZIP archive"),
    (b"PK\x07\x08", "spanned ZIP archive"),
    (b"\xca\xfe\xba\xbe", "Java class file"),
    (b"MZ", "PE/Windows executable"),
    (b"\x7fELF", "ELF executable/shared library"),
    (b"\xfe\xed\xfa\xce", "Mach-O binary"),
    (b"\xce\xfa\xed\xfe", "Mach-O binary"),
    (b"\xfe\xed\xfa\xcf", "Mach-O 64-bit binary"),
    (b"\xcf\xfa\xed\xfe", "Mach-O 64-bit binary"),
    (b"\xca\xfe\xba\xbe", "Java class/Mach-O universal magic"),
    (b"\xca\xfe\xba\xbf", "Mach-O universal binary"),
    (b"7z\xbc\xaf\x27\x1c", "7z archive"),
    (b"Rar!\x1a\x07", "RAR archive"),
    (b"\x1f\x8b", "gzip-compressed payload"),
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


def blob_prefix(oid: str, limit: int = 16) -> bytes:
    proc = subprocess.Popen(
        ["git", "cat-file", "blob", oid],
        cwd=ROOT,
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
    )
    try:
        if proc.stdout is None:
            return b""
        prefix = proc.stdout.read(limit)
        proc.stdout.close()
        return prefix
    finally:
        if proc.poll() is None:
            proc.kill()
        proc.wait()


def path_violation(path: str) -> str | None:
    lower = path.lower()
    if lower.endswith(FORBIDDEN_SUFFIXES) or FORBIDDEN_PART.search(lower):
        return "forbidden binary/archive extension"
    if any(lower.startswith(prefix) for prefix in FORBIDDEN_UPSTREAM_PREFIXES):
        return "forbidden upstream payload tree"
    return None


def magic_violation(prefix: bytes) -> str | None:
    for magic, description in FORBIDDEN_MAGIC:
        if prefix.startswith(magic):
            return description
    return None


def scan(refs: list[str]) -> list[str]:
    failures: list[str] = []
    objects = enumerate_objects(refs)
    meta = blob_sizes(sorted({item.oid for item in objects}))
    prefix_cache: dict[str, bytes] = {}

    for item in objects:
        reason = path_violation(item.path)
        if reason:
            failures.append(f"{reason}: {item.path} [{item.oid}]")
            continue

        object_type, size = meta.get(item.oid, ("", 0))
        if object_type != "blob":
            continue

        prefix = prefix_cache.setdefault(item.oid, blob_prefix(item.oid))
        magic_reason = magic_violation(prefix)
        if magic_reason:
            failures.append(
                f"forbidden binary signature ({magic_reason}): {item.path} [{item.oid}]"
            )
            continue

        lower = item.path.lower()
        if size >= LARGE_BLOB_TRIPWIRE and not lower.endswith(ALLOWED_LARGE_SUFFIXES):
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
