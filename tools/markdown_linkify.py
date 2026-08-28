#!/usr/bin/env python3
"""Convert resolvable documentation targets into explicit clickable Markdown links.

The linkifier is intentionally conservative. It rewrites prose references only and
skips fenced code, existing Markdown links/images, HTML href/src attributes,
Markdown autolinks, and reference-style link definitions. Supported targets include:

- repository files and directories, regardless of extension;
- inline-code repository paths;
- raw http:// and https:// URLs;
- raw www. addresses and common bare-domain addresses;
- raw email addresses;
- inline-code web/email addresses.

Visible text is preserved. For example `` `tools/check.py` `` becomes
``[`tools/check.py`](tools/check.py)`` and ``https://example.com`` becomes
``[https://example.com](https://example.com)``.

Usage:
    python tools/markdown_linkify.py --fix
    python tools/markdown_linkify.py --check
"""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import sys
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]

ANCHOR = r"(?:#[A-Za-z0-9_.%:/?&=+~-]+)?"
EXTENSIONLESS_REPO_NAMES = (
    r"README|LICENSE|NOTICE|CONTRIBUTING|CODE_OF_CONDUCT|SECURITY|SAFETY|"
    r"SUPPORT|CHANGELOG|Dockerfile|Makefile|gradlew|gradlew\.bat"
)
REPO_REF_BODY = (
    rf"(?:"
    rf"(?:\.\.?/)?(?:[A-Za-z0-9_.-]+/)+[A-Za-z0-9_.-]+/?"
    rf"|(?:\.\.?/)?[A-Za-z0-9_.-]+/"
    rf"|[A-Za-z0-9_.-]+\.[A-Za-z0-9_.-]+"
    rf"|(?:{EXTENSIONLESS_REPO_NAMES})"
    rf")"
)
REPO_REF_RE = re.compile(rf"(?P<path>{REPO_REF_BODY}{ANCHOR})")
INLINE_CODE_REPO_REF_RE = re.compile(rf"`(?P<path>{REPO_REF_BODY}{ANCHOR})`")

COMMON_TLDS = (
    r"com|org|net|io|dev|app|ai|co|edu|gov|info|biz|me|tech|cloud|site|"
    r"online|xyz|gg|au|uk|us|ca|nz|de|fr|jp"
)
BARE_DOMAIN = rf"(?:[A-Za-z0-9](?:[A-Za-z0-9-]{{0,62}}[A-Za-z0-9])?\.)+(?:{COMMON_TLDS})"
WEB_TARGET_BODY = rf"(?:https?://|www\.|{BARE_DOMAIN})(?:[^\s<>`]*)"
INLINE_CODE_URL_RE = re.compile(rf"`(?P<url>{WEB_TARGET_BODY})`", re.IGNORECASE)
RAW_URL_RE = re.compile(rf"(?P<url>{WEB_TARGET_BODY})", re.IGNORECASE)
EMAIL_BODY = r"[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,63}"
INLINE_CODE_EMAIL_RE = re.compile(rf"`(?P<email>{EMAIL_BODY})`", re.IGNORECASE)
RAW_EMAIL_RE = re.compile(rf"(?<![A-Z0-9._%+-])(?P<email>{EMAIL_BODY})(?![A-Z0-9._%+-])", re.IGNORECASE)

FENCE_RE = re.compile(r"^\s*(```|~~~)")
REFERENCE_DEFINITION_RE = re.compile(r"^\s{0,3}\[[^\]]+\]:\s*\S+")

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

TRAILING_URL_PUNCTUATION = ".,;:!?\"'"


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


def resolve_repo_target(source: Path, written: str) -> Path | None:
    raw_path, _ = split_anchor(unquote(written))
    if not raw_path:
        return None

    candidates = [source.parent / raw_path, ROOT / raw_path]
    for candidate in candidates:
        try:
            resolved = candidate.resolve(strict=False)
            resolved.relative_to(ROOT)
        except ValueError:
            continue
        if resolved.exists() and (resolved.is_file() or resolved.is_dir()):
            return resolved
    return None


def relative_href(source: Path, target: Path, anchor: str) -> str:
    relative = os.path.relpath(target, start=source.parent).replace(os.sep, "/")
    if relative == ".":
        relative = target.name
    if target.is_dir() and not relative.endswith("/"):
        relative += "/"
    return relative + anchor


def protected_spans(line: str) -> list[tuple[int, int]]:
    """Return spans that should never be rewritten on this line."""
    spans: list[tuple[int, int]] = []

    # Existing inline Markdown links/images.
    for match in re.finditer(r"!?\[[^\]]*\]\((?:[^()]|\([^)]*\))*\)", line):
        spans.append(match.span())

    # Markdown autolinks.
    for match in re.finditer(r"<(?:https?://|www\.|mailto:)[^>]+>", line, re.IGNORECASE):
        spans.append(match.span())

    # HTML attributes containing addresses.
    for match in re.finditer(r"(?:href|src)\s*=\s*[\"'][^\"']+[\"']", line, re.IGNORECASE):
        spans.append(match.span())

    return spans


def inside_any(start: int, end: int, spans: list[tuple[int, int]]) -> bool:
    return any(start < protected_end and end > protected_start for protected_start, protected_end in spans)


def split_url_suffix(url: str) -> tuple[str, str]:
    """Keep prose punctuation and unmatched closing delimiters outside the URL."""
    suffix = ""
    while url and url[-1] in TRAILING_URL_PUNCTUATION:
        suffix = url[-1] + suffix
        url = url[:-1]

    pairs = ((")", "("), ("]", "["), ("}", "{"))
    changed = True
    while url and changed:
        changed = False
        for closing, opening in pairs:
            if url.endswith(closing) and url.count(closing) > url.count(opening):
                suffix = closing + suffix
                url = url[:-1]
                changed = True
                break

    return url, suffix


