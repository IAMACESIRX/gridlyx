#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import os
import subprocess

ROOT = Path(__file__).resolve().parents[1]
MODS = ROOT / "mods"


def workspaces() -> list[Path]:
    return [p for p in sorted(MODS.glob("*")) if p.is_dir() and (p / "build.gradle").is_file()]


def run(project: Path, tasks: list[str], advanced: bool = False) -> int:
    wrapper = project / ("gradlew.bat" if os.name == "nt" else "gradlew")
    if os.name != "nt":
        wrapper.chmod(wrapper.stat().st_mode | 0o111)
    cmd = [str(wrapper), "--no-daemon"]
    if advanced:
        cmd.append("-Penable_advanced_engines=true")
    cmd.extend(tasks)
    return subprocess.run(cmd, cwd=project).returncode


def main() -> int:
    parser = argparse.ArgumentParser(description="Operate independent mod JAR workspaces side by side.")
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("list")
    for name in ("quality", "build", "datagen", "gametest"):
        p = sub.add_parser(name)
        p.add_argument("mod", nargs="?", help="Omit to run every workspace")
        p.add_argument("--advanced", action="store_true")
    args = parser.parse_args()
    spaces = workspaces()
    if args.command == "list":
        for project in spaces:
            print(project.name)
        return 0
    selected = spaces if not args.mod else [MODS / args.mod]
    task_map = {
        "quality": ["spotlessCheck", "check"],
        "build": ["spotlessCheck", "check", "build"],
        "datagen": ["runData"],
        "gametest": ["runGameTestServer"],
    }
    for project in selected:
        if not (project / "build.gradle").is_file():
            raise SystemExit(f"Unknown workspace: {project}")
        print(f"=== {args.command.upper()} {project.name} ===", flush=True)
        code = run(project, task_map[args.command], args.advanced)
        if code:
            return code
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
