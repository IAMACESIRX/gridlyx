#!/usr/bin/env python3
from __future__ import annotations

import argparse
import csv
import json
import tempfile
from pathlib import Path


def recipe_from_row(row: dict[str, str]) -> dict:
    recipe_type = row.get("type", "").strip() or "minecraft:crafting_shapeless"
    ingredient = row["ingredient"].strip()
    result = row["result"].strip()
    count = int(row.get("count", "1") or "1")
    return {
        "type": recipe_type,
        "ingredients": [{"item": ingredient}],
        "result": {"id": result, "count": count},
    }


def convert(source: Path, output_root: Path) -> int:
    written = 0
    with source.open(newline="", encoding="utf-8-sig") as handle:
        for row in csv.DictReader(handle):
            namespace = (row.get("namespace") or "examplemod").strip()
            path = row["path"].strip().strip("/")
            destination = output_root / "data" / namespace / "recipe" / f"{path}.json"
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_text(
                json.dumps(recipe_from_row(row), indent=2) + "\n",
                encoding="utf-8",
            )
            written += 1
    return written


def self_test() -> int:
    with tempfile.TemporaryDirectory() as temporary:
        root = Path(temporary)
        source = root / "recipes.csv"
        source.write_text(
            "namespace,path,ingredient,result,count,type\n"
            "examplemod,test,minecraft:stone,minecraft:diamond,2,minecraft:crafting_shapeless\n",
            encoding="utf-8",
        )
        count = convert(source, root / "out")
        generated = root / "out/data/examplemod/recipe/test.json"
        if count != 1 or not generated.exists():
            print("ERROR: CSV recipe pipeline self-test failed")
            return 2
    print("PASS: CSV recipe pipeline self-test")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", nargs="?")
    parser.add_argument("--output-root", default="src/generated/resources")
    parser.add_argument("--self-test", action="store_true")
    args = parser.parse_args()
    if args.self_test:
        return self_test()
    if not args.source:
        parser.error("source CSV is required")
    count = convert(Path(args.source), Path(args.output_root))
    print(f"WROTE: {count} recipe file(s)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
