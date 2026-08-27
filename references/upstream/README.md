# Upstream Readable Snapshots

This directory is generated from checksummed artifacts in `vault/`.

Run:

```bash
python tools/hydrate_references.py
```

The current canonical snapshot is `mdk-26.2/`, extracted from the exact supplied MDK archive. Treat files here as immutable upstream evidence; modify `templates/` or `mods/` instead.
