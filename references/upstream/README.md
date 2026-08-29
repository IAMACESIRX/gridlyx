# Upstream Reference Policy

Tracked upstream snapshots are intentionally not stored in this directory.

When a maintainer needs the canonical NeoForge 26.2 MDK for comparison or provenance, run:

```bash
python tools/hydrate_references.py --mdk
```

The pinned checkout is created under `.reference-cache/upstream/mdk-26.2`, outside the tracked repository surface. `vault/manifest.json` records the official repository and immutable revision used for that checkout.

This directory remains as a policy/navigation anchor only. Do not copy hydrated Minecraft, NeoForge, JDK, LWJGL, Maven-cache or MDK payloads here.
