#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
import shlex
import subprocess
import urllib.request
from pathlib import Path

SYSTEM_RULES = """You are writing Gridelyx release notes from supplied deterministic changelog evidence.
Do not invent features, supported targets, benchmark results, compatibility claims, issue numbers or readiness.
Preserve explicit limitations and distinguish planned/framework work from validated behavior.
Return concise Markdown with: Summary, User-visible changes, Developer changes, Validation/evidence, Known limitations/migration notes.
"""


def ai_via_command(command: str, evidence: str) -> str:
    payload = json.dumps({"system": SYSTEM_RULES, "evidence": evidence}, ensure_ascii=False)
    result = subprocess.run(shlex.split(command), input=payload, text=True, capture_output=True, check=True)
    return result.stdout.strip()


def ai_via_endpoint(endpoint: str, token: str | None, evidence: str) -> str:
    body = json.dumps({"system": SYSTEM_RULES, "evidence": evidence}).encode("utf-8")
    headers = {"Content-Type": "application/json", "Accept": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(endpoint, data=body, headers=headers, method="POST")
    with urllib.request.urlopen(request, timeout=60) as response:
        result = json.loads(response.read().decode("utf-8"))
    text = result.get("text") if isinstance(result, dict) else None
    if not isinstance(text, str) or not text.strip():
        raise SystemExit("FAIL: AI release-note endpoint must return JSON with non-empty 'text'")
    return text.strip()


def deterministic_notes(evidence: str) -> str:
    return (
        "# Gridelyx release notes candidate\n\n"
        "## Summary\n\n"
        "This candidate is generated directly from repository history. It has not been AI-rewritten.\n\n"
        "## Changes\n\n"
        + evidence.strip()
        + "\n\n## Validation / evidence\n\n"
        "Review the linked commit range, CI results, `docs/FEATURE_MAP.md` and target-specific tests before publishing support claims.\n\n"
        "## Known limitations / migration notes\n\n"
        "Carry forward applicable limitations from `docs/FEATURE_MAP.md`, `docs/TODO.md`, open migration issues and the release's Feature Decision Packets.\n"
    )


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate Gridelyx release notes from deterministic changelog evidence")
    parser.add_argument("evidence", type=Path, help="Generated changelog/evidence Markdown")
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--ai", action="store_true", help="Use configured AI command or HTTP endpoint")
    args = parser.parse_args()

    evidence = args.evidence.read_text(encoding="utf-8")
    text = deterministic_notes(evidence)

    if args.ai:
        command = os.getenv("GRIDELYX_AI_RELEASE_NOTES_COMMAND")
        endpoint = os.getenv("GRIDELYX_AI_RELEASE_NOTES_ENDPOINT")
        token = os.getenv("GRIDELYX_AI_RELEASE_NOTES_TOKEN")
        if command:
            text = ai_via_command(command, evidence)
        elif endpoint:
            text = ai_via_endpoint(endpoint, token, evidence)
        else:
            raise SystemExit("FAIL: --ai requires GRIDELYX_AI_RELEASE_NOTES_COMMAND or GRIDELYX_AI_RELEASE_NOTES_ENDPOINT")

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(text.rstrip() + "\n", encoding="utf-8")
    print(f"WROTE: {args.output} ({'AI-assisted' if args.ai else 'deterministic'})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
