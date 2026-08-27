#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import argparse
import json
import os
import re

ROOT = Path(__file__).resolve().parents[1]
LOCK = json.loads((ROOT / "platform/versions.json").read_text(encoding="utf-8"))
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
        "asm_version": LOCK["advanced"]["asm"],
        "lwjgl_version": LOCK["advanced"]["lwjgl_reference"],
    }
    for key, expected in locks.items():
        if props.get(key) != expected:
            error(errors, f"{label}: {key} drift; expected {expected!r}, got {props.get(key)!r}")
    if not props.get("mod_license"):
        error(errors, f"{label}: mod_license is empty")

    build = (project / "build.gradle").read_text(encoding="utf-8", errors="replace")
    required_build_tokens = [
        f"net.neoforged.moddev' version '{LOCK['moddevgradle']}'",
        "id 'checkstyle'",
        f"id 'com.diffplug.spotless' version '{LOCK['quality']['spotless']}'",
        "JavaLanguageVersion.of(25)",
        "src/generated/resources",
        "advancedJar",
        "agentJar",
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
        error(errors, f"{label}: Gradle wrapper drift")
    if os.name != "nt" and (project / "gradlew").exists() and not os.access(project / "gradlew", os.X_OK):
        error(errors, f"{label}: gradlew is not executable")

    java_files = list((project / "src/main/java").rglob("*.java"))
    if not any("@Mod(" in path.read_text(encoding="utf-8", errors="ignore") for path in java_files):
        error(errors, f"{label}: no @Mod main class found")
    if not (project / "blueprints/localization/en_us.json").exists():
        error(errors, f"{label}: localization blueprint missing")
    if not (project / "blueprints/data/codec-worldgen.json").exists():
        error(errors, f"{label}: codec/worldgen blueprint missing")


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
        validate_project(ROOT / LOCK["template"], errors, seen_ids)
        for project in sorted((ROOT / "mods").glob("*")):
            if project.is_dir() and (project / "build.gradle").exists():
                validate_project(project, errors, seen_ids)
        manifest = json.loads((ROOT / "vault/manifest.json").read_text(encoding="utf-8"))
        ids = {artifact["id"] for artifact in manifest["artifacts"]}
        expected = {"mdk", "neoforge_installer", "jdk", "lwjgl"}
        if ids != expected:
            error(errors, f"vault manifest ids {ids} != {expected}")
    if errors:
        print(f"FAILED: {len(errors)} validation error(s)")
        return 2
    print("PASS: platform static validation")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
