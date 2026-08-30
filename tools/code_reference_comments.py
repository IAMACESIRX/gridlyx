#!/usr/bin/env python3
from __future__ import annotations

import argparse
import ast
import re
import subprocess
from collections import defaultdict
from pathlib import Path, PurePosixPath
from typing import Iterable

ROOT = Path(__file__).resolve().parents[1]
CANONICAL_BLOB_BASE = "https://github.com/IAMACESIRX/gridlyx/blob/main/"
MARKER = "Gridelyx local reference:"

COMMENT_PREFIX = {
    ".py": "#", ".yml": "#", ".yaml": "#", ".toml": "#",
    ".sh": "#", ".bash": "#", ".zsh": "#", ".ps1": "#",
    ".java": "//", ".kt": "//", ".kts": "//", ".groovy": "//",
    ".gradle": "//", ".js": "//", ".jsx": "//", ".ts": "//",
    ".tsx": "//", ".rs": "//", ".c": "//", ".cc": "//",
    ".cpp": "//", ".cxx": "//", ".h": "//", ".hh": "//",
    ".hpp": "//", ".hxx": "//", ".cs": "//", ".go": "//",
}
SOURCE_SUFFIXES = frozenset(COMMENT_PREFIX)
PATH_SUFFIXES = (
    ".md", ".py", ".json", ".yml", ".yaml", ".toml", ".java", ".kt",
    ".kts", ".groovy", ".gradle", ".rs", ".c", ".cc", ".cpp", ".cxx",
    ".h", ".hh", ".hpp", ".hxx", ".js", ".jsx", ".ts", ".tsx", ".cs",
    ".go", ".xml", ".properties", ".txt", ".svg", ".mmd", ".proto",
)

QUOTED_STRING_RE = re.compile(r"[\"']([^\"'\\\\\n]+)[\"']")
QUOTED_INCLUDE_RE = re.compile(r'^\s*#\s*include\s*"([^"]+)"')
JS_IMPORT_RE = re.compile(
    r'(?:\bfrom\s*|\brequire\s*\(\s*|\bimport\s*\(\s*)[\"\']([^\"\']+)[\"\']'
)
JAVA_IMPORT_RE = re.compile(r"^\s*import\s+(?:static\s+)?([A-Za-z0-9_.$]+)\s*;")
RUST_MOD_RE = re.compile(r"^\s*(?:pub\s+)?mod\s+([A-Za-z_][A-Za-z0-9_]*)\s*;")
RUST_PATH_MOD_RE = re.compile(
    r'^\s*#\s*\[\s*path\s*=\s*[\"\']([^\"\']+)[\"\']\s*\]\s*(?:pub\s+)?mod\b'
)
GRADLE_APPLY_RE = re.compile(
    r'(?:apply\s+from\s*:\s*|from\s*\()\s*[\"\']([^\"\']+)[\"\']'
)
YAML_LOCAL_USE_RE = re.compile(r"^\s*uses\s*:\s*(\./[^\s#]+)")
PATH_TOKEN_RE = re.compile(
    r"(?<![A-Za-z0-9_.-])((?:\./|\.\./|[A-Za-z0-9_.-]+/)+(?:[A-Za-z0-9_.@+-]+(?:\.[A-Za-z0-9_.@+-]+)+))(?![A-Za-z0-9_.-])"
)


def git_lines(*args: str) -> list[str]:
    proc = subprocess.run(
        ["git", *args],
        cwd=ROOT,
        check=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )
    return [line for line in proc.stdout.splitlines() if line]


def tracked_files() -> set[PurePosixPath]:
    return {PurePosixPath(line) for line in git_lines("ls-files")}


def normalize_repo_path(path: Path) -> PurePosixPath | None:
    try:
        resolved = path.resolve(strict=False)
        rel = resolved.relative_to(ROOT.resolve())
    except (ValueError, OSError):
        return None
    return PurePosixPath(rel.as_posix())


