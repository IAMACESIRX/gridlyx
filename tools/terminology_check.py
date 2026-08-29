#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "platform/terminology.json"
BRAND = ROOT / "platform/brand.json"


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def load(path: Path) -> dict:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"{path.relative_to(ROOT)}: {exc}")
    if not isinstance(value, dict):
        fail(f"{path.relative_to(ROOT)} must contain a JSON object")
    return value


def contains_excluded_part(path: Path, excluded: set[str]) -> bool:
    try:
        relative = path.relative_to(ROOT)
    except ValueError:
        return True
    return any(part in excluded for part in relative.parts)


def main() -> int:
    terminology = load(MANIFEST)
    brand = load(BRAND)
    if terminology.get("schema_version") != 2:
        fail("platform/terminology.json schema_version must be 2")

    canonical = terminology.get("canonical")
    if not isinstance(canonical, dict):
        fail("platform/terminology.json canonical must be an object")
    required_pairs = {
        "root_brand": "root_brand",
        "suite_name": "product_name",
        "bridge_magic": "bridge_magic",
        "native_symbol_prefix": "native_symbol_prefix",
    }
    for terminology_key, brand_key in required_pairs.items():
        if canonical.get(terminology_key) != brand.get(brand_key):
            fail(f"terminology {terminology_key} disagrees with platform/brand.json {brand_key}")
    if canonical.get("root_brand") != "Gridelyx":
        fail("canonical root brand must be Gridelyx")

    enforcement = terminology.get("enforcement")
    if not isinstance(enforcement, dict):
        fail("enforcement must be an object")
    if enforcement.get("mode") != "strict":
        fail("terminology enforcement mode must be strict")
    if enforcement.get("whole_current_tree") is not True:
        fail("whole_current_tree enforcement must be enabled")

    codepoints = enforcement.get("retired_identifier_codepoints")
    if not isinstance(codepoints, list) or not codepoints or not all(isinstance(value, int) for value in codepoints):
        fail("retired_identifier_codepoints must be a non-empty integer list")
    retired = "".join(chr(value) for value in codepoints).lower()

    excluded_values = enforcement.get("excluded_directories", [])
    suffix_values = enforcement.get("binary_or_generated_suffixes", [])
    if not isinstance(excluded_values, list) or not isinstance(suffix_values, list):
        fail("terminology exclusions must be arrays")
    excluded = {str(value) for value in excluded_values}
    binary_suffixes = {str(value).lower() for value in suffix_values}

    path_violations: list[str] = []
    text_violations: list[str] = []
    for path in ROOT.rglob("*"):
        if contains_excluded_part(path, excluded):
            continue
        relative = path.relative_to(ROOT)
        if enforcement.get("scan_path_names") is True and retired in str(relative).lower():
            path_violations.append(str(relative))
        if not path.is_file() or path.suffix.lower() in binary_suffixes:
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            continue
        for line_number, line in enumerate(text.splitlines(), 1):
            if retired in line.lower():
                text_violations.append(f"{relative}:{line_number}")

    if path_violations:
        fail("retired identifier remains in project-owned path(s): " + ", ".join(path_violations))
    if text_violations:
        preview = ", ".join(text_violations[:30])
        extra = len(text_violations) - 30
        if extra > 0:
            preview += f", ... (+{extra} more)"
        fail("retired identifier remains in current-tree text: " + preview)

    legacy = terminology.get("known_legacy_technical_identifiers")
    if legacy != []:
        fail("known_legacy_technical_identifiers must be empty after the Gridelyx v2 migration")

    required_gridelyx_files = [
        "README.md",
        "AGENTS.md",
        "AI_HANDOFF.md",
        "docs/PROJECT_OVERVIEW.md",
        "docs/GRIDELYX_BRIDGE_PROTOCOL.md",
        "platform/brand.json",
    ]
    missing_identity: list[str] = []
    for relative in required_gridelyx_files:
        path = ROOT / relative
        if not path.is_file() or "gridelyx" not in path.read_text(encoding="utf-8", errors="ignore").lower():
            missing_identity.append(relative)
    if missing_identity:
        fail("canonical Gridelyx identity missing from: " + ", ".join(missing_identity))

    print("PASS: current project-owned tree uses Gridelyx identity exclusively and protocol/native v2 is canonical")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
