# Mod workspaces

Each direct child directory is an independent NeoForge project and produces its own JAR. This allows multiple mods to be developed, built and validated side by side without merging their dependency graphs.

Create one with:

```bash
python tools/new_mod.py my_mod "My Mod" com.iamacesirx.mods.mymod
```

Build all workspaces with `python tools/build_all.py`. Keep experimental cross-cutting work in `experiments/` until it has a stable interface and validation evidence.
