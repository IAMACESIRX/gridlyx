#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

REQUIRED_FILES = [
    "docs/FEATURE_DECISION_FRAMEWORK.md",
    "docs/PROJECT_VALUES.md",
    "docs/DEVELOPMENT_MAP.md",
    "docs/BENCHMARKING_MATRIX.md",
    "docs/templates/FEATURE_EVALUATION_TEMPLATE.md",
    "platform/feature-analysis.schema.json",
    "platform/repository-metadata.json",
    ".github/ISSUE_TEMPLATE/feature-evaluation.yml",
]

FRAMEWORK_MARKERS = [
    "W5x5x5",
    "Who not",
    "What isn't",
    "When isn't",
    "Where isn't",
    "How not",
    "Why isn't",
    "10 minutes",
    "10 hours",
    "10 days",
    "10 months",
    "1 year",
    "5 years",
    "10 years",
    "Opportunity cost",
    "Regret minimisation",
    "Reversible vs irreversible",
    "Inversion",
    "Second-order",
    "Eisenhower",
    "First-principles",
    "Benchmarking",
    "Feynman",
    "Minimum Viable Product",
    "Timeboxing",
    "Pre-mortem",
    "Asymmetric risk",
    "Working backward",
    "Pareto",
    "Critical Path",
    "Cynefin",
    "Kanban",
]


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def main() -> int:
    missing = [path for path in REQUIRED_FILES if not (ROOT / path).exists()]
    if missing:
        fail("missing feature-planning files: " + ", ".join(missing))

    framework = (ROOT / "docs/FEATURE_DECISION_FRAMEWORK.md").read_text(encoding="utf-8")
    lower = framework.lower()
    absent = [marker for marker in FRAMEWORK_MARKERS if marker.lower() not in lower]
    if absent:
        fail("feature framework missing required concepts: " + ", ".join(absent))

    try:
        schema = json.loads((ROOT / "platform/feature-analysis.schema.json").read_text(encoding="utf-8"))
        metadata = json.loads((ROOT / "platform/repository-metadata.json").read_text(encoding="utf-8"))
        requirements = json.loads((ROOT / "platform/chat-requirements.json").read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(str(exc))

    required_schema = set(schema.get("required", []))
    for field in ("w5x5x5", "time_horizons", "risks", "critical_path", "cynefin", "rollback"):
        if field not in required_schema:
            fail(f"feature-analysis schema does not require {field}")

    if metadata.get("product_brand") != "Gridelyx":
        fail("repository metadata must retain Gridelyx as product brand")
    if metadata.get("requested_repository_slug") != "gridlyx":
        fail("requested GitHub repository slug must be gridlyx")

    cr_ids = {item.get("id") for item in requirements.get("requirements", []) if isinstance(item, dict)}
    if "CR-034" not in cr_ids:
        fail("CR-034 advanced feature decision/planning requirement is missing")

    print("PASS: Gridelyx feature-analysis, development-map and repository-metadata controls are present")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
