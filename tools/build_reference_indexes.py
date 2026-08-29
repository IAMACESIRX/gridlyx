#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / ".reference-cache"
UPSTREAM = CACHE / "upstream" / "mdk-26.2"
INDEX = CACHE / "index"


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for block in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def ensure_mdk() -> None:
    if UPSTREAM.is_dir():
        return
    subprocess.run(
        [sys.executable, str(ROOT / "tools" / "hydrate_references.py"), "--mdk"],
        cwd=ROOT,
        check=True,
    )


def write_mdk_index() -> Path:
    INDEX.mkdir(parents=True, exist_ok=True)
    output = INDEX / "neoforge-mdk-26.2-files.tsv"
    with output.open("w", encoding="utf-8", newline="\n") as stream:
        stream.write("path\tbytes\tsha256\n")
        for path in sorted(item for item in UPSTREAM.rglob("*") if item.is_file() and ".git" not in item.parts):
            relative = path.relative_to(UPSTREAM).as_posix()
            stream.write(f"{relative}\t{path.stat().st_size}\t{sha256(path)}\n")
    return output


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Build local indexes for dynamically hydrated upstream reference material."
    )
    parser.add_argument("--no-hydrate", action="store_true", help="fail instead of hydrating a missing MDK checkout")
    args = parser.parse_args()

    if not UPSTREAM.is_dir():
        if args.no_hydrate:
            raise SystemExit(f"Missing local reference checkout: {UPSTREAM}")
        ensure_mdk()

    output = write_mdk_index()
    print(f"PASS: local reference index written to {output.relative_to(ROOT)}")
    print("Index output remains under .reference-cache and is never committed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
