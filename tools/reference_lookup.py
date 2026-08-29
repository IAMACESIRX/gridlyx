#!/usr/bin/env python3
from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / ".reference-cache"
UPSTREAM = CACHE / "upstream" / "mdk-26.2"
INDEX = CACHE / "index"


def ensure_mdk() -> None:
    if UPSTREAM.is_dir():
        return
    subprocess.run(
        [sys.executable, str(ROOT / "tools" / "hydrate_references.py"), "--mdk"],
        cwd=ROOT,
        check=True,
    )


def search(term: str, limit: int) -> None:
    ensure_mdk()
    needle = term.casefold()
    count = 0
    for path in sorted(item for item in UPSTREAM.rglob("*") if item.is_file() and ".git" not in item.parts):
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError:
            continue
        for line_number, line in enumerate(text.splitlines(), 1):
            if needle in line.casefold():
                print(f"{path.relative_to(UPSTREAM)}:{line_number}: {line}")
                count += 1
                if count >= limit:
                    return
    if count == 0:
        print("No local upstream-reference matches.")


def show(relative: str) -> None:
    ensure_mdk()
    target = (UPSTREAM / relative).resolve()
    if not target.is_relative_to(UPSTREAM.resolve()) or not target.is_file():
        raise SystemExit(f"MDK reference path not found: {relative}")
    try:
        sys.stdout.write(target.read_text(encoding="utf-8"))
    except UnicodeDecodeError as exc:
        raise SystemExit("Reference file is binary; direct binary extraction is intentionally unsupported") from exc


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Search the dynamically hydrated, ignored NeoForge MDK reference checkout."
    )
    sub = parser.add_subparsers(dest="command", required=True)
    search_parser = sub.add_parser("search")
    search_parser.add_argument("term")
    search_parser.add_argument("--limit", type=int, default=50)
    show_parser = sub.add_parser("show")
    show_parser.add_argument("path")
    sub.add_parser("index")
    args = parser.parse_args()

    if args.command == "search":
        search(args.term, args.limit)
    elif args.command == "show":
        show(args.path)
    else:
        subprocess.run([sys.executable, str(ROOT / "tools" / "build_reference_indexes.py")], cwd=ROOT, check=True)
        print(f"Local index directory: {INDEX}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
