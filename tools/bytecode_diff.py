#!/usr/bin/env python3
from __future__ import annotations

import argparse
import difflib
import shutil
import subprocess
import tempfile
import zipfile
from pathlib import Path


def disassemble_class(path: Path, classpath: Path) -> str:
    class_name = str(path.relative_to(classpath).with_suffix("")).replace("/", ".").replace("\\", ".")
    result = subprocess.run(
        ["javap", "-classpath", str(classpath), "-c", "-p", "-s", class_name],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode:
        return f"ERROR disassembling {class_name}\n{result.stderr}"
    return result.stdout


def tree_dump(root: Path) -> dict[str, str]:
    return {
        str(path.relative_to(root)).replace("\\", "/"): disassemble_class(path, root)
        for path in sorted(root.rglob("*.class"))
    }


def target_dump(path: Path) -> dict[str, str]:
    if path.is_dir():
        return tree_dump(path)
    if path.suffix.lower() == ".jar":
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            with zipfile.ZipFile(path) as archive:
                for member in archive.namelist():
                    if member.endswith(".class"):
                        archive.extract(member, root)
            return tree_dump(root)
    raise ValueError(f"Unsupported bytecode target: {path}")


def diff_maps(left: dict[str, str], right: dict[str, str]) -> str:
    output: list[str] = []
    for name in sorted(set(left) | set(right)):
        before = left.get(name, "")
        after = right.get(name, "")
        if before == after:
            continue
        output.extend(
            difflib.unified_diff(
                before.splitlines(),
                after.splitlines(),
                fromfile=f"a/{name}",
                tofile=f"b/{name}",
                lineterm="",
            )
        )
    return "\n".join(output) + ("\n" if output else "")


def self_test() -> int:
    diff = diff_maps({"A.class": "one\n"}, {"A.class": "two\n"})
    if "-one" not in diff or "+two" not in diff:
        print("ERROR: internal bytecode diff self-test failed")
        return 2
    print("PASS: bytecode diff engine self-test")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("left", nargs="?")
    parser.add_argument("right", nargs="?")
    parser.add_argument("--output")
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    if args.self_test:
        return self_test()
    if not args.left or not args.right:
        parser.error("left and right bytecode targets are required")
    if not shutil.which("javap"):
        raise SystemExit("javap is required")
    text = diff_maps(target_dump(Path(args.left)), target_dump(Path(args.right)))
    if args.output:
        Path(args.output).write_text(text, encoding="utf-8")
    else:
        print(text, end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
