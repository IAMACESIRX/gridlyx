#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

REQUIRED = [
    "studio/Cargo.toml",
    "studio/core/Cargo.toml",
    "studio/core/src/lib.rs",
    "studio/core/src/model.rs",
    "studio/core/src/provider.rs",
    "studio/core/src/provenance.rs",
    "studio/core/src/solver.rs",
    "studio/providers/providers.json",
    "studio/providers/loader-adapters.json",
    "docs/PROJECT_PLAN.md",
    "docs/PROJECT_OVERVIEW.md",
    "docs/ROADMAP.md",
    "docs/FEATURE_MAP.md",
    "docs/PROJECT_STRUCTURE.md",
    "docs/ACQUISITION_AND_RESOLUTION.md",
    "docs/MACHINIMA_PRODUCTION.md",
    "docs/AI_CONTEXT_SYSTEM.md",
    "AI_HANDOFF.md",
    "ai/AI_ORGANISATION.md",
    "ai/DRIFT_MITIGATION.md",
    "ai/CONTEXT.md",
    "ai/context-map.json",
]

ALLOWED_PROVIDER_IDS = {
    "mojang", "fabric-meta", "quilt-meta", "forge-official", "neoforge-maven",
    "modrinth", "curseforge", "adoptium", "local-import",
}

IDENTITY_MARKERS = {
    "AI_HANDOFF.md": ("Minecraft", "launcher", "creator"),
    "ai/CONTEXT.md": ("Minecraft", "instance", "loader"),
    "docs/PROJECT_PLAN.md": ("Minecraft", "cross-edition", "readiness"),
}


def main() -> int:
    errors: list[str] = []
    for rel in REQUIRED:
        if not (ROOT / rel).exists():
            errors.append(f"missing required Studio path: {rel}")

    provider_path = ROOT / "studio/providers/providers.json"
    if provider_path.exists():
        data = json.loads(provider_path.read_text(encoding="utf-8"))
        ids = {entry["id"] for entry in data.get("providers", [])}
        if ids != ALLOWED_PROVIDER_IDS:
            errors.append(f"provider manifest mismatch: {sorted(ids)} != {sorted(ALLOWED_PROVIDER_IDS)}")
        curseforge = next((entry for entry in data["providers"] if entry["id"] == "curseforge"), None)
        if not curseforge or "API key" not in curseforge.get("auth", ""):
            errors.append("CurseForge provider must explicitly require an API key")
        if curseforge and "never bypass" not in curseforge.get("redistribution", ""):
            errors.append("CurseForge provider must explicitly prohibit distribution bypass")

    loader_path = ROOT / "studio/providers/loader-adapters.json"
    if loader_path.exists():
        data = json.loads(loader_path.read_text(encoding="utf-8"))
        ids = {entry["id"] for entry in data.get("adapters", [])}
        for required in ("vanilla", "fabric", "quilt", "forge", "neoforge", "external-adapter"):
            if required not in ids:
                errors.append(f"missing loader adapter contract: {required}")

    for rel, markers in IDENTITY_MARKERS.items():
        path = ROOT / rel
        if not path.exists():
            continue
        text = path.read_text(encoding="utf-8")
        missing = [marker for marker in markers if marker.lower() not in text.lower()]
        if missing:
            errors.append(f"{rel} is missing project identity markers: {', '.join(missing)}")

    if errors:
        for error in errors:
            print("ERROR:", error)
        return 2
    print("PASS: Studio architecture, provider and AI-context contracts")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
