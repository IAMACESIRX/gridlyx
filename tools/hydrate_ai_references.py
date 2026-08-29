#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "platform" / "reference-sources.json"
CACHE_ROOT = ROOT / ".reference-cache"
PROVENANCE = CACHE_ROOT / "index" / "reference-provenance.json"

CORE_IDS = (
    "neoforge-mdk-26.2-moddevgradle",
    "neoforge-documentation",
    "moddevgradle",
)
OPEN_SOURCE_IDS = CORE_IDS + (
    "neoforge-source",
    "lwjgl",
)


def load_manifest() -> dict:
    data = json.loads(MANIFEST.read_text(encoding="utf-8"))
    if data.get("schema_version") != 1:
        raise RuntimeError("platform/reference-sources.json must use schema_version 1")
    policy = data.get("policy", {})
    if policy.get("repository_storage") != "metadata-and-project-owned-source-only":
        raise RuntimeError("AI reference policy must remain metadata/project-source only")
    if policy.get("official_or_authorized_sources_only") is not True:
        raise RuntimeError("AI reference corpus must use official or authorized sources only")
    return data


def references_by_id(data: dict) -> dict[str, dict]:
    return {entry["id"]: entry for entry in data.get("references", [])}


def run_git(*args: str, cwd: Path | None = None, capture: bool = False) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=cwd,
        check=True,
        text=True,
        stdout=subprocess.PIPE if capture else None,
    )
    return result.stdout.strip() if capture and result.stdout else ""


def assert_cache_destination(destination: Path) -> None:
    resolved_root = CACHE_ROOT.resolve()
    resolved_destination = destination.resolve()
    if resolved_destination == resolved_root or resolved_root not in resolved_destination.parents:
        raise RuntimeError(f"reference destination must stay under .reference-cache: {destination}")


def hydrate_git(entry: dict, refresh: bool) -> dict:
    source = entry.get("source_url")
    local_destination = entry.get("local_destination")
    if not source or not local_destination:
        raise RuntimeError(f"{entry['id']}: git hydration requires source_url and local_destination")

    destination = ROOT / local_destination
    assert_cache_destination(destination)
    destination.parent.mkdir(parents=True, exist_ok=True)

    if refresh and destination.exists():
        shutil.rmtree(destination)

    revision = entry.get("revision")
    branch_hint = entry.get("branch_hint")

    if not destination.exists():
        run_git("clone", "--filter=blob:none", "--no-checkout", source, str(destination))

    if revision:
        run_git("fetch", "--depth", "1", "origin", revision, cwd=destination)
        run_git("checkout", "--detach", "FETCH_HEAD", cwd=destination)
        resolved = run_git("rev-parse", "HEAD", cwd=destination, capture=True)
        if resolved != revision:
            raise RuntimeError(f"{entry['id']}: expected revision {revision}, got {resolved}")
        requested_ref = revision
    elif branch_hint:
        run_git("fetch", "--depth", "1", "origin", branch_hint, cwd=destination)
        run_git("checkout", "--detach", "FETCH_HEAD", cwd=destination)
        resolved = run_git("rev-parse", "HEAD", cwd=destination, capture=True)
        requested_ref = branch_hint
    else:
        run_git("fetch", "--depth", "1", "origin", "HEAD", cwd=destination)
        run_git("checkout", "--detach", "FETCH_HEAD", cwd=destination)
        resolved = run_git("rev-parse", "HEAD", cwd=destination, capture=True)
        requested_ref = "remote HEAD"

    print(f"HYDRATED {entry['id']} -> {destination.relative_to(ROOT)} @ {resolved}")
    return {
        "source_id": entry["id"],
        "kind": entry.get("kind"),
        "version": entry.get("version"),
        "source_url": source,
        "requested_ref": requested_ref,
        "resolved_revision": resolved,
        "local_destination": destination.relative_to(ROOT).as_posix(),
        "repository_storage": "prohibited",
        "roles": entry.get("roles", []),
    }


def write_provenance(records: list[dict], selected_ids: list[str]) -> None:
    PROVENANCE.parent.mkdir(parents=True, exist_ok=True)
    payload = {
        "schema_version": 1,
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "manifest": MANIFEST.relative_to(ROOT).as_posix(),
        "selected_ids": selected_ids,
        "records": records,
        "policy": "local reference state only; do not commit .reference-cache",
    }
    PROVENANCE.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")
    print(f"WROTE {PROVENANCE.relative_to(ROOT)}")


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Hydrate official/open-source AI reference material into ignored .reference-cache state."
    )
    selection = parser.add_mutually_exclusive_group()
    selection.add_argument("--core", action="store_true", help="hydrate MDK + NeoForged docs + ModDevGradle")
    selection.add_argument(
        "--all-open-source",
        action="store_true",
        help="hydrate core references plus NeoForge and LWJGL source repositories",
    )
    selection.add_argument(
        "--ids",
        nargs="+",
        metavar="SOURCE_ID",
        help="hydrate explicit source IDs that define a source_url and local_destination",
    )
    parser.add_argument("--refresh", action="store_true", help="replace existing selected checkouts")
    parser.add_argument("--list", action="store_true", help="list available source IDs and exit")
    args = parser.parse_args()

    data = load_manifest()
    refs = references_by_id(data)

    if args.list:
        for entry in data.get("references", []):
            hydrateable = bool(entry.get("source_url") and entry.get("local_destination"))
            print(f"{entry['id']}\t{entry.get('version')}\thydrateable={str(hydrateable).lower()}")
        return 0

    if args.ids:
        selected_ids = list(args.ids)
    elif args.all_open_source:
        selected_ids = list(OPEN_SOURCE_IDS)
    else:
        selected_ids = list(CORE_IDS)

    records: list[dict] = []
    for source_id in selected_ids:
        entry = refs.get(source_id)
        if entry is None:
            raise RuntimeError(f"unknown reference source id: {source_id}")
        if entry.get("repository_storage") in {"tracked-project-owned-source", "metadata-link-only"}:
            print(f"SKIP {source_id}: no local upstream checkout required")
            continue
        if not entry.get("source_url") or not entry.get("local_destination"):
            print(f"SKIP {source_id}: use resolver/docs/local-generation path from platform/reference-sources.json")
            continue
        records.append(hydrate_git(entry, args.refresh))

    write_provenance(records, selected_ids)
    print("PASS: AI reference corpus hydrated outside Git")
    print("Run tools/build_reference_indexes.py --corpus to create a local searchable index.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
