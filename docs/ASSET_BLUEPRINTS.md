# Localization and asset blueprints

Every mod workspace contains `blueprints/` as AI/human input specifications. They are deliberately outside `src/main/resources`, so unfinished design data cannot accidentally ship in the mod JAR.

- `blueprints/localization/en_us.json` defines the minimum translation surface.
- `blueprints/assets/item-model.json` and `block-model.json` describe identifiers and source textures.
- `blueprints/data/codec-worldgen.json` records the dynamic-registry/worldgen generation contract.

The preferred pipeline is blueprint -> typed provider/code -> NeoForge data run -> `src/generated/resources` -> review -> commit. Hand-authored JSON is acceptable when it is simpler, but generated output should remain deterministic and reproducible.

Binary assets should have source/provenance notes. Do not use copied vanilla textures as convenient placeholders in distributable output.
