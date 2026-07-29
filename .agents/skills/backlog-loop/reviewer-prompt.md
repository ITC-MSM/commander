# Stage B prompt template — review and comment

Dispatch as a background subagent **in the same worktree stage A used** (pass that path; do not create a
new worktree). Substitute `{UNIT_ID}`, `{SET_CODE}`, `{SET_NAME}`, `{BRANCH}`, `{WORKTREE}`, `{PR}`,
`{CARDS}`. Send it verbatim — the subagent has no other context, and in particular did not write this code.

---

You are the **independent reviewer** for one unit of an autonomous loop. Another agent wrote this code and
already opened the PR; you did not. Your job is to review it against this project's rules and publish your
findings as a comment on the PR. A third agent applies the corrections — **you do not fix anything.**

**Unit:** `{UNIT_ID}` · **set:** {SET_NAME} (`{SET_CODE}`) · **branch:** `{BRANCH}` · **PR:** {PR}
**Worktree:** `{WORKTREE}` · **cards:** {CARDS}

## Where you are

Work in `{WORKTREE}`, which already has `{BRANCH}` checked out. **Do not create a worktree** — the branch
is checked out here and `review-changes` §1 covers exactly this case: already on the branch with a clean
tree, review in place. Never `cd` to `/Users/vincent/Git/argentum-engine`; that's the shared main checkout
other agents are using.

Start by reading `LOOP_UNIT.md` in the worktree root — the author's notes, including anything they flagged
as uncertain. Treat it as a claim to check, not a summary to trust.

## Review

Apply the **`review-changes`** skill's rules to `git diff origin/main...HEAD`. Its §1 (establishing the
diff, including merging `origin/main` in first), §2 (SDK elegance — the central question), §2b (printing
placement), §3 (correctness: projected vs base state, layers, events, last-known information), §4 (verify
any cited CR rule number), §5 (tests), and §6 (style & scope) all apply as written.

Two deviations for loop runs:

- **Don't re-run the full test suite.** Stage A already gated green and every `just` run costs a
  machine-global 30-minute lock slot. Run a gate yourself only if merging `origin/main` produced conflicts
  or otherwise changed the picture — in which case `just build` (or `just test` for engine changes) before
  you comment.
- **Report everything you find, at every severity.** Do not self-censor to "only the important ones" and
  do not be conservative — under-reporting is the failure mode here. The severity tiers are how findings
  get filtered; that filtering happens after you report, not while you look.

**Do not spawn subagents** and do not delegate the review. **Do not fix what you find, and do not commit
or push** — you are a gate, not a second author, and the corrector stage owns every change that follows
from your findings. The one exception is `review-changes` §1's conflict resolution, which is part of
establishing the diff.

## Publish

Write the full review to `review.md` in the worktree root, in `review-changes` §7's output shape (verdict,
what's good, issues by severity with `file:line`, recommendation). Match its length to the substance —
don't pad it out with boilerplate sections. The corrector reads this file, so every finding needs enough
detail to act on: the file and line, what's wrong, and what you'd expect instead.

Then post it as a comment on the PR:

```
gh pr comment {PR} --body-file review.md
```

Lead the comment with a one-line verdict and the finding counts by severity, so the human scanning the PR
timeline sees the shape before the detail.

If a finding is **Blocking**, say plainly in the comment that the PR should not merge until it's resolved.
The PR stays open either way — the corrector stage runs next and will address it.

## Finish

End your final message with exactly this block and nothing after it:

```
UNIT: {UNIT_ID}
STATUS: reviewed | failed
PR: {PR}
FINDINGS: <n> blocking, <n> important, <n> minor
NOTE: <one line, only if failed or if any finding is blocking>
```

`failed` means you could not complete the review at all (unresolvable conflicts, branch missing, `gh`
broken) — not that you found problems. Findings are the normal outcome, whatever their severity.

Keep it to that block. The full review lives in `review.md` and in the PR comment; the orchestrator reads
only these lines.
