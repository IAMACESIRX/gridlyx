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
- **Verification:** later synchronization run `33249273662` completed `--fix`, `--check`, and `git diff --check` successfully.
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
- **Symptom:** run `33249079667` created a valid 55-file commit but `git push` was rejected with GitHub refusing a GitHub App update to `.github/workflows/advanced-ci.yml` without workflow permission.
- **Root cause:** the normal `GITHUB_TOKEN` had `contents: write`, but GitHub separately protects creation/modification of workflow definitions.
- **Fix:** split synchronization into two trust domains: validate the complete generated state, print the workflow-only patch, restore `.github/workflows`, then bot-commit only non-workflow changes. Workflow files are changed only through a privileged repository write path.
- **Verification:** run `33249273662` succeeded and pushed the non-workflow patch; the protected workflow files were not included in the bot commit.
- **Status:** **Resolved**.
- **Prevention:** automation must never grant itself authority to rewrite its own workflow definitions.

### FIX-2026-08-29-004 — Bedrock canonical runtime deleted after rename

- **Subsystem:** Bedrock scripting runtime / Platform CI
- **Symptom:** Platform CI run `33249273717`, job `99092011002`, failed `tools/bedrock_check.py` with `missing bedrock/addon/behavior_pack/scripts/gridelyx_runtime.js`.
- **Root cause:** commit `23ac32a5b7040218042fdcfe5456b99cd8528e72` established the canonical Gridelyx runtime module, then commit `c6f5eb97f6f06c923a092890ad281204cc5144a8` deleted that same 106-line module seven seconds later. `main.js`, `bedrock_check.py`, and CI continued to reference it.
- **Fix:** restore `bedrock/addon/behavior_pack/scripts/gridelyx_runtime.js` exactly at the canonical Gridelyx/GLXB v2 state, including script-event dispatch, built-in capability reporting, and lifecycle subscribe/unsubscribe behavior.
- **Verification:** **pending** the Platform CI/Bedrock checks triggered by the corrective commit.
- **Status:** **Fixed / verification pending**.
- **Prevention:** rename cleanup must validate inbound references before deleting the supposed old path; canonical runtime files must be protected by `bedrock_check.py` and code-reference analysis.

### FIX-2026-08-29-005 — Public clean-build workflow rejected before job creation

- **Subsystem:** `.github/workflows/public-clean-build.yml`
- **Symptom:** run `33249273282` concluded `failure` with zero jobs created.
- **Root cause:** `jobs.clean-room-java.env` used `${{ runner.temp }}`. GitHub's context-availability contract does not expose the `runner` context in `jobs.<job_id>.env`; `runner` only exists after a job has been assigned to a runner.
- **Fix:** remove the pre-run `runner.temp` expression and, immediately after checkout, export `GRADLE_USER_HOME=$RUNNER_TEMP/gridelyx-empty-gradle-home` through `$GITHUB_ENV` from a running shell step.
- **Verification:** **pending** the Public Clean Build triggered by the corrective workflow commit.
- **Status:** **Fixed / verification pending**.
- **Prevention:** workflow expressions must use only contexts valid at their exact YAML key; runner-derived paths are initialized from a step, not pre-run job metadata.

### FIX-2026-08-29-006 — Early Platform CI failure produced secondary artifact/cache warnings

- **Subsystem:** Platform CI post-processing
- **Symptom:** after the Bedrock contract failure, artifact upload found no build outputs and Gradle caching reported no path to save.
- **Root cause:** these were downstream effects of Platform CI terminating before the build stages, not independent build or cache defects.
- **Fix:** repair the upstream Bedrock runtime regression rather than suppressing the warnings.
- **Verification:** **pending** successful progression beyond the Bedrock validation stage.
- **Status:** **Fixed / verification pending**.
- **Prevention:** the fix log distinguishes root failures from cascaded warnings to avoid treating consequences as separate defects.

### FIX-2026-08-29-007 — Source synchronization can normalize line endings

- **Subsystem:** `tools/code_reference_comments.py`
- **Symptom:** synchronization produced an unrelated line-ending-only diff in a Windows batch file during the generated patch sequence.
- **Root cause:** the current rewriter reconstructs text with `\n` and writes with `newline="\n"`; this is unsafe for source files whose canonical worktree representation is CRLF.
- **Fix:** update the rewriter to preserve each file's original newline convention and remove the temporary runtime self-repair step once the corrected generator is committed.
- **Verification:** **pending** a two-pass synchronization with no unrelated newline-only changes.
- **Status:** **Open**.
- **Prevention:** text-rewrite tools must preserve encoding/newline conventions unless normalization is their explicit purpose.

## Verification evidence

- Code-reference synchronization success: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273662`
- Code-reference protected-workflow push rejection: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249079667`
- Platform CI Bedrock failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273717`
- Public Clean Build pre-job failure: `https://github.com/IAMACESIRX/gridlyx/actions/runs/33249273282`
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
