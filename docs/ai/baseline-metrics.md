# Engine AI — Baseline Metrics

The numbers every later phase of [`backlog/engine-ai-improvement.md`](../../backlog/engine-ai-improvement.md)
is budgeted against. Phase 0 produces them; each later phase appends its own section rather than
overwriting this one, so regressions stay visible.

**Measured:** 2026-07-27, Phase 0.

---

## How to reproduce

```bash
just benchmark-throughput 100 BLB     # the table below
just benchmark-random 200 BLB         # the raw-engine random-action baseline
```

`SimulationThroughputBenchmark` drives **real `AIPlayer` games on both seats** — not random
actions. Branching factor and projection cost are both state-dependent, and random play reaches
states no real game reaches, so the state distribution has to be realistic.

It discards `cores × 2` warmup games before measuring. That matters: before warmup existed, the
same code reported **838 / 1,299 / 2,332** `process()`/sec at 20 / 40 / 200 games. Any number
quoted from a run without warmup is measuring the JIT, not the engine.

**Hardware:** Apple M1 Pro, 8 logical cores, 32 GB, Corretto JDK 21.0.6. Rates are **per thread
with all 8 threads busy**, which is the condition the arena will run under. A single-threaded
number would be higher and would not describe anything we actually do.

---

## Simulation throughput

100 games, BLB sealed, 16 warmup games discarded. Two independent runs, agreeing within ±3%:

| Metric | Run A | Run B | Use |
|---|---|---|---|
| `ActionProcessor.process`, **candidate mix** (casts / activations) | 2,386/s · 0.419 ms | 2,493/s · 0.401 ms | cost of scoring one candidate |
| `ActionProcessor.process`, **as-played mix** (mostly passes) | 3,367/s · 0.297 ms | 3,413/s · 0.293 ms | **the rollout mix** |
| `GameSimulator.simulate` (incl. `resolveToQuietState`) | 1,288/s · 0.776 ms | 1,365/s · 0.733 ms | what the Strategist pays per candidate today |
| `LegalActionEnumerator.enumerate` | 2,419/s · 0.413 ms | 2,550/s · 0.392 ms | paid at every priority window |
| `StateProjector.project`, **cold** | 47.1 µs | 46.3 µs | 11.2–11.5% of one candidate `process()` |

`simulate` costs ~1.8× a candidate `process()` — that ratio is the auto-pass work hidden inside
`resolveToQuietState`.

Projection is timed on `state.copy()`, whose `by lazy` is unforced. That is deliberately the
**zero-cache-hit** case, because a rollout visits each state exactly once and never benefits from
the per-instance memo.

## Branching factor

| Metric | Value |
|---|---|
| Priority windows per game | **483.9** |
| Legal actions per window, pre-filter | 6.36 |
| Candidates per window, post-filter | **0.42** |
| Windows offering **zero** candidates | **76.2%** (36,879 / 48,393) |
| Candidates per window when non-empty | **1.75** |
| Pending decisions per game | 11.6 |
| Turns per game | 10.9 |

Post-filter = the Strategist's current filter: `affordable && !isManaAbility && != PassPriority`.

## Current AI decision cost

| Metric | Value |
|---|---|
| `Strategist.chooseAction` mean | 2.6–2.7 ms |
| AI thinking per game | ~1.25 s |
| Games completed | 100 / 100, 0 engine exceptions |

---

## What these numbers change in the plan

Three of the plan's working assumptions do not survive measurement. None of them invalidate the
phase ordering, but two of them re-rank effort.

### 1. The Phase 5 throughput target is already met — perf is not a rollout blocker

Phase 5 sets a target of **1,500–2,000 `process()`/sec/thread**, derived from the stale
`~404 actions/sec/thread` figure in [`engine-performance.md`](../../backlog/engine-performance.md).