def url_href(written: str) -> str:
    lower = written.lower()
    href = written if lower.startswith(("http://", "https://")) else "https://" + written
    # Parentheses are legal URL characters but awkward in Markdown destinations.
    return href.replace("(", "%28").replace(")", "%29")


def is_bare_web_target(written: str) -> bool:
    return not written.lower().startswith(("http://", "https://", "www."))


def linkify_inline_code_urls(source: Path, line: str) -> str:
    spans = protected_spans(line)
    output: list[str] = []
    cursor = 0
    for match in INLINE_CODE_URL_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        written, suffix = split_url_suffix(match.group("url"))
        if not written:
            continue
        # If a bare domain-looking token is an actual local file, let the repo-path
        # pass link it locally instead of converting it into an external website.
        if is_bare_web_target(written) and resolve_repo_target(source, written) is not None:
            continue
        output.append(line[cursor : match.start()])
        output.append(f"[`{written}`]({url_href(written)}){suffix}")
        cursor = match.end()
    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_inline_code_emails(line: str) -> str:
    spans = protected_spans(line)
    output: list[str] = []
    cursor = 0
    for match in INLINE_CODE_EMAIL_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        email = match.group("email")
        output.append(line[cursor : match.start()])
        output.append(f"[`{email}`](mailto:{email})")
        cursor = match.end()
    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_inline_code_repo_refs(source: Path, line: str) -> str:
    spans = protected_spans(line)
    output: list[str] = []
    cursor = 0
    for match in INLINE_CODE_REPO_REF_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        written = match.group("path")
        target = resolve_repo_target(source, written)
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


def linkify_raw_urls(source: Path, line: str) -> str:
    spans = protected_spans(line)
    for match in re.finditer(r"`[^`]*`", line):
        spans.append(match.span())

    output: list[str] = []
    cursor = 0
    for match in RAW_URL_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        if match.start() > 0 and line[match.start() - 1] in "@/":
            continue

        written, suffix = split_url_suffix(match.group("url"))
        if not written:
            continue
        # Prefer a valid repository destination over treating a local filename such
        # as something.dev as a bare external domain.
        if is_bare_web_target(written) and resolve_repo_target(source, written) is not None:
            continue

        output.append(line[cursor : match.start()])
        output.append(f"[{written}]({url_href(written)}){suffix}")
        cursor = match.end()

    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_raw_emails(line: str) -> str:
    spans = protected_spans(line)
    for match in re.finditer(r"`[^`]*`", line):
        spans.append(match.span())

    output: list[str] = []
    cursor = 0
    for match in RAW_EMAIL_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        email = match.group("email")
        output.append(line[cursor : match.start()])
        output.append(f"[{email}](mailto:{email})")
        cursor = match.end()

    if cursor == 0:
        return line
    output.append(line[cursor:])
    return "".join(output)


def linkify_plain_repo_refs(source: Path, line: str) -> str:
    spans = protected_spans(line)
    for match in re.finditer(r"`[^`]*`", line):
        spans.append(match.span())

    output: list[str] = []
    cursor = 0
    for match in REPO_REF_RE.finditer(line):
        if inside_any(match.start(), match.end(), spans):
            continue
        if match.start() > 0 and line[match.start() - 1] in "@:/":
            continue

        written = match.group("path")
        target = resolve_repo_target(source, written)
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
        if in_fence or REFERENCE_DEFINITION_RE.match(line):
            output.append(line)
            continue

        newline = linkify_inline_code_urls(source, line)
        newline = linkify_inline_code_emails(newline)
        newline = linkify_inline_code_repo_refs(source, newline)
        newline = linkify_raw_urls(source, newline)
        newline = linkify_raw_emails(newline)
        newline = linkify_plain_repo_refs(source, newline)
        output.append(newline)

    return "".join(output)


def validate_generated_repo_links(source: Path, text: str) -> list[str]:
    """Validate local Markdown destinations that look like repository references."""
    errors: list[str] = []
    in_fence = False
    link_re = re.compile(r"!?\[[^\]]*\]\((?P<dest>(?:[^()]|\([^)]*\))+)\)")

    for line_number, line in enumerate(text.splitlines(), start=1):
        if FENCE_RE.match(line):
            in_fence = not in_fence
            continue
        if in_fence or REFERENCE_DEFINITION_RE.match(line):
            continue

        for match in link_re.finditer(line):
            destination = match.group("dest").strip()
            if not destination or destination.startswith("#"):
                continue
            if destination.lower().startswith(("http://", "https://", "mailto:", "tel:")):
                continue

            raw_path, _ = split_anchor(unquote(destination))
            candidate = (source.parent / raw_path).resolve(strict=False)
            try:
                candidate.relative_to(ROOT)
            except ValueError:
                continue

            if candidate.exists():
                continue

            # Only flag destinations that clearly resemble repository paths.
            if "/" in raw_path or "." in Path(raw_path).name:
                errors.append(
                    f"{source.relative_to(ROOT).as_posix()}:{line_number}: "
                    f"broken repository link: {destination}"
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

        validation_text = transformed if args.fix else original
        errors.extend(validate_generated_repo_links(path, validation_text))

    if args.check and changed:
        print("Documentation contains non-clickable resolvable link targets:")
        for rel in changed:
            print(f"  - {rel}")

    if errors:
        print("Documentation link validation errors:")
        for error in errors:
            print(f"  - {error}")

    if args.fix:
        print(f"Linkified documentation targets in {len(changed)} file(s).")
        for rel in changed:
            print(f"  - {rel}")
        return 1 if errors else 0

    return 1 if changed or errors else 0


if __name__ == "__main__":
    sys.exit(main())
