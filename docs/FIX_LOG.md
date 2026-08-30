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

### FIX-2026-08-29-007 — Source synchronization normalized line endings

- **Subsystem:** `tools/code_reference_comments.py`
- **Symptom:** synchronization produced an unrelated line-ending-only diff in a Windows batch file.
- **Root cause:** `Path.read_text()` used universal-newline translation and the writer forced `newline="\n"`.
- **Fix:** preserve raw input newlines, transform a normalized logical representation, restore the original consistent newline convention on write, and reject mixed newline conventions instead of silently normalizing them.
- **Verification:** Code Reference run `33287351035` repaired the generator, generated the required patch, passed `--check` and `git diff --check`, and pushed `54ef6c13c05c91e99a1c433eb3ddfef47a566c3e` without the prior line-ending-only batch-file regression.
- **Status:** **Resolved**.
- **Prevention:** text rewriters preserve encoding and newline conventions unless normalization is explicitly their purpose.

### FIX-2026-08-30-008 — Canonical native bridge header deleted during cleanup

- **Subsystem:** native bridge / Bedrock contract validation
- **Symptom:** Bedrock run `33287033311`, job `99191903872`, failed with `missing native/cpp/include/gridelyx_native.h` after the runtime restoration succeeded.
- **Root cause:** commit `0219800d995822d0858d9fdf6bc6256d48f1f925` added the canonical header, then commit `c96b30081e260fad09562d7eaeabdde995ac0b6c` removed it as if it were retired even though active bridge contracts still referenced it.
- **Fix:** restore the canonical ABI v2 header with `gridelyx_abi_version`, `gridelyx_protocol_version`, shared-memory handle/snapshot declarations and exported `gridelyx_shm_*` functions.
- **Verification:** Native CI run `33287351065` progressed past the header check and exposed the independent missing `native/cpp/src/gridelyx_native.cpp` implementation.
- **Status:** **Resolved**.
- **Prevention:** cleanup/deletion changes must pass inbound-reference and contract checks before canonical files are removed.

### FIX-2026-08-30-009 — Generated code-reference edit invalidated master Gradle lock

- **Subsystem:** build reproducibility / code-reference synchronizer
- **Symptom:** Platform CI run `33287033314` and Public Clean Build run `33287033324` both failed because canonical `build.gradle` blob `c56cf6ff32d31c95d7a1ee5a2a779f26852d5458` did not match locked `daa82666eefb4472471b250b70e2505e3fad95c9`.
- **Root cause:** code-reference synchronization commit `710efff1be41cf44c1e1e82e87f94b5df235c62c` modified the canonical Gradle file without refreshing `platform/master-build.lock.json`.
- **Fix:** refresh the lock to `c56cf6ff32d31c95d7a1ee5a2a779f26852d5458`; additionally, when the code-reference generator itself changes canonical `build.gradle`, its workflow refreshes and re-checks the lock in the same transaction.
- **Verification:** Public Clean Build run `33287351044` and Platform CI run `33287351071` both passed the master-build-lock check at `c56cf6ff32d31c95d7a1ee5a2a779f26852d5458` before reaching later independent failures.
- **Status:** **Resolved**.
- **Prevention:** generated changes to lock-governed inputs must update their derived lock atomically.

### FIX-2026-08-30-010 — Markdown linkifier remained wired to merged feature branch

- **Subsystem:** `.github/workflows/markdown-linkify.yml` / Documentation CI
- **Symptom:** Documentation CI run `33287033327` found dozens of resolvable prose targets still non-clickable even though a linkification workflow existed.
- **Root cause:** after the feature branch was merged, the workflow still listened for pushes to `gridelyx-rebrand-reload-orchestrator` and still pushed generated Markdown back to that branch instead of `main`.
- **Fix:** retarget push/PR automation to `main`, push synchronized Markdown to `main`, and serialize it with the code-reference synchronizer using a shared repository-autofix concurrency group.
- **Verification:** Markdown Linkify run `33287351039` was triggered from `main`, waited behind the code-reference run, then checked out the current `main` head. Its later resolver failure is tracked separately as FIX-2026-08-30-013.
- **Status:** **Resolved**.
- **Prevention:** branch retirement/merge checklists must migrate every workflow trigger and push destination.

### FIX-2026-08-30-011 — Bot-authored synchronization commits do not trigger chained Actions

- **Subsystem:** GitHub Actions orchestration
- **Symptom:** code-reference bot commit `bb26fd2e9d43dc453401a951c27d411da831e2f3` became `main` but produced zero follow-up workflow runs.
- **Root cause:** GitHub suppresses most new workflow runs created by pushes made with a repository `GITHUB_TOKEN`, preventing recursive automation loops.
- **Fix:** do not rely on bot-push recursion. The two autofix workflows share one concurrency group and explicitly check out the branch ref for push runs so whichever executes second consumes the latest stabilized branch head.
- **Verification:** on the `600525442e234075a5fa9e5dcbcd1978c1b89206` cycle, Code Reference run `33287351035` pushed `54ef6c13c05c91e99a1c433eb3ddfef47a566c3e`; the queued Markdown run `33287351039` then checked out that newer bot-authored head even though the bot commit itself emitted no new workflow graph.
- **Status:** **External limitation** with verified repository-side orchestration mitigation.
- **Prevention:** workflow chains must not assume a `GITHUB_TOKEN` push emits a second push-triggered workflow graph.

