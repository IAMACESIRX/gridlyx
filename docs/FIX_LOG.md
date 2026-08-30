# Gridelyx Fix Log

This is the durable remediation ledger for repository, CI, build, tooling, and migration failures. It records the failure mechanism rather than only the visible symptom so the same class of defect can be recognized and prevented later.

## Logging rule

For each material failure, record: **ID**, **date**, **subsystem**, **symptom/evidence**, **root cause**, **fix**, **verification**, **status**, and **prevention/follow-up**. A cascading warning is linked to its upstream failure rather than treated as an independent root cause.

Status values:

- **Resolved** — root cause fixed and directly verified.
- **Fixed / verification pending** — corrective change is committed; the relevant CI proof has not completed yet.
- **Open** — root cause identified but not yet corrected.
- **External limitation** — repository behavior is correct but an external platform boundary remains.

## Incidents

### FIX-2026-08-29-001 — Generated-comment cleanup deleted generator state

- **Subsystem:** `tools/code_reference_comments.py`
- **Symptom:** the first synchronization pass rewrote files, then the idempotence/check pass crashed because the generator's own marker constant had disappeared.
- **Root cause:** cleanup removed every line containing `Gridelyx local reference:` instead of only generated comment lines. The literal marker definition therefore matched its own cleanup rule.
- **Fix:** restrict removal to generated comment prefixes beginning with `# Gridelyx local reference:` or `// Gridelyx local reference:` followed by the canonical repository URL.
- **Verification:** synchronization run `33249273662` completed `--fix`, `--check`, and `git diff --check` successfully.
- **Status:** **Resolved**.
- **Prevention:** every source-rewriter must pass a second no-op/idempotence execution before it can commit changes.

### FIX-2026-08-29-002 — Synchronizer staging failed on unmatched extension pathspec

- **Subsystem:** `.github/workflows/code-reference-comments.yml`
- **Symptom:** generated changes were valid but the commit step failed while staging an extension list that included a source type not present in the repository.
- **Root cause:** the workflow treated optional filename globs as guaranteed pathspec matches.
- **Fix:** replace the fragile extension-by-extension staging list with complete staging of the clean workflow worktree after generated changes are validated.
- **Verification:** run `33249273662` successfully created and pushed commit `710efff1be41cf44c1e1e82e87f94b5df235c62c`.
- **Status:** **Resolved**.
- **Prevention:** staging logic must not assume every supported language currently exists in the tree.

### FIX-2026-08-29-003 — GitHub rejected bot updates to workflow definitions

- **Subsystem:** code-reference synchronization / GitHub Actions trust boundary
- **Symptom:** run `33249079667` created a valid 55-file commit but GitHub rejected the push because the bot attempted to update `.github/workflows/advanced-ci.yml` without workflow permission.
- **Root cause:** the normal `GITHUB_TOKEN` had `contents: write`, but GitHub separately protects creation/modification of workflow definitions.
- **Fix:** split synchronization into two trust domains: validate the complete generated state, print the workflow-only patch, restore `.github/workflows`, then bot-commit only non-workflow changes. Workflow files are changed only through a privileged repository write path.
- **Verification:** run `33249273662` succeeded and pushed the non-workflow patch; protected workflow files were not included.
- **Status:** **Resolved**.
- **Prevention:** automation must never grant itself authority to rewrite its own workflow definitions.

### FIX-2026-08-29-004 — Bedrock canonical runtime deleted after rename

- **Subsystem:** Bedrock scripting runtime / Platform CI
- **Symptom:** Platform CI run `33249273717`, job `99092011002`, failed `tools/bedrock_check.py` with `missing bedrock/addon/behavior_pack/scripts/gridelyx_runtime.js`.
- **Root cause:** commit `23ac32a5b7040218042fdcfe5456b99cd8528e72` established the canonical Gridelyx runtime module, then commit `c6f5eb97f6f06c923a092890ad281204cc5144a8` deleted that same 106-line module seven seconds later while its dependants remained.
- **Fix:** restore `bedrock/addon/behavior_pack/scripts/gridelyx_runtime.js` at the canonical Gridelyx/GLXB v2 state.
- **Verification:** Bedrock run `33287033311` progressed beyond the runtime-file check and exposed the next independent missing native-header failure.
- **Status:** **Resolved**.
- **Prevention:** rename cleanup must validate inbound references before deleting a supposed old path.

