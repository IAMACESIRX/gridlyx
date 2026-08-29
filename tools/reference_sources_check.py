#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERSIONS = json.loads((ROOT / "platform" / "versions.json").read_text(encoding="utf-8"))
REFERENCES = json.loads((ROOT / "platform" / "reference-sources.json").read_text(encoding="utf-8"))
ACQUISITION = json.loads((ROOT / "vault" / "manifest.json").read_text(encoding="utf-8"))

REQUIRED_IDS = {
    "gridelyx-template",
    "neoforge-mdk-26.2-moddevgradle",
    "neoforge-documentation",
    "moddevgradle",
    "neoforge-source",
    "minecraft-version-metadata",
    "minecraft-development-sources",
    "jdk-sources",
    "gradle-docs",
    "asm",
    "lwjgl",
    "graalvm-polyglot",
    "junit",
    "archunit",
}


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def by_id(items: list[dict]) -> dict[str, dict]:
    result: dict[str, dict] = {}
    for item in items:
        item_id = item.get("id")
        if not item_id:
            fail("reference/acquisition entry missing id")
        if item_id in result:
            fail(f"duplicate id: {item_id}")
        result[item_id] = item
    return result


def require_version(entry: dict, expected: str, source_id: str) -> None:
    if str(entry.get("version")) != str(expected):
        fail(f"{source_id} version {entry.get('version')!r} != canonical {expected!r}")


def check_urls(value: object, path: str = "root") -> None:
    if isinstance(value, dict):
        for key, child in value.items():
            child_path = f"{path}.{key}"
            if key.endswith("_url") and isinstance(child, str) and not child.startswith("https://"):
                fail(f"non-HTTPS source URL at {child_path}: {child}")
            check_urls(child, child_path)
    elif isinstance(value, list):
        for index, child in enumerate(value):
            check_urls(child, f"{path}[{index}]")


def main() -> int:
    if REFERENCES.get("schema_version") != 1:
        fail("platform/reference-sources.json schema_version must be 1")
    policy = REFERENCES.get("policy", {})
    if policy.get("repository_storage") != "metadata-and-project-owned-source-only":
        fail("reference corpus repository-storage policy drifted")
    if policy.get("official_or_authorized_sources_only") is not True:
        fail("reference corpus must require official/authorized sources")
    if policy.get("generated_minecraft_source_is_local_only") is not True:
        fail("generated Minecraft development source must remain local-only")

    refs = by_id(REFERENCES.get("references", []))
    missing = sorted(REQUIRED_IDS - refs.keys())
    if missing:
        fail(f"missing required reference source IDs: {', '.join(missing)}")

    artifacts = by_id(ACQUISITION.get("artifacts", []))
    if ACQUISITION.get("policy", {}).get("repository_must_not_redistribute_upstream_binaries") is not True:
        fail("acquisition policy no longer forbids upstream binary redistribution")

    require_version(refs["gridelyx-template"], VERSIONS["minecraft"], "gridelyx-template")
    require_version(refs["neoforge-mdk-26.2-moddevgradle"], VERSIONS["minecraft"], "neoforge-mdk")
    require_version(refs["moddevgradle"], VERSIONS["moddevgradle"], "moddevgradle")
    require_version(refs["neoforge-source"], VERSIONS["neoforge"], "neoforge-source")
    require_version(refs["minecraft-development-sources"], VERSIONS["minecraft"], "minecraft-development-sources")
    require_version(refs["jdk-sources"], VERSIONS["java"]["exact"], "jdk-sources")
    require_version(refs["gradle-docs"], VERSIONS["gradle"], "gradle-docs")
    require_version(refs["asm"], VERSIONS["advanced"]["asm"], "asm")
    require_version(refs["lwjgl"], VERSIONS["advanced"]["lwjgl_reference"], "lwjgl")
    require_version(refs["graalvm-polyglot"], VERSIONS["advanced"]["graalvm_polyglot"], "graalvm-polyglot")
    require_version(refs["junit"], VERSIONS["quality"]["junit"], "junit")
    require_version(refs["archunit"], VERSIONS["quality"]["archunit"], "archunit")

    if refs["neoforge-source"].get("coordinate") != f"net.neoforged:neoforge:{VERSIONS['neoforge']}":
        fail("NeoForge source coordinate does not match canonical NeoForge version")

    expected_asm = {
        f"org.ow2.asm:asm:{VERSIONS['advanced']['asm']}",
        f"org.ow2.asm:asm-commons:{VERSIONS['advanced']['asm']}",
    }
    if not expected_asm.issubset(set(refs["asm"].get("coordinates", []))):
        fail("ASM source coordinates do not match canonical ASM version")

    expected_lwjgl = {
        f"org.lwjgl:lwjgl:{VERSIONS['advanced']['lwjgl_reference']}",
        f"org.lwjgl:lwjgl-opengl:{VERSIONS['advanced']['lwjgl_reference']}",
        f"org.lwjgl:lwjgl-glfw:{VERSIONS['advanced']['lwjgl_reference']}",
    }
    if not expected_lwjgl.issubset(set(refs["lwjgl"].get("coordinates", []))):
        fail("LWJGL source coordinates do not match canonical LWJGL version")

    mdk_acquisition = artifacts.get("neoforge_mdk")
    if not mdk_acquisition:
        fail("vault acquisition manifest missing neoforge_mdk")
    if refs["neoforge-mdk-26.2-moddevgradle"].get("source_url") != mdk_acquisition.get("source_url"):
        fail("MDK source URL differs between reference and acquisition manifests")
    if refs["neoforge-mdk-26.2-moddevgradle"].get("revision") != mdk_acquisition.get("revision"):
        fail("MDK pinned revision differs between reference and acquisition manifests")

    for source_id, entry in refs.items():
        destination = entry.get("local_destination")
        if destination and not str(destination).startswith(".reference-cache/"):
            fail(f"{source_id} local_destination escapes .reference-cache: {destination}")
        storage = entry.get("repository_storage")
        if source_id != "gridelyx-template" and storage == "tracked-project-owned-source":
            fail(f"upstream reference {source_id} may not be marked as tracked project source")

    required_paths = [
        "docs/AI_MODDING_REFERENCE_CORPUS.md",
        "ai/skills/neoforge-26.2-mod-development/SKILL.md",
        "tools/hydrate_ai_references.py",
        "tools/build_reference_indexes.py",
        "tools/history_redistribution_guard.py",
    ]
    for relative in required_paths:
        if not (ROOT / relative).is_file():
            fail(f"required human/AI reference artifact missing: {relative}")

    check_urls(REFERENCES, "platform/reference-sources.json")
    print("PASS: AI reference sources align with canonical build/dependency locks")
    print(f"PASS: {len(refs)} reference source records validated")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
