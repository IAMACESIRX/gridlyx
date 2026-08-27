#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "platform/chat-requirements.json"
LEDGER = ROOT / "docs/CHAT_REQUIREMENTS_TRACEABILITY.md"
VALID_STATES = {"implemented", "framework", "planned", "mixed", "transition"}
MIN_REQUIREMENTS = 32


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def main() -> int:
    try:
        data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"platform/chat-requirements.json: {exc}")

    if not isinstance(data, dict) or data.get("schema_version") != 1:
        fail("chat requirements manifest must be a schema_version 1 object")

    requirements = data.get("requirements")
    if not isinstance(requirements, list) or len(requirements) < MIN_REQUIREMENTS:
        fail(f"expected at least {MIN_REQUIREMENTS} retained requirement groups")

    ids: list[str] = []
    missing_paths: list[str] = []
    for requirement in requirements:
        if not isinstance(requirement, dict):
            fail("every requirement must be an object")
        requirement_id = requirement.get("id")
        title = requirement.get("title")
        state = requirement.get("state")
        paths = requirement.get("paths")
        if not isinstance(requirement_id, str) or not requirement_id.startswith("CR-"):
            fail("every requirement needs a CR-* id")
        if not isinstance(title, str) or not title.strip():
            fail(f"{requirement_id}: missing title")
        if state not in VALID_STATES:
            fail(f"{requirement_id}: invalid state {state!r}")
        if not isinstance(paths, list) or not paths:
            fail(f"{requirement_id}: at least one evidence/planning path is required")
        ids.append(requirement_id)
        for relative in paths:
            if not isinstance(relative, str) or not relative:
                fail(f"{requirement_id}: invalid path entry")
            if not (ROOT / relative).exists():
                missing_paths.append(f"{requirement_id}:{relative}")

    if len(ids) != len(set(ids)):
        fail("duplicate requirement ids")
    if missing_paths:
        fail("missing requirement evidence/planning paths: " + ", ".join(sorted(missing_paths)))

    try:
        ledger = LEDGER.read_text(encoding="utf-8")
    except OSError as exc:
        fail(f"docs/CHAT_REQUIREMENTS_TRACEABILITY.md: {exc}")

    for marker in ("CR-001", "CR-032", "## Coverage rule"):
        if marker not in ledger:
            fail(f"traceability ledger missing marker: {marker}")
    for requirement_id in ids:
        if requirement_id not in ledger:
            fail(f"traceability ledger does not mention {requirement_id}")

    print(f"PASS: {len(requirements)} conversation requirement groups retain repository evidence or planning paths")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
