#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_OUTPUT = ROOT / "build/ai/repo-index.json"
EXCLUDED_PARTS = {".git", ".gradle", ".idea", "build", "target", "node_modules", "run", "vault", "__pycache__"}
TEXT_SUFFIXES = {
    ".md", ".txt", ".java", ".kt", ".kts", ".rs", ".py", ".js", ".ts", ".tsx", ".jsx",
    ".json", ".toml", ".yml", ".yaml", ".gradle", ".properties", ".xml", ".cpp", ".cc", ".c",
    ".h", ".hpp", ".go", ".cs", ".sh", ".bat", ".ps1", ".lang", ".html", ".css",
}
TOKEN_RE = re.compile(r"[A-Za-z][A-Za-z0-9_.:/-]{2,}")


def git_sha() -> str | None:
    try:
        return subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True, stderr=subprocess.DEVNULL).strip()
    except Exception:
        return None


def excluded(path: Path) -> bool:
    rel = path.relative_to(ROOT)
    return any(part in EXCLUDED_PARTS for part in rel.parts)


def area_for(rel: str) -> str:
    first = rel.split("/", 1)[0]
    return {
        "studio": "studio",
        "bedrock": "bedrock",
        "native": "native",
        "templates": "java-runtime",
        "mods": "generated-mods",
        "bridges": "bridges",
        "tools": "tooling",
        "docs": "documentation",
        "ai": "ai-context",
        "platform": "platform",
        "references": "references",
        ".github": "ci",
    }.get(first, "root")


def first_heading(text: str) -> str | None:
    for line in text.splitlines():
        if line.startswith("# "):
            return line[2:].strip()
    return None


def terms(text: str, limit: int = 40) -> list[str]:
    found = {match.group(0).lower() for match in TOKEN_RE.finditer(text)}
    return sorted(found)[:limit]


def chunks(lines: list[str], chunk_lines: int = 80) -> list[dict]:
    result = []
    for offset in range(0, len(lines), chunk_lines):
        section = lines[offset : offset + chunk_lines]
        content = "\n".join(section)
        heading = None
        for line in reversed(lines[: offset + 1]):
            if line.startswith("#"):
                heading = line.lstrip("#").strip()
                break
        result.append({
            "start_line": offset + 1,
            "end_line": offset + len(section),
            "heading": heading,
            "terms": terms(content),
        })
    return result


def build_index() -> dict:
    files = []
    for path in sorted(ROOT.rglob("*")):
        if not path.is_file() or path.is_symlink() or excluded(path):
            continue
        rel = path.relative_to(ROOT).as_posix()
        data = path.read_bytes()
        entry = {
            "path": rel,
            "size": len(data),
            "sha256": hashlib.sha256(data).hexdigest(),
            "suffix": path.suffix.lower(),
            "area": area_for(rel),
        }
        if path.suffix.lower() in TEXT_SUFFIXES and len(data) <= 2_000_000:
            try:
                text = data.decode("utf-8")
            except UnicodeDecodeError:
                text = ""
            if text:
                entry["heading"] = first_heading(text)
                entry["terms"] = terms(text)
                entry["chunks"] = chunks(text.splitlines())
        files.append(entry)
    return {
        "schema_version": 1,
        "commit": git_sha(),
        "file_count": len(files),
        "files": files,
    }


def validate_required() -> list[str]:
    required = [
        "AGENTS.md", "AI_HANDOFF.md", "ai/CONTEXT.md", "ai/context-map.json",
        "docs/PROJECT_OVERVIEW.md", "docs/ROADMAP.md", "docs/FEATURE_MAP.md",
        "studio/Cargo.toml", "studio/core/Cargo.toml", "studio/providers/providers.json",
    ]
    return [path for path in required if not (ROOT / path).exists()]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    missing = validate_required()
    if missing:
        for path in missing:
            print(f"ERROR: missing AI/index contract path: {path}")
        return 2

    index = build_index()
    if args.check:
        print(f"PASS: repository index scanned {index['file_count']} files")
        return 0

    output = args.output if args.output.is_absolute() else ROOT / args.output
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(index, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print(f"WROTE: {output.relative_to(ROOT)} ({index['file_count']} files)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
