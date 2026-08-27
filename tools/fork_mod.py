#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import shutil
import subprocess
import zipfile
from pathlib import Path, PurePosixPath

ROOT = Path(__file__).resolve().parents[1]


def safe_extract(archive: zipfile.ZipFile, destination: Path) -> None:
    destination.mkdir(parents=True, exist_ok=True)
    for member in archive.infolist():
        path = PurePosixPath(member.filename)
        if path.is_absolute() or ".." in path.parts:
            raise ValueError(f"Unsafe archive member: {member.filename}")
        if member.is_dir():
            continue
        output = destination.joinpath(*path.parts)
        output.parent.mkdir(parents=True, exist_ok=True)
        with archive.open(member) as source, output.open("wb") as target:
            shutil.copyfileobj(source, target)


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def disassemble(classes_root: Path, output_root: Path) -> int:
    if not shutil.which("javap"):
        return 0
    written = 0
    for class_file in sorted(classes_root.rglob("*.class")):
        relative = class_file.relative_to(classes_root)
        class_name = str(relative.with_suffix("")).replace("/", ".").replace("\\", ".")
        result = subprocess.run(
            ["javap", "-classpath", str(classes_root), "-c", "-p", "-s", class_name],
            capture_output=True,
            text=True,
            check=False,
        )
        destination = output_root / relative.with_suffix(".javap.txt")
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_text(result.stdout + result.stderr, encoding="utf-8")
        written += 1
    return written


def run_decompiler(decompiler: Path, jar: Path, output_root: Path) -> None:
    if not decompiler.is_file():
        raise ValueError("Decompiler JAR does not exist")
    output_root.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["java", "-jar", str(decompiler), str(jar), "--outputdir", str(output_root)],
        check=False,
    )
    if result.returncode:
        raise RuntimeError(f"External decompiler exited with {result.returncode}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("jar")
    parser.add_argument("--output", required=True)
    parser.add_argument("--decompiler", help="Optional local decompiler JAR; never downloaded automatically")
    parser.add_argument("--source-url")
    parser.add_argument("--license-note")
    args = parser.parse_args()

    jar = Path(args.jar).resolve()
    output = Path(args.output).resolve()
    if not jar.is_file() or jar.suffix.lower() != ".jar":
        raise SystemExit("Input must be an existing JAR")
    if output.exists() and any(output.iterdir()):
        raise SystemExit("Output directory must be empty")

    extracted = output / "extracted"
    with zipfile.ZipFile(jar) as archive:
        safe_extract(archive, extracted)
    disassembled = disassemble(extracted, output / "disassembly")
    if args.decompiler:
        run_decompiler(Path(args.decompiler).resolve(), jar, output / "decompiled")

    manifest = {
        "schema_version": 1,
        "input": jar.name,
        "sha256": sha256(jar),
        "source_url": args.source_url,
        "license_note": args.license_note,
        "disassembled_classes": disassembled,
        "decompiler": Path(args.decompiler).name if args.decompiler else None,
        "policy": "Import/decompile only software you are authorised to inspect, modify, or fork.",
    }
    output.mkdir(parents=True, exist_ok=True)
    (output / "fork-manifest.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
    print(f"WROTE: {output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