### FIX-2026-08-29-005 — Public clean-build workflow rejected before job creation

- **Subsystem:** `.github/workflows/public-clean-build.yml`
- **Symptom:** run `33249273282` concluded `failure` with zero jobs created.
- **Root cause:** `jobs.clean-room-java.env` used `${{ runner.temp }}` before a runner existed; that context is not valid at job-level `env` evaluation.
- **Fix:** export `GRADLE_USER_HOME=$RUNNER_TEMP/gridelyx-empty-gradle-home` through `$GITHUB_ENV` from a running shell step immediately after checkout.
- **Verification:** run `33287033324` created a real job, configured the isolated Gradle home, installed the toolchain and reached source/build-lock validation.
- **Status:** **Resolved**.
- **Prevention:** runner-derived paths are initialized from steps, not pre-run job metadata.

### FIX-2026-08-29-006 — Early CI failure produced secondary artifact/cache warnings

- **Subsystem:** CI post-processing
- **Symptom:** artifact upload found no build outputs and Gradle caching reported no path after an earlier validation step terminated the build.
- **Root cause:** these are cascading effects of a pre-build failure, not independent artifact or cache root causes.
- **Fix:** repair the upstream validation failure and retain `if-no-files-found: warn` evidence behavior.
- **Verification:** the same warning pattern followed the later build-lock failure, confirming it is downstream rather than causal.
- **Status:** **Resolved** as classification/handling; no warning suppression required.
- **Prevention:** root failures and consequential warnings are logged separately.

### FIX-2026-08-29-007 — Source synchronization can normalize line endings

- **Subsystem:** `tools/code_reference_comments.py`
- **Symptom:** synchronization produced an unrelated line-ending-only diff in a Windows batch file.
- **Root cause:** `Path.read_text()` used universal-newline translation and the writer forced `newline="\n"`.
- **Fix:** preserve raw input newlines, transform a normalized logical representation, restore the original consistent newline convention on write, and reject mixed newline conventions instead of silently normalizing them.
- **Verification:** **pending** a two-pass synchronizer run after the permanent generator repair lands.
- **Status:** **Fixed / verification pending**.
- **Prevention:** text rewriters preserve encoding and newline conventions unless normalization is explicitly their purpose.

### FIX-2026-08-30-008 — Canonical native bridge header deleted during cleanup

- **Subsystem:** native bridge / Bedrock contract validation
- **Symptom:** Bedrock run `33287033311`, job `99191903872`, failed with `missing native/cpp/include/gridelyx_native.h` after the runtime restoration succeeded.
- **Root cause:** commit `0219800d995822d0858d9fdf6bc6256d48f1f925` added the canonical header, then commit `c96b30081e260fad09562d7eaeabdde995ac0b6c` removed it as if it were retired even though active bridge contracts still referenced it.
- **Fix:** restore the canonical ABI v2 header with `gridelyx_abi_version`, `gridelyx_protocol_version`, shared-memory handle/snapshot declarations and exported `gridelyx_shm_*` functions.
- **Verification:** **pending** Bedrock and Native CI after the corrective commit.
- **Status:** **Fixed / verification pending**.
- **Prevention:** cleanup/deletion changes must pass inbound-reference and contract checks before canonical files are removed.

### FIX-2026-08-30-009 — Generated code-reference edit invalidated master Gradle lock

