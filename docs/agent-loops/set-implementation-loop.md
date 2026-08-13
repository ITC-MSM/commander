# Set implementation loop (Claude Code `/loop`)

Ships every unimplemented card in a set, one reviewed PR at a time, in a Claude Code session.

`/loop [interval] <prompt>` re-fires the prompt on a schedule; omit the interval and the model
self-paces via `ScheduleWakeup`. Self-paced is the right mode here — a card takes minutes, a
feature can take far longer, and a fixed interval would either thrash or idle.

The prompt fires **unchanged** every iteration, so it must re-orient from real state (open PRs,
current branch) rather than carry memory between turns.

## The command

Substitute the set name for `<SET>` in both places.

```
/loop Ship cards for MTG set <SET> one PR at a time. Orient first — run `gh pr list --author @me --state open --json number,title,reviewDecision,statusCheckRollup` and check the current branch — then advance exactly ONE step:

1. No open PR of ours → pick the next unimplemented cards from <SET> (compare the set's card list against `mtg-sets` definitions). Group up to five that all compose from existing Effects.*/Patterns.* into one batch and implement them with /add-card, preferring cards sharing a colour, mechanic, or cycle. A card that needs a large new engine/SDK capability is NOT batchable — give it its own PR via /add-feature. Verify once for the batch, then open the PR.
2. Open PR with no review yet → run /review-changes on it.
3. Open PR with unresolved review findings → fix them, re-verify, push.
4. Open PR clean and checks green → `gh pr merge --squash --delete-branch`, then switch to main and pull.

Rules: one batch or one feature per PR, never two in flight. A card that turns out to need new SDK vocabulary drops out of the batch — reset its commit, note it, ship the rest. Run gates through `just`, never raw ./gradlew, and gate once per batch rather than per card. Each card still gets its own definition file and its own scenario test file — never a shared batch test. Never revert or stash changes you didn't make; if someone else's work breaks the build, report it and stop. When every card in <SET> is implemented, say so and stop the loop.
```

## Why batches

`CONTRIBUTING.md` sets the shape: cards built entirely from existing `Effects.*` / `Patterns.*` may share
a PR, while a card introducing new engine vocabulary gets one to itself with tests for the primitive. For
the loop that also means one 30-minute gate slot covers five cards instead of one, which is most of the
wall-clock cost of a run.

The rule that does **not** relax: each card keeps its own definition file and its own
`{CardName}ScenarioTest.kt`. Batching is a PR-shape decision and never merges two cards into one artifact
(AGENTS.md → Hard rules).

## Caveats

**Context grows in your session.** Every iteration runs in the orchestrating session, so a
200-card set will fill the window. The [`backlog-loop`](../../.agents/skills/backlog-loop/SKILL.md)
skill exists for exactly this shape and keeps the orchestrator flat by spawning a fresh
implement → review → correct agent per unit. Rough split:

| Want | Use |
|------|-----|
| Watch each step, interject, correct course | `/loop` |
| Unattended burn-down of a whole set | `backlog-loop` |

**Step 1 re-derives the work list every iteration.** The tail of a set is where the hard cards
cluster, so the loop can keep re-picking whatever it judges smallest and stall on a genuinely
blocked card. If the set has a backlog file, point step 1 at it so progress is recorded on disk
instead of re-inferred.

## See also

- [Codex `/goal` variant](set-implementation-goal.md) — same workflow, different harness