Measured today: **~3,400/sec** at the as-played mix, **~2,400/sec** at the candidate mix. Steps 1–3
of the performance plan (component keying, `getBattlefield()` memoization) landed since that
baseline and evidently did most of the work.

So **Phase 5a and 5c are no longer prerequisites for Phase 7.** They remain worthwhile as standing
engine wins — `ManaSolver.findAvailableManaSources` is still O(n²) and was 59% inclusive — but the
rollout evaluator can be built without them. Treat 5a as an independent perf task, not a gate.

### 2. The branching factor is ~4× smaller than assumed, which moves Phase 4's payoff

Phase 5's budget arithmetic assumes "**candidates after Phase 4 filter ≈ 8**". Measured, *before*
any Phase 4 work, the mean is **1.75 candidates** on the 24% of windows that offer any at all.

Two consequences:

- **`filterMeaningful` has little left to cut.** At 1.75 candidates the Strategist is already
  scoring a nearly minimal set. Do not expect the "branching factor down 30–50%" exit criterion in
  Phase 4 to be reachable — it was written against the wrong number.
- **`shouldAutoPass` is where the whole win is, and it is still real.** 76.2% of windows already
  yield zero candidates, so the Strategist already passes immediately there — but it pays
  `enumerate` (0.40 ms) first. That is `0.762 × 483.9 × 0.40 ms ≈ 148 ms per game` spent
  enumerating windows with nothing to do, and a rollout pays it on every crossing. Phase 4a should
  be scoped as "skip the enumeration", not "shrink the candidate list".

### 3. Rollouts are comfortably affordable at today's speed

At the as-played rate, a 2 s NORMAL budget buys **~6,800 `process()` calls**.

A 2-turn-horizon rollout crosses ≈ 89 priority windows (483.9 windows ÷ 10.9 turns ≈ 44 per turn),
costing ~1.0 `process()` at a pass window and ~1.8 where something resolves — call it ~110
`process()` calls per rollout. That is **~60 rollouts per decision**, or **~35 per candidate**
across the 1.75 candidates a real window offers.

Phase 7 wants R = 3–4 rollouts per (candidate, world) with K = 1–2 determinizations. The measured
budget clears that by an order of magnitude. **The constraint on Phase 7 is leaf-evaluator quality
(Phases 3, 6, 9), not simulation speed** — which is what the plan's Gate 3 already argued, now with
a number behind it.

### 4. Projection: the decision to skip a projection cache is confirmed

The May 2026 profile put `StateProjector.project` at 7.4% inclusive and Phase 5b dropped the
projection-cache idea on that basis. Measured cold, with no memo hit, it is **11.2–11.5% of one
`process()` call**. Same conclusion, independently: a perfect cross-state projection cache caps out
around a 12% win, nowhere near the 2–5× a cache of that complexity and silent-wrongness risk would
need to justify itself.

---

## Not yet refreshed

`just benchmark-random 200 BLB` — the raw random-action baseline that
[`engine-performance.md`](../../backlog/engine-performance.md) quotes as `~404 actions/sec/thread`
(pre-Steps-1–3) — **was not re-run in Phase 0.** The AI-driven numbers above supersede it for
budgeting the rollout evaluator, but the random-action figure is what the performance plan's
validation loop compares against step-by-step, so it still needs one clean run before Step 4 / Phase 5a
starts. It is one command and no code.

## Not measured here

Deliberately out of scope for Phase 0, each owned by a later phase:

- **Puzzle pass rate per category** — Phase 2. The "non-creature valuation" category is expected to
  baseline near 0%.
- ~~**Arena win rate vs `LEGACY_V0`**~~ — Phase 1, below.
- **p50/p95 decision latency per budget tier** — Phase 4b, once `DecisionBudget` exists. The 2.6 ms
  mean above is the whole distribution's mean with no tiering.

---

# Phase 1 — Arena baselines

**Measured:** 2026-07-27. Same hardware. How to run and read these:
[`measurement.md`](measurement.md).

