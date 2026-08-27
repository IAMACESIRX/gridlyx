#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "platform/toolchain-requirements.json"
DOC = ROOT / "docs/DEPENDENCIES_AND_TOOLCHAIN.md"


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def main() -> int:
    try:
        data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"platform/toolchain-requirements.json: {exc}")

    if not isinstance(data, dict) or data.get("schema_version") != 1:
        fail("toolchain manifest must be a schema_version 1 object")

    tools = data.get("tools")
    libraries = data.get("libraries")
    if not isinstance(tools, list) or not tools:
        fail("toolchain manifest needs a non-empty tools list")
    if not isinstance(libraries, list) or not libraries:
        fail("toolchain manifest needs a non-empty libraries list")

    tool_ids: list[str] = []
    missing_paths: list[str] = []
    for tool in tools:
        if not isinstance(tool, dict):
            fail("every tool entry must be an object")
        tool_id = tool.get("id")
        if not isinstance(tool_id, str) or not tool_id:
            fail("every tool entry needs a non-empty id")
        tool_ids.append(tool_id)
        for field in ("name", "state", "required_for", "acquisition", "evidence"):
            if field not in tool:
                fail(f"{tool_id}: missing {field}")
        if not isinstance(tool["required_for"], list) or not tool["required_for"]:
            fail(f"{tool_id}: required_for must be a non-empty list")
        evidence = tool["evidence"]
        if not isinstance(evidence, list) or not evidence:
            fail(f"{tool_id}: evidence must be a non-empty list")
        for relative in evidence:
            if not isinstance(relative, str) or not relative:
                fail(f"{tool_id}: invalid evidence path")
            if not (ROOT / relative).exists():
                missing_paths.append(f"{tool_id}:{relative}")

    if len(tool_ids) != len(set(tool_ids)):
        fail("duplicate tool ids")

    library_ids: list[str] = []
    for library in libraries:
        if not isinstance(library, dict):
            fail("every library entry must be an object")
        library_id = library.get("id")
        if not isinstance(library_id, str) or not library_id:
            fail("every library entry needs a non-empty id")
        library_ids.append(library_id)
        for field in ("version", "source", "evidence"):
            if not library.get(field):
                fail(f"{library_id}: missing {field}")
        evidence = ROOT / library["evidence"]
        if not evidence.exists():
            missing_paths.append(f"{library_id}:{library['evidence']}")

    if len(library_ids) != len(set(library_ids)):
        fail("duplicate library ids")
    if missing_paths:
        fail("missing toolchain evidence paths: " + ", ".join(sorted(missing_paths)))

    try:
        doc = DOC.read_text(encoding="utf-8")
    except OSError as exc:
        fail(f"docs/DEPENDENCIES_AND_TOOLCHAIN.md: {exc}")

    for marker in ("Gridelyx", "Java", "Rust", "CMake", "Bedrock", "reference vault"):
        if marker.lower() not in doc.lower():
            fail(f"dependency documentation missing marker: {marker}")

    print(f"PASS: {len(tools)} tool/program requirements and {len(libraries)} locked libraries are documented")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
