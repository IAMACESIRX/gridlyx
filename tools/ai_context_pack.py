#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
INDEX = ROOT / "build/ai/repo-index.json"
TOKEN_RE = re.compile(r"[A-Za-z][A-Za-z0-9_.:/-]{2,}")


def tokenize(text: str) -> set[str]:
    return {match.group(0).lower() for match in TOKEN_RE.finditer(text)}


def ensure_index() -> dict:
    if not INDEX.exists():
        import repo_index
        INDEX.parent.mkdir(parents=True, exist_ok=True)
        INDEX.write_text(json.dumps(repo_index.build_index(), indent=2, sort_keys=True) + "\n", encoding="utf-8")
    return json.loads(INDEX.read_text(encoding="utf-8"))


def score(query: set[str], entry: dict, chunk: dict | None = None) -> int:
    haystack = set(entry.get("terms", [])) | tokenize(entry["path"])
    if entry.get("heading"):
        haystack |= tokenize(entry["heading"])
    if chunk:
        haystack |= set(chunk.get("terms", []))
        if chunk.get("heading"):
            haystack |= tokenize(chunk["heading"])
    overlap = query & haystack
    path_bonus = sum(3 for token in query if token in entry["path"].lower())
    return len(overlap) * 2 + path_bonus


def main() -> int:
    parser = argparse.ArgumentParser(description="Emit task-scoped Gridelyx source chunks for AI/navigation.")
    parser.add_argument("query", nargs="+", help="task/query terms")
    parser.add_argument("--limit", type=int, default=12)
    parser.add_argument("--json", action="store_true")
    args = parser.parse_args()

    query_text = " ".join(args.query)
    query = tokenize(query_text)
    index = ensure_index()
    ranked = []

    for entry in index["files"]:
        chunks = entry.get("chunks") or [None]
        for chunk in chunks:
            value = score(query, entry, chunk)
            if value <= 0:
                continue
            ranked.append({
                "score": value,
                "path": entry["path"],
                "area": entry.get("area"),
                "start_line": chunk.get("start_line") if chunk else None,
                "end_line": chunk.get("end_line") if chunk else None,
                "heading": chunk.get("heading") if chunk else entry.get("heading"),
                "sha256": entry["sha256"],
            })

    ranked.sort(key=lambda item: (-item["score"], item["path"], item["start_line"] or 0))
    selected = ranked[: args.limit]

    if args.json:
        print(json.dumps({"query": query_text, "commit": index.get("commit"), "results": selected}, indent=2))
    else:
        print(f"Gridelyx context pack: {query_text}")
        print(f"commit: {index.get('commit') or 'unknown'}")
        for item in selected:
            lines = ""
            if item["start_line"]:
                lines = f":{item['start_line']}-{item['end_line']}"
            heading = f" — {item['heading']}" if item.get("heading") else ""
            print(f"{item['score']:>3}  {item['path']}{lines}{heading}")

    return 0 if selected else 1


if __name__ == "__main__":
    raise SystemExit(main())
