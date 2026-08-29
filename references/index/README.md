# Reference Index

Gridelyx no longer commits generated indexes derived from vendored upstream binary archives.

Canonical dependency identity and acquisition provenance live in [`vault/manifest.json`](../../vault/manifest.json). Optional upstream source/reference material is hydrated into `.reference-cache/` and may be indexed locally without creating tracked derivative payloads.

Generate the current local NeoForge MDK file index with [`tools/build_reference_indexes.py`](../../tools/build_reference_indexes.py):

```bash
python tools/build_reference_indexes.py
```

The output is written beneath `.reference-cache/index/` and is ignored by Git.

Repository documentation should link to official upstream sources or Gridelyx-owned analysis instead of mirroring complete third-party archive/source inventories.
