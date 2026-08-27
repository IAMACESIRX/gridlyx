#!/usr/bin/env python3
from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--advanced", action="store_true")
    args = parser.parse_args()
    command = [sys.executable, str(ROOT / "tools/build_all.py"), "--gametest"]
    if args.advanced:
        command.append("--advanced")
    print("HEADLESS:", " ".join(command))
    return subprocess.run(command, cwd=ROOT, check=False).returncode


if __name__ == "__main__":
    raise SystemExit(main())