def build_indexes(
    tracked: set[PurePosixPath],
) -> tuple[dict[str, list[PurePosixPath]], list[PurePosixPath]]:
    by_basename: dict[str, list[PurePosixPath]] = defaultdict(list)
    java_roots: set[PurePosixPath] = set()
    for path in tracked:
        by_basename[path.name].append(path)
        parts = path.parts
        if path.suffix == ".java" and "java" in parts:
            idx = max(i for i, part in enumerate(parts) if part == "java")
            java_roots.add(PurePosixPath(*parts[: idx + 1]))
    return by_basename, sorted(java_roots)


def url_for(path: PurePosixPath) -> str:
    return CANONICAL_BLOB_BASE + path.as_posix()


def resolve_candidate(
    raw: str,
    current: PurePosixPath,
    tracked: set[PurePosixPath],
    by_basename: dict[str, list[PurePosixPath]],
) -> PurePosixPath | None:
    raw = raw.strip().strip("'\"")
    if not raw or raw.startswith(("http://", "https://", "git@", "ssh://")):
        return None

    if raw.startswith("./"):
        action_dir = PurePosixPath(raw[2:].rstrip("/"))
        for name in ("action.yml", "action.yaml"):
            candidate = action_dir / name
            if candidate in tracked:
                return candidate

    raw_posix = raw.replace("\\", "/")
    path = PurePosixPath(raw_posix)
    candidates: list[PurePosixPath] = []

    if raw_posix.startswith(("./", "../")):
        normalized = normalize_repo_path(ROOT / current.parent.as_posix() / raw_posix)
        if normalized is not None:
            candidates.append(normalized)
    else:
        candidates.append(path)
        normalized = normalize_repo_path(ROOT / current.parent.as_posix() / raw_posix)
        if normalized is not None:
            candidates.append(normalized)

    for candidate in candidates:
        if candidate in tracked:
            return candidate

    matches = by_basename.get(path.name, [])
    if len(matches) == 1:
        return matches[0]
    return None


def java_import_target(
    symbol: str,
    tracked: set[PurePosixPath],
    java_roots: list[PurePosixPath],
) -> PurePosixPath | None:
    parts = symbol.replace("$", ".").split(".")
    for end in range(len(parts), 0, -1):
        rel = PurePosixPath(*parts[:end]).with_suffix(".java")
        for root in java_roots:
            candidate = root / rel
            if candidate in tracked:
                return candidate
    return None


def python_module_target(
    module: str | None,
    level: int,
    current: PurePosixPath,
    tracked: set[PurePosixPath],
) -> PurePosixPath | None:
    base = current.parent
    for _ in range(max(level - 1, 0)):
        base = base.parent

    module_parts = tuple(part for part in (module or "").split(".") if part)
    candidates: list[PurePosixPath] = []
    if level:
        if module_parts:
            candidates.append(base.joinpath(*module_parts).with_suffix(".py"))
            candidates.append(base.joinpath(*module_parts, "__init__.py"))
    elif module_parts:
        root_module = PurePosixPath(*module_parts)
        candidates.extend(
            (
                root_module.with_suffix(".py"),
                root_module / "__init__.py",
                current.parent / root_module.with_suffix(".py"),
                current.parent / root_module / "__init__.py",
            )
        )

    for candidate in candidates:
        if candidate in tracked and candidate != current:
            return candidate
    return None


def strings_in_call(node: ast.Call) -> Iterable[str]:
    values = list(node.args) + [keyword.value for keyword in node.keywords]
    for item in values:
        if isinstance(item, ast.Constant) and isinstance(item.value, str):
            yield item.value
        elif isinstance(item, (ast.List, ast.Tuple, ast.Set)):
            for element in item.elts:
                if isinstance(element, ast.Constant) and isinstance(element.value, str):
                    yield element.value