All runs: BLB sealed, seed 20260727, mirror decklists, `skipMulligans`, `maxTurns = 50`.

## The scoreboard's own calibration

| Run | Result | What it proves |
|---|---|---|
| `just arena v0 v0 300` | **50.0%**, CI **[50.0%, 50.0%]**, 300/300 completed | No seat or seed leak. A mirror is exact, not merely "within the interval" |
| `just arena v0 v0-blind 200` | **100.0%**, 200-0 | The harness **discriminates**. Without this, a 50% reading is indistinguishable from a broken harness |

`v0-blind` is `LEGACY_V0` with every evaluation weight zeroed, so its Strategist can never prefer
an action to passing. Losing 200-0 to a greedy 1-ply agent is the expected floor.

## Reference-opponent baselines

| Agent A | Agent B | Games | Pair win % | 95% CI | Verdict |
|---|---|---|---|---|---|
| `v0` | `v0` | 300 | 50.0% | [50.0%, 50.0%] | mirror (harness check) |
| `v0` | `blb-advisors` | 1,000 | **50.0%** | **[49.3%, 50.8%]** | **not distinguishable** |
| `v0` | `v0-blind` | 200 | 100.0% | [100.0%, 100.0%] | v0 wins |

## Throughput

**~5 games/sec on 8 threads** (~1.6 s of wall clock per game, ~11 turns, ~497 actions). A
1,000-game merge gate is **3.5 minutes**, not the ~30–60 minutes the plan budgeted.

The plan's 111-CPU-hour estimate assumed a 2 s `DecisionBudget` per decision. That budget does not
exist yet — combat has a 1 s cap and nothing else is bounded at all. **So the plan's "run the arena
at a reduced ~150 ms budget" mitigation is not needed in Phase 1, and Phase 4b must re-measure this
table before it ships a budget**: it is the budget, not the game count, that decides whether a
1,000-game gate is affordable.

---

## What Phase 1 found

### 1. The BLB card advisors are not an improvement over generic `v0`

1,000 paired games: **50.0%, CI [49.3%, 50.8%]**, 500W-500L-0D. Only ~3% of pairs came out
differently at all, and those split evenly. The advisors change behaviour — the CI is non-degenerate,
unlike the `v0` mirror's `[0.000, 0.000]` — they just do not change results.

`AdvisorBenchmark`, run at the same 1,000 games for comparison, reports **46.1%** for the advised
side. The two do not contradict each other so much as bracket the same conclusion: *advisors are
not helping*. The difference is explained by `AdvisorBenchmark` being **unseeded** — it sets no
`GameConfig.seed`, so the two games of its "pair" are different shuffles, and its estimator is
therefore unpaired. On the same 1,000 games the arena's paired interval is **±0.8 pp** against the
unpaired **±3.1 pp** — a **4× variance reduction**, well beyond the 15–30% the plan predicted,
because mirror decklists plus an identical game seed make the pairing unusually tight.

This is worth stating plainly because Phase 6 plans to retire advisors that `CardIntent` reproduces:
**the retirement bar for the 42 BLB/ONS advisor entries is lower than it looked.** Two of them had
already been silently overwritten before Phase 0 fixed the registry collision, and the module as a
whole is measurably neutral.

### 2. The AI proposes illegal actions roughly once per game

The arena is a free bug finder at scale, and it found one immediately. Across the 1,000-game run:

| Count | Rejection |
|---|---|
| 889 | `CastSpell: No valid targets available` |
| 33 | `CastSpell: Not enough mana to cast this spell` |
| 23 | `ActivateAbility: Must choose 1 card(s) to discard` |

Every one of these is a defect — either the enumerator offered an action it should not have, or the
Strategist mangled it on the way out. The runner recovers with a safe fallback and continues, so
none of them break a game, but ~0.9 wasted decisions per game is real.

