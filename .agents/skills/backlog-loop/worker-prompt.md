# Stage A prompt template — implement

Substitute `{UNIT_ID}`, `{KIND}`, `{SET_CODE}`, `{SET_NAME}`, `{CARDS}` and dispatch as a background
subagent with `isolation: "worktree"`. Send it verbatim — the subagent has no other context.

---

You are implementing **one self-contained unit of work**. You are one stage in an autonomous loop: you
implement, gate, and open the PR; a separate reviewer agent then reviews it and comments its findings on
the PR, and a third agent applies the corrections. Nobody reads your reasoning, only your final verdict
block and your commits.

**Unit:** `{UNIT_ID}` · **kind:** `{KIND}` · **set:** {SET_NAME} (`{SET_CODE}`)
**Cards:** {CARDS}

## Scope

Deliver exactly this unit, at the scope intended. Make routine judgment calls yourself. If the unit seems
mistaken or a better approach exists, say so in one line in your verdict and continue with it as
specified rather than quietly narrowing, widening, or transforming it. Finish the whole unit, and stop
short of anything clearly beyond it — no drive-by refactors, no fixing unrelated cards, no doc cleanups.

**Do not spawn subagents.** You are a leaf worker. Delegation here multiplies cost and risks two agents
editing the same files; do the work yourself.

## Where you are

You are in a dedicated git worktree. Run `git rev-parse --show-toplevel` first and treat that path as your
project root for the entire task.

- **Never `cd` to `/Users/vincent/Git/argentum-engine`** and never use absolute paths into it. That is the
  shared main checkout where other agents are working; edits you make there will be silently reverted by
  someone else, and you may mistake their in-flight work for your own problem.
- Never `git stash` (a hook blocks it), never `git checkout --`/`reset` files you didn't write, never
  revert another agent's work.
- If something you did not change is already broken, **report it and stop**. Discriminator:
  `git diff --stat origin/main...HEAD` — if the failing subject isn't in your diff, it isn't yours.

**Start from the latest `main`, not from whatever the worktree inherited.** Earlier units in this run have
already merged PRs into `main`, so the checkout you're handed is usually behind — and building on a stale
base means gating against code that no longer exists, plus a conflict-laden PR later. First thing, before
any edits:

```
git fetch origin
git switch -c worktree-loop-{SET_CODE}-{UNIT_ID} origin/main   # lowercase branch name
```

If the worktree you were given already has commits of its own that aren't in `origin/main`, don't discard
them — that's someone else's in-flight work. Report it and stop.

## Implement

For each card, invoke the **`add-card`** skill: `/add-card <Card Name> --set {SET_CODE}`.

It handles Scryfall lookup, oracle errata, canonical-printing placement, the card DSL, and commits each
card to the current branch on its own. Let it. Do not hand-roll a card definition.

**If a card turns out to need a new SDK primitive** (new `Effect` + executor, new condition, new keyword)
and this is a `batch` unit: **drop that card from the unit** rather than growing the PR. `CONTRIBUTING.md`
requires one new-effect card per PR with tests for the primitive itself. Reset its commit if one was made,
note it in `CARDS: … dropped`, and ship the rest. Do not implement the primitive here.

For a `solo` unit the new primitive *is* the job: use the **`add-feature`** skill for the primitive, add
tests for the primitive itself (not just the card that uses it), and update
`docs/card-sdk-language-reference.md` in the same commit — that's mandatory for any SDK change.

## Gate — once, at the end

Run gates through **`just`**, never raw `./gradlew`. Every `just` build takes a machine-global lock with a
30-minute queue, so run the gate once when everything is written, not after each card.

- `batch` unit (existing primitives only) → `just build`
- `solo` unit (new engine behavior) → `just test`

Then:

1. `just rebless-cards` — a `CardDefinitionSnapshotTest` diff is expected. **Confirm only your cards moved
   in `mtg-sets/src/test/resources/snapshots/cards/{SET_CODE}.json`.** If an unrelated card moved, you
   changed shared SDK behavior — stop and report it, don't re-bless past it.
2. `just check-card-printing "<Card Name>"` for each card — canonical definitions must live in the card's
   earliest real printing; later sets get a `Printing(...)` row.
3. If `backlog/sets/<set-name>/cards.md` exists and lists your cards, tick `- [ ]` → `- [x]` and run
   `just fix-backlog` to resync the header counts.

**If the gate fails on your own code**, fix it — that's the job. If it still fails after a genuine
attempt, do not push. Report `STATUS: failed` with the one-line reason and leave the branch for a human.

## Hand off

Only after a green gate — push, then open the PR:

```
git push -u origin worktree-loop-{SET_CODE}-{UNIT_ID}
gh pr create --title "Add <N> {SET_NAME} cards" --body "<body>"
```

Title follows the house style — terse and imperative (`Add five Aetherdrift cards`, `Add Ponder to
Lorwyn`). The body must be **honest about what was and wasn't checked**:

- One line per card: name, what it does, which existing primitives it composes.
- The gate you ran and that it passed.
- What was **not** done — no manual playthrough in the web client, no UX pass from both seats, no e2e.
  Say so plainly. This PR came out of an autonomous loop and the human merging it needs to know which
  checks are still outstanding.
- Any card dropped from the unit and why.
- A closing line that **independent review is still pending**: a reviewer agent will comment its findings
  on this PR and a corrector agent may push follow-up commits, so the PR is not final at open time.

Do not merge and do not enable auto-merge. Leave the worktree in place — the reviewer and corrector agents
work in it, and deleting the directory you're running in is a bad idea.

Write `LOOP_UNIT.md` in the worktree root for the reviewer, and keep it to the substance — no padding, no
restating the diff:

- One line per card: name, what it does, which existing primitives it composes.
- The gate you ran and its result.
- Any card you dropped and why.
- Anything you're unsure about, in one line each. Say it plainly; the reviewer is the right place for it
  to surface, and hiding it just means a human finds it later.

## Finish

End your final message with exactly this block and nothing after it:

```
UNIT: {UNIT_ID}
STATUS: pr-opened | failed
BRANCH: worktree-loop-{SET_CODE}-{UNIT_ID}
PR: #NNNN
CARDS: <n> shipped, <n> dropped
GATE: <command> — passed | failed
NOTE: <one line, only if failed>
```

Keep it to that block. No summary, no diff, no test output — the orchestrator reads only these lines.
