#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import io
import re
import zipfile
import sys

ROOT = Path(__file__).resolve().parents[1]
INDEX = ROOT / "references" / "index"
CACHE = ROOT / ".reference-cache" / "raw"
sys.path.insert(0, str(ROOT / "tools"))
import vault as vault_tool

TYPE_RE = re.compile(r"\b(?:class|interface|enum|record|@interface)\s+([A-Za-z_$][\w$]*)")
PACKAGE_RE = re.compile(r"\bpackage\s+([A-Za-z_$][\w$.]*)\s*;")


def ensure_artifact(artifact_id: str) -> tuple[dict, Path]:
    manifest = vault_tool.load()
    artifact = next(a for a in manifest["artifacts"] if a["id"] == artifact_id)
    CACHE.mkdir(parents=True, exist_ok=True)
    path = CACHE / artifact["original_filename"]
    if not (path.exists() and path.stat().st_size == artifact["size_bytes"] and vault_tool.digest(path) == artifact["sha256"]):
        path = vault_tool.reconstruct(artifact, CACHE)
    return artifact, path


def archive_index(artifact_id: str, archive: Path) -> None:
    outdir = INDEX / "archive-contents"
    outdir.mkdir(parents=True, exist_ok=True)
    out = outdir / f"{artifact_id}.tsv"
    with zipfile.ZipFile(archive) as z, out.open("w", encoding="utf-8", newline="\n") as f:
        f.write("path\tuncompressed_bytes\tcompressed_bytes\tcrc32\n")
        for info in z.infolist():
            f.write(f"{info.filename}\t{info.file_size}\t{info.compress_size}\t{info.CRC:08x}\n")
    print(out.relative_to(ROOT))


def jdk_source_index(archive: Path) -> None:
    out = INDEX / "jdk-25.0.4-source-index.tsv"
    with zipfile.ZipFile(archive) as outer:
        src_name = next((n for n in outer.namelist() if n.endswith("/lib/src.zip")), None)
        if src_name is None:
            raise RuntimeError("JDK lib/src.zip not found")
        with zipfile.ZipFile(io.BytesIO(outer.read(src_name))) as src, out.open("w", encoding="utf-8", newline="\n") as f:
            f.write("path\tuncompressed_bytes\tcompressed_bytes\n")
            for info in src.infolist():
                f.write(f"{info.filename}\t{info.file_size}\t{info.compress_size}\n")
    print(out.relative_to(ROOT))


def lwjgl_indexes(archive: Path) -> None:
    source_out = INDEX / "lwjgl-3.4.1-source-index.tsv"
    type_out = INDEX / "lwjgl-3.4.1-type-index.tsv"
    with zipfile.ZipFile(archive) as outer, source_out.open("w", encoding="utf-8", newline="\n") as sf, type_out.open("w", encoding="utf-8", newline="\n") as tf:
        sf.write("source_jar\tpath\tuncompressed_bytes\n")
        tf.write("module\tpackage\ttype\tpath\n")
        for jar_info in sorted((i for i in outer.infolist() if i.filename.endswith("-sources.jar")), key=lambda i: i.filename):
            module = jar_info.filename.split("/")[0]
            with zipfile.ZipFile(io.BytesIO(outer.read(jar_info))) as src:
                for info in src.infolist():
                    sf.write(f"{jar_info.filename}\t{info.filename}\t{info.file_size}\n")
                    if not info.filename.endswith(".java"):
                        continue
                    text = src.read(info).decode("utf-8", errors="replace")
                    package = ""
                    pm = PACKAGE_RE.search(text)
                    if pm:
                        package = pm.group(1)
                    for tm in TYPE_RE.finditer(text):
                        tf.write(f"{module}\t{package}\t{tm.group(1)}\t{info.filename}\n")
    print(source_out.relative_to(ROOT))
    print(type_out.relative_to(ROOT))


def main() -> int:
    parser = argparse.ArgumentParser(description="Regenerate searchable indexes from the exact supplied reference vault.")
    parser.add_argument("--skip-source-indexes", action="store_true")
    args = parser.parse_args()
    INDEX.mkdir(parents=True, exist_ok=True)
    paths = {}
    for artifact_id in ("mdk", "neoforge_installer", "jdk", "lwjgl"):
        _, paths[artifact_id] = ensure_artifact(artifact_id)
        archive_index(artifact_id, paths[artifact_id])
    if not args.skip_source_indexes:
        jdk_source_index(paths["jdk"])
        lwjgl_indexes(paths["lwjgl"])
    print("PASS: reference indexes generated from checksummed artifacts")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
