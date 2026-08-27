# Data generation and asset generation

The template routes generated output to `src/generated/resources` and exposes `generateAssets` and `generateData` aliases over the NeoForge data run. The starter `ModDataGenerators` registers an automated US-English `LanguageProvider`; extend it with model, tag, recipe, loot, data-map and codec-backed registry providers as needed.

`blueprints/` is specification input for AI/human generators, not a Minecraft runtime folder. Generated JSON must be reviewed and may then be committed from `src/generated/resources` for reproducibility.

For codec-driven worldgen, define stable `ResourceKey`s, bootstrap them through a `RegistrySetBuilder`, register via `GatherDataEvent.Client#createDatapackRegistryObjects`, and ensure every generated entry has deterministic identifiers. Never generate worldgen from nondeterministic runtime state.
