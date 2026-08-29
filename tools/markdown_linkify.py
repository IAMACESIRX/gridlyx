#!/usr/bin/env python3
"""Make documentation targets explicitly clickable and locally resolvable.

This tool rewrites Markdown prose only. It intentionally does not inject Markdown
syntax into Python, JSON, YAML, Java, Gradle, or other executable/configuration
formats because doing so would corrupt their semantics.

What it enforces in Markdown:
- repository files and directories referenced in prose are clickable;
- inline-code repository references such as `tools/check.py` are clickable;
- http/https/www addresses and email addresses in prose are clickable;
- existing relative Markdown links point at a file/directory that exists;
- fenced code remains literal so commands and examples stay copy/paste safe.

Use --fix to rewrite Markdown files and --check to fail when a resolvable prose
target remains unlinked or an existing local Markdown link is broken.
"""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import sys
from urllib.parse import unquote, urlsplit

ROOT = Path(__file__).resolve().parents[1]
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
FENCE_RE = re.compile(r"^\s*(```|~~~)")
REFERENCE_DEFINITION_RE = re.compile(r"^\s{0,3}\[(?P<label>[^\]]+)\]:\s*(?P<dest>\S+)")
MARKDOWN_LINK_RE = re.compile(r"!?\[[^\]]*\]\((?P<dest>(?:[^()]|\([^)]*\))*)\)")

ANCHOR = r"(?:#[A-Za-z0-9_.%:/?&=+~-]+)?"
EXTENSIONLESS_NAMES = (
    r"README|LICENSE|NOTICE|CONTRIBUTING|CODE_OF_CONDUCT|SECURITY|SAFETY|"
    r"SUPPORT|CHANGELOG|Dockerfile|Makefile|gradlew|gradlew\.bat"
)
# Support ordinary repository paths plus glob-like documentation references such
# as tools/*.py. Glob references resolve to their containing repository directory.
PATH_SEGMENT = r"[A-Za-z0-9_.<>{}*?\[\]-]+"
REPO_BODY = (
    rf"(?:"
    rf"(?:\.\.?/)?(?:{PATH_SEGMENT}/)+{PATH_SEGMENT}/?"
    rf"|(?:\.\.?/)?{PATH_SEGMENT}/"
    rf"|{PATH_SEGMENT}\.[A-Za-z0-9_.-]+"
    rf"|(?:{EXTENSIONLESS_NAMES})"
    rf")"
)
REPO_RE = re.compile(rf"(?P<target>{REPO_BODY}{ANCHOR})")
INLINE_REPO_RE = re.compile(rf"`(?P<target>{REPO_BODY}{ANCHOR})`")

COMMON_TLDS = (
    r"com|org|net|io|dev|app|ai|co|edu|gov|info|biz|me|tech|cloud|site|"
    r"online|xyz|gg|au|uk|us|ca|nz|de|fr|jp"
)
BARE_DOMAIN = rf"(?:[A-Za-z0-9](?:[A-Za-z0-9-]{{0,62}}[A-Za-z0-9])?\.)+(?:{COMMON_TLDS})"
WEB_BODY = rf"(?:https?://|www\.|{BARE_DOMAIN})[^\s<>`]*"
WEB_RE = re.compile(rf"(?P<target>{WEB_BODY})", re.IGNORECASE)
INLINE_WEB_RE = re.compile(rf"`(?P<target>{WEB_BODY})`", re.IGNORECASE)
EMAIL_BODY = r"[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,63}"
EMAIL_RE = re.compile(rf"(?<![A-Z0-9._%+-])(?P<target>{EMAIL_BODY})(?![A-Z0-9._%+-])", re.IGNORECASE)
INLINE_EMAIL_RE = re.compile(rf"`(?P<target>{EMAIL_BODY})`", re.IGNORECASE)

TRAILING_PUNCTUATION = ".,;:!?\"'"
GLOB_CHARS = "*?["


def markdown_files() -> list[Path]:
    result: list[Path] = []
    for path in ROOT.rglob("*.md"):
        rel = path.relative_to(ROOT).as_posix()
        if any(rel == item or rel.startswith(item + "/") for item in SKIP_DIRS):
            continue
        if path.is_file():
            result.append(path)
    return sorted(result)


def split_anchor(value: str) -> tuple[str, str]:
    if "#" not in value:
        return value, ""
    path, anchor = value.split("#", 1)
    return path, "#" + anchor


