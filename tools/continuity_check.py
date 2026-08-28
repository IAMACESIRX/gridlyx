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
    "docs/FEATURE_DECISION_FRAMEWORK.md",
    "docs/PROJECT_VALUES.md",
    "docs/DEVELOPMENT_MAP.md",
    "docs/BENCHMARKING_MATRIX.md",
    "docs/DEPENDENCIES_AND_TOOLCHAIN.md",
    "platform/brand.json",
    "platform/repository-metadata.json",
    "platform/chat-requirements.json",
    "platform/feature-analysis.schema.json",
    "platform/toolchain-requirements.json",
    "ai/AI_ORGANISATION.md",
    "ai/DRIFT_MITIGATION.md",
    "ai/work-state.json",
    "ai/decision-ledger.json",
    "ai/assumption-ledger.json",
    "ai/context-map.json",
    "tools/feature_planning_check.py",
]

HANDOFF_HEADINGS = [
    "## Source-of-truth order",
    "## Non-negotiable invariants",
    "## Feature-planning protocol",
    "## Session start protocol",
    "## Session end protocol",
]


def load_json(relative: str, schema_version: int = 1) -> dict:
    path = ROOT / relative
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise SystemExit(f"FAIL: {relative}: {exc}") from exc
    if not isinstance(value, dict):
        raise SystemExit(f"FAIL: {relative}: top-level value must be an object")
    if value.get("schema_version") != schema_version:
        raise SystemExit(f"FAIL: {relative}: schema_version must be {schema_version}")
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


def validate_brand() -> None:
    data = load_json("platform/brand.json", schema_version=2)
    if data.get("root_brand") != "Gridelyx" or data.get("product_name") != "Gridelyx Studio":
        raise SystemExit("FAIL: platform/brand.json must identify Gridelyx / Gridelyx Studio")

    metadata = load_json("platform/repository-metadata.json")
    if metadata.get("product_brand") != "Gridelyx":
        raise SystemExit("FAIL: repository metadata must identify Gridelyx as product brand")
    if metadata.get("requested_repository_slug") != "gridlyx":
        raise SystemExit("FAIL: requested GitHub repository slug must be gridlyx")


def validate_requirements() -> None:
    data = load_json("platform/chat-requirements.json")
    requirements = data.get("requirements")
    if not isinstance(requirements, list):
        raise SystemExit("FAIL: chat requirements must contain a list")
    ids = {item.get("id") for item in requirements if isinstance(item, dict)}
    for number in range(1, 35):
        expected = f"CR-{number:03d}"
        if expected not in ids:
            raise SystemExit(f"FAIL: retained scope missing {expected}")


def validate_feature_analysis() -> None:
    schema = load_json("platform/feature-analysis.schema.json")
    required = set(schema.get("required", []))
    for field in ("w5x5x5", "cost", "time_horizons", "risks", "critical_path", "cynefin", "rollback"):
        if field not in required:
            raise SystemExit(f"FAIL: feature-analysis schema must require {field}")


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
    validate_brand()
    validate_requirements()
    validate_feature_analysis()
    validate_work_state()
    validate_ledgers()
    validate_context_map()
    validate_handoff()
    print("PASS: Gridelyx AI continuity, brand, retained scope, feature planning and drift-control structure is coherent")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
