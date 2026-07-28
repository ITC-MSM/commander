# Engine AI — How to Measure a Change

How to run the arena, how to read a report, and what does and does not count as evidence that the
AI got better. Built in Phase 1 of [`backlog/engine-ai-improvement.md`](../../backlog/engine-ai-improvement.md).

The numbers themselves live in [`baseline-metrics.md`](baseline-metrics.md).

---

## The one-paragraph version

Two agents play N games. Each **pair** is the same two decks and the same game seed played twice
with the seats swapped, so the first-player advantage and the shuffle cancel out. The number that
decides anything is the **pair win share** and its bootstrap interval. **A change that cannot clear
53% over 1,000 games is not a demonstrated improvement** — quote the interval, never the point
estimate alone.

```bash
just arena v0 blb-advisors 1000      # head-to-head, the merge gate
just arena-gauntlet 200              # everyone vs everyone + the pairwise matrix
```

---

## Running it

```
just arena A B [GAMES] [SET] [SEED]
```

| Purpose | Games | Roughly | CI half-width at parity |
|---|---|---|---|
| Smoke — "did I break it" | 100 | 25 s | ±5 pp |
| Directional | 300 | 70 s | ±3 pp |
| **Merge gate** | **1,000** | **3.5 min** | **±0.8 pp** |
| Publish | 3,000 | 10 min | ±0.4 pp |

Wall clock is measured on an 8-core M1 Pro at ~5 games/sec. **This is ~100× cheaper than the plan
assumed**, because the plan budgeted a 2 s `DecisionBudget` that does not exist yet; today the AI
spends ~1.6 s of wall clock on a whole game. When Phase 4b lands a real budget, re-measure this
table before promising anyone a 1,000-game gate — it is the budget, not the game count, that makes
an arena expensive.

Agents are named in `ai/src/test/kotlin/com/wingedsheep/ai/arena/ArenaAgent.kt`:

| Name | What it is |
|---|---|
| `v0` | `AiProfile.LEGACY_V0` — **the permanent reference opponent** |
| `current` | whatever `AIPlayer.create(registry, playerId)` builds today |
| `production` | what a player actually faces: BLB + ONS card advisors |
| `blb-advisors` / `ons-advisors` | v0 plus one advisor module |
| `v0-blind` | all evaluation weights zero. Not playable — it is the harness's own control |

Results land in `benchmarks/arena/<timestamp>-<a>-vs-<b>/` (gitignored): `results.csv` is one row
per game, `summary.md` is the report below.

---

## Reading a report

```
--- ARENA: v0 vs blb-advisors ---
Games:        1000 (500 pairs), set=BLB, seed=20260727
Record:       500W-500L-0D for v0

Pair score:   +0.000  CI [-0.014, 0.016]   (0 = parity)
Pair win %:   50.0%  CI [49.3%, 50.8%]  <- the merge gate
Game score %: 50.0%  Wilson [46.9%, 53.1%]  (unpaired, draws = 1/2)

Seat 0 wins:  510 / 1000 (51.0%) — first-player advantage, cancelled by pairing
Completed:    1000 / 1000 (100.0%)
Illegal acts: 945 (actions the processor rejected — should be 0)
```

- **Pair score** — per pair, +1 if agent A swept it, 0 if they split, −1 if A was swept. Draws land
  on the halves. The interval is a **percentile bootstrap over 2,000 resamples of whole pairs**,
  seeded, so rerunning the analysis on the same games gives the same interval.
- **Pair win %** is that score rescaled so 50% is parity. **This is the merge gate.**
- **Game score %** is the same games scored *unpaired*, with a Wilson interval. It is reported for
  comparability with older benchmarks and as a sanity check — the two point estimates should agree,
  and the paired interval should be much tighter. On the run above it is **4× tighter** (±0.8 pp vs
  ±3.1 pp). If they ever *disagree*, something is wrong with the pairing, not with the agent.
- **Seat 0 wins** is the diagnostic that pairing is doing its job. It is normal for this to sit
  away from 50% — BLB sealed reads ~46–51% depending on the seed — and it does not bias the result,
  because both agents sit in both seats.
- **Illegal acts** is a bug counter, not a metric. Every entry is an action the AI proposed and the
  processor rejected; the run recovers and continues so the histogram sees all of them.

### The promotion rule

A new version must:

1. beat `v0` with the **lower CI bound above 50%**, *and*
2. beat the **immediately preceding version** on the same terms, *and*
3. not lose to **any** gauntlet member worse than 45%.