def glob_base(raw_path: str) -> str:
    """Return the stable directory portion of a glob-like repository reference."""
    if not any(char in raw_path for char in GLOB_CHARS):
        return raw_path
    parts = raw_path.replace("\\", "/").split("/")
    stable: list[str] = []
    for part in parts:
        if any(char in part for char in GLOB_CHARS):
            break
        stable.append(part)
    if not stable:
        return "."
    if "." in stable[-1] and not raw_path.endswith("/"):
        stable = stable[:-1]
    return "/".join(stable) or "."


def resolve_repo_target(source: Path, written: str) -> Path | None:
    raw_path, _ = split_anchor(unquote(written))
    if not raw_path:
        return None
    raw_path = glob_base(raw_path)
    for candidate in (source.parent / raw_path, ROOT / raw_path):
        try:
            resolved = candidate.resolve(strict=False)
            resolved.relative_to(ROOT)
        except ValueError:
            continue
        if resolved.exists() and (resolved.is_file() or resolved.is_dir()):
            return resolved
    return None


def repo_href(source: Path, target: Path, written: str) -> str:
    _, anchor = split_anchor(written)
    relative = os.path.relpath(target, start=source.parent).replace(os.sep, "/")
    if relative == ".":
        relative = target.name
    if target.is_dir() and not relative.endswith("/"):
        relative += "/"
    return relative + anchor


def protected_spans(line: str, include_code: bool = True) -> list[tuple[int, int]]:
    spans: list[tuple[int, int]] = []
    for match in MARKDOWN_LINK_RE.finditer(line):
        spans.append(match.span())
    for match in re.finditer(r"<(?:https?://|www\.|mailto:)[^>]+>", line, re.IGNORECASE):
        spans.append(match.span())
    for match in re.finditer(r"(?:href|src)\s*=\s*[\"'][^\"']+[\"']", line, re.IGNORECASE):
        spans.append(match.span())
    if include_code:
        for match in re.finditer(r"`[^`]*`", line):
            spans.append(match.span())
    return spans


def overlaps(start: int, end: int, spans: list[tuple[int, int]]) -> bool:
    return any(start < right and end > left for left, right in spans)


def split_web_suffix(value: str) -> tuple[str, str]:
    suffix = ""
    while value and value[-1] in TRAILING_PUNCTUATION:
        suffix = value[-1] + suffix
        value = value[:-1]
    for closing, opening in ((")", "("), ("]", "["), ("}", "{")):
        while value.endswith(closing) and value.count(closing) > value.count(opening):
            suffix = closing + suffix
            value = value[:-1]
    return value, suffix


def web_href(value: str) -> str:
    lower = value.lower()
    href = value if lower.startswith(("http://", "https://")) else "https://" + value
    return href.replace("(", "%28").replace(")", "%29")


def is_explicit_web(value: str) -> bool:
    return value.lower().startswith(("http://", "https://", "www."))


def replace_matches(
    line: str,
    pattern: re.Pattern[str],
    builder,
    *,
    include_code_in_protection: bool,
) -> str:
    spans = protected_spans(line, include_code=include_code_in_protection)
    output: list[str] = []
    cursor = 0
    changed = False
    for match in pattern.finditer(line):
        if overlaps(match.start(), match.end(), spans):
            continue
        replacement = builder(match)
        if replacement is None:
            continue
        output.append(line[cursor:match.start()])
        output.append(replacement)
        cursor = match.end()
        changed = True
    if not changed:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_inline_web(source: Path, line: str) -> str:
    def build(match: re.Match[str]) -> str | None:
        written, suffix = split_web_suffix(match.group("target"))
        if not written:
            return None
        if not is_explicit_web(written) and resolve_repo_target(source, written) is not None:
            return None
        return f"[`{written}`]({web_href(written)}){suffix}"
    return replace_matches(line, INLINE_WEB_RE, build, include_code_in_protection=False)


def linkify_inline_email(line: str) -> str:
    def build(match: re.Match[str]) -> str:
        email = match.group("target")
        return f"[`{email}`](mailto:{email})"
    return replace_matches(line, INLINE_EMAIL_RE, build, include_code_in_protection=False)


def linkify_inline_repo(source: Path, line: str) -> str:
    def build(match: re.Match[str]) -> str | None:
        written = match.group("target")
        target = resolve_repo_target(source, written)
        if target is None:
            return None
        return f"[`{written}`]({repo_href(source, target, written)})"
    return replace_matches(line, INLINE_REPO_RE, build, include_code_in_protection=False)


def linkify_raw_web(source: Path, line: str) -> str:
    def build(match: re.Match[str]) -> str | None:
        if match.start() > 0 and line[match.start() - 1] in "@/":
            return None
        written, suffix = split_web_suffix(match.group("target"))
        if not written:
            return None
        if not is_explicit_web(written) and resolve_repo_target(source, written) is not None:
            return None
        return f"[{written}]({web_href(written)}){suffix}"
    return replace_matches(line, WEB_RE, build, include_code_in_protection=True)


