#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import shutil
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / ".reference-cache" / "raw"
UPSTREAM = ROOT / "references" / "upstream" / "mdk-26.2"
TEMPLATE = ROOT / "templates" / "neoforge-26.2"
sys.path.insert(0, str(ROOT / "tools"))
import vault as vault_tool


def exact_artifact(artifact_id: str) -> tuple[dict, Path]:
    manifest = vault_tool.load()
    artifact = next(a for a in manifest["artifacts"] if a["id"] == artifact_id)
    CACHE.mkdir(parents=True, exist_ok=True)
    dest = CACHE / artifact["original_filename"]
    if not (dest.exists() and dest.stat().st_size == artifact["size_bytes"] and vault_tool.digest(dest) == artifact["sha256"]):
        dest = vault_tool.reconstruct(artifact, CACHE)
    return artifact, dest


def safe_extract_mdk(archive: Path, destination: Path) -> None:
    staging = ROOT / ".reference-cache" / "mdk-extract"
    shutil.rmtree(staging, ignore_errors=True)
    staging.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(archive) as z:
        root = None
        for info in z.infolist():
            p = Path(info.filename)
            if p.is_absolute() or ".." in p.parts:
                raise RuntimeError(f"Unsafe path in MDK archive: {info.filename}")
            if p.parts:
                root = root or p.parts[0]
                if p.parts[0] != root:
                    raise RuntimeError("MDK archive has multiple top-level roots")
        z.extractall(staging)
    extracted = staging / root
    if not extracted.is_dir():
        raise RuntimeError("Expected MDK top-level directory was not produced")
    shutil.rmtree(destination, ignore_errors=True)
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(extracted, destination)


def main() -> int:
    ap = argparse.ArgumentParser(description="Hydrate readable upstream references from the exact checksummed binary vault.")
    ap.add_argument("--skip-indexes", action="store_true")
    ns = ap.parse_args()
    _, mdk = exact_artifact("mdk")
    safe_extract_mdk(mdk, UPSTREAM)
    print(f"HYDRATED {UPSTREAM.relative_to(ROOT)}")
    wrapper = UPSTREAM / "gradle" / "wrapper" / "gradle-wrapper.jar"
    if not wrapper.is_file():
        raise RuntimeError("Upstream MDK does not contain gradle-wrapper.jar")
    target = TEMPLATE / "gradle" / "wrapper" / "gradle-wrapper.jar"
    target.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(wrapper, target)
    print(f"RESTORED {target.relative_to(ROOT)}")
    shutil.copy2(UPSTREAM / "gradlew", TEMPLATE / "gradlew")
    shutil.copy2(UPSTREAM / "gradlew.bat", TEMPLATE / "gradlew.bat")
    try:
        (TEMPLATE / "gradlew").chmod((TEMPLATE / "gradlew").stat().st_mode | 0o111)
    except OSError:
        pass
    print("RESTORED template Gradle wrapper launchers")
    if not ns.skip_indexes:
        import subprocess
        subprocess.run([sys.executable, str(ROOT / "tools" / "build_reference_indexes.py")], check=True)
    print("PASS: readable reference layer hydrated from exact supplied bytes")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