Rule 3 is why `just arena-gauntlet` prints the full pairwise matrix and not just an Elo. MTG agents
are frequently non-transitive — an aggressive agent beats a controlling one that beats a midrange
one that beats the aggressive one — and a single rating erases exactly that structure. The
Bradley–Terry numbers underneath the matrix are a convenience; `ArenaStatsTest` contains a
rock-paper-scissors case where all three agents rate 1500 despite every matchup being 70/30.

---

## What makes the scoreboard trustworthy

Four properties, each with a test that fails if it stops holding. They are in the always-on
`:ai:test` suite, not behind the benchmark flag, because a broken scoreboard is worse than none.

| Property | Test | Why |
|---|---|---|
| No seat or seed leak | `ArenaHarnessTest` — a `v0` mirror is **exactly** 50%, CI `[0.000, 0.000]` | With the same agent on both seats, the two games of a pair are literally the same game. Anything other than an exact mirror is a defect, and would otherwise hide inside a confidence interval |
| Determinism | same test — same seed replays identically at 8 threads and at 1 | `GameSimulator.isResolving` is mutable instance state; a shared `AIPlayer` would corrupt its own recursion guard |
| It can tell agents apart | `just arena v0 v0-blind 200` → **200-0** | A harness that reports 50% for everything is indistinguishable from a broken one. This is the control |
| `v0` has not drifted | `FrozenBaselineTest` — golden action-stream hash | `v0` is the permanent reference. If it moves, every historical number silently stops meaning what it said |

`FrozenBaselineTest` plays one fixed game: 24 Mountains and four vanilla Portal creatures, fixed
seed, `LEGACY_V0` on both seats, SHA-256 over every action and decision. The deck is deliberately
all-vanilla so the hash tracks *AI* behaviour rather than going red every time somebody implements
a card. If it fails and you did not mean to change how `v0` plays, you have found real drift; the
test's KDoc says when re-blessing is legitimate.

---

## What the arena does **not** measure

Say these out loud rather than letting a reader assume otherwise.

- **Mulligans.** `skipMulligans = true`, because a mulligan decision would make a seed
  irreproducible. Mulligan quality needs its own A/B.
- **Deck diversity within a pair.** Both seats get the *same* 40-card sealed decklist (they still
  draw different shuffles of it). Lowest variance, and it matches what `AdvisorBenchmark` measured —
  but it means the arena tests symmetric matchups only.
