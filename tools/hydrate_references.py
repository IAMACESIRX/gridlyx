#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "vault" / "manifest.json"
CACHE_ROOT = ROOT / ".reference-cache"


def load_manifest() -> dict:
    data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    if data.get("schema_version") != 2:
        raise RuntimeError("vault/manifest.json must use acquisition schema_version 2")
    policy = data.get("policy", {})
    if policy.get("mode") != "acquire-at-build-or-run-time":
        raise RuntimeError("upstream acquisition policy is not enabled")
    if policy.get("repository_must_not_redistribute_upstream_binaries") is not True:
        raise RuntimeError("upstream binary redistribution must be prohibited by project policy")
    for artifact in data.get("artifacts", []):
        if artifact.get("repository_storage") != "prohibited":
            raise RuntimeError(f"artifact {artifact.get('id')} is not marked repository_storage=prohibited")
    return data


def artifact(manifest: dict, artifact_id: str) -> dict:
    try:
        return next(item for item in manifest["artifacts"] if item["id"] == artifact_id)
    except StopIteration as exc:
        raise RuntimeError(f"missing acquisition manifest entry: {artifact_id}") from exc


def run_git(*args: str, cwd: Path | None = None) -> None:
    subprocess.run(["git", *args], cwd=cwd, check=True)


def hydrate_mdk(manifest: dict, refresh: bool) -> Path:
    mdk = artifact(manifest, "neoforge_mdk")
    destination = ROOT / mdk["local_destination"]
    source = mdk["source_url"]
    revision = mdk.get("revision")
    destination.parent.mkdir(parents=True, exist_ok=True)

    if destination.exists() and refresh:
        shutil.rmtree(destination)

    if not destination.exists():
        run_git("clone", "--filter=blob:none", "--no-checkout", source, str(destination))

    run_git("fetch", "--depth", "1", "origin", revision or "main", cwd=destination)
    run_git("checkout", "--detach", "FETCH_HEAD", cwd=destination)
    if revision:
        actual = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=destination, text=True).strip()
        if actual != revision:
            raise RuntimeError(f"MDK revision mismatch: expected {revision}, got {actual}")

    print(f"HYDRATED optional NeoForge MDK reference -> {destination.relative_to(ROOT)}")
    return destination


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Validate Gridelyx upstream acquisition policy and optionally hydrate ignored reference checkouts."
    )
    parser.add_argument("--check", action="store_true", help="validate the acquisition manifest without network access")
    parser.add_argument("--mdk", action="store_true", help="hydrate the pinned NeoForge MDK into .reference-cache")
    parser.add_argument("--refresh", action="store_true", help="replace an existing local reference checkout")
    args = parser.parse_args()

    manifest = load_manifest()
    print("PASS: upstream acquisition manifest is valid and forbids repository binary storage")

    if args.check:
        return 0
    if args.mdk:
        hydrate_mdk(manifest, args.refresh)
    else:
        print("No optional reference requested. Build dependencies are resolved by Gradle/ModDevGradle on demand.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
