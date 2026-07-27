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
- **Arena win rate vs `LEGACY_V0`** — Phase 1. There is no scoreboard yet, so no strength claim in
  this document is possible or intended.
- **p50/p95 decision latency per budget tier** — Phase 4b, once `DecisionBudget` exists. The 2.6 ms
  mean above is the whole distribution's mean with no tiering.
