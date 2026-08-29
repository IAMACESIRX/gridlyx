#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / ".reference-cache"
UPSTREAM = CACHE / "upstream" / "mdk-26.2"
CORPUS = CACHE / "corpus"
INDEX = CACHE / "index"
REFERENCE_MANIFEST = ROOT / "platform" / "reference-sources.json"
PROVENANCE = INDEX / "reference-provenance.json"

TEXT_SUFFIXES = {
    ".c",
    ".cc",
    ".cpp",
    ".gradle",
    ".groovy",
    ".h",
    ".hpp",
    ".html",
    ".java",
    ".js",
    ".json",
    ".kt",
    ".kts",
    ".md",
    ".properties",
    ".py",
    ".rs",
    ".toml",
    ".ts",
    ".txt",
    ".xml",
    ".yaml",
    ".yml",
}
TEXT_FILENAMES = {
    "license",
    "license.txt",
    "notice",
    "notice.txt",
    "copying",
    "readme",
    "changelog",
}
SKIP_DIRS = {
    ".git",
    ".gradle",
    ".idea",
    ".vscode",
    "build",
    "out",
    "target",
    "node_modules",
}
MAX_TEXT_FILE_BYTES = 2 * 1024 * 1024
CHUNK_CHARS = 12_000
CHUNK_OVERLAP = 1_000


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


def load_reference_manifest() -> dict:
    data = json.loads(REFERENCE_MANIFEST.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1:
        raise RuntimeError("platform/reference-sources.json must use schema_version 1")
    return data


def load_provenance() -> dict[str, dict]:
    if not PROVENANCE.is_file():
        return {}
    data = json.loads(PROVENANCE.read_text(encoding="utf-8"))
    return {record["source_id"]: record for record in data.get("records", [])}


def source_roots(manifest: dict, provenance: dict[str, dict]) -> list[tuple[Path, dict]]:
    roots: list[tuple[Path, dict]] = []
    for entry in manifest.get("references", []):
        destination = entry.get("local_destination")
        if not destination:
            continue
        root = ROOT / destination
        if not root.is_dir() or CORPUS.resolve() not in root.resolve().parents:
            continue
        resolved = provenance.get(entry["id"], {})
        metadata = {
            "source_id": entry["id"],
            "kind": entry.get("kind"),
            "version": entry.get("version"),
            "resolved_revision": resolved.get("resolved_revision", entry.get("revision")),
            "source_url": entry.get("source_url"),
            "docs_url": entry.get("docs_url"),
            "roles": entry.get("roles", []),
            "ai_priority": entry.get("ai_priority", 0),
            "redistribution": "local-reference-only",
        }
        roots.append((root, metadata))
    return roots


def is_indexable_text(path: Path) -> bool:
    if any(part in SKIP_DIRS for part in path.parts):
        return False
    if path.stat().st_size > MAX_TEXT_FILE_BYTES:
        return False
    lower_name = path.name.lower()
    return path.suffix.lower() in TEXT_SUFFIXES or lower_name in TEXT_FILENAMES


def read_text(path: Path) -> str | None:
    raw = path.read_bytes()
    if b"\x00" in raw[:4096]:
        return None
    try:
        return raw.decode("utf-8")
    except UnicodeDecodeError:
        try:
            return raw.decode("utf-8", errors="replace")
        except Exception:
            return None


def chunks(text: str) -> list[tuple[int, int, str]]:
    if not text:
        return []
    result: list[tuple[int, int, str]] = []
    start = 0
    while start < len(text):
        end = min(len(text), start + CHUNK_CHARS)
        if end < len(text):
            # Prefer a semantic-ish boundary without making indexing dependent on a parser.
            boundary = max(text.rfind("\n\n", start, end), text.rfind("\n", start, end))
            if boundary > start + CHUNK_CHARS // 2:
                end = boundary + 1
        result.append((start, end, text[start:end]))
        if end >= len(text):
            break
        start = max(start + 1, end - CHUNK_OVERLAP)
    return result


def write_corpus_index() -> tuple[Path, int, int]:
    manifest = load_reference_manifest()
    provenance = load_provenance()
    roots = source_roots(manifest, provenance)
    if not roots:
        raise SystemExit(
            "No hydrated corpus sources found. Run `python tools/hydrate_ai_references.py --core` first."
        )

    INDEX.mkdir(parents=True, exist_ok=True)
    output = INDEX / "reference-corpus.jsonl"
    file_count = 0
    chunk_count = 0

    with output.open("w", encoding="utf-8", newline="\n") as stream:
        for source_root, metadata in sorted(roots, key=lambda item: item[1]["source_id"]):
            for path in sorted(item for item in source_root.rglob("*") if item.is_file()):
                if not is_indexable_text(path):
                    continue
                text = read_text(path)
                if text is None:
                    continue
                file_count += 1
                file_hash = sha256(path)
                relative = path.relative_to(source_root).as_posix()
                content_location = path.relative_to(ROOT).as_posix()
                for chunk_index, (start, end, content) in enumerate(chunks(text)):
                    record = {
                        **metadata,
                        "path": relative,
                        "content_location": content_location,
                        "sha256": file_hash,
                        "bytes": path.stat().st_size,
                        "chunk_index": chunk_index,
                        "char_start": start,
                        "char_end": end,
                        "content": content,
                    }
                    stream.write(json.dumps(record, ensure_ascii=False) + "\n")
                    chunk_count += 1

    return output, file_count, chunk_count


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Build ignored local indexes for dynamically hydrated upstream reference material."
    )
    parser.add_argument(
        "--no-hydrate",
        action="store_true",
        help="fail instead of hydrating a missing legacy MDK reference checkout",
    )
    parser.add_argument(
        "--corpus",
        action="store_true",
        help="build provenance-aware JSONL chunks for sources hydrated under .reference-cache/corpus",
    )
    args = parser.parse_args()

    outputs: list[Path] = []
    if args.corpus:
        output, files, chunk_count = write_corpus_index()
        outputs.append(output)
        print(
            f"PASS: AI reference corpus indexed {files} files into {chunk_count} chunks at {output.relative_to(ROOT)}"
        )
    else:
        if not UPSTREAM.is_dir():
            if args.no_hydrate:
                raise SystemExit(f"Missing local reference checkout: {UPSTREAM}")
            ensure_mdk()
        output = write_mdk_index()
        outputs.append(output)
        print(f"PASS: local MDK reference index written to {output.relative_to(ROOT)}")

    print("Index output remains under .reference-cache and is never committed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
