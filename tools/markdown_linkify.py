#!/usr/bin/env python3
"""Convert resolvable Markdown-document references into clickable relative links.

The linkifier is intentionally Markdown-aware enough to avoid mutating fenced code,
existing Markdown links/images, HTML href/src attributes, or URL text. It supports
both inline-code path references (for example `` `SECURITY.md` ``) and ordinary
text references (for example ``docs/FEATURE_MAP.md``).

Usage:
    python tools/markdown_linkify.py --fix
    python tools/markdown_linkify.py --check
"""

from __future__ import annotations

import argparse
import os
from pathlib import Path, PurePosixPath
import re
import sys
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]

# A repository document reference. Keep this deliberately conservative: project
# Markdown paths do not need spaces, query strings, or URL schemes.
MD_PATH_RE = re.compile(
    r"(?P<path>(?:\.\.?/|[A-Za-z0-9_.-]+/)*[A-Za-z0-9_.-]+\.md(?:#[A-Za-z0-9_.%/-]+)?)"
)
INLINE_CODE_MD_RE = re.compile(
    r"`(?P<path>(?:\.\.?/|[A-Za-z0-9_.-]+/)*[A-Za-z0-9_.-]+\.md(?:#[A-Za-z0-9_.%/-]+)?)`"
)
LINK_DEST_MD_RE = re.compile(r"\[[^\]]*\]\((?P<dest>[^)]+\.md(?:#[^)\s]+)?)\)")
FENCE_RE = re.compile(r"^\s*(```|~~~)")

SKIP_DIRS = {
    ".git",
    ".gradle",
    ".idea",
    ".reference-cache",
    "build",
    "node_modules",
    "target",
    "vault/objects",
}


def markdown_files() -> list[Path]:
    files: list[Path] = []
    for path in ROOT.rglob("*.md"):
        rel = path.relative_to(ROOT).as_posix()
        if any(rel == d or rel.startswith(d + "/") for d in SKIP_DIRS):
            continue
        if path.is_file():
            files.append(path)
    return sorted(files)


def split_anchor(text: str) -> tuple[str, str]:
    if "#" not in text:
        return text, ""
    path, anchor = text.split("#", 1)
    return path, "#" + anchor


def resolve_target(source: Path, written: str) -> Path | None:
    raw_path, _ = split_anchor(unquote(written))
    if not raw_path:
        return None

    # First respect normal Markdown semantics: resolve from the source file's
    # directory. If that does not exist, accept a repository-root path. This
    # keeps existing human-written root-style references useful while emitting
    # a correct relative href.
    candidates = [source.parent / raw_path, ROOT / raw_path]
    for candidate in candidates:
        try:
            resolved = candidate.resolve(strict=False)
            resolved.relative_to(ROOT)
        except ValueError:
            continue
        if resolved.is_file() and resolved.suffix.lower() == ".md":
            return resolved
    return None


def relative_href(source: Path, target: Path, anchor: str) -> str:
    relative = os.path.relpath(target, start=source.parent).replace(os.sep, "/")
    if relative == ".":
        relative = target.name
    return relative + anchor


def protected_spans(line: str) -> list[tuple[int, int]]:
    """Return spans that should never be rewritten on this line."""
    spans: list[tuple[int, int]] = []

    # Existing Markdown links and images.
    for match in re.finditer(r"!?\[[^\]]*\]\([^)]*\)", line):
        spans.append(match.span())

    # Autolinks / raw URLs.
    for match in re.finditer(r"https?://[^\s<>]+|<https?://[^>]+>", line):
        spans.append(match.span())

    # HTML href/src attributes.
    for match in re.finditer(r"(?:href|src)\s*=\s*[\"'][^\"']+[\"']", line, re.IGNORECASE):
        spans.append(match.span())

    return spans


def inside_any(start: int, end: int, spans: list[tuple[int, int]]) -> bool:
    return any(start < protected_end and end > protected_start for protected_start, protected_end in spans)


def linkify_inline_code(source: Path, line: str) -> str:
    spans = protected_spans(line)
    output: list[str] = []
    cursor = 0
    for match in INLINE_CODE_MD_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        written = match.group("path")
        target = resolve_target(source, written)
        if target is None:
            continue
        _, anchor = split_anchor(written)
        href = relative_href(source, target, anchor)
        output.append(line[cursor : match.start()])
        output.append(f"[`{written}`]({href})")
        cursor = match.end()
    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_plain_text(source: Path, line: str) -> str:
    spans = protected_spans(line)

    # Protect inline-code spans after the dedicated inline-code pass. This also
    # protects non-path code examples from accidental path matching.
    for match in re.finditer(r"`[^`]*`", line):
        spans.append(match.span())

    output: list[str] = []
    cursor = 0
    for match in MD_PATH_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue

        # Avoid treating an email/domain/URL fragment as a repository path.
        if match.start() > 0 and line[match.start() - 1] in "@:/":
            continue

        written = match.group("path")
        target = resolve_target(source, written)
        if target is None:
            continue

        _, anchor = split_anchor(written)
        href = relative_href(source, target, anchor)
        output.append(line[cursor : match.start()])
        output.append(f"[{written}]({href})")
        cursor = match.end()

    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def transform(source: Path, text: str) -> str:
    lines = text.splitlines(keepends=True)
    in_fence = False
    output: list[str] = []

    for line in lines:
        if FENCE_RE.match(line):
            in_fence = not in_fence
            output.append(line)
            continue
        if in_fence:
            output.append(line)
            continue

        newline = linkify_inline_code(source, line)
        newline = linkify_plain_text(source, newline)
        output.append(newline)

    return "".join(output)


def validate_markdown_links(source: Path, text: str) -> list[str]:
    errors: list[str] = []
    in_fence = False
    for line_number, line in enumerate(text.splitlines(), start=1):
        if FENCE_RE.match(line):
            in_fence = not in_fence
            continue
        if in_fence:
            continue
        for match in LINK_DEST_MD_RE.finditer(line):
            destination = match.group("dest").strip()
            if destination.startswith(("http://", "https://")):
                continue
            target = resolve_target(source, destination)
            if target is None:
                errors.append(
                    f"{source.relative_to(ROOT).as_posix()}:{line_number}: "
                    f"broken Markdown document link: {destination}"
                )
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--fix", action="store_true", help="rewrite Markdown files in place")
    mode.add_argument("--check", action="store_true", help="fail if linkification is required")
    args = parser.parse_args()

    changed: list[str] = []
    errors: list[str] = []

    for path in markdown_files():
        original = path.read_text(encoding="utf-8")
        transformed = transform(path, original)
        rel = path.relative_to(ROOT).as_posix()

        if transformed != original:
            changed.append(rel)
            if args.fix:
                path.write_text(transformed, encoding="utf-8")

        errors.extend(validate_markdown_links(path, transformed if args.fix else original))

    if args.check and changed:
        print("Markdown references are not fully clickable:")
        for rel in changed:
            print(f"  - {rel}")

    if errors:
        print("Markdown link validation errors:")
        for error in errors:
            print(f"  - {error}")

    if args.fix:
        print(f"Linkified Markdown references in {len(changed)} file(s).")
        for rel in changed:
            print(f"  - {rel}")
        return 1 if errors else 0

    return 1 if changed or errors else 0


if __name__ == "__main__":
    sys.exit(main())