def python_references(
    text: str,
    current: PurePosixPath,
    tracked: set[PurePosixPath],
    by_basename: dict[str, list[PurePosixPath]],
) -> dict[int, set[PurePosixPath]]:
    refs: dict[int, set[PurePosixPath]] = defaultdict(set)
    try:
        tree = ast.parse(text)
    except SyntaxError:
        return refs

    for node in ast.walk(tree):
        if isinstance(node, ast.Import):
            for alias in node.names:
                target = python_module_target(alias.name, 0, current, tracked)
                if target:
                    refs[node.lineno].add(target)
        elif isinstance(node, ast.ImportFrom):
            target = python_module_target(node.module, node.level, current, tracked)
            if target:
                refs[node.lineno].add(target)
        elif isinstance(node, ast.Call):
            for literal in strings_in_call(node):
                target = resolve_candidate(literal, current, tracked, by_basename)
                if target:
                    refs[node.lineno].add(target)
    return refs


def resolve_js_target(
    raw: str,
    current: PurePosixPath,
    tracked: set[PurePosixPath],
) -> PurePosixPath | None:
    source = ROOT / current.parent.as_posix() / raw
    candidates = [source]
    for suffix in (".js", ".jsx", ".ts", ".tsx", ".mjs", ".cjs"):
        candidates.append(Path(str(source) + suffix))
    for suffix in (".js", ".jsx", ".ts", ".tsx"):
        candidates.append(source / f"index{suffix}")
    for candidate in candidates:
        normalized = normalize_repo_path(candidate)
        if normalized in tracked:
            return normalized
    return None


def line_references(
    text: str,
    current: PurePosixPath,
    tracked: set[PurePosixPath],
    by_basename: dict[str, list[PurePosixPath]],
    java_roots: list[PurePosixPath],
) -> dict[int, set[PurePosixPath]]:
    suffix = current.suffix.lower()
    if suffix == ".py":
        return python_references(text, current, tracked, by_basename)

    refs: dict[int, set[PurePosixPath]] = defaultdict(set)
    for lineno, line in enumerate(text.splitlines(), 1):
        stripped = line.strip()
        if not stripped or MARKER in line:
            continue

        if suffix == ".java":
            match = JAVA_IMPORT_RE.match(line)
            if match:
                target = java_import_target(match.group(1), tracked, java_roots)
                if target and target != current:
                    refs[lineno].add(target)

        if suffix in {".c", ".cc", ".cpp", ".cxx", ".h", ".hh", ".hpp", ".hxx"}:
            match = QUOTED_INCLUDE_RE.match(line)
            if match:
                target = resolve_candidate(match.group(1), current, tracked, by_basename)
                if target and target != current:
                    refs[lineno].add(target)

        if suffix in {".js", ".jsx", ".ts", ".tsx"}:
            for match in JS_IMPORT_RE.finditer(line):
                raw = match.group(1)
                if raw.startswith("."):
                    target = resolve_js_target(raw, current, tracked)
                    if target and target != current:
                        refs[lineno].add(target)

        if suffix == ".rs":
            path_match = RUST_PATH_MOD_RE.match(line)
            if path_match:
                target = resolve_candidate(path_match.group(1), current, tracked, by_basename)
                if target and target != current:
                    refs[lineno].add(target)
            mod_match = RUST_MOD_RE.match(line)
            if mod_match:
                name = mod_match.group(1)
                for candidate in (
                    current.parent / f"{name}.rs",
                    current.parent / name / "mod.rs",
                ):
                    if candidate in tracked and candidate != current:
                        refs[lineno].add(candidate)
                        break

        if suffix in {".gradle", ".groovy", ".kts"}:
            for match in GRADLE_APPLY_RE.finditer(line):
                target = resolve_candidate(match.group(1), current, tracked, by_basename)
                if target and target != current:
                    refs[lineno].add(target)

        if suffix in {".yml", ".yaml"}:
            local_use = YAML_LOCAL_USE_RE.match(line)
            if local_use:
                target = resolve_candidate(local_use.group(1), current, tracked, by_basename)
                if target and target != current:
                    refs[lineno].add(target)

        if stripped.startswith(("//", "#", "/*", "*", "REM ")):
            continue

        scan_paths = "(" in line or suffix in {
            ".yml", ".yaml", ".sh", ".bash", ".zsh", ".ps1",
            ".gradle", ".groovy", ".kts",
        }
        if scan_paths:
            for match in QUOTED_STRING_RE.finditer(line):
                raw = match.group(1)
                if raw.lower().endswith(PATH_SUFFIXES):
                    target = resolve_candidate(raw, current, tracked, by_basename)
                    if target and target != current:
                        refs[lineno].add(target)

            for match in PATH_TOKEN_RE.finditer(line):
                raw = match.group(1)
                if raw.lower().endswith(PATH_SUFFIXES):
                    target = resolve_candidate(raw, current, tracked, by_basename)
                    if target and target != current:
                        refs[lineno].add(target)

    return refs


