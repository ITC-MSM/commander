# backlog-loop

Point an agent at a set or a backlog and let it deliver a stream of pull requests — one per
self-contained unit of work, each independently reviewed before it opens.

This is the human-facing guide. The files next to it (`SKILL.md`, `planner-prompt.md`,
`worker-prompt.md`, `reviewer-prompt.md`) are instructions for the agents and aren't meant to be read
top-to-bottom.

## Quick start

```
/backlog-loop --set ECL --units 3
```

That plans a queue for Lorwyn Eclipsed, works through three units, and stops. Each unit becomes one
branch and one PR. Drop `--units` to run the whole set.

You can also point it at a backlog file:

```
/backlog-loop backlog/sets/innistrad-crimson-vow/cards.md
```

**Start small.** A first run with `--units 1` tells you whether the handoffs work on your machine before
you commit an evening to it.

## What you get

A PR per unit, each one:

- a batch of ~5 cards that compose existing primitives, or a single card that needed a new one
- gated green (`just build`, or `just test` when engine behavior changed)
- reviewed by an agent that did not write it, against `docs/sdk-design-principles.md`
- with a body that says what was checked **and what wasn't** — no manual playthrough, no UX pass from
  both seats, no e2e

Nothing merges. Nothing is pushed to `main`. You review and merge as usual.

## How it works

Three stages, each a fresh agent:

| Stage | When | What it does |
|---|---|---|
| **plan** | once per run | works out what's missing, splits it into units, writes a ledger |
| **implement** | per unit | own worktree, cards via `add-card`, one gate run, pushes a branch — no PR |
| **review** | per unit | different agent, same worktree, reviews the branch and opens the PR |

The session you're talking to is only a dispatcher. It never reads a diff, a test log, or a card file —
just a short verdict from each stage. That's what lets a long run stay coherent instead of degrading as
the context fills.

Only one agent runs at a time. That's deliberate: `scripts/gradle-locked` serializes every build on the
machine, and three concurrent worktree builds have OOM'd the Kotlin daemon here before. More parallelism
would mostly buy you queueing.

## Options

| Flag | Effect |
|---|---|
| `--set <CODE>` | work through a set's missing cards |
| `<backlog-file>` | work through a `cards.md` or a topic doc's `- [ ]` items |
| `--units N` | stop after N units (default: drain the queue) |
| `--include-blocked` | also queue cards that need engine work — off by default |

Cards needing a new mechanic are **skipped** unless you pass `--include-blocked`. They usually want a
design call from you, and that's `add-feature` territory rather than something to decide unattended.

## Following a run

Progress lives in `.claude/loop-runs/<run-id>.md` (gitignored). One line per unit:

```
- [x] u01 | batch | Adept Watershaper, Ajani Outland Chaperone, … | PR #1511
- [r] u02 | batch | Elvish Handservant, Fallowsage, …             |
- [!] u03 | solo  | Gilt-Leaf Ambush | blocked: 1 blocking finding, see review.md
- [-] u04 | solo  | Bramblewood Paragon | needs-feature: Graft
```

`[ ]` pending · `[~]` implementing · `[r]` in review · `[x]` PR opened · `[!]` needs you · `[-]` skipped

The ledger is the run's memory, not the conversation — so if a session is interrupted or compacted, point
a new one at the ledger and it picks up where it stopped.

## When it stops early

- **A collision.** Another branch or PR is already touching that set. Both would edit the same
  `snapshots/cards/<SET>.json`, so the planner refuses rather than racing. Pick a different set.
- **A blocked unit.** The reviewer found something blocking. No PR; the branch and a `review.md` are left
  in the worktree. The run continues with the next unit.
- **Three failures in a row.** Treated as environmental — gradle lock contention, a broken `main`, an
  expired `gh` token — and the whole run stops rather than burning 30-minute lock slots.

Failed units are never retried automatically. A retry costs a full gate slot on a problem the dispatcher
can't see, so that call is left to you.

## Afterwards

Each unit leaves its worktree behind, since its branch is pushed and nothing is lost. Once the PRs are
merged:

```
git worktree list
git worktree remove <path>
git worktree prune
```

## Caveats worth knowing

- **Cards are not played.** No manual playthrough, no UX check from both seats. Every PR body says so.
  For anything with an interesting trigger or targeting flow, that pass is still yours.
- **The triage is a prediction.** The planner uses `just coverage` to guess which cards compose from
  existing primitives. It's non-authoritative — a card can turn out to need a new primitive mid-unit, in
  which case it's dropped from the batch and reported rather than quietly implemented badly.
- **`CONTRIBUTING.md` means what it says about agent batches.** This automates the *pipeline*, not the
  judgment. The independent review stage exists so PRs arrive pre-filtered, not so they arrive unread.
