#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import os
import re
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]

HINTS = [
    (re.compile(r"Unsupported class file major version|invalid source release", re.I), "Check that Java 25 is active."),
    (re.compile(r"Could not resolve|Could not find", re.I), "Check repositories, network access, and the locked dependency versions."),
    (re.compile(r"Mixin.*(failed|error)|InvalidMixin", re.I), "Verify target mappings/descriptors and disable advanced mixins to isolate the failure."),
    (re.compile(r"No OpenGL context|capabilities.*not.*active", re.I), "Run GPU code only on a thread with the active Minecraft render context."),
    (re.compile(r"OutOfMemoryError", re.I), "Inspect heap/native/GPU allocation ownership before raising memory limits."),
]


def projects() -> list[Path]:
    result = [ROOT / "templates" / "neoforge-26.2"]
    result.extend(p for p in sorted((ROOT / "mods").glob("*")) if (p / "build.gradle").is_file())
    return result


def static_checks() -> int:
    errors = 0
    seen: dict[str, Path] = {}
    for project in projects():
        props = project / "gradle.properties"
        manifest = project / "src/main/templates/META-INF/neoforge.mods.toml"
        if not props.is_file() or not manifest.is_file():
            print("ERROR missing project metadata:", project.relative_to(ROOT))
            errors += 1
            continue
        values = {}
        for line in props.read_text(encoding="utf-8").splitlines():
            if "=" in line and not line.lstrip().startswith("#"):
                key, value = line.split("=", 1)
                values[key.strip()] = value.strip()
        mod_id = values.get("mod_id", "")
        if mod_id in seen:
            print("ERROR duplicate mod_id", mod_id, "in", seen[mod_id], "and", project)
            errors += 1
        seen[mod_id] = project
        if not values.get("mod_license"):
            print("ERROR empty mod_license:", project.relative_to(ROOT))
            errors += 1
        if not any((project / name).is_file() for name in ("LICENSE", "LICENSE.txt", "COPYING")):
            print("ERROR missing mod licence notice:", project.relative_to(ROOT))
            errors += 1
    if os.name != "nt":
        wrapper = ROOT / "templates/neoforge-26.2/gradlew"
        if wrapper.exists() and not os.access(wrapper, os.X_OK):
            print("ERROR template gradlew is not executable")
            errors += 1
    print("Static diagnostics:", "PASS" if errors == 0 else f"FAIL ({errors})")
    return errors


def run_gradle(project: Path, task: str, advanced: bool) -> int:
    wrapper = project / ("gradlew.bat" if os.name == "nt" else "gradlew")
    cmd = [str(wrapper), "--no-daemon"]
    if advanced:
        cmd.append("-Penable_advanced_engines=true")
    cmd.append(task)
    proc = subprocess.run(cmd, cwd=project, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    print(proc.stdout)
    if proc.returncode:
        for pattern, hint in HINTS:
            if pattern.search(proc.stdout):
                print("DIAGNOSTIC HINT:", hint)
    return proc.returncode


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--static", action="store_true")
    parser.add_argument("--mod")
    parser.add_argument("--task", default="check")
    parser.add_argument("--advanced", action="store_true")
    args = parser.parse_args()
    errors = static_checks()
    if args.static or not args.mod:
        return 2 if errors else 0
    project = ROOT / "mods" / args.mod
    if not (project / "build.gradle").is_file():
        raise SystemExit(f"Unknown mod workspace: {args.mod}")
    return run_gradle(project, args.task, args.advanced)


if __name__ == "__main__":
    raise SystemExit(main())
