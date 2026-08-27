#!/usr/bin/env python3
from pathlib import Path
import argparse
import os
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def run_project(project: Path, gametest: bool = False, advanced: bool = False) -> int:
    print(f"\n=== BUILD {project.relative_to(ROOT)} ===", flush=True)
    wrapper = project / ("gradlew.bat" if os.name == "nt" else "gradlew")
    if os.name != "nt" and wrapper.exists():
        wrapper.chmod(wrapper.stat().st_mode | 0o111)
    gradle = str(wrapper) if wrapper.exists() else "gradle"
    command = [gradle, "--no-daemon"]
    if advanced:
        command.append("-Penable_advanced_engines=true")
    command.extend(["spotlessCheck", "check", "build"])
    rc = subprocess.run(command, cwd=project).returncode
    if rc:
        return rc
    jars = [
        path
        for path in (project / "build/libs").glob("*.jar")
        if not path.name.endswith(("-sources.jar", "-javadoc.jar", "-agent.jar"))
    ]
    if not jars:
        print("ERROR: no built mod JAR")
        return 3
    for jar in jars:
        with zipfile.ZipFile(jar) as archive:
            if "META-INF/neoforge.mods.toml" not in set(archive.namelist()):
                print("ERROR: missing META-INF/neoforge.mods.toml in", jar)
                return 4
        print("JAR OK", jar.relative_to(ROOT))
    if gametest:
        test_command = [gradle, "--no-daemon"]
        if advanced:
            test_command.append("-Penable_advanced_engines=true")
        test_command.append("runGameTestServer")
        rc = subprocess.run(test_command, cwd=project).returncode
        if rc:
            return rc
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--include-template", action="store_true")
    parser.add_argument("--gametest", action="store_true")
    parser.add_argument("--advanced", action="store_true")
    args = parser.parse_args()
    projects = []
    if args.include_template:
        projects.append(ROOT / "templates/neoforge-26.2")
    projects += [p for p in sorted((ROOT / "mods").glob("*")) if p.is_dir() and (p / "build.gradle").exists()]
    if not projects:
        print("No projects to build")
        return 0
    for project in projects:
        code = run_project(project, args.gametest and (project / ".enable-gametest").exists(), args.advanced)
        if code:
            return code
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
