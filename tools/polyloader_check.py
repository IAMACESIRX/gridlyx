#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ADVANCED = ROOT / "templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced"
POLYLOADER = ADVANCED / "polyloader"

REQUIRED = [
    POLYLOADER / "PolyloaderBootstrap.java",
    POLYLOADER / "PolyloaderKernel.java",
    POLYLOADER / "UnifiedAbstractionLayer.java",
    POLYLOADER / "AsmInvocationTranslator.java",
    POLYLOADER / "DynamicHandleScanner.java",
    POLYLOADER / "SideloadContainer.java",
    ADVANCED / "assets/DynamicModelRegistry.java",
    ADVANCED / "assets/DynamicTextureRegistry.java",
    ADVANCED / "sandbox/ScriptSupervisor.java",
    ADVANCED / "sandbox/TransactionalWorldSandbox.java",
    ROOT / "docs/POLYLOADER_ARCHITECTURE.md",
    ROOT / "docs/LIVE_ASSET_EDITING.md",
    ROOT / "docs/FAULT_TOLERANCE.md",
    ROOT / "platform/polyloader-capabilities.json",
]

FORBIDDEN_IMPORTS = (
    "import net.minecraft.",
    "import net.fabricmc.",
    "import net.minecraftforge.",
    "import net.neoforged.",
    "import org.quiltmc.",
)


def fail(message: str) -> None:
    raise SystemExit(f"FAIL: {message}")


def main() -> None:
    missing = [str(path.relative_to(ROOT)) for path in REQUIRED if not path.is_file()]
    if missing:
        fail("missing required polyloader files: " + ", ".join(missing))

    for path in POLYLOADER.glob("*.java"):
        content = path.read_text(encoding="utf-8")
        for forbidden in FORBIDDEN_IMPORTS:
            if forbidden in content:
                fail(f"loader-neutral package has forbidden compile-time dependency: {path}: {forbidden}")

    agent = (ADVANCED / "bytecode/AgentBootstrap.java").read_text(encoding="utf-8")
    if "PolyloaderBootstrap.install" not in agent:
        fail("instrumentation agent does not install the polyloader bootstrap")

    capabilities = json.loads((ROOT / "platform/polyloader-capabilities.json").read_text(encoding="utf-8"))
    if capabilities.get("plane") != "gridelyx-polyloader":
        fail("Polyloader capability manifest has the wrong Gridelyx plane identifier")
    if capabilities["capabilities"].get("absolute_all-version-compatibility") != "not-claimed":
        fail("capability manifest must not claim universal compatibility before validation")

    print("PASS: Gridelyx Polyloader, live-asset and sandbox invariants")


if __name__ == "__main__":
    main()
