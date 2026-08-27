#!/usr/bin/env python3
from pathlib import Path
import argparse
import json
import re
import shutil

ROOT = Path(__file__).resolve().parents[1]
TEMPLATE = ROOT / "templates/neoforge-26.2"


def move_package_root(dest: Path, source_root: str, group: str) -> None:
    root = dest / source_root
    old = root / "com/example/examplemod"
    if not old.exists():
        return
    target = root / Path(*group.split("."))
    target.parent.mkdir(parents=True, exist_ok=True)
    shutil.move(str(old), str(target))
    for directory in (root / "com/example", root / "com"):
        try:
            directory.rmdir()
        except OSError:
            pass


def main() -> int:
    parser = argparse.ArgumentParser(description="Create an isolated NeoForge mod workspace.")
    parser.add_argument("mod_id")
    parser.add_argument("mod_name")
    parser.add_argument("group")
    args = parser.parse_args()
    if not re.fullmatch(r"[a-z][a-z0-9_]{1,63}", args.mod_id):
        raise SystemExit("Invalid NeoForge mod_id")
    if not re.fullmatch(r"[A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)+", args.group):
        raise SystemExit("Use a Java package/group such as com.example.mod")
    dest = ROOT / "mods" / args.mod_id
    if dest.exists():
        raise SystemExit(f"Workspace already exists: {dest}")
    shutil.copytree(TEMPLATE, dest)
    shutil.rmtree(dest / ".github", ignore_errors=True)
    replacements = {
        "examplemod": args.mod_id,
        "Example Mod": args.mod_name,
        "com.example.examplemod": args.group,
    }
    for path in dest.rglob("*"):
        if not path.is_file():
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        for before, after in replacements.items():
            text = text.replace(before, after)
        path.write_text(text, encoding="utf-8", newline="\n")
    move_package_root(dest, "src/main/java", args.group)
    move_package_root(dest, "src/advanced/java", args.group)
    metadata = {
        "mod_id": args.mod_id,
        "mod_name": args.mod_name,
        "group": args.group,
        "template": "neoforge-26.2",
        "advanced_engines": False,
    }
    (dest / "workspace.json").write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
    print(dest)
    print(f"Validate: python tools/validate_platform.py --mod {args.mod_id}")
    print(f"Build:    python tools/workspace.py build {args.mod_id}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
