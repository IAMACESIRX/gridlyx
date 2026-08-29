#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import hashlib
import json
import os
import re

ROOT = Path(__file__).resolve().parents[1]
LOCK = json.loads((ROOT / "platform/versions.json").read_text(encoding="utf-8"))
BUILD_LOCK = json.loads((ROOT / "platform/master-build.lock.json").read_text(encoding="utf-8"))
REQUIRED = {
    "build.gradle",
    "gradle.properties",
    "settings.gradle",
    "gradlew",
    "gradlew.bat",
    "gradle/wrapper/gradle-wrapper.properties",
    "config/checkstyle/checkstyle.xml",
    "LICENSE.txt",
    "src/main/templates/META-INF/neoforge.mods.toml",
}


def error(errors: list[str], message: str) -> None:
    errors.append(message)
    print("ERROR:", message)


def properties(path: Path) -> dict[str, str]:
    result = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if line and not line.startswith("#") and "=" in line:
            key, value = line.split("=", 1)
            result[key.strip()] = value.strip()
    return result


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def validate_project(project: Path, errors: list[str], seen_ids: dict[str, Path]) -> None:
    label = str(project.relative_to(ROOT))
    for relative in REQUIRED:
        if not (project / relative).exists():
            error(errors, f"{label}: missing {relative}")
    props_path = project / "gradle.properties"
    if not props_path.exists():
        return
    props = properties(props_path)
    mod_id = props.get("mod_id", "")
    if not re.fullmatch(r"[a-z][a-z0-9_]{1,63}", mod_id):
        error(errors, f"{label}: invalid mod_id {mod_id!r}")
    if mod_id in seen_ids and seen_ids[mod_id] != project:
        error(errors, f"{label}: duplicate mod_id also used by {seen_ids[mod_id].relative_to(ROOT)}")
    seen_ids[mod_id] = project

    locks = {
        "minecraft_version": LOCK["minecraft"],
        "neo_version": LOCK["neoforge"],
        "checkstyle_version": LOCK["quality"]["checkstyle"],
        "google_java_format_version": LOCK["quality"]["google_java_format"],
        "junit_version": LOCK["quality"]["junit"],
        "archunit_version": LOCK["quality"]["archunit"],
        "asm_version": LOCK["advanced"]["asm"],
        "lwjgl_version": LOCK["advanced"]["lwjgl_reference"],
        "graalvm_version": LOCK["advanced"]["graalvm_polyglot"],
        "mcp_protocol_version": LOCK["protocols"]["mcp"],
    }
    for key, expected in locks.items():
        if props.get(key) != expected:
            error(errors, f"{label}: {key} drift; expected {expected!r}, got {props.get(key)!r}")
    if not props.get("mod_license"):
        error(errors, f"{label}: mod_license is empty")

    build_path = project / "build.gradle"
    build = build_path.read_text(encoding="utf-8", errors="replace")
    if sha256(build_path) != BUILD_LOCK["sha256"]:
        error(errors, f"{label}: build.gradle differs from the locked master build")
    required_build_tokens = [
        f"net.neoforged.moddev' version '{LOCK['moddevgradle']}'",
        "id 'checkstyle'",
        f"id 'com.diffplug.spotless' version '{LOCK['quality']['spotless']}'",
        "JavaLanguageVersion.of(25)",
        "src/generated/resources",
        "advancedJar",
        "agentJar",
        "junit-jupiter",
        "com.tngtech.archunit:archunit",
        "org.graalvm.polyglot:polyglot",
        "gridelyxSmokeTest",
        "gridelyxModuleSmokeTest",
        "polyglotSmokeTest",
    ]
    for token in required_build_tokens:
        if token not in build:
            error(errors, f"{label}: build.gradle missing required platform token {token!r}")

    suspicious = [
        r"\bimplementation\s+[\"']org\.lwjgl:",
        r"\bimplementation\s+[\"']net\.neoforged:neoforge:",
    ]
    for pattern in suspicious:
        if re.search(pattern, build):
            error(errors, f"{label}: vendor/runtime reference leaked into normal implementation configuration")

    wrapper = project / "gradle/wrapper/gradle-wrapper.properties"
    if wrapper.exists() and f"gradle-{LOCK['gradle']}-bin.zip" not in wrapper.read_text(encoding="utf-8"):
        error(errors, f"{label}: Gradle launcher version drift")
    if os.name != "nt" and (project / "gradlew").exists() and not os.access(project / "gradlew", os.X_OK):
        error(errors, f"{label}: gradlew is not executable")

    java_files = list((project / "src/main/java").rglob("*.java"))
    if not any("@Mod(" in path.read_text(encoding="utf-8", errors="ignore") for path in java_files):
        error(errors, f"{label}: no @Mod main class found")
    if not (project / "blueprints/localization/en_us.json").exists():
        error(errors, f"{label}: localization blueprint missing")
    if not (project / "blueprints/data/codec-worldgen.json").exists():
        error(errors, f"{label}: codec/worldgen blueprint missing")


