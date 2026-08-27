#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAX_MODEL_OUTPUT_BYTES = 4 * 1024 * 1024


def build_request() -> dict:
    capabilities = json.loads((ROOT / "platform/capabilities.json").read_text(encoding="utf-8"))
    versions = json.loads((ROOT / "platform/versions.json").read_text(encoding="utf-8"))
    project_plan = (ROOT / "docs/PROJECT_PLAN.md").read_text(encoding="utf-8")
    decisions = (ROOT / "docs/DECISIONS.md").read_text(encoding="utf-8")
    return {
        "schema_version": 1,
        "task": (
            "Generate concise technical documentation for this Minecraft mod engineering platform. "
            "Distinguish implemented, framework, validated, and pending capabilities. "
            "Do not invent runtime validation, benchmarks, APIs, or compatibility claims."
        ),
        "versions": versions,
        "capabilities": capabilities,
        "project_plan": project_plan,
        "architecture_decisions": decisions,
    }


def invoke(command: list[str], request: dict) -> str:
    result = subprocess.run(
        command,
        input=json.dumps(request),
        capture_output=True,
        text=True,
        timeout=180,
        check=False,
        cwd=ROOT,
    )
    if result.returncode:
        raise RuntimeError(
            f"Documentation model adapter exited with {result.returncode}: {result.stderr[-2000:]}"
        )
    output = result.stdout.strip()
    if not output:
        raise RuntimeError("Documentation model adapter produced no output")
    if len(output.encode("utf-8")) > MAX_MODEL_OUTPUT_BYTES:
        raise RuntimeError("Documentation model output exceeds safety limit")
    return output + "\n"


def self_test() -> int:
    request = build_request()
    if request["schema_version"] != 1 or "capabilities" not in request:
        print("ERROR: AI auto-documentation context self-test failed")
        return 2
    if "Do not invent" not in request["task"]:
        print("ERROR: AI auto-documentation anti-fabrication rule is missing")
        return 2
    print("PASS: AI auto-documentation context self-test")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--context", help="Write the model-neutral documentation request JSON")
    parser.add_argument("--output", help="Write generated Markdown here")
    parser.add_argument("--self-test", action="store_true")
    parser.add_argument(
        "--provider-command",
        nargs=argparse.REMAINDER,
        help="Local or sidecar model-adapter command. Receives request JSON on stdin and returns Markdown.",
    )
    args = parser.parse_args()

    if args.self_test:
        return self_test()
    request = build_request()
    if args.context:
        Path(args.context).write_text(json.dumps(request, indent=2) + "\n", encoding="utf-8")
    if args.provider_command:
        if not args.output:
            parser.error("--output is required with --provider-command")
        Path(args.output).write_text(invoke(args.provider_command, request), encoding="utf-8")
        print(f"WROTE: {args.output}")
    elif not args.context:
        parser.error("specify --context, --provider-command, or --self-test")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