- **Multiplayer.** `just arena` is two seats and always will be — the paired swap has no meaning at
  a bigger table. Pods have their own harness; see [the pod arena](#the-pod-arena) below.
- **Latency per budget tier.** There are no tiers until Phase 4b.
- **Anything about a real player.** The reference opponent is another bot.

---

## The pod arena

Two-player pairing does not generalize. At a bigger table there is no "swap the seats" — there are
N seats, N! assignments, and no reason to believe two agents should split a pod evenly. So the pod
arena asks a different, well-posed question: **can one agent beat a field of another?**

```bash
just arena-pod ffa3 current v0-blind 300     # 3-player free-for-all
just arena-pod ffa4 current v0-blind 300     # 4-player free-for-all
just arena-pod 2hg  current v0-blind 300     # Two-Headed Giant, 2v2 (CR 810)
```

Agent A takes one seat and agent B takes all the others. The estimator is the multiplayer
generalization of the paired swap: a **rotation group** plays the same decks and the same seed once
per cyclic shift of the assignment, so A occupies every team position exactly once and turn-order
advantage cancels. Groups are the resampling unit for the bootstrap, exactly as pairs are in the
head-to-head arena.

> **The null is 1/teams, not 50%.** One seat in a three-way field is a **33.3%** proposition; four-way
> is **25%**; 2HG is **50%** because there are two teams. A 34% pod result is *parity*, not a rout.
> Every line of the report quotes the null next to the number for exactly this reason.

```
--- POD ARENA (ffa3): v0 vs a field of v0-blind ---
Games:        300 (100 rotation groups x 3), set=BLB, seed=20260727
Record:       268 wins for v0, 20 for the field, 12 with no winner

Win share:    89.3%  CI [85.0%, 93.0%]   vs null 33.3%  <- the gate
Decisive:     93.1% of the 288 games that produced a winner

Wins by team position: 96, 90, 82 — turn-order advantage, cancelled by the rotation
```

- **Win share** is A's share of *all* games; unfinished games count for nobody and drag it down for
  both sides. **Decisive** is the same number over the games that produced a winner. Read them
  together: a change that wins more but finishes less is something you want to see.
- **Wins by team position** is the pod analogue of "seat 0 wins" — the diagnostic that the rotation
  is doing its job, not a result.
- **Illegal acts / exceptions** matter more here than in the duel arena. Multiplayer is the
  least-exercised engine path in the repo, and a pod run is the cheapest crash finder for it.

### What makes it trustworthy

Same shape as the head-to-head guarantees, in `PodArenaHarnessTest` (always-on):

| Property | Test |
|---|---|
| No seat or seed leak | a `v0` mirror is **exactly** the null share — with the same agent everywhere, a group's games are one game relabelled, so a decisive group gives A exactly one win |
| Pod games are real games | one FFA3 and one FFA4 game played to a natural finish, no rejected actions, no exceptions, no wedge |
| It can tell agents apart | an evaluating agent beats a field of `v0-blind` |

The always-on runs are capped by **actions**, not turns — a late pod position is expensive (three
or four growing boards, and the Strategist simulates every candidate against all of them) and an
uncapped mirror spends minutes per game proving what a few hundred actions already proved. Full
length pod games are what `just arena-pod` is for.

### Two traps this harness had to work around

Both are real engine behaviours, not harness bugs, and anything else that drives multiplayer games
will hit them:

1. **`GameState.turnNumber` is a round counter that stops advancing after the first elimination.**
   `TurnManager.startTurn` only increments it when `turnOrder.first()` begins a turn, and
   `turnOrder` keeps eliminated players — so once seat 0 is knocked out it never moves again. Any
   progress detector or length cap keyed on it declares a healthy three-way endgame wedged forever.
   The runner counts player-turn handovers instead.
2. **A pod round costs several turns' worth of actions.** Even before an elimination, one
   `turnNumber` at a four-seat table is four player turns, so a flat "300 actions without a turn
   change means stuck" threshold fires on a game that is making perfectly good progress.

---

## The puzzle suite

The arena tells you *that* something regressed. A puzzle tells you *what*.

```bash
just arena-puzzles              # the gate — always-on, seconds
just arena-puzzles-compare      # the same 48 across v0 / production / v0-blind
```

48 hand-authored positions in `ai/src/test/kotlin/com/wingedsheep/ai/puzzles/`, 8 categories × 6.
Each builds a board with `ScenarioTestBase`, asks the AI for **one** move, and asserts a predicate
over it. Current per-category numbers: [`baseline-metrics.md`](baseline-metrics.md#phase-2--puzzle-baselines).

### The gate is `KNOWN_FAILURES`, not 48/48

`PuzzleSuiteTest` asserts the failing-id set **equals** a committed set. Today's AI solves 39 of 48,
and a suite pinned to 48/48 would be red forever and therefore ignored.

Equality — not "is a subset of" — is the point. It flags a regression *and* an unexpected fix:

- **A new id appears** → you broke something. The report names the puzzle and prints the move.
- **An id no longer fails** → the test goes red until you delete it from `KNOWN_FAILURES`. That is
  the moment you want to notice, and the deletion is the evidence a phase actually landed.

Each entry carries a comment naming the mechanism it is waiting on, so shrinking the set is a
checklist for Phases 6, 7 and 9.

### Writing a puzzle

Put it in the right `categories/*.kt` file, give it the next id in sequence, run the suite, and add
it to `KNOWN_FAILURES` if the AI does not solve it yet.

**Assert a predicate, never an exact action.** `PuzzleMove` is the vocabulary:
`shouldCast("Murder")`, `shouldTarget("Craw Wurm")`, `shouldAttackWithAtLeast("Wind Drake")`,
`shouldAttackForAtLeast(6)`, `shouldBlock("Hill Giant", "Craw Wurm")`, `shouldNotBlock()`. Exact
`GameAction` equality breaks on harmless tie-break changes and trains you to ignore the suite.

Three things the runner enforces so a mis-built position cannot score as a pass:

- the position must leave the AI's seat holding priority with **no pending decision**;
- the chosen move must be **legal** — the arena found the AI proposing ~0.9 illegal actions per
  game, and a puzzle that "passes" on a move the engine rejects measures nothing;
- the scenario RNG is pinned (`ScenarioBuilder` otherwise seeds itself from `System.nanoTime()`).

`advanceToDeclaration(seat, step)` stops where a seat is asked to declare attackers or blockers;
`advanceToPriority(seat, step)` stops at the ordinary priority window *after* declarations, which
is where a combat trick is cast. They differ by one window and using the wrong one is the easiest
way to write a puzzle that measures the wrong decision.

### Include positive controls

Half of "holding instants" is *don't* cast the trick; the other half is *do*, in the right window.
A category made only of "don't cast" puzzles scores 100% for an AI that never casts anything.
`just arena-puzzles-compare`'s `v0-blind` column is the check on that: a category where the
zero-weight agent scores as well as the real one is not measuring the evaluator. Today that is true
of `lethal` and `blocking` — both are carried by `CombatAdvisor`'s heuristics, so they are a
regression net for *that* code, not for `BoardFeatures.kt`.
