#!/usr/bin/env python3
"""Static validation for the Gridelyx Studio Bedrock target plane."""

from __future__ import annotations

import json
import uuid
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def load_json(relative: str) -> dict:
    path = ROOT / relative
    if not path.is_file():
        fail(f"missing {relative}")
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"invalid JSON in {relative}: {exc}")


def require_file(relative: str) -> None:
    if not (ROOT / relative).is_file():
        fail(f"missing {relative}")


def collect_uuids(manifest: dict) -> list[str]:
    values: list[str] = []
    header_uuid = manifest.get("header", {}).get("uuid")
    if header_uuid:
        values.append(header_uuid)
    for module in manifest.get("modules", []):
        module_uuid = module.get("uuid")
        if module_uuid:
            values.append(module_uuid)
    return values


def main() -> None:
    required = [
        "platform/brand.json",
        "platform/terminology.json",
        "platform/bedrock-capabilities.json",
        "docs/BEDROCK_ARCHITECTURE.md",
        "docs/GRIDELYX_BRIDGE_PROTOCOL.md",
        "bedrock/README.md",
        "bedrock/addon/behavior_pack/manifest.json",
        "bedrock/addon/behavior_pack/scripts/main.js",
        "bedrock/addon/behavior_pack/scripts/gridelyx_runtime.js",
        "bedrock/addon/resource_pack/manifest.json",
        "bedrock/addon/resource_pack/texts/en_US.lang",
        "bedrock/editor-extension/behavior_pack/manifest.json",
        "bedrock/editor-extension/behavior_pack/scripts/main.js",
        "native/CMakeLists.txt",
        "native/cpp/include/gridelyx_native.h",
        "native/cpp/src/gridelyx_native.cpp",
        "native/bedrock/include/gridelyx_bedrock_adapter.h",
        "native/bedrock/src/main.cpp",
        "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bedrock/BedrockBridgeFrame.java",
        "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bedrock/BedrockBridgeCodec.java",
        "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/bedrock/BedrockNativeSession.java",
        "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/nativeinterop/GridelyxNativeBridge.java",
    ]
    for relative in required:
        require_file(relative)

    brand = load_json("platform/brand.json")
    if brand.get("product_name") != "Gridelyx Studio":
        fail("canonical product name is not Gridelyx Studio")
    if brand.get("bedrock_plane") != "Gridelyx Bedrock Runtime":
        fail("canonical Bedrock plane name is not Gridelyx Bedrock Runtime")
    if brand.get("native_symbol_prefix") != "gridelyx_":
        fail("canonical future native ABI prefix is not gridelyx_")
    if brand.get("bridge_magic_status") != "legacy_compatibility_identifier_pending_protocol_migration":
        fail("legacy bridge magic must remain explicitly classified during migration")

    capabilities = load_json("platform/bedrock-capabilities.json")
    if capabilities.get("plane") != "gridelyx-bedrock":
        fail("Bedrock capability manifest has wrong Gridelyx plane")
    if capabilities.get("baseline", {}).get("minecraft_server") != "2.9.0":
        fail("stable Bedrock @minecraft/server baseline is not 2.9.0")

    behavior = load_json("bedrock/addon/behavior_pack/manifest.json")
    resources = load_json("bedrock/addon/resource_pack/manifest.json")
    editor = load_json("bedrock/editor-extension/behavior_pack/manifest.json")
    if behavior.get("format_version") != 2 or resources.get("format_version") != 2:
        fail("Bedrock stable manifests must use format_version 2")

    script_modules = [module for module in behavior.get("modules", []) if module.get("type") == "script"]
    if len(script_modules) != 1 or script_modules[0].get("entry") != "scripts/main.js":
        fail("stable behavior pack must declare scripts/main.js")

    server_dependencies = [
        dependency
        for dependency in behavior.get("dependencies", [])
        if dependency.get("module_name") == "@minecraft/server"
    ]
    if len(server_dependencies) != 1 or server_dependencies[0].get("version") != "2.9.0":
        fail("stable behavior pack must depend on @minecraft/server 2.9.0")

    editor_dependencies = [
        dependency
        for dependency in editor.get("dependencies", [])
        if dependency.get("module_name") == "@minecraft/server-editor"
    ]
    if len(editor_dependencies) != 1 or not editor_dependencies[0].get("version", "").startswith("0.1.0-beta."):
        fail("Editor extension must remain on an explicit preview @minecraft/server-editor version")

    all_uuids = collect_uuids(behavior) + collect_uuids(resources) + collect_uuids(editor)
    if len(all_uuids) != len(set(all_uuids)):
        fail("Bedrock pack UUIDs are not unique")
    try:
        for value in all_uuids:
            uuid.UUID(value)
    except ValueError as exc:
        fail(f"invalid Bedrock pack UUID: {exc}")

    forbidden = "madk_"
    for path in (ROOT / "native").rglob("*"):
        if not path.is_file() or path.suffix.lower() not in {".h", ".hpp", ".c", ".cc", ".cpp", ".rs", ".toml", ".md"}:
            continue
        if forbidden in path.read_text(encoding="utf-8", errors="ignore"):
            fail(f"legacy MADK native symbol remains in {path.relative_to(ROOT)}")

    # These Gridelyx-prefixed symbols are explicitly legacy compatibility ABI during the
    # staged Gridelyx migration. They remain required until Issue #26 lands a versioned ABI
    # transition with interoperability/rollback evidence.
    header = (ROOT / "native/cpp/include/gridelyx_native.h").read_text(encoding="utf-8")
    for symbol in (
        "gridelyx_abi_version",
        "gridelyx_protocol_version",
        "gridelyx_shm_create",
        "gridelyx_shm_publish",
        "gridelyx_shm_snapshot",
    ):
        if symbol not in header:
            fail(f"missing legacy compatibility native bridge symbol {symbol}")

    print("PASS: Gridelyx Studio Bedrock manifests, capability plane, native compatibility bridge and brand invariants")


if __name__ == "__main__":
    main()
