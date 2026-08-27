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


def main() -> int:
    terminology = load(MANIFEST)
    brand = load(BRAND)
    if terminology.get("schema_version") != 1:
        fail("platform/terminology.json schema_version must be 1")

    canonical = terminology.get("canonical")
    if not isinstance(canonical, dict):
        fail("platform/terminology.json canonical must be an object")
    if canonical.get("root_brand") != brand.get("root_brand"):
        fail("terminology root_brand disagrees with platform/brand.json")
    if canonical.get("suite_name") != brand.get("product_name"):
        fail("terminology suite_name disagrees with platform/brand.json")
    if canonical.get("root_brand") != "Gridelyx":
        fail("canonical root brand must be Gridelyx")

    retired = terminology.get("retired_brand_terms")
    enforcement = terminology.get("enforcement")
    legacy = terminology.get("known_legacy_technical_identifiers")
    if not isinstance(retired, list) or not retired:
        fail("retired_brand_terms must be a non-empty list")
    if not isinstance(enforcement, dict):
        fail("enforcement must be an object")
    if not isinstance(legacy, list):
        fail("known_legacy_technical_identifiers must be a list")

    missing_legacy: list[str] = []
    for entry in legacy:
        if not isinstance(entry, dict):
            fail("every known legacy identifier must be an object")
        for field in ("identifier", "class", "status", "evidence"):
            if not entry.get(field):
                fail(f"legacy identifier missing {field}: {entry!r}")
        if not (ROOT / entry["evidence"]).exists():
            missing_legacy.append(f"{entry['identifier']}:{entry['evidence']}")
    if missing_legacy:
        fail("legacy compatibility evidence path(s) missing: " + ", ".join(missing_legacy))

    strict_files = enforcement.get("strict_public_files")
    markers = enforcement.get("allowed_retired_context_markers")
    if not isinstance(strict_files, list) or not strict_files:
        fail("strict_public_files must be a non-empty list")
    if not isinstance(markers, list) or not markers:
        fail("allowed_retired_context_markers must be a non-empty list")

    violations: list[str] = []
    missing_gridelyx: list[str] = []
    for relative in strict_files:
        path = ROOT / relative
        if not path.exists():
            violations.append(f"missing strict public file: {relative}")
            continue
        text = path.read_text(encoding="utf-8")
        if "gridelyx" not in text.lower():
            missing_gridelyx.append(relative)
        for number, line in enumerate(text.splitlines(), 1):
            lowered = line.lower()
            hit = next((term for term in retired if term.lower() in lowered), None)
            if hit is None:
                continue
            if not any(marker.lower() in lowered for marker in markers):
                violations.append(f"{relative}:{number}: retired term {hit!r} lacks migration/legacy context")

    if missing_gridelyx:
        fail("strict public files missing Gridelyx identity: " + ", ".join(missing_gridelyx))
    if violations:
        fail("; ".join(violations))

    mode = enforcement.get("mode")
    if mode not in {"staged", "strict"}:
        fail("terminology enforcement mode must be staged or strict")

    print(
        "PASS: Gridelyx public terminology is enforced; "
        f"{len(legacy)} legacy technical identifiers remain explicitly classified under {mode} migration"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
