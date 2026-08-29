#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]

REQUIRED = [
    "README.md",
    "mkdocs.yml",
    "requirements-docs.txt",
    "CHANGELOG.md",
    "docs/index.md",
    "docs/LINKING_POLICY.md",
    "docs/assets/gridelyx-hero.svg",
    "docs/STAKEHOLDER_DASHBOARD.md",
    "docs/ARCHITECTURE_DIAGRAMS.md",
    "docs/USER_JOURNEYS.md",
    "docs/IMPACT_EFFORT_MATRIX.md",
    "docs/LABELS_AND_FILTERING.md",
    "docs/DOCUMENTATION_DRIVEN_MARKETING.md",
    "docs/DOCUMENTATION_TOOLCHAIN.md",
    "docs/RELEASE_NOTES_AND_CHANGELOGS.md",
    "docs/api/index.md",
    "docs/api/gridelyx-development-api.openapi.yaml",
    "docs/diagrams/platform-architecture.mmd",
    "docs/diagrams/user-journey.mmd",
    "docs/diagrams/impact-effort.mmd",
    "platform/label-taxonomy.json",
    "platform/portfolio-board.json",
    "tools/generate_changelog.py",
    "tools/generate_release_notes.py",
    "tools/sync_labels.py",
    "tools/markdown_linkify.py",
]

EXPECTED_JSON_SCHEMAS = {
    "platform/label-taxonomy.json": 2,
    "platform/portfolio-board.json": 1,
}


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def run_link_check() -> None:
    result = subprocess.run(
        [sys.executable, str(ROOT / "tools/markdown_linkify.py"), "--check"],
        cwd=ROOT,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        details = "\n".join(part for part in (result.stdout.strip(), result.stderr.strip()) if part)
        fail("documentation hyperlink policy failed" + (f":\n{details}" if details else ""))


def main() -> int:
    missing = [path for path in REQUIRED if not (ROOT / path).exists()]
    if missing:
        fail("missing documentation/presentation paths: " + ", ".join(missing))

    run_link_check()

    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    docs_home = (ROOT / "docs/index.md").read_text(encoding="utf-8")
    dashboard = (ROOT / "docs/STAKEHOLDER_DASHBOARD.md").read_text(encoding="utf-8")
    architecture = (ROOT / "docs/ARCHITECTURE_DIAGRAMS.md").read_text(encoding="utf-8")
    marketing = (ROOT / "docs/DOCUMENTATION_DRIVEN_MARKETING.md").read_text(encoding="utf-8")

    for marker in ("img.shields.io", "Gridelyx", "Value proposition"):
        if marker.lower() not in (readme + docs_home).lower():
            fail(f"README/docs landing missing presentation marker: {marker}")
    for marker in ("3-bullet value proposition", "Portfolio Kanban", "critical path"):
        if marker.lower() not in dashboard.lower():
            fail(f"stakeholder dashboard missing marker: {marker}")
    for marker in ("```mermaid", "platform-architecture.mmd", "user-journey.mmd", "impact-effort.mmd"):
        if marker not in architecture and marker not in (ROOT / "docs/USER_JOURNEYS.md").read_text(encoding="utf-8") and marker not in (ROOT / "docs/IMPACT_EFFORT_MATRIX.md").read_text(encoding="utf-8"):
            fail(f"diagrams-as-code system missing marker: {marker}")
    for marker in ("Claim-to-proof rule", "Documentation funnel", "Anti-hype constraints"):
        if marker.lower() not in marketing.lower():
            fail(f"documentation-driven marketing missing marker: {marker}")

    for json_path, expected_schema in EXPECTED_JSON_SCHEMAS.items():
        try:
            data = json.loads((ROOT / json_path).read_text(encoding="utf-8"))
        except json.JSONDecodeError as exc:
            fail(f"invalid JSON in {json_path}: {exc}")
        if data.get("schema_version") != expected_schema:
            fail(f"{json_path} must use schema_version {expected_schema}")

    hero = (ROOT / "docs/assets/gridelyx-hero.svg").read_text(encoding="utf-8")
    if "<svg" not in hero or "GRIDELYX STUDIO" not in hero:
        fail("hero SVG does not contain canonical Gridelyx identity")

    openapi = (ROOT / "docs/api/gridelyx-development-api.openapi.yaml").read_text(encoding="utf-8")
    if "openapi: 3.1.0" not in openapi or "/v1/capabilities:" not in openapi:
        fail("interactive API contract is missing OpenAPI 3.1/capability discovery")

    print("PASS: Gridelyx stakeholder, diagram, documentation-site, API, hyperlink and release-communication surfaces are coherent")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