def validate_acquisition_manifest(errors: list[str]) -> None:
    manifest_path = ROOT / "vault/manifest.json"
    try:
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        error(errors, f"vault/manifest.json: {exc}")
        return

    if manifest.get("schema_version") != 2:
        error(errors, "vault/manifest.json must use acquisition schema_version 2")
    policy = manifest.get("policy", {})
    if policy.get("mode") != "acquire-at-build-or-run-time":
        error(errors, "vault acquisition mode must be acquire-at-build-or-run-time")
    if policy.get("repository_must_not_redistribute_upstream_binaries") is not True:
        error(errors, "vault acquisition policy must prohibit upstream binary redistribution")

    artifacts = manifest.get("artifacts", [])
    ids = {artifact.get("id") for artifact in artifacts if isinstance(artifact, dict)}
    required_ids = {
        "minecraft",
        "neoforge",
        "neoforge_mdk",
        "jdk",
        "gradle",
        "lwjgl",
        "java_maven_dependencies",
    }
    missing = required_ids - ids
    if missing:
        error(errors, "upstream acquisition manifest missing entries: " + ", ".join(sorted(missing)))
    for artifact in artifacts:
        if not isinstance(artifact, dict):
            error(errors, "vault acquisition artifact entry is not an object")
            continue
        if artifact.get("repository_storage") != "prohibited":
            error(errors, f"{artifact.get('id', '<unknown>')}: repository_storage must be prohibited")


def validate_platform_files(errors: list[str]) -> None:
    required = [
        "platform/master-build.lock.json",
        "platform/capabilities.json",
        "vault/manifest.json",
        ".github/actions/gridelyx-toolchain/action.yml",
        "tools/build_lock.py",
        "tools/script_gatekeeper.py",
        "tools/autodoc.py",
        "tools/bytecode_diff.py",
        "tools/csv_recipe_pipeline.py",
        "tools/headless_validate.py",
        "tools/hydrate_references.py",
        "tools/redistribution_guard.py",
        "docs/AUTO_CAPABILITIES.md",
        "docs/PROJECT_PLAN.md",
        "docs/HOTLOAD_ARCHITECTURE.md",
        "docs/POLYGLOT_AND_BRIDGES.md",
        "docs/REFERENCE_VAULT.md",
    ]
    for relative in required:
        if not (ROOT / relative).exists():
            error(errors, f"platform: missing {relative}")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--mod")
    args = parser.parse_args()
    errors: list[str] = []
    seen_ids: dict[str, Path] = {}
    if args.mod:
        project = ROOT / "mods" / args.mod
        if not project.is_dir():
            raise SystemExit(f"No mod workspace: {project}")
        validate_project(project, errors, seen_ids)
    else:
        validate_platform_files(errors)
        validate_acquisition_manifest(errors)
        validate_project(ROOT / LOCK["template"], errors, seen_ids)
        for project in sorted((ROOT / "mods").glob("*")):
            if project.is_dir() and (project / "build.gradle").exists():
                validate_project(project, errors, seen_ids)
    if errors:
        print(f"FAILED: {len(errors)} validation error(s)")
        return 2
    print("PASS: platform static validation")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
