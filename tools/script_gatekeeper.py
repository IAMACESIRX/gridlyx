#!/usr/bin/env python3
from __future__ import annotations

import ast
import re
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SCAN_ROOTS = ("tools", "scripts", "bridges", ".devcontainer")
SCRIPT_SUFFIXES = {".py", ".sh", ".bash", ".js", ".mjs", ".ps1"}
MAX_SCRIPT_BYTES = 1024 * 1024
FORBIDDEN = (
    re.compile(r"\bcurl\b[^\n|]*\|\s*(?:ba)?sh\b", re.IGNORECASE),
    re.compile(r"\bwget\b[^\n|]*\|\s*(?:ba)?sh\b", re.IGNORECASE),
    re.compile(r"\bInvoke-Expression\b[^\n]*\bDownloadString\b", re.IGNORECASE),
)


def script_files() -> list[Path]:
    found: list[Path] = []
    for root_name in SCAN_ROOTS:
        root = ROOT / root_name
        if not root.exists():
            continue
        for path in root.rglob("*"):
            if path.is_file() and path.suffix.lower() in SCRIPT_SUFFIXES:
                found.append(path)
    return sorted(found)


def syntax_check(path: Path, text: str, errors: list[str]) -> None:
    suffix = path.suffix.lower()
    if suffix == ".py":
        try:
            ast.parse(text, filename=str(path))
        except SyntaxError as exc:
            errors.append(f"{path.relative_to(ROOT)}: Python syntax error: {exc}")
    elif suffix in {".js", ".mjs"} and shutil.which("node"):
        result = subprocess.run(
            ["node", "--check", str(path)],
            cwd=ROOT,
            capture_output=True,
            text=True,
            check=False,
        )
        if result.returncode:
            errors.append(f"{path.relative_to(ROOT)}: JavaScript syntax check failed")
    elif suffix in {".sh", ".bash"} and shutil.which("bash"):
        result = subprocess.run(
            ["bash", "-n", str(path)],
            cwd=ROOT,
            capture_output=True,
            text=True,
            check=False,
        )
        if result.returncode:
            errors.append(f"{path.relative_to(ROOT)}: shell syntax check failed")


def main() -> int:
    errors: list[str] = []
    checked = 0
    for path in script_files():
        checked += 1
        data = path.read_bytes()
        relative = path.relative_to(ROOT)
        if len(data) > MAX_SCRIPT_BYTES:
            errors.append(f"{relative}: executable script exceeds {MAX_SCRIPT_BYTES} bytes")
            continue
        if b"\x00" in data:
            errors.append(f"{relative}: NUL byte in executable script")
            continue
        text = data.decode("utf-8", errors="strict")
        for pattern in FORBIDDEN:
            if pattern.search(text):
                errors.append(f"{relative}: forbidden download-and-execute pattern")
        syntax_check(path, text, errors)

    if errors:
        for item in errors:
            print("ERROR:", item)
        print(f"FAILED: {len(errors)} script gate violation(s)")
        return 2
    print(f"PASS: script gatekeeper checked {checked} non-Java scripts")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
