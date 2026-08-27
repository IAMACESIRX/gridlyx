# Quality gates

Every mod workspace inherits Spotless and Checkstyle. The normal pre-merge gate is:

```bash
./gradlew spotlessCheck check build
```

Use `./gradlew spotlessApply` to apply deterministic Java formatting. Checkstyle is intentionally structural and relatively small so formatting is not duplicated between tools.

The CI/security layer adds platform validation, diagnostics, GameTests when requested, JAR inspection and CodeQL where the GitHub plan/repository configuration permits private-repository code scanning.
