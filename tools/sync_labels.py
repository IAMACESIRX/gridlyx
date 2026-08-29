#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TAXONOMY = ROOT / "platform/label-taxonomy.json"


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def main() -> int:
    # Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/platform/label-taxonomy.json
    parser = argparse.ArgumentParser(description="Synchronize Gridelyx GitHub labels from platform/label-taxonomy.json")
    parser.add_argument("--repo", required=True, help="GitHub repository in owner/name form")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    if shutil.which("gh") is None and not args.dry_run:
        fail("GitHub CLI 'gh' is required unless --dry-run is used")

    data = json.loads(TAXONOMY.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1 or not isinstance(data.get("labels"), list):
        fail("invalid label taxonomy")

    for label in data["labels"]:
        name = label.get("name")
        color = label.get("color")
        description = label.get("description")
        if not all(isinstance(value, str) and value for value in (name, color, description)):
            fail(f"invalid label record: {label!r}")
        command = [
            "gh", "label", "create", name,
            "--repo", args.repo,
            "--color", color,
            "--description", description,
            "--force",
        ]
        if args.dry_run:
            print("DRY-RUN:", " ".join(command))
        else:
            subprocess.run(command, check=True)
            print(f"SYNC: {name}")

    print(f"PASS: {len(data['labels'])} Gridelyx labels {'validated' if args.dry_run else 'synchronized'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