def strip_generated_comments(text: str) -> str:
    generated_prefixes = (
        "# Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/",
        "// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/",
    )
    lines = [
        line
        for line in text.splitlines()
        if not line.lstrip().startswith(generated_prefixes)
    ]
    result = "\n".join(lines)
    if text.endswith("\n"):
        result += "\n"
    return result


def transformed_text(
    path: PurePosixPath,
    text: str,
    tracked: set[PurePosixPath],
    by_basename: dict[str, list[PurePosixPath]],
    java_roots: list[PurePosixPath],
) -> str:
    clean = strip_generated_comments(text)
    refs = line_references(clean, path, tracked, by_basename, java_roots)
    if not refs:
        return clean

    prefix = COMMENT_PREFIX[path.suffix.lower()]
    lines = clean.splitlines()
    output: list[str] = []
    for lineno, line in enumerate(lines, 1):
        targets = sorted(refs.get(lineno, ()), key=lambda value: value.as_posix())
        if targets:
            indent = line[: len(line) - len(line.lstrip())]
            for target in targets:
                output.append(f"{indent}{prefix} {MARKER} {url_for(target)}")
        output.append(line)

    result = "\n".join(output)
    if clean.endswith("\n"):
        result += "\n"
    return result


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Add/check clickable comments above project-local file references."
    )
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--fix", action="store_true", help="rewrite tracked source files")
    mode.add_argument("--check", action="store_true", help="fail when comments are stale")
    args = parser.parse_args()

    tracked = tracked_files()
    by_basename, java_roots = build_indexes(tracked)
    changed: list[str] = []

    for path in sorted(tracked):
        if path.suffix.lower() not in SOURCE_SUFFIXES:
            continue
        full_path = ROOT / path.as_posix()
        try:
            with full_path.open("r", encoding="utf-8", newline="") as handle:
                original = handle.read()
        except (UnicodeDecodeError, OSError):
            continue

        crlf_count = original.count("\r\n")
        remainder = original.replace("\r\n", "")
        if crlf_count and ("\r" in remainder or "\n" in remainder):
            raise ValueError(f"mixed newline conventions are unsupported: {path}")
        if crlf_count:
            logical = original.replace("\r\n", "\n")
            newline = "\r\n"
        elif "\r" in original:
            if "\n" in original:
                raise ValueError(f"mixed newline conventions are unsupported: {path}")
            logical = original.replace("\r", "\n")
            newline = "\r"
        else:
            logical = original
            newline = "\n"

        updated_logical = transformed_text(path, logical, tracked, by_basename, java_roots)
        updated = updated_logical if newline == "\n" else updated_logical.replace("\n", newline)
        if updated == original:
            continue

        changed.append(path.as_posix())
        if args.fix:
            with full_path.open("w", encoding="utf-8", newline="") as handle:
                handle.write(updated)

    if args.check and changed:
        print("FAIL: project-local reference comments are missing or stale:")
        for path in changed:
            print(f"  - {path}")
        print("Run: python tools/code_reference_comments.py --fix")
        return 1

    if args.fix:
        print(f"PASS: synchronized project-local reference comments in {len(changed)} file(s)")
    else:
        print("PASS: project-local reference comments are synchronized")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