The rate is **advisor-dependent**: 0.34/game in the `v0` mirror versus 0.95/game with
`blb-advisors` on one side. That points at a `CardAdvisor` recommending a cast whose target
selection then fails, and it is a concrete lead rather than a vague one. **Not fixed in Phase 1** —
it is an AI/enumerator bug, not scoreboard work — but it is now measured, and any fix has a
number to move.

### 3. Seat 0 is not worth what you would guess

Seat 0 (on the play) wins **46–51%** depending on the run — at 300 games it was 46.0%, at 1,000 it
was 51.0%. In BLB sealed against this AI, being on the play is close to neutral and may be slightly
negative. It does not bias any result here (both agents sit in both seats), but it is a reminder
that "on the play wins more" is an assumption about human play, not a property of the engine.

---

# Phase 2 — Puzzle baselines

**Measured:** 2026-07-27, `just arena-puzzles` (the whole suite, ~15 s including Gradle startup).
How to read one: [`measurement.md`](measurement.md#the-puzzle-suite).

48 hand-authored positions, 8 categories × 6. Each asks the AI for exactly one move and asserts a
*predicate* over it — "removal targets the 3/3 it can kill, not the 6/4 it bounces off" — never an
exact `GameAction`.

## Per-category baseline

| Category | `v0` | `production` | `v0-blind` | What it catches |
|---|---|---|---|---|
| lethal | 6/6 | 6/6 | 6/6 | Missing an alpha strike / burn-to-face kill |
| blocking | 6/6 | 6/6 | 6/6 | Chump vs trade vs no-block; deathtouch / first strike |
| removal | 6/6 | 6/6 | 0/6 | Shooting the 1/1 instead of the bomb |
| instants | 3/6 | 3/6 | 2/6 | Casting a combat trick in your own main phase |
| sequencing | 5/6 | 5/6 | 0/6 | Land before spell; the land that unlocks the spell |
| wipe | 6/6 | 6/6 | 3/6 | Wrathing while ahead |
| race | 5/6 | 5/6 | 5/6 | Attack-vs-hold when both players are on a clock |
| **noncreature** | **2/6** | **2/6** | 0/6 | Ignoring an opposing O-Ring / mana rock / anthem |
| **total** | **39/48 (81%)** | **39/48 (81%)** | 22/48 (46%) | |

`v0-blind` (every evaluation weight zeroed) is the discrimination control, the same one the arena
uses. 22/48 versus 39/48 is the suite proving it measures something; the always-on
`PuzzleSuiteTest` asserts that gap rather than leaving it to a manual run.

## What Phase 2 found

### 1. Non-creature blindness is real, and it is a *casting* failure before it is a targeting one

The plan predicted ~0% here and named `heuristicTargetRank`'s `else -> 0.0` as the cause. The
measurement is more specific: on all four failures the AI does not mis-target the Disenchant, **it
never casts it at all**. Destroying an artifact moves `BoardPresence` by `permanentValue`'s flat
`0.5` (weight 1.5, so +0.75) and costs a card (`CardAdvantage`, weight 1.0, −1.5 at a typical hand
size). Passing scores higher, so the removal is held forever.

That reframes Phase 6's exit criterion slightly: `staticPriorValue` has to be large enough to clear
the *card-advantage* cost of casting, not merely to outrank a sibling target.

The two that pass are the two whose effect shows up in **creature** stats, which the evaluator can
already see: `noncreature-05` (an anthem pumping three creatures) and `noncreature-06` (Disenchant
on a Pacifism that is holding down a 6/4). So the deficit is precisely "permanents whose value is
not visible in someone's power and toughness."

### 2. The last card in hand never gets played

`sequencing-02` and `sequencing-04` are the same decision — play a land — with one card of
difference in hand. 04 passes, 02 fails. The cause is `CardAdvantage.cardValue(0) = -3.0` against
`cardValue(1) = 1.0`: emptying your hand reads as a 4-point disaster, which swamps the land drop's
tempo and board gain, so the AI would rather hold its last land indefinitely. Land drops are free;
this is a hand-drawn constant that Phase 9's logistic fit should remove.

### 3. A one-ply evaluator cannot see a prevention effect

`instants-05` — Fog at 2 life facing 9 power of attackers — is passed up. After the simulation
resolves Fog, the state has the same life totals as passing did: the prevention only shows up when
combat damage would be dealt, which is past the one-ply horizon. Fog therefore evaluates as "−1
card" in every position, which is also why `instants-04` (hold Fog in your own main) *passes* —
right answer, wrong reason. This is a Phase 7 rollout puzzle, not a weight-tuning one.

### 4. Combat is carried by `CombatAdvisor`'s heuristics, not by the evaluator

`v0-blind` scores 6/6 on lethal and 6/6 on blocking despite scoring every board identically. The
combat categories are measuring `CombatAdvisor`'s seed heuristics, which are evaluator-independent.
That is worth knowing before reading a future improvement: a change to `BoardFeatures.kt` will not
move those two categories, and a change to `CombatAdvisor` will.

The one combat position the evaluator does own is `race-03` (send the flier, keep the ground
blocker home), and it fails for both — there is no model of holding a creature back at all.

### 5. The card advisors are neutral here too

`v0` and `production` score identically, 39/48, category for category. That is the same conclusion
Phase 1's arena reached at 1,000 paired games (50.0%, CI [49.3%, 50.8%]), from a completely
independent measurement. Two signals agreeing lowers the retirement bar for the 42 advisor entries
further.

---

# Phase 3 — Multiplayer baselines

**Measured:** 2026-07-28, `just arena-pod <table> <a> <b> <games>`, BLB sealed, seed 20260727, on
the same 8-core M1 Pro. How to read one:
[`measurement.md`](measurement.md#the-pod-arena).

Phase 3 made the evaluator see a whole table instead of one neighbour. Before it, every feature in
`BoardFeatures.kt` opened with `soleOpponent(playerId)`; the pod arena (`just arena-pod`) is the
scoreboard that can tell whether that mattered.

## The pod scoreboard's own calibration

One agent in a field of the other, rotated through every team position. **The null is 1/teams, not
50%.**

| Table | Seats | Null | `v0` vs a field of `v0-blind` | Games |
|---|---|---|---|---|
| `ffa3` | 3 | 33.3% | **100.0%** CI [100%, 100%] | 150 |
| `ffa4` | 4 | 25.0% | **100.0%** CI [100%, 100%] | 120 |
| `2hg` | 4 (2 teams) | 50.0% | **100.0%** CI [100%, 100%] | 120 |

Every game, at every table. `v0-blind` zeroes the evaluation weights, so this is the pod arena's
discrimination control, the same one Phases 1 and 2 use — and a cleaner sweep than the head-to-head
arena's 200-0, because a blind agent in a pod is being hunted by two or three opponents rather than
one.

## Health at a pod table

Multiplayer is the least-exercised engine path in the repo, so these matter as much as the win
share:

| Table | Completion | Mean rounds | Mean actions | Mean game | Rejected AI actions / game |
|---|---|---|---|---|---|
| `ffa3` (`v0` vs blind) | 150/150 | 7.7 | 615 | 1.3 s | 0.18 |
| `ffa4` (`v0` vs blind) | 120/120 | 7.9 | 1,133 | 2.3 s | 0.53 |
| `2hg` (`v0` vs blind) | 120/120 | 8.3 | 601 | 1.0 s | 0.08 |
| `ffa3` (`production` vs `v0`) | 286/300 | 13.2 | 1,244 | 14.3 s | 1.26 |

Zero engine exceptions across all 690 games. Note "rounds", not turns —
`GameState.turnNumber` counts rounds, so 13.2 at a three-seat table is ~40 player turns.

**A pod game is 5-10× the wall clock of a duel.** 14.3 s per game in the `production` vs `v0` run
against ~1.6 s for a head-to-head game: three agents deciding instead of two, over boards that keep
growing because nobody is closing the game out. A 1,000-game pod merge gate is hours, not the
head-to-head arena's 3.5 minutes. Size pod runs accordingly — 300 games is already a 10-minute run.

## Reference-opponent baselines

| Matchup | Table | Win share | CI | Null | Games |
|---|---|---|---|---|---|
| `production` vs a field of `v0` | `ffa3` | 31.7% | [29.3%, 33.7%] | 33.3% | 300 |

**The BLB + ONS card advisors are neutral in a pod too.** That is now three independent
measurements agreeing — Phase 1's 1,000 paired duels (50.0%, CI [49.3%, 50.8%]), Phase 2's puzzle
suite (39/48 for both `v0` and `production`, category for category), and this one. It lowers the
retirement bar for the 42 advisor entries in Phase 6 again.

## What Phase 3 found

### 1. The plan's diagnosis was wrong in a way that made the bug sound smaller

The plan says the five features' `state.soleOpponent(playerId) ?: return 0.0` meant "in any
multiplayer game the evaluator returns exactly 0.0 for every candidate". It does not.
`soleOpponent` was `getOpponents(playerId).firstOrNull()`, and in a pod `getOpponents` returns two
or three players — so the helper returned the **first opponent in turn order** and the evaluator
scored the position as a two-player game against one arbitrary neighbour.

That is not "no evaluation". It is worse in one respect and better in another:

- **Better:** the AI was not choosing at random. It was playing a real, if one-eyed, game.
- **Worse:** a one-eyed evaluation is *confidently* wrong. The runaway leader across the table is
  invisible; a removal spell aimed at them scores exactly 0.0 while the same spell aimed at the
  first opponent scores normally, so the AI systematically attacks the wrong player.

And in Two-Headed Giant the same expression produced three separate failures at once: the teammate's
board did not count, the teammate's cards in hand did not count, and — the sharpest one —
`LifeDifferential` read `getEntity(playerId).get<LifeTotalComponent>()` directly. A 2HG team's life
lives on the team's canonical owner (`GameState.teamLifeOwnerOf`); the *other* member's component is
never written again after setup. So for half the table the life differential was **frozen at the
starting 30 for the whole game**.

`MultiplayerEvaluationTest` asserts each of these as a positive claim rather than describing them.

### 2. `GameState.turnNumber` stopped advancing after the first elimination — fixed since

As measured, `TurnManager.startTurn` incremented `turnNumber` only when
`playerId == state.turnOrder.first()`, and `turnOrder` keeps eliminated players. So in a pod, the
moment seat 0 was knocked out, **`turnNumber` never changed again** — the game played on for another
twenty turns at "turn 16". The arena's wedge detector and length cap both keyed on it and declared
every healthy three-way endgame stuck.

This was never an AI bug. `turnNumber` now counts **player turns**, so it advances on every turn at
any table size and the harness reads it directly again. See `backlog/multiplayer.md` for the engine
side — the same freeze reached delayed triggers and everything else that read `turnNumber + 1` as
"next turn".

One thing that survives the fix and is still worth knowing before writing a pod harness: a pod turn
costs more actions than a duel turn, because the Strategist simulates every candidate against three
or four growing boards. An action budget tuned on duels is too tight regardless of the clock.

### 3. `ThreatAssessment` has a ~130-point cliff at "opponent has no creatures"

`turnsUntilDead` falls back to a sentinel `99.0` when a side has no attack power, and the score is
`(99 − turnsUntilWeKill) × 1.5`. So removing an opponent's **last** creature is worth about 130
points while removing one of their four is worth about 2. The cliff was always there; a pod makes it
visible, because it now competes across opponents and can outweigh any amount of progress against
the actual leader. One more hand-drawn constant for Phase 9's fit.

### 4. `heuristicTargetRank` can never rank an opponent *player* as a target

Found while auditing the same neighbourhood, **not fixed** — it is a 1v1 bug, it would move the
frozen baseline, and `Strategist.heuristicTargetRank` is Phase 6's consumer (b) anyway.

`heuristicTargetRank` computes `isOpponent` from `projected.getController(entityId)`. `ProjectedState`
is built from `state.getBattlefield()` only, so `getController` on a *player* entity returns null —
and the player branch therefore always takes the `else` arm and returns **−5.0**. Every burn spell
ranks its own controller and its opponent identically badly. Phase 6 should fix this when it
rewrites the function's `else -> 0.0`.

---

# Phase 4 — Branching factor + budget

**Measured:** 2026-07-28, Phase 4. Same hardware as above.

```bash
just arena v0 v0-meaningful 1000     # the filter alone
just arena v0 v0-phase4 1000         # filter + tiered budget — what the phase proposes to ship
just arena-budget-scaling 300        # the monotonicity ladder
just arena-pod ffa3 v0-phase4 v0 150 # pod crash check
```

## Agent baselines

| Matchup | Win share for A | CI | Games | Verdict |
|---|---|---|---|---|
| `v0-meaningful` vs `v0` | 51.3% | [49.8%, 52.7%] | 1,000 | neutral — spans parity |
| `v0-phase4` vs `v0` | 50.8% | [49.4%, 52.2%] | 1,000 | neutral — spans parity |
| `v0-phase4` vs a field of `v0` (`ffa3`) | 30.0% | [25.3%, 34.7%] | 150 | neutral — spans the 33.3% null |

**Phase 4 is enabling infrastructure and it measures like it.** Neither agent is a demonstrated
improvement, and neither is a regression — which is the result the phase was designed to produce.
The exit criterion was `just arena v0 v0-meaningful 1000` **≥50%**, phrased that way precisely
because a filtered agent that *loses* is discarding a real option. It does not lose.

## Budget scaling — the safety net, and it passes

`just arena-budget-scaling 300`. The same agent, differing in nothing but the size of its
`DecisionBudget`, played against itself. **Strength is monotone in the budget, with every rung's
lower CI bound above parity:**

| Rung | Win share for the bigger budget | CI |
|---|---|---|
| 1000 ms vs 100 ms | 55.7% | [52.7%, 58.7%] |
| 3000 ms vs 1000 ms | 54.0% | [51.0%, 57.0%] |
| 3000 ms vs 100 ms (end to end) | 55.3% | [52.0%, 59.0%] |

This is the single most important number in the phase. It says the search the AI already has
converts *more thinking* into *more winning*, so when Phase 7 stacks rollouts on top of it there is
a calibrated instrument that will notice if that stops being true. Note the end-to-end rung is not
larger than the first: returns diminish above ~1 s, and the three runs share decks and seeds, so
they are correlated — read the ladder as "monotone", not as "linear".

**Allowances are counted, not timed.** `SearchAllowances` converts a budget into a number of
simulations once, and the wall clock is only a hard safety stop. A stopwatch-driven search would
have made the arena non-reproducible and `ArenaHarnessTest`'s "identical at 8 threads and at 1"
assertion flaky, which would have cost more than it bought.

## Enumeration skipping — what 4a actually saves

Phase 0 re-scoped 4a from "shrink the candidate list" (there are only 1.75 candidates to shrink) to
"skip the enumeration". `MeaningfulActionFilter.canAutoPassWithoutEnumerating` decides a whole
priority window from the state alone, without calling `LegalActionEnumerator.enumerate` at all.

Measured over 884 real priority windows from two full AI-vs-AI games (`AutoPassParityTest`, which
prints the figure): **40% of windows are decided without enumerating.** That is below Phase 0's
76.2% "windows offering zero candidates" because the fast path deliberately declines every window
whose verdict depends on what the player is holding — both main phases, both combat declarations,
first-strike damage, end of combat, and the opponent's end step. At ~0.40 ms per `enumerate` and
~380 windows per game, it is ~60 ms per game today, and a Phase 7 rollout would have paid it again
on every window it crossed.

## What Phase 4 found

### 1. The Phase 1 illegal-action finding was a *targeting* bug, not a filtering one

Phase 1 measured the AI proposing ~0.9 illegal actions per game, **889 of 945 being exactly
`CastSpell: No valid targets available`**, and left it open. The meaningful-action filter was the
obvious suspect and it turned out not to be the cause at all.

The actual mechanism: `Strategist.resolveTargetsForSimulation` and `chooseCommittedTargets` both
opened with `if (targetInfos.any { it.validTargets.isEmpty() }) return action.action` — abandoning
target selection for the **whole spell** the moment *any* requirement had no legal target, and then
submitting an untargeted cast that the engine rejects. Almost every instance is an **optional**
trailing slot. Conduct Electricity is "destroy target creature" *and* "up to one target creature
token"; with no token on the board the AI declines to target the mandatory creature either, and
throws the card away on a rejection.

`Strategist.fillableRequirements` now fills the slots it can. Targets are submitted as one flat list
that `TargetValidator` slices back by max counts, so an unfilled slot can only ever be a trailing
one — the function returns null (old behaviour) when a mandatory slot is empty, or when a *later*
slot has targets that a skipped one would displace.

Measured effect, mirror matches over 200 games: **`No valid targets available` 36 → 0.** Only 10
`Not enough mana` rejections remain, a separate bug.

It is behind `AiProfile.useMeaningfulFilter` rather than applied unconditionally. Not because the
old behaviour is defensible, but because `LEGACY_V0` is the permanent reference opponent and quietly
strengthening it would silently rebase every number ever published against it. `FrozenBaselineTest`
would not have caught this: its frozen game is all-vanilla Portal, which has no multi-requirement
spell.

### 2. `validTargets` cannot see a multi-requirement spell's second slot

The same neighbourhood, a different consumer. `LegalAction.validTargets` and
`LegalActionInfo.validTargets` only ever mirror the *first* target requirement, so the obvious
"targeted spell with no legal target" test passes a two-requirement spell whose second mandatory
slot is empty — an action the engine will reject.

`PriorityAction.hasUnfillableTargetRequirement` asks the real question (any **mandatory**
requirement with no legal target), and both the AI's candidate filter and the client's auto-pass now
use it. The client half is a UX fix in its own right: it was stopping the player on spells they
could not cast.

### 3. The "next stop point" button used a weaker notion of "meaningful" than the stop itself

`GameSession` computed `hasMeaningfulActions` inline as "not PassPriority, and not a plain mana
ability" — which counts unaffordable spells and zero-target spells that the actual stop decision
discards. So the Pass button could promise a stop that never arrived. It now calls
`AutoPassManager.getMeaningfulActions`, the same code path the stop uses.

### 4. Threading a budget through `DecisionResponder` would have changed no number

The plan lists `DecisionResponder` in the budget's threading chain. Every scan in it is already
bounded by construction — a yes/no is 2 simulations, a colour is 5, a number is sampled to 11,
targets are pre-ranked and truncated to 8 — which is at or below what even the ROUTINE tier allows.
The budget is wired into the one place it can bind (the target pre-rank cut) and deliberately not
into the other twenty responders.

### 5. Two tiers from the plan's table are not implemented, on purpose

`BudgetPolicy`'s CRITICAL tier fires on combat declaration and on either side being within one
swing of lethal. The plan also lists "sweeper castable or on the stack" and "a real counterspell
window". Both need to know what a card *does* — Phase 6's `CardIntent` — and guessing them from a
mana cost would put the most expensive tier on the wrong windows, which is worse than leaving those
windows at NORMAL.
