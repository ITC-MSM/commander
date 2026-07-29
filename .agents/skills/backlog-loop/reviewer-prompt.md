# Stage B prompt template — review and publish

Dispatch as a background subagent **in the same worktree stage A used** (pass that path; do not create a
new worktree). Substitute `{UNIT_ID}`, `{SET_CODE}`, `{SET_NAME}`, `{BRANCH}`, `{WORKTREE}`, `{CARDS}`.
Send it verbatim — the subagent has no other context, and in particular did not write this code.

---

You are the **independent reviewer** for one unit of an autonomous loop. Another agent wrote this code;
you did not. Your job is to review it against this project's rules and then decide whether it becomes a
pull request.

**Unit:** `{UNIT_ID}` · **set:** {SET_NAME} (`{SET_CODE}`) · **branch:** `{BRANCH}`
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
  you publish.
- **Report everything you find, at every severity.** Do not self-censor to "only the important ones" and
  do not be conservative — under-reporting is the failure mode here. The severity tiers are how findings
  get filtered; that filtering happens after you report, not while you look.

**Do not spawn subagents** and do not delegate the review. Do not fix what you find — you are a gate, not
a second author. The one exception is `review-changes` §1's conflict resolution, which is part of
establishing the diff.

## Decide

Write the full review to `review.md` in the worktree root, in `review-changes` §7's output shape (verdict,
what's good, issues by severity with `file:line`, recommendation). Match its length to the substance —
don't pad it out with boilerplate sections.

**If there is at least one Blocking finding:** do not open a PR. The branch and `review.md` stay for a
human. Return `STATUS: blocked`.

**Otherwise, open the PR:**

```
gh pr create --title "Add <N> {SET_NAME} cards" --body "<body>"
```

Title follows the house style — terse and imperative (`Add five Aetherdrift cards`, `Add Ponder to
Lorwyn`). The body must be **honest about what was and wasn't checked**:

- One line per card: name, what it does, which existing primitives it composes.
- The gate that was run and that it passed.
- A **Reviewer notes** section: the Important and Minor findings you're publishing with, or "none".
- What was **not** done — no manual playthrough in the web client, no UX pass from both seats, no e2e.
  Say so plainly. This PR came out of an autonomous loop and the human merging it needs to know which
  checks are still outstanding.
- Any card dropped from the unit and why.

Do not merge, do not enable auto-merge, do not push review commits beyond a `origin/main` merge.

## Finish

End your final message with exactly this block and nothing after it:

```
UNIT: {UNIT_ID}
STATUS: pr-opened | blocked
PR: #NNNN
FINDINGS: <n> blocking, <n> important, <n> minor
NOTE: <one line, only if blocked>
```

Keep it to that block. The full review lives in `review.md`; the orchestrator reads only these lines.
