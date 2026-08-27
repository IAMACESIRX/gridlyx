# Reference Index

The reference index is the AI-facing lookup layer.

The complete prepared repository also contains generated indexes for every supplied archive entry, every path in the JDK 25 embedded `src.zip`, and every file/type in all LWJGL 3.4.1 source JARs. Those indexes are generated from the immutable vault rather than guessed.

Core identity is always available in `supplied-artifacts.tsv` and `vault/manifest.json`.

Use `tools/vault.py` to reconstruct exact inputs. After binary import, deeper indexes can be regenerated from those exact bytes if needed.
