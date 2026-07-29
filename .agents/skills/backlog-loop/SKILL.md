---
name: backlog-loop
description: Work autonomously through a set's missing cards or a backlog checklist, delivering one reviewed PR per self-contained unit of work. A planner agent builds the queue, then a fresh implement agent and a fresh review agent run each unit, so the orchestrator's context stays flat over long runs. Use when asked to "work through set X", "burn down backlog Y", or "keep shipping PRs until done".
argument-hint: --set <CODE> | <backlog-file> [--units N] [--include-blocked]
---

# Backlog loop

You are the **orchestrator**. You do not plan, implement, or review — you dispatch and you record. A
planner builds the queue once, then each unit runs through two stages, implement and review. Every stage
is a fresh subagent with its own context; the only thing that crosses back into yours is its verdict block.

The whole design exists to keep *your* context flat. A run of 40 units must cost you roughly what a run of
4 units costs. That property is the feature — protect it.

## Hard rules for the orchestrator

- **Never read a stage's output.** Not card `.kt` files, not diffs, not test logs, not `review.md`, not
  `card-status` / `coverage` output, not a subagent's transcript file. The verdict block is the answer.
- **Never run `just`, `./gradlew`, or any build.** Stages own the gates. You'd take the machine-global
  gradle lock away from the stage that needs it.
- **Never plan, implement, or review a unit yourself**, even a "quick" one. That is how the loop dies —
  one inlined card pulls Scryfall lookups, card DSL, and test output into your context, and every later
  dispatch pays for it.
- **The ledger is your memory, not this conversation.** Re-read it before every dispatch. If your context
  is compacted mid-run you must be able to resume from the ledger alone, and you can.
- **Exactly one subagent in flight at a time**: one planner per run, then two per unit (implement,
  review). `scripts/gradle-locked` serializes every build machine-wide with a 30-minute wait, and 3
  concurrent worktree builds have OOM'd the Kotlin daemon here before. Serial is the correct setting, not
  a limitation to route around.
- **Never merge and never fix review findings yourself.** The run ends with PRs open for a human.

## Step 0: Plan the run (once, delegated)

**Do not build the queue yourself.** Deriving it means reading a whole set's missing-card list plus two
`coverage` reports — hundreds of card names and a feature leaderboard. That is the single largest context
hit in the run, and it lands before any work starts. Delegate it.

Spawn one background subagent with [`planner-prompt.md`](planner-prompt.md). It runs `card-status` and
`just coverage`, intersects them, checks `git worktree list` / `gh pr list` for a collision on the same
set, partitions the work into units, and writes the ledger to `.claude/loop-runs/<run-id>.md` (gitignored).

It returns counts and a ledger path — not a card list. That is all you need:

- `planned` → report the unit count to the user, then start Step 1.
- `collision` → another branch or PR is already on this set, and you would both edit
  `mtg-sets/src/test/resources/snapshots/cards/<SET>.json`. Stop and tell the user; choosing a different
  set is their call.
- `empty` → nothing to do. Say so and stop.

The planner is worth its own stage beyond context economy: `coverage`'s free-vs-blocked call is predictive
and non-authoritative, so partitioning wants an agent actually reading the cards — and a planning mistake
is far cheaper to make there than to discover in stage A.

### Ledger format

The planner writes it; you only ever edit single lines in it.

```markdown
# Loop run: ecl-cards
source: --set ECL
policy: open-pr after independent review (never merge)
started: 2026-07-29

legend: [ ] pending · [~] implementing · [r] in review · [x] PR opened · [!] blocked, needs human · [-] skipped

## Units
- [ ] u01 | batch | Adept Watershaper, Ajani Outland Chaperone, Boggart Forager, Cloudgoat Ranger, Dawnglare Invoker |
- [ ] u02 | batch | Elvish Handservant, Fallowsage, Gilt-Leaf Ambush, Hillcomber Giant, Ink Dissolver |
- [-] u03 | solo  | Bramblewood Paragon | needs-feature: Graft
```

One unit per line. State transitions are a single-character edit plus an appended result, so updating the
ledger costs you almost nothing.

## Step 1: Two-stage dispatch loop

Repeat until no `[ ]` units remain (or `--units N` is exhausted).

**Stage A — implement.** Flip the unit to `[~]`. Spawn one background subagent, `isolation: "worktree"`,
with [`worker-prompt.md`](worker-prompt.md), values substituted. It fetches and branches off the current
`origin/main` before touching anything — units run serially, so by unit 5 the checkout it inherits is
several merged PRs behind — then implements, gates, commits, and **pushes a branch — it does not open a
PR.** Wait for its verdict.

- `branch-pushed` → go to stage B.
- `failed` → mark `[!]` with the one-line reason. No stage B; there's nothing to review.

**Stage B — review.** Flip the unit to `[r]`. Spawn a **new** subagent — a fresh context that did not
write the code — with [`reviewer-prompt.md`](reviewer-prompt.md). It reviews **in place in the same
worktree** (no new worktree; `review-changes` §1 explicitly supports reviewing on the branch when it's
already checked out and clean). It is the gate that opens the PR:

- `pr-opened` → mark `[x]` with the PR number.
- `blocked` → mark `[!]` with the reason. The branch and `review.md` stay for a human.

Then take the next `[ ]` unit.

**On failure, do not retry and do not debug.** Mark `[!]` and move on. Retrying burns a 30-minute lock
slot on a problem you cannot see from here.

**Stop the whole run and report** if three consecutive units fail — that is environmental (lock
contention, broken `main`, expired auth), not bad luck, and further dispatches will fail too.

### Why the reviewer is a separate agent

The implementer is the worst reviewer of its own work: it will re-derive the same reasoning that produced
the bug. A fresh agent reading only the diff catches what the author's context hides. It also means every
PR you hand back has had an independent pass against `docs/sdk-design-principles.md`, which is the check
`CONTRIBUTING.md` asks for before an agent-produced card batch is opened.

Review is also where to economize if you need to: review accuracy holds up well at lower effort, while
implementation is the demanding half. Don't cut effort on stage A to save tokens.

## Step 2: Final report

When the queue drains, give the user a short table: unit, kind, cards, PR number or block reason. Then the
totals — PRs opened, cards shipped, units blocked. Nothing else; the PRs speak for themselves.

Each unit leaves its worktree behind (its branch is pushed, so nothing is lost). Once the PRs are merged,
those are reclaimable with `git worktree list` → `git worktree remove <path>` → `git worktree prune`.
Mention it; don't do it during the run.

## Verdict contracts

The planner ends with exactly:

```
RUN: ecl-cards
STATUS: planned | collision | empty
LEDGER: .claude/loop-runs/ecl-cards.md
UNITS: 12 pending (10 batch, 2 solo), 4 skipped
CARDS: 52 queued, 4 skipped as needs-feature
NOTE: <one line, only if collision or empty>
```

Stage A ends with exactly:

```
UNIT: u07
STATUS: branch-pushed | failed
BRANCH: worktree-loop-ecl-u07
CARDS: 5 shipped, 0 dropped
GATE: just build — passed
NOTE: <one line, only if failed>
```

Stage B ends with exactly:

```
UNIT: u07
STATUS: pr-opened | blocked
PR: #1511
FINDINGS: 0 blocking, 2 important, 3 minor
NOTE: <one line, only if blocked>
```

If a subagent returns prose instead, take the first status word you can find and move on. Do not read its
transcript to reconstruct what happened.