### FIX-2026-08-30-012 — Stakeholder dashboard retained link to deliberately removed marker

- **Subsystem:** documentation integrity
- **Symptom:** Documentation CI reported `docs/STAKEHOLDER_DASHBOARD.md:54` linking to missing `../vault/REMOTE_BINARY_IMPORT_PENDING.md`.
- **Root cause:** commit `a06f64c7ed7258f606375a225c31c210e2f40c39` deliberately removed the obsolete pending marker, but the dashboard retained the old link and old state description.
- **Fix:** point the health indicator at `vault/README.md` and describe the current acquisition-only/no-redistributed-binaries policy rather than recreating an obsolete marker.
- **Verification:** Markdown Linkify run `33287351039` no longer reported `REMOTE_BINARY_IMPORT_PENDING.md`; its remaining failures were separate resolver-semantics defects captured in FIX-2026-08-30-013.
- **Status:** **Resolved**.
- **Prevention:** deletion of documentation-state markers must include repository-wide inbound-link validation.

### FIX-2026-08-30-013 — Markdown converter and link audit used inconsistent path semantics

- **Subsystem:** `tools/markdown_linkify.py`
- **Symptom:** Markdown Linkify run `33287351039` successfully converted 47 files, then failed its own audit with bogus broken destinations such as `gridlyx/`, `docs/`, and `community/`.
- **Root cause:** `repo_href()` rewrote a target equal to the source directory from `.` to `target.name`, producing self-links like `docs/` from inside `docs/`. Separately, `local_link_exists()` only checked source-relative paths even though conversion deliberately supports both source-relative and repository-root target resolution.
- **Fix:** represent same-directory links as `./` and make local-link validation use the same `resolve_repo_target()` semantics as conversion: source-relative first, repository-root fallback only when the target actually exists.
- **Verification:** **pending** the Markdown Linkify run triggered by the corrective commit.
- **Status:** **Fixed / verification pending**.
- **Prevention:** transformation and validation must share one resolver contract; a fixer must pass its own audit after a rewrite.

### FIX-2026-08-30-014 — Canonical C++ native implementation deleted after ABI v2 migration

- **Subsystem:** native C++ bridge / Bedrock and Native CI
- **Symptom:** Native CI run `33287351065` failed on both Linux and Windows at `tools/bedrock_check.py` with `missing native/cpp/src/gridelyx_native.cpp`.
- **Root cause:** commit `24a9a193becf94a527c32d77edec2e0ceb302d9b` implemented the canonical Gridelyx ABI v2 / GLXM shared-memory source, then commit `7342092b5002e183f5be6286674611350762ac99` deleted that canonical implementation fifteen seconds later as supposedly retired while the header, CMake/native workspace, Bedrock checks, and consumers still required it.
- **Fix:** restore the exact canonical `24a9a193` C++ implementation with cross-platform shared memory, GLXM magic/protocol validation, sequence publication/snapshot semantics, payload access and cleanup.
- **Verification:** **pending** Native CI and Platform/Bedrock contract validation.
- **Status:** **Fixed / verification pending**.
- **Prevention:** identity-cleanup deletion requires an inbound-reference graph and a successful native/Bedrock contract check before removal.

### FIX-2026-08-30-015 — Canonical Java FFM native bridge deleted after rename

- **Subsystem:** advanced Java FFM/native interoperability
- **Symptom:** Public Clean Build run `33287351044` reached real advanced compilation, then `BedrockNativeSession.java` failed because `com.example.examplemod.advanced.nativeinterop.GridelyxNativeBridge` and its `SharedMemorySession` type did not exist.
- **Root cause:** commit `d5cd0f618e3102f33271e7217f5298141fc88595` renamed/established the canonical Java Gridelyx ABI v2 bridge, then commit `f3fa4b1184f3b885afb0beae477b0791cd649897` deleted it nine seconds later as supposedly retired while `BedrockNativeSession` still imported it.
- **Fix:** restore the exact canonical `d5cd0f61` Java FFM bridge, including ABI/protocol verification, shared-memory create/open/unlink, payload publication with CRC32, sequence access and safe lifecycle close behavior.
- **Verification:** **pending** Public Clean Build and Advanced Engine CI.
- **Status:** **Fixed / verification pending**.
- **Prevention:** migration cleanup must compile all active consumers and validate inbound imports before deleting a renamed implementation.

## Verification evidence

- Code-reference synchronization success: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273662`
- Code-reference protected-workflow push rejection: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249079667`
- First Platform CI Bedrock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273717`
- First Public Clean Build pre-job failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273282`
- Bedrock native-header follow-on failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033311`
- Platform build-lock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033314`
- Public Clean Build build-lock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033324`
- Documentation link-state failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287033327`
- Verified code-reference/newline synchronization: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351035`
- Markdown resolver failure after branch wiring repair: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351039`
- Public clean build advanced Java bridge failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351044`
- Native C++ implementation failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351065`
- Platform CI follow-on Bedrock/native failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351071`
- Advanced Engine follow-on validation failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351076`
- Reachable-history publication audit success: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33287351092`
- Successful generated non-workflow synchronization commit: `https://github.com/IAMACESIRX/gridlyx/commit/54ef6c13c05c91e99a1c433eb3ddfef47a566c3e`

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
