#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

REQUIRED_PATHS = [
    "AGENTS.md",
    "AI_HANDOFF.md",
    "docs/PROJECT_PLAN.md",
    "docs/CHAT_REQUIREMENTS_TRACEABILITY.md",
    "platform/chat-requirements.json",
    "tools/chat_requirements_check.py",
    "COMMUNITY.md",
    "CONTRIBUTING.md",
    "CODE_OF_CONDUCT.md",
    "SUPPORT.md",
    "ai/AI_ORGANISATION.md",
    "ai/DRIFT_MITIGATION.md",
    "ai/work-state.json",
    "ai/decision-ledger.json",
    "ai/assumption-ledger.json",
    "ai/context-map.json",
]

HANDOFF_HEADINGS = [
    "## Source-of-truth order",
    "## Non-negotiable invariants",
    "## Session start protocol",
    "## Session end protocol",
]


def load_json(relative: str) -> dict:
    path = ROOT / relative
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SystemExit(f"FAIL: {relative}: {exc}") from exc
    if not isinstance(value, dict):
        raise SystemExit(f"FAIL: {relative}: top-level value must be an object")
    if value.get("schema_version") != 1:
        raise SystemExit(f"FAIL: {relative}: schema_version must be 1")
    return value


def require_paths() -> None:
    missing = [relative for relative in REQUIRED_PATHS if not (ROOT / relative).exists()]
    if missing:
        raise SystemExit("FAIL: missing continuity paths: " + ", ".join(missing))


def require_unique(records: list[dict], key: str, label: str) -> None:
    values = [record.get(key) for record in records]
    if any(not value or not isinstance(value, str) for value in values):
        raise SystemExit(f"FAIL: {label}: every record needs a non-empty {key}")
    if len(values) != len(set(values)):
        raise SystemExit(f"FAIL: {label}: duplicate {key}")


def validate_work_state() -> None:
    data = load_json("ai/work-state.json")
    objectives = data.get("active_objectives")
    if not isinstance(objectives, list):
        raise SystemExit("FAIL: ai/work-state.json: active_objectives must be a list")
    require_unique(objectives, "id", "active objectives")
    for objective in objectives:
        if not objective.get("objective") or not objective.get("state"):
            raise SystemExit("FAIL: work objective missing objective/state")


def validate_ledgers() -> None:
    decisions = load_json("ai/decision-ledger.json").get("decisions")
    assumptions = load_json("ai/assumption-ledger.json").get("assumptions")
    if not isinstance(decisions, list) or not isinstance(assumptions, list):
        raise SystemExit("FAIL: decision/assumption ledgers must contain lists")
    require_unique(decisions, "id", "decisions")
    require_unique(assumptions, "id", "assumptions")

    for decision in decisions:
        for field in ("status", "scope", "decision", "reason", "review_trigger"):
            if not decision.get(field):
                raise SystemExit(f"FAIL: decision {decision.get('id')} missing {field}")

    for assumption in assumptions:
        for field in ("status", "scope", "statement", "confidence", "validation_route", "review_trigger"):
            if not assumption.get(field):
                raise SystemExit(f"FAIL: assumption {assumption.get('id')} missing {field}")


def validate_context_map() -> None:
    data = load_json("ai/context-map.json")
    entrypoints = data.get("entrypoints", [])
    domains = data.get("domains", {})
    if not isinstance(entrypoints, list) or not isinstance(domains, dict):
        raise SystemExit("FAIL: ai/context-map.json entrypoints/domains have invalid types")

    referenced: list[str] = list(entrypoints)
    for values in domains.values():
        if not isinstance(values, list):
            raise SystemExit("FAIL: every context-map domain must be a list")
        referenced.extend(values)

    missing = sorted({relative for relative in referenced if not (ROOT / relative).exists()})
    if missing:
        raise SystemExit("FAIL: context-map references missing paths: " + ", ".join(missing))


def validate_handoff() -> None:
    text = (ROOT / "AI_HANDOFF.md").read_text(encoding="utf-8")
    missing = [heading for heading in HANDOFF_HEADINGS if heading not in text]
    if missing:
        raise SystemExit("FAIL: AI_HANDOFF.md missing headings: " + ", ".join(missing))


def main() -> int:
    require_paths()
    validate_work_state()
    validate_ledgers()
    validate_context_map()
    validate_handoff()
    print("PASS: AI continuity, requirements, handoff and drift-control structure is coherent")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