def linkify_raw_email(line: str) -> str:
    def build(match: re.Match[str]) -> str:
        email = match.group("target")
        return f"[{email}](mailto:{email})"
    return replace_matches(line, EMAIL_RE, build, include_code_in_protection=True)


def linkify_raw_repo(source: Path, line: str) -> str:
    def build(match: re.Match[str]) -> str | None:
        if match.start() > 0 and line[match.start() - 1] in "@:/":
            return None
        written = match.group("target")
        target = resolve_repo_target(source, written)
        if target is None:
            return None
        return f"[{written}]({repo_href(source, target, written)})"
    return replace_matches(line, REPO_RE, build, include_code_in_protection=True)


def transform(source: Path, text: str) -> str:
    output: list[str] = []
    in_fence = False
    for line in text.splitlines(keepends=True):
        if FENCE_RE.match(line):
            in_fence = not in_fence
            output.append(line)
            continue
        if in_fence or REFERENCE_DEFINITION_RE.match(line):
            output.append(line)
            continue
        line = linkify_inline_web(source, line)
        line = linkify_inline_email(line)
        line = linkify_inline_repo(source, line)
        line = linkify_raw_web(source, line)
        line = linkify_raw_email(line)
        line = linkify_raw_repo(source, line)
        output.append(line)
    return "".join(output)


def is_external_destination(dest: str) -> bool:
    lowered = dest.lower()
    return lowered.startswith(("http://", "https://", "mailto:", "tel:", "data:")) or dest.startswith("#")


def strip_markdown_destination(dest: str) -> str:
    value = dest.strip()
    if value.startswith("<") and value.endswith(">"):
        value = value[1:-1]
    # Markdown allows an optional quoted title after a destination. Keep only the
    # destination for local existence checks.
    match = re.match(r"^(\S+?)(?:\s+[\"'].*[\"'])?$", value)
    return match.group(1) if match else value


def local_link_exists(source: Path, dest: str) -> bool:
    dest = strip_markdown_destination(dest)
    if not dest or is_external_destination(dest):
        return True
    parsed = urlsplit(dest)
    if parsed.scheme:
        return True
    raw_path = unquote(parsed.path)
    if not raw_path:
        return True
    candidate = (source.parent / raw_path).resolve(strict=False)
    try:
        candidate.relative_to(ROOT)
    except ValueError:
        return False
    return candidate.exists() and (candidate.is_file() or candidate.is_dir())


def broken_local_links(source: Path, text: str) -> list[str]:
    broken: list[str] = []
    in_fence = False
    for lineno, line in enumerate(text.splitlines(), start=1):
        if FENCE_RE.match(line):
            in_fence = not in_fence
            continue
        if in_fence:
            continue
        ref = REFERENCE_DEFINITION_RE.match(line)
        if ref and not local_link_exists(source, ref.group("dest")):
            broken.append(f"{source.relative_to(ROOT)}:{lineno}: {ref.group('dest')}")
        for match in MARKDOWN_LINK_RE.finditer(line):
            dest = match.group("dest")
            if not local_link_exists(source, dest):
                broken.append(f"{source.relative_to(ROOT)}:{lineno}: {dest}")
    return broken


def main() -> int:
    parser = argparse.ArgumentParser()
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--fix", action="store_true")
    mode.add_argument("--check", action="store_true")
    args = parser.parse_args()

    changed: list[str] = []
    broken: list[str] = []
    for path in markdown_files():
        original = path.read_text(encoding="utf-8")
        converted = transform(path, original)
        if converted != original:
            rel = path.relative_to(ROOT).as_posix()
            changed.append(rel)
            if args.fix:
                path.write_text(converted, encoding="utf-8")
        check_text = converted if args.fix else original
        broken.extend(broken_local_links(path, check_text))

    if args.fix:
        print(f"Linkified documentation targets in {len(changed)} file(s).")
        for rel in changed:
            print(f"  - {rel}")
        if broken:
            print("WARNING: broken local Markdown links remain:")
            for item in broken:
                print(f"  - {item}")
            return 1
        return 0

    failed = False
    if changed:
        failed = True
        print("Documentation contains non-clickable resolvable targets:")
        for rel in changed:
            print(f"  - {rel}")
    if broken:
        failed = True
        print("Documentation contains broken local Markdown links:")
        for item in broken:
            print(f"  - {item}")
    if failed:
        return 1

    print("PASS: documentation targets are clickable and local Markdown links resolve")
    return 0


if __name__ == "__main__":
    sys.exit(main())
