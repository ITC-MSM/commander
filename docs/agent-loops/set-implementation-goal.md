# Set implementation goal (Codex `/goal`)

The [Claude Code `/loop`](set-implementation-loop.md) workflow, ported to Codex.

`/goal` is a **builtin** in Codex (verified on 0.147.0) — there is no custom command to author.
The `goals` feature is stable and enabled by default, and `~/.codex/goals_1.sqlite` holds one
`thread_goals` row per thread: an `objective`, a status (`active` / `paused` / `blocked` /
`usage_limited` / `budget_limited` / `complete`), and an optional token budget.

That makes it a persistent, self-continuing objective rather than a prompt re-fired into fresh
context — so the text below reads as a goal with a completion criterion, not as a per-iteration
state machine.

## Skills are not discoverable by Codex

Codex only looks in `.codex/skills` and `~/.codex/skills`. This repo's skills live in
`.agents/skills/` (`.claude/skills` is a symlink to it), so `/add-card`, `/review-changes` and
friends **do not exist as commands in Codex** and must be loaded by path.

To fix that properly, mirror the Claude symlink:

```bash
mkdir -p .codex && ln -s ../.agents/skills .codex/skills
```

Codex then discovers them as real skills and the file-path list can be dropped from the
objective.

`AGENTS.md` at the repo root *is* read natively by Codex, so the project hard rules are already
in context. The few restated in the objective are there because they survive compaction better
than the file does, and they are the ones that break an unattended overnight run.

## The command

Substitute the set name for `<SET>` in both places. The batch policy and the reasoning behind it are in
[Why batches](set-implementation-loop.md#why-batches).

```
/goal Implement every unimplemented card in MTG set <SET>, one reviewed PR at a time.

These are Claude Code skills, not Codex commands — read the file when the step calls for it:
- .agents/skills/add-card/SKILL.md       implementing a single card
- .agents/skills/add-feature/SKILL.md    any capability that isn't one card
- .agents/skills/review-changes/SKILL.md reviewing a PR
- .agents/skills/verify/SKILL.md         which build/test gate to run

Repeat until the set is done:
1. Re-orient from real state — `gh pr list --author @me --state open --json number,title,reviewDecision,statusCheckRollup` and the current branch. After a compaction, re-read; never assume where you left off.
2. No PR of ours open → pick the next unimplemented cards from <SET> (diff the set's card list against the definitions under mtg-sets). Group up to five that all compose from existing Effects.*/Patterns.* into one batch and follow add-card, preferring cards sharing a colour, mechanic, or cycle. A card needing a large new engine/SDK capability is NOT batchable — give it its own PR via add-feature. Verify once for the batch per verify/SKILL.md, then open the PR.
3. PR open, unreviewed → follow review-changes on it.
4. Findings unresolved → fix, re-verify, push.
5. Clean and checks green → `gh pr merge --squash --delete-branch`, back to main, pull.

Constraints: one batch or one feature per PR, never two in flight. A card that turns out to need new SDK vocabulary drops out of the batch — reset its commit, note it, ship the rest. Build only through `just`, never raw ./gradlew, and gate once per batch rather than per card. Each card still gets its own definition file and its own scenario test file — never a shared batch test. Never revert, stash, or discard changes you did not make; if someone else's work breaks the build, report it and mark the goal blocked.

Done when every card in <SET> is implemented and merged. If a card turns on a rules question you can't confirm against the Comprehensive Rules, mark blocked rather than guessing.
```

## Checking on a long run

Without attaching to the TUI:

```bash
sqlite3 -header ~/.codex/goals_1.sqlite \
  "select status, tokens_used, time_used_seconds from thread_goals order by updated_at_ms desc limit 1;"
```

A run that stopped on its own will show `blocked`, `usage_limited`, `budget_limited`, or
`complete` rather than `active`.
