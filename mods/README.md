# Mod Workspaces

Create standalone mod projects here with `python tools/new_mod.py ...`.

Each workspace owns its source, resources, tests and build output. Reference-vault material must not be copied into a mod unless the mod genuinely owns/adapts that source and its licence permits it.

To opt a workspace into automated NeoForge GameTest execution, add registered GameTests and create an `.enable-gametest` marker file in that workspace.
