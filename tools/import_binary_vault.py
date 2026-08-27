#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import hashlib
import json
import shutil

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "vault" / "manifest.json"
PENDING = ROOT / "vault" / "REMOTE_BINARY_IMPORT_PENDING.md"


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for block in iter(lambda: f.read(1024 * 1024), b""):
            h.update(block)
    return h.hexdigest()


def load_manifest() -> dict:
    return json.loads(MANIFEST.read_text(encoding="utf-8"))


def find_source(source_dir: Path, artifact: dict) -> Path:
    exact = source_dir / artifact["original_filename"]
    if exact.is_file():
        return exact
    candidates = [p for p in source_dir.iterdir() if p.is_file() and p.stat().st_size == artifact["size_bytes"]]
    matches = [p for p in candidates if sha256(p) == artifact["sha256"]]
    if len(matches) == 1:
        return matches[0]
    if not matches:
        raise FileNotFoundError(
            f"Could not find exact bytes for {artifact['id']} ({artifact['original_filename']}) in {source_dir}"
        )
    raise RuntimeError(f"Multiple byte-identical candidates found for {artifact['id']}: {matches}")


def verify_source(path: Path, artifact: dict) -> None:
    size = path.stat().st_size
    digest = sha256(path)
    if size != artifact["size_bytes"] or digest != artifact["sha256"]:
        raise RuntimeError(
            f"Source identity mismatch for {artifact['id']}: size={size}, sha256={digest}; "
            f"expected size={artifact['size_bytes']}, sha256={artifact['sha256']}"
        )


def import_whole(src: Path, artifact: dict) -> None:
    dest = ROOT / artifact["path"]
    dest.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(src, dest)
    if dest.stat().st_size != artifact["size_bytes"] or sha256(dest) != artifact["sha256"]:
        dest.unlink(missing_ok=True)
        raise RuntimeError(f"Post-copy verification failed for {artifact['id']}")
    print(f"OK whole {artifact['id']}: {dest.relative_to(ROOT)}")


def import_chunks(src: Path, artifact: dict) -> None:
    with src.open("rb") as f:
        for part in artifact["parts"]:
            dest = ROOT / part["path"]
            dest.parent.mkdir(parents=True, exist_ok=True)
            remaining = part["size_bytes"]
            h = hashlib.sha256()
            with dest.open("wb") as out:
                while remaining:
                    block = f.read(min(1024 * 1024, remaining))
                    if not block:
                        raise RuntimeError(f"Unexpected EOF while writing {part['path']}")
                    out.write(block)
                    h.update(block)
                    remaining -= len(block)
            if dest.stat().st_size != part["size_bytes"] or h.hexdigest() != part["sha256"]:
                dest.unlink(missing_ok=True)
                raise RuntimeError(f"Chunk verification failed: {part['path']}")
            print(f"OK chunk {part['path']}")
        if f.read(1):
            raise RuntimeError(f"Manifest does not consume the complete source for {artifact['id']}")


def verify_vault(manifest: dict) -> None:
    for artifact in manifest["artifacts"]:
        if artifact["storage"] == "whole":
            path = ROOT / artifact["path"]
            if not path.is_file() or path.stat().st_size != artifact["size_bytes"] or sha256(path) != artifact["sha256"]:
                raise RuntimeError(f"Vault verification failed: {artifact['id']}")
        else:
            total = 0
            concat = hashlib.sha256()
            for part in artifact["parts"]:
                path = ROOT / part["path"]
                if not path.is_file() or path.stat().st_size != part["size_bytes"] or sha256(path) != part["sha256"]:
                    raise RuntimeError(f"Vault part verification failed: {part['path']}")
                total += path.stat().st_size
                with path.open("rb") as f:
                    for block in iter(lambda: f.read(1024 * 1024), b""):
                        concat.update(block)
            if total != artifact["size_bytes"] or concat.hexdigest() != artifact["sha256"]:
                raise RuntimeError(f"Reconstructed identity failed: {artifact['id']}")
    print("PASS: exact binary vault matches manifest")


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Import the exact supplied Minecraft R&D reference archives into Git-safe vault storage."
    )
    parser.add_argument("source_dir", type=Path, help="Directory containing the four original supplied files")
    parser.add_argument("--keep-pending-marker", action="store_true", help="Do not remove the remote-import marker")
    args = parser.parse_args()
    source_dir = args.source_dir.expanduser().resolve()
    if not source_dir.is_dir():
        raise SystemExit(f"Not a directory: {source_dir}")
    manifest = load_manifest()
    for artifact in manifest["artifacts"]:
        src = find_source(source_dir, artifact)
        verify_source(src, artifact)
        print(f"IMPORT {artifact['id']} <- {src.name}")
        if artifact["storage"] == "whole":
            import_whole(src, artifact)
        elif artifact["storage"] == "chunks":
            import_chunks(src, artifact)
        else:
            raise RuntimeError(f"Unknown storage mode: {artifact['storage']}")
    verify_vault(manifest)
    if not args.keep_pending_marker and PENDING.exists():
        PENDING.unlink()
        print(f"REMOVED {PENDING.relative_to(ROOT)}")
    print("\nBinary vault is ready. Recommended next commands:")
    print("  python tools/vault.py verify --all")
    print("  python tools/build_reference_indexes.py")
    print("  git add vault references/index")
    print('  git commit -m "Import exact supplied Minecraft reference vault"')
    print("  git push origin main")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