- **Subsystem:** build reproducibility / code-reference synchronizer
- **Symptom:** Platform CI run `33287033314` and Public Clean Build run `33287033324` both failed because canonical `build.gradle` blob `c56cf6ff32d31c95d7a1ee5a2a779f26852d5458` did not match locked `daa82666eefb4472471b250b70e2505e3fad95c9`.
- **Root cause:** code-reference synchronization commit `710efff1be41cf44c1e1e82e87f94b5df235c62c` modified the canonical Gradle file without refreshing `platform/master-build.lock.json`.
- **Fix:** refresh the lock to `c56cf6ff32d31c95d7a1ee5a2a779f26852d5458`; additionally, when the code-reference generator itself changes canonical `build.gradle`, its workflow now refreshes and re-checks the lock in the same transaction.
- **Verification:** **pending** Platform CI and Public Clean Build.
- **Status:** **Fixed / verification pending**.
- **Prevention:** generated changes to lock-governed inputs must update their derived lock atomically.

### FIX-2026-08-30-010 — Markdown linkifier remained wired to merged feature branch

- **Subsystem:** `.github/workflows/markdown-linkify.yml` / Documentation CI
- **Symptom:** Documentation CI run `33287033327` found dozens of resolvable prose targets still non-clickable even though a linkification workflow existed.
- **Root cause:** after the feature branch was merged, the workflow still listened for pushes to `gridelyx-rebrand-reload-orchestrator` and still pushed generated Markdown back to that branch instead of `main`.
- **Fix:** retarget push/PR automation to `main`, push synchronized Markdown to `main`, and serialize it with the code-reference synchronizer using a shared repository-autofix concurrency group.
- **Verification:** **pending** Markdown Linkify and Documentation CI.
- **Status:** **Fixed / verification pending**.
- **Prevention:** branch retirement/merge checklists must migrate every workflow trigger and push destination.

### FIX-2026-08-30-011 — Bot-authored synchronization commits do not trigger chained Actions

- **Subsystem:** GitHub Actions orchestration
- **Symptom:** code-reference bot commit `bb26fd2e9d43dc453401a951c27d411da831e2f3` became `main` but produced zero follow-up workflow runs.
- **Root cause:** GitHub suppresses most new workflow runs created by pushes made with a repository `GITHUB_TOKEN`, preventing recursive automation loops.
- **Fix:** do not rely on bot-push recursion. Autofix workflows are serialized from the original authorized push and check out the current branch head when they actually execute; a final authorized verification commit is used after generated state stabilizes.
- **Verification:** **pending** the serialized autofix cycle and final verification pass.
- **Status:** **External limitation** with repository-side orchestration mitigation.
- **Prevention:** workflow chains must not assume a `GITHUB_TOKEN` push emits a second push-triggered workflow graph.

### FIX-2026-08-30-012 — Stakeholder dashboard retained link to deliberately removed marker

- **Subsystem:** documentation integrity
- **Symptom:** Documentation CI reported `docs/STAKEHOLDER_DASHBOARD.md:54` linking to missing `../vault/REMOTE_BINARY_IMPORT_PENDING.md`.
- **Root cause:** commit `a06f64c7ed7258f606375a225c31c210e2f40c39` deliberately removed the obsolete pending marker, but the dashboard retained the old link and old state description.
- **Fix:** point the health indicator at `vault/README.md` and describe the current acquisition-only/no-redistributed-binaries policy rather than recreating an obsolete marker.
- **Verification:** **pending** Markdown link validation.
- **Status:** **Fixed / verification pending**.
- **Prevention:** deletion of documentation-state markers must include repository-wide inbound-link validation.

## Verification evidence

- Code-reference synchronization success: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273662`
- Code-reference protected-workflow push rejection: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249079667`
- First Platform CI Bedrock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273717`
- First Public Clean Build pre-job failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273282`
- Bedrock native-header follow-on failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033311`
- Platform build-lock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033314`
- Public Clean Build build-lock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033324`
- Documentation link-state failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033327`
- Successful generated non-workflow synchronization commit: `https://github.com/IAMACESIRX/gridlyx/commit/710efff1be41cf44c1e1e82e87f94b5df235c62c`

## Entry template

```text
### FIX-YYYY-MM-DD-NNN — Short failure name

- Subsystem:
- Symptom/evidence:
- Root cause:
- Fix:
- Verification:
- Status:
- Prevention:
```
