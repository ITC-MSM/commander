# Engine AI — Structural Improvement Plan

A phased plan to make the in-game engine AI measurably stronger, on a scoreboard we trust, using
mechanisms that generalize across the whole card catalog rather than per-card special cases.

**Status:** **Phases 0, 1, 2, 3 and 4 shipped** (Phase 4 on 2026-07-28) — baselines in
[`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md), measurement guide in
[`docs/ai/measurement.md`](../docs/ai/measurement.md). Four scoreboards now exist: the arena
(`just arena`), the 48-puzzle suite (`just arena-puzzles`, **39/48 today**), the multiplayer pod
arena (`just arena-pod`) and the budget-scaling ladder (`just arena-budget-scaling`, **monotone**).
Next up is **Phase 6, CardIntent** — the highest strength-per-effort item left, and the first one
that is meant to move the win rate rather than enable it. Phases are individually shippable and
ordered by dependency, not by appeal.

**Related:** [`engine-performance.md`](engine-performance.md) — the CPU profile this plan's
performance phase builds on. See "Cross-reference" below; parts of that doc are stale.

> **Phase 0's measurements moved two later phases.** Simulation is ~3,400 `process()`/sec/thread,
> already above Phase 5's 1,500–2,000 target, so **Phase 5 is no longer a gate on Phase 7**. And the
> real branching factor is **1.75 candidates** on the 24% of priority windows that offer any (not the
> ~8 this plan assumed), so **Phase 4a's value is skipping enumeration, not shrinking the candidate
> list**. Details and the corrected budget arithmetic are in the baseline doc.

---

## Why

The engine AI (`ai/src/main/kotlin/com/wingedsheep/ai/engine/`, ~5,575 LOC) is the opponent players
actually face. It should be better, but today we can neither say how good it is nor tell whether a
change helped. Three findings set the shape of the plan.

### 1. It is greedy 1-ply, and the search it appears to have is dead code

> **Resolved in Phase 0** — `Searcher.kt` and the dead `CombatMath` helpers are deleted,
> `CardAdvisor.attackPenalty` is wired in. The Strategist is now *honestly* greedy 1-ply.

`Strategist.chooseAction` scored each candidate with one simulation and picked the max.
`Searcher.kt` (alpha-beta, 315 L) was **unreachable**: `recommendDepth` bailed because
`canRespond(state, opponentId)` opened with

```kotlin
if (state.priorityPlayerId != playerId) return false
```

and the Strategist only ever runs on *our* priority — so `canRespond(…, opponentId)` was always
false and `recommendDepth` always returned 1. `deepSearch`, `opponentPly`, `ourPly` never
executed in production; only two tests reached them.

Latent behind that: the −∞ sentinel was `Double.MIN_VALUE / 2`, which is **`0.0`**
(`Double.MIN_VALUE` is the smallest *positive* subnormal, 4.9E-324). Had the reachability bug been
fixed, the search would have scored **illegal** actions above any position where we're behind
(board scores are signed differentials and routinely negative), and `opponentPly`'s
`if (alpha >= currentBeta) break` would have skipped the whole opponent ply exactly when we're losing.

Also dead: `CardAdvisor.attackPenalty` was never invoked — `CombatAdvisor.advisorRegistry` was a
write-only field. And `CombatMath.calculateAggressionLevel`, `turnsToKill`,
`simulateAttritionalAttack` (~150 LOC) had no production callers.

### 2. The evaluator is blind to most of Magic

- `BoardFeatures.permanentValue` flat-values **every non-creature permanent at `0.5`** regardless of
  text (`BoardFeatures.kt:94-98`) — a signet, an Oblivion Ring and a Bitterblossom are the same
  number. The code comments this as a known limitation.
- `Strategist.heuristicTargetRank` (`:289-310`) has `else -> 0.0`, so an opponent's non-creature
  permanent ranks equal to nothing. **The AI cannot Disenchant correctly at all.**
- ~~All five features open with `state.soleOpponent(playerId) ?: return 0.0`, so in **any multiplayer
  game (FFA, Commander, 2HG) the evaluator returns exactly 0.0 for every candidate** — the AI is
  choosing with no evaluation whatsoever.~~ **Wrong, and fixed in Phase 3.** `soleOpponent` returned
  the *first opponent in turn order*, never null, so the evaluator scored a pod as a duel against one
  arbitrary neighbour — confidently wrong rather than absent. In 2HG it also read a
  `LifeTotalComponent` the engine stops maintaining once life pools on the team. See Phase 3.
- Every weight (`AIPlayer.kt:179-187`) and every constant inside `BoardFeatures.kt` is a hand-guessed
  literal. There is no tuning harness.
- `Tempo` counts lands only — mana rocks, rituals and colour availability are invisible.

### 3. There is no scoreboard

`AIBenchmark` (game-server, 644 L) and `AdvisorBenchmark` (`:ai`) both have the correct paired-swap
methodology, but: no `GameConfig.seed` is ever set, no confidence intervals, no frozen reference
opponent, and `AIBenchmark` is **sequential** (`for (pairId in 1..numGames)`). Nothing aggregates
win rates across agent versions.

### And the card-knowledge approach doesn't scale

Card knowledge is 19 hand-written `CardAdvisor`s covering **42 card names across 2 sets** (BLB, ONS)
out of a ~30-set catalog. Worse, `CardAdvisorRegistry.register` **silently overwrites on collision** —
`Starfall Invocation` and `Wildfire Howl` are registered by both `BoardWipeAdvisor` and
`GiftBoardWipeAdvisor` (`BloomburrowAdvisorModule.kt:30,32`), and the later registration wins, so
those two cards have already lost their "only wipe when behind" logic.

**Intended outcome:** a measurably stronger AI, on a trustworthy scoreboard, built on generalizing
mechanisms.

---

## Decisions taken

- **Time budget:** one global `DecisionBudget` — ~0 ms trivial, ~200 ms routine priority windows,
  **2 s** normal, **5 s** hard cap on critical decisions. Today only combat has a budget at all
  (1000 ms, `CombatAdvisor.kt:181`); everything else is unbounded.
- **Hidden information:** the AI must **play fair**. It currently reads the fully unmasked
  `GameState` — opponent's hand contents and library order. Build a visibility oracle +
  determinization.
- **Primary strength lever:** **rollout evaluation** — keep the heuristic candidate generator,
  replace each candidate's static leaf score with the mean of K short playouts. Not alpha-beta, not
  full MCTS as the first move.

---

## Strategy

Three gates, in order. Skipping one wastes the next.

1. **You cannot know an improvement without a trustworthy scoreboard** → Arena + puzzles first.
2. **You cannot afford rollouts without cheap simulation** → cut branching factor, then raw speed.
3. **A rollout is only as good as its leaf evaluator** → fix evaluation blindness before stacking
   samples on top of it.

Gate 3 is why **CardIntent (Phase 6) is sequenced before rollouts (Phase 7)**, even though rollouts
are the headline lever. Averaging many playouts of an evaluator that cannot see an Oblivion Ring
produces a confident wrong answer.

---

## How we measure

Four independent signals. No single one is trusted alone.

### 1. Arena win rate — the merge gate

Agent A vs agent B, **paired-swap seats** (identical decks, identical game seed, both seat orders),
seeded decks, parallel across cores.

- **Reference opponent is `AiProfile.LEGACY_V0`, permanently** — every version reports against it so
  numbers stay comparable across months.
- Report **pair-level score** (−1 / 0 / +1 per pair, paired bootstrap CI over 2,000 resamples)
  alongside per-game win rate with a **Wilson interval**. Pairing is the estimator; report it as such.

| Purpose | Games | CI at p=0.5 |
|---|---|---|
| Smoke ("did I break it") | 100 (50 pairs) | ±10% |
| Directional | 300 (150 pairs) | ±5.7% |
| **Merge gate** | **1,000 (500 pairs)** | **±3.1%** |
| Publish / Elo | 3,000 | ±1.8% |

At p≈0.5 unpaired, n = (1.96/0.03)² · 0.25 ≈ 1,067 games; pairing on identical decks and seeds buys
15–30% variance, so ~800–900 games is a real ±3% read. For 80% power to detect a true 55% vs 50%,
n ≈ 780. **A change that cannot clear 53% over 1,000 games is not a demonstrated improvement.**

**Wall-clock reality:** 1,000 games × ~200 decisions × 2 s ≈ **111 CPU-hours**. Unusable. So the
arena runs at a **reduced budget (~150 ms)**, sized so 1,000 games fit in ~30–60 min on 10 cores,
with 300-game full-budget runs as a cross-check.

**`ArenaBudgetScalingTest` is the key safety net:** the same agent at 100 / 1000 / 3000 ms.
**If strength is not monotone in budget, the search is generating noise, not signal.** Build this in
Phase 4, *before* rollouts exist — it is the single best early warning for "search made it slower
*and* worse."

**Gauntlet + full pairwise matrix, not just Elo.** MTG agents are frequently non-transitive (an
aggressive agent beats a controlling one that beats a midrange one that beats the aggressive one).
Fit Bradley–Terry/Elo by iterative MM (~40 LOC, no dependency) but **report the matrix**.
Promotion rule: a new version must beat **both** V0 **and** the immediately preceding version, and
must not lose to any gauntlet member worse than 45%.

### 2. Tactical puzzle suite — the localizing signal

Win rate says *that* you regressed; a puzzle says *what*. Runs in ~15 s, CI-gated.
**48 puzzles, 8 categories × 6.** Built in Phase 2; scores are the measured 2026-07-27 baseline for
`v0`/`production`, with the zero-weight `v0-blind` control in brackets.

| Category | Baseline | What it catches |
|---|---|---|
| Lethal detection | 6/6 [6/6] | Missing an alpha strike / burn-to-face kill |
| Blocking | 6/6 [6/6] | Chump vs trade vs no-block; deathtouch / first strike |
| Removal targeting | 6/6 [0/6] | Shooting the 1/1 instead of the bomb (`heuristicTargetRank`) |
| Holding instants | 3/6 [2/6] | Casting a combat trick in your own main phase |
| Sequencing | 5/6 [0/6] | Land before spell; the land that unlocks the spell |
| Board-wipe timing | 6/6 [3/6] | Wrathing while ahead |
| Race math | 5/6 [5/6] | Attack-vs-hold when both players are on a clock |
| **Non-creature valuation** | **2/6** [0/6] | Ignoring an opposing O-Ring / mana rock / anthem |

Two readings the bracketed column forces. Lethal and blocking are carried entirely by
`CombatAdvisor`'s seed heuristics — the blind agent matches the real one — so they are a regression
net for *that* code, not for `BoardFeatures.kt`. And the plan's "expect ~0%" on non-creature
valuation was close but for the wrong reason: see Phase 2's findings.

### 3. Latency

p50/p95 decision latency in production, per budget tier. A strength gain that blows the 5 s cap is
not shippable.

### 4. Health

Games completed %, draw-reason histogram, stuck-game detection, and distinct engine exceptions —
`RandomActionBenchmark` already groups exceptions, so the arena is a free crash-finder at scale.

### 5. Pod win share — added in Phase 3

`just arena-pod <table> <a> <b> <games>` over `ffa3` / `ffa4` / `2hg`. One agent against a field of
the other, rotated through every team position so turn order cancels; **the null is 1/teams, not
50%**. This is the only signal that exercises teammates, multiple opponents, and elimination at all,
and it is the cheapest crash finder we have for the engine's least-travelled path. A pod game costs
5–10× a duel, so size runs accordingly.

---

## Phases

### Phase 0 — Instrumentation + honesty fixes · *2–3 d* — ✅ **DONE 2026-07-27**

Produce the numbers everything else is budgeted against, and kill the bugs a rollout engine would
hit thousands of times more often than production does.

**Created** `ai/src/test/.../engine/SimulationThroughputBenchmark.kt` (`just benchmark-throughput`).
It drives real `AIPlayer` games on both seats rather than random actions — branching factor and
projection cost are state-dependent, so the state distribution has to be realistic — and discards
`cores × 2` warmup games, without which the reported rate is measuring the JIT (838 → 1,299 → 2,332
`process()`/sec at 20 → 40 → 200 games, same code).

Reports per thread: `ActionProcessor.process` calls/sec at two action mixes (candidate and
as-played) · `GameSimulator.simulate` calls/sec (incl. `resolveToQuietState`) · mean ns in
`StateProjector.project`, timed cold on `state.copy()` · legal-action count per priority window pre-
and post-filter, **plus the share of windows offering zero candidates** · priority windows per game.

**Fixed, in `:ai`:**
- `CardAdvisorRegistry.register` — now throws on collision. `Starfall Invocation` / `Wildfire Howl`
  were claimed by both `BoardWipeAdvisor` and `GiftBoardWipeAdvisor`, and the later registration
  won, so both cards had already lost their "only wipe when behind" logic. The two advisors are
  merged; `Valley Rally`'s duplicate between `CombatTrickAdvisor` and `GiftCombatTrickAdvisor` is
  resolved in favour of the gift advisor, which delegates its cast timing back.
- `respondBudgetModal` — `while (mode.cost <= remaining)` spun forever on a zero-cost mode.
- `LimitedCardRater.ratingsCache` → `ConcurrentHashMap` + `computeIfAbsent`.
- `CombatAdvisor.advisorRegistry` / `CardAdvisor.attackPenalty` — wired into `evaluateAttackPlan`
  (subtracted from the plan score) and the heuristic seed (a discouraged creature is left out of the
  seed but can still be added back by the local search). The lethal alpha-strike path deliberately
  ignores penalties. No advisor declares one today, so this is behaviour-neutral.
- `Searcher.kt` **deleted** rather than deprecated, along with `Strategist.deepSearch`, its two
  tests, and the ~150 LOC of unreachable `CombatMath` (`calculateAggressionLevel`, `turnsToKill`,
  `simulateAttritionalAttack`, `simulateOneTrade`). The broken sentinels were not repaired — Phase 7
  replaces the mechanism outright, and a repaired-but-untested alpha-beta is worse than none.

**Not done:** `just benchmark-random 200 BLB` was not re-run, so `engine-performance.md`'s
`~404 actions/sec/thread` is still stale. It is one command; do it before Phase 5a.

**Exit:** ✅ [`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md) committed with
throughput, projection share and branching factor — plus the three plan corrections they imply.

---

### Phase 1 — The Arena · *4–6 d* — ✅ **DONE 2026-07-27**

> **Shipped.** Baselines and findings in
> [`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md#phase-1--arena-baselines); how to
> run and read a report in [`docs/ai/measurement.md`](../docs/ai/measurement.md).
>
> Three corrections the build produced:
>
> 1. **The arena is ~100× cheaper than budgeted.** ~5 games/sec on 8 threads, so a 1,000-game
>    merge gate is **3.5 minutes**, not 30–60. The 111-CPU-hour estimate assumed a 2 s
>    `DecisionBudget` that does not exist yet, so **the "reduced ~150 ms budget" mitigation was not
>    needed and was not built.** Phase 4b must re-measure before shipping a budget — the budget, not
>    the game count, is what makes an arena expensive.
> 2. **The v0-vs-v0 exit criterion was weaker than what is actually true.** With the same agent on
>    both seats the two games of a pair are *literally the same game*, so the mirror is **exactly**
>    50% with CI `[0.000, 0.000]`. That is asserted in the always-on suite rather than checked by
>    hand. A control run (`v0` vs a zero-weight `v0-blind`) proves the harness discriminates: 200-0.
> 3. **The BLB advisors are measurably neutral** — 1,000 paired games, 50.0%, CI [49.3%, 50.8%].
>    `AdvisorBenchmark`'s 46.1% on the same game count is the *unpaired, unseeded* view of the same
>    conclusion; the arena's paired interval is **4× tighter**. This lowers the retirement bar for
>    the 42 advisor entries in Phase 6.
>
> Also found, and **not fixed** (an AI/enumerator bug, not scoreboard work): the AI proposes an
> illegal action ~0.9 times per game, 889 of 945 being `CastSpell: No valid targets available`, at
> ~3× the rate when a `CardAdvisor` is in play. Quantified in the baseline doc.
>
> **Deferred:** `arena-puzzles` ships with the puzzles in Phase 2 — a recipe pointing at a test
> that does not exist is worse than no recipe. `ArenaBudgetScalingTest` stays in Phase 4b as
> planned; there is no budget to scale yet.

**Lives in `ai/src/test/kotlin/com/wingedsheep/ai/arena/`.** `:ai` already declares
`testImplementation(testFixtures(project(":rules-engine")))` and `testImplementation(project(":mtg-sets"))`,
and `just benchmark` already targets `:ai:test` — zero new build wiring. Not `gym-trainer`: its
`GameEnvironment.playGame` no-agent fallback is `actions.first { it.affordable }`
(`GameEnvironment.kt:314`), a near-random bot, despite its KDoc claiming the built-in AI.

Files: `ArenaAgent.kt`, `Arena.kt`, `ArenaGameRunner.kt`, `ArenaStats.kt`, `ArenaReport.kt`,
`ArenaBenchmark.kt`.

**Reuse `AdvisorBenchmark.playAdvisorGame` (`ai/src/test/.../AdvisorBenchmark.kt:162`) as the game
loop — not `AIBenchmark.playGame`**, which round-trips through `ClientStateTransformer.transform` +
`LegalActionEnricher.enrich` on every action to satisfy the `AiPlayerController` DTO interface. For
engine-vs-engine that is pure overhead. Take `AIBenchmark`'s stuck-detector
(`actionCount - lastProgressAction > 300 && turns == lastProgressTurn`) and draw taxonomy, and
`GameBenchmark`'s `ExecutorCompletionService` pool — submit **pairs** as units so a pair never
straddles a partial run.

**Create `ai/src/main/kotlin/com/wingedsheep/ai/engine/AiProfile.kt`** — the versioning seam and the
switchboard for every feature this plan adds (`candidateEvaluator`, `rolloutsPerCandidate`,
`determinizations`, `evalWeightsId`, `useMeaningfulFilter`, `useCardIntent`, budget overrides).
`AiProfile.LEGACY_V0` pins everything off and reproduces today's AI by construction.
`AIPlayer.create(registry, playerId, profile)` becomes the single constructor; the existing 3-arg
overload delegates with `AiProfile.CURRENT`.

**Guard against baseline drift:** `FrozenBaselineTest` runs one fixed-seed, fixed-deck game with
`LEGACY_V0` on both seats and asserts the action stream hashes to a golden constant. If a refactor
moves the baseline you find out immediately. Far cheaper and more honest than copying 5,575 LOC into
a `frozen/v0/` package.

**Seeding gaps to close:**
- Set `GameConfig.seed` explicitly — `GameConfig.seed: Long?` exists (`GameInitializer.kt:91`) and
  `InitializationResult.seed` records the drawn value, but no benchmark sets it.
- Add a `random: Random` param to `ConstructedDeckGenerator` (`:65` constructs `RandomDeckGenerator`
  without one). `RandomDeckGenerator.kt:21` is already seedable.
- `skipMulligans = true` for determinism — note this puts mulligan quality **out of test**; schedule
  a separate mulligan A/B later.

**justfile:** `arena A B GAMES="300"`, `arena-gauntlet GAMES="200"`. (`arena-puzzles` moved to
Phase 2, with the puzzles it would run.) Gauntlet membership in
`ai/src/test/resources/arena/gauntlet.json`. Results go to `benchmarks/arena/<timestamp>-<a>-vs-<b>/`
(results.csv + summary.md) — **gitignored**; there was no committed `benchmarks/ai-benchmark-*`
convention at the repo root, that claim was wrong.

**Exit:** ✅ `just arena v0 v0 300` returns exactly 50%, CI `[50.0%, 50.0%]` — a seat/seed-leak
detector, asserted in the always-on suite rather than eyeballed. ✅ `just arena v0 blb-advisors 1000`
returns 50.0% CI [49.3%, 50.8%] against `AdvisorBenchmark`'s unpaired 46.1% — the same conclusion
(advisors are not an improvement) at 4× the precision.

---

### Phase 2 — Tactical puzzle suite · *3–4 d* — ✅ **DONE 2026-07-27**

> **Shipped.** 48 puzzles, 8 categories × 6, in
> `ai/src/test/kotlin/com/wingedsheep/ai/puzzles/`. `just arena-puzzles` is the gate (~15 s);
> `just arena-puzzles-compare` runs the same suite across `v0` / `production` / `v0-blind`.
> Per-category baseline and findings:
> [`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md#phase-2--puzzle-baselines); how to
> write one: [`docs/ai/measurement.md`](../docs/ai/measurement.md#the-puzzle-suite).
>
> **Baseline: 39/48 (81%).** `v0` and `production` score identically, category for category — the
> same "advisors are neutral" conclusion Phase 1's arena reached, from an independent measurement.
> `v0-blind` scores 22/48, which is the suite proving it discriminates; that gap is asserted in the
> always-on suite rather than eyeballed.
>
> Four corrections the build produced:
>
> 1. **Non-creature blindness is a *casting* failure before it is a targeting one.** The plan
>    predicted ~0% and blamed `heuristicTargetRank`'s `else -> 0.0`. In all four failures the AI
>    never casts the Disenchant at all: destroying an artifact is worth `permanentValue`'s flat
>    `0.5` (+0.75 weighted) and costs a card (−1.5 weighted), so passing wins. **Phase 6's
>    `staticPriorValue` has to clear the card-advantage cost of casting, not merely outrank a
>    sibling target.** The two that pass are the two whose effect shows up in *creature* stats
>    (an anthem on three bodies; Disenchanting a Pacifism off a 6/4) — so the deficit is exactly
>    "permanents whose value is invisible in someone's P/T".
> 2. **`CardAdvantage.cardValue(0) = -3.0` means the last card in hand is never played.**
>    `sequencing-02` and `-04` are the same land drop one card apart; 04 passes, 02 fails. Land
>    drops are free. One more hand-drawn constant for Phase 9.
> 3. **A one-ply evaluator cannot see prevention.** Fog at 2 life facing lethal is passed up,
>    because the post-simulation state has the same life totals as passing — the prevention only
>    materializes at the damage step. It also means "hold Fog in your own main" passes for the
>    wrong reason. A Phase 7 puzzle, not a Phase 9 one.
> 4. **Combat is carried by `CombatAdvisor`, not the evaluator.** `v0-blind` still scores 6/6 on
>    lethal *and* blocking. Those two categories are a regression net for `CombatAdvisor`'s seed
>    heuristics; a `BoardFeatures.kt` change will not move them. The one combat position the
>    evaluator owns — hold a blocker home rather than attack with everything — fails.

Authored against **`ScenarioTestBase`** (`rules-engine/src/testFixtures/.../support/ScenarioTestBase.kt`,
1562 L, already on `:ai`'s test classpath). Not scenario JSON — `manual-scenarios/` +
`DevScenarioController` exist to drive the *server* for manual UI inspection, and consuming that from
`:ai` would drag in game-server. The `.claude/skills/generate-scenario` skill can bootstrap authoring,
but the committed artifact is Kotlin.

Files: `ai/src/test/.../puzzles/{AiPuzzle,PuzzleRunner,PuzzleSuiteTest}.kt` + `categories/*.kt`.

- **Assert a predicate over the chosen action**, never exact equality — *"removal targets the 4/4
  flier, not the 1/1"*, *"attacks with at least the 3 evasive creatures"*. Exact-action assertions
  break on harmless tie-break changes and train you to ignore the suite.
- `PuzzleSuiteTest` asserts the failing-id set **equals** a committed `KNOWN_FAILURES` set. Flags
  regressions *and* unexpected fixes; keeps CI green without hiding anything.
- `PuzzleReport` (benchmark-gated) prints pass rate overall and per category — the number quoted
  alongside arena win rate.

**Exit:** ✅ 48 puzzles committed, ✅ per-category baseline in `docs/ai/baseline-metrics.md`,
✅ `just arena-puzzles` runs in ~15 s.

---

### Phase 3 — Multiplayer evaluation · *1–2 d* — ✅ **DONE 2026-07-28**

> **Shipped.** `AiOpponent.kt` is now `Sides.kt`: `state.sidesFor(playerId)` returns the AI's own
> side (itself plus still-in teammates) and one entry per opposing team, and every feature folds a
> per-opponent score over it with `OpponentAggregate.THREAT` (board presence, threat assessment) or
> `.FIELD` (life, cards, tempo). Pod scoreboard: `just arena-pod <table> <a> <b> <games>` over
> `ffa3` / `ffa4` / `2hg`. Numbers and findings:
> [`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md#phase-3--multiplayer-baselines);
> how to read a pod report: [`docs/ai/measurement.md`](../docs/ai/measurement.md#the-pod-arena).
>
> **Baseline: `v0` beats a field of `v0-blind` 100% at all three tables** (150 / 120 / 120 games)
> against nulls of 33.3% / 25% / 50%. `production` vs a field of `v0` at `ffa3` is 31.7%,
> CI [29.3%, 33.7%] — **the card advisors are neutral in a pod too**, a third independent
> measurement agreeing with Phase 1's arena and Phase 2's puzzles.
>
> Four corrections the build produced:
>
> 1. **The diagnosis below was wrong, in the direction that made the bug sound smaller.**
>    `soleOpponent` was `getOpponents(playerId).firstOrNull()`, and `getOpponents` returns *every*
>    opponent — so it returned the **first opponent in turn order**, not null, and the evaluator did
>    not return 0.0. It scored a pod as a duel against one arbitrary neighbour. That is worse than
>    "no evaluation" in the way that matters: the runaway leader across the table was invisible, so
>    removal aimed at them scored 0.0 while the same spell aimed at the first opponent scored
>    normally, and the AI systematically attacked the wrong player.
> 2. **Two-Headed Giant was failing three ways at once, and the sharpest one is a stale component.**
>    A 2HG team's life lives on the team's canonical owner (`GameState.teamLifeOwnerOf`); the other
>    member's own `LifeTotalComponent` is never written again after setup. `LifeDifferential` read it
>    directly, so for half the table the life differential was **frozen at the starting 30 for the
>    whole game**. Everything now reads `state.lifeTotal()` and values a side per *life pool*.
> 3. **`GameState.turnNumber` stopped advancing after the first elimination.**
>    `TurnManager.startTurn` only incremented it for `turnOrder.first()`, and `turnOrder` keeps
>    eliminated players — so a pod played on for twenty more turns at "turn 16". The arena's wedge
>    detector and length cap both keyed on it and declared every healthy three-way endgame stuck.
>    **Fixed in the engine since**: `turnNumber` now counts player turns, so it advances on every
>    turn at any table size and the harness reads it directly again. The same freeze had reached
>    delayed triggers and every other `turnNumber + 1` reading of "next turn" — see
>    `backlog/multiplayer.md` for that half.
> 4. **No `AiProfile` flag, deliberately.** In 1v1 the new code is bit-identical by construction (one
>    opposing side of one player, short-circuited before the fold), which `FrozenBaselineTest` and the
>    unchanged 39/48 puzzle score both confirm. In multiplayer the old behaviour was a bug, not a
>    strategy — preserving it behind a switch so it can be A/B'd against itself buys a number and
>    costs a permanent dual path through `:ai`, `:gym` and `:gym-trainer`. The pod arena's control is
>    `v0-blind`, the same one the other two scoreboards use.
>
> Also found, and **not fixed** (a 1v1 bug, and Phase 6 owns the function): `heuristicTargetRank`
> derives `isOpponent` from `projected.getController(entityId)`, but `ProjectedState` only covers
> battlefield entities — so `getController` on a *player* returns null and an opponent player always
> ranks **−5.0**, exactly as our own face does. Quantified in the baseline doc.

**The original diagnosis, kept for the record:** all five features in `BoardFeatures.kt` open with
`state.soleOpponent(playerId) ?: return 0.0`. In FFA, Commander or 2HG the evaluator returns 0.0 for
every candidate — no evaluation at all. (`AiOpponent.kt:6-15` documents this as deliberate:
multiplayer pods launch without AI seats. That is a workaround, not a fix.)

Generalize to `opponentsOf(playerId)`: differentials become "me vs. strongest opponent"
(threat-focused) or "me vs. mean" (positional); 2HG treats a team as one entity via `teammatesOf`.
Add multiplayer games to the arena to verify.

**Exit:** ✅ every feature folds over all opposing sides, ✅ `just arena-pod` over three tables with
an exact-mirror / clean-game / discrimination harness in the always-on suite, ✅ pod baselines in
`docs/ai/baseline-metrics.md`, ✅ 1v1 provably unchanged (`FrozenBaselineTest` green, puzzles 39/48).

> **Sizing a pod run.** A pod game is 5–10× the wall clock of a duel — three agents deciding instead
> of two, over boards that keep growing because nobody is closing the game out. 300 pod games is
> ~10 minutes where 300 duels is ~70 seconds, so the head-to-head merge-gate table does not transfer.

---

### Phase 4 — Branching factor + budget · *5–8 d* — ✅ **DONE 2026-07-28**

> **Shipped.** `rules-engine/.../legalactions/MeaningfulActionFilter.kt` owns the auto-pass rules;
> `AutoPassManager` adapts the DTO and delegates. `ai/.../engine/budget/{DecisionBudget,BudgetPolicy}.kt`
> carries the four tiers, threaded through `AIPlayer` → `Strategist` → `CombatAdvisor` →
> `DecisionResponder` and switched by `AiProfile.useMeaningfulFilter` / `.budgetPolicy`. New
> scoreboard: `just arena-budget-scaling`. Numbers and findings:
> [`docs/ai/baseline-metrics.md`](../docs/ai/baseline-metrics.md#phase-4--branching-factor--budget);
> how to read the ladder: [`docs/ai/measurement.md`](../docs/ai/measurement.md#the-budget-scaling-ladder).
>
> **Baselines: `v0-meaningful` 51.3% CI [49.8%, 52.7%] and `v0-phase4` 50.8% CI [49.4%, 52.2%]**
> over 1,000 paired games each — neutral, which is the result enabling infrastructure should
> produce. The exit criterion was ≥50% precisely because a filtered agent that *loses* is
> discarding a real option; it does not lose. **The budget-scaling ladder is monotone with every
> rung's lower CI bound above parity** (1000 vs 100: 55.7% [52.7%, 58.7%] · 3000 vs 1000: 54.0%
> [51.0%, 57.0%] · end to end: 55.3% [52.0%, 59.0%]) — the safety net is calibrated and passing
> before rollouts exist, which was the whole point of building it now.
>
> Five corrections the build produced:
>
> 1. **Phase 1's illegal-action finding was a targeting bug, not a filtering one — and it is now
>    fixed.** 889 of 945 rejections were `CastSpell: No valid targets available`, and the
>    meaningful-action filter turned out not to be the cause. `Strategist` abandoned target
>    selection for the *whole spell* whenever *any* requirement had no legal target, then submitted
>    an untargeted cast. Almost every case is an **optional** trailing slot — Conduct Electricity's
>    "up to one target creature token" with no token on the board makes the AI decline to target the
>    mandatory creature either. `fillableRequirements` fills what it can. Measured: **36 → 0** over
>    200 mirror games.
> 2. **`validTargets` cannot see a multi-requirement spell's second slot.** It only ever mirrors the
>    first requirement, so "targeted spell with no legal target" passed a spell whose second
>    *mandatory* slot was empty. `PriorityAction.hasUnfillableTargetRequirement` asks the real
>    question, and the client's auto-pass gets the fix too — it was stopping players on spells they
>    could not cast.
> 3. **A budget must be spent as work, not wall clock.** `SearchAllowances` converts a tier into a
>    count of simulations once; the millisecond figure is a hard safety stop only. A stopwatch would
>    have made every arena run irreproducible and `ArenaHarnessTest`'s "identical at 8 threads and
>    at 1" assertion flaky — and it is why the arena's 3.5-minute 1,000-game gate survives Phase 4b
>    intact, contrary to Phase 1's warning that shipping a budget would make the arena expensive.
> 4. **The plan's threading list was one module too long.** Every scan in `DecisionResponder` is
>    already bounded to ≤11 simulations by construction, at or below what even ROUTINE allows. The
>    budget is wired into the one place it can bind (the target pre-rank cut) and deliberately not
>    into the other twenty responders.
> 5. **Two CRITICAL triggers from the table are not implemented, on purpose.** "Sweeper castable"
>    and "a real counterspell window" both need to know what a card *does* — Phase 6's `CardIntent`.
>    Guessing from a mana cost would put the most expensive tier on the wrong windows.
>
> **`LEGACY_V0` is untouched.** Both halves are behind profile flags, including the target-filling
> bug fix — not because the old behaviour is defensible, but because quietly strengthening the
> permanent reference opponent would silently rebase every number published against it.
> `FrozenBaselineTest` would not have caught it: its frozen game is all-vanilla Portal, which has no
> multi-requirement spell.
>
> **The branching-factor exit criterion was dropped, as Phase 0 predicted.** "Down 30–50%" was
> written against an assumed ~8 candidates; the real figure is 1.75, and `filterMeaningful` has
> almost nothing left to cut. The delivered win is `canAutoPassWithoutEnumerating`: **40% of
> priority windows are now decided without calling the enumerator at all** (measured over 884 real
> windows by `AutoPassParityTest`, which prints the figure).

#### 4a. Port the meaningful-action filter down into the engine

`AutoPassManager.getMeaningfulActions` (`game-server/.../priority/AutoPassManager.kt:281`) is a
complete, Arena-style implementation — drops `PassPriority`, mana abilities without a sacrifice cost,
targeted spells with no legal targets, unaffordable casts/cycles/crews — but it lives in game-server
and consumes the server DTO `LegalActionInfo`, so `:ai` and `:gym` can't call it.

**Create** `rules-engine/.../legalactions/MeaningfulActionFilter.kt` over `LegalAction`, exposing:

- **`filterMeaningful(...)`** — shrinks the Strategist's candidate set. `Strategist.kt:57` currently
  filters only `affordable && !isManaAbility && != PassPriority`.
- **`shouldAutoPass(...)`** — **the bigger win**: lets a playout skip an entire priority window
  without scoring anything. A 2-turn rollout crosses ~20–30 windows; auto-passing the ~80% that are
  trivially skippable cuts playout length ~4–5×.

Port `shouldAutoPassOnMyTurn` / `shouldAutoPassOnOpponentTurn` and the `SPELL_CAST_ACTION_TYPES`
(`:76`) / `INSTANT_RESPONSE_ACTION_TYPES` (`:93`) constants with it. `AutoPassManager` then
**delegates** — one source of truth.

This is production UX code, so: **`AutoPassParityTest`** runs both the old inline logic and the new
shared filter over a corpus of arena-harvested states and asserts identical verdicts. Delete the
inline logic only once it's green.

Route `Strategist.kt:57` and `GameSimulator.kt:63` through the filter, behind
`AiProfile.useMeaningfulFilter` so V0 is preserved.

The crude version that used to live in `Searcher.canRespond` — "has an untapped land AND a non-empty
hand" — went with the file in Phase 0; there is nothing to supersede. Note `LegalAction.holdPriority`
(`LegalAction.kt:154`) is the engine's explicit "never auto-pass while this is available" flag;
honour it.

> **Re-scope this sub-phase against Phase 0's measurement.** The branching factor is **1.75
> candidates** on the 24% of windows that offer any — `filterMeaningful` has almost nothing left to
> cut, and the "branching factor down 30–50%" exit criterion below is not reachable because it was
> written against an assumed ~8. The win is entirely `shouldAutoPass`: 76.2% of windows already
> yield zero candidates and still pay `enumerate` (0.40 ms) first, which is ~148 ms per game of pure
> waste and is paid again on every rollout crossing. Scope 4a as "skip the enumeration".
>
> **As built,** that became a third entry point, `canAutoPassWithoutEnumerating`, which decides a
> window from the **state alone** — the other two still need the enumerated list. It fires on 40%
> of windows rather than 76.2%, because it declines every window whose verdict depends on what the
> player is holding, and `AutoPassParityTest` holds it to being a strict subset of the full verdict
> over a corpus of real windows.

#### 4b. `DecisionBudget`

`ai/.../engine/budget/DecisionBudget.kt` — `deadlineNanos`, `expired()`, `remainingMs()`, and a
tier assigned by `BudgetPolicy.tierFor(state, meaningfulActions, playerId)`:

| Tier | Budget | When |
|---|---|---|
| TRIVIAL | 0 ms | ≤1 legal action, or `shouldAutoPass` says yes |
| ROUTINE | 200 ms | Opponent's-turn priority with no immediate threat; upkeep/draw/end-step |
| NORMAL | 2,000 ms | Our main phase with meaningful actions; most decisions |
| CRITICAL | 5,000 ms | Combat declaration; either player in lethal range; sweeper castable or on the stack; a real counterspell window |

Thread through `AIPlayer` → `Strategist` → `CombatAdvisor` → `DecisionResponder`. Replace
`CombatAdvisor.kt:181`'s hardcoded `+ 1000` with the budget deadline, and `MAX_BLOCK_SIMULATIONS = 10`
(`:35`) with a budget-driven loop keeping 10 as a **floor**. Combat declaration is always CRITICAL,
so combat never gets *less* time than today.

**Anytime contract:** every consumer must have a valid answer after its first iteration. Enforced
structurally by Phase 7's sequential halving.

**Exit:** ~~branching factor down 30–50%~~ (unreachable at 1.75 candidates — replaced by "40% of
priority windows decided without enumerating"); ✅ `just arena v0 v0-meaningful 1000` = 51.3%,
CI [49.8%, 52.7%]; ✅ `ArenaBudgetScalingTest` running **and monotone**.

---

### Phase 5 — Simulation speed · *3–5 d* — **no longer a gate on Phase 7**

> **Phase 0 measured this target as already met.** The budget below assumed ~8 candidates and a
> ~404 actions/sec/thread engine. Reality: **~3,400 `process()`/sec/thread** (as-played mix,
> ~2,400 at the candidate mix) and **1.75 candidates** per non-trivial window. A 2 s budget buys
> ~6,800 `process()` calls ≈ **60 rollouts per decision, ~35 per candidate** — an order of magnitude
> more than Phase 7's R = 3–4 × K = 1–2 needs. Steps 1–3 of `engine-performance.md`, which landed
> after that baseline, evidently did the work.
>
> **Build Phase 7 without waiting on this.** 5a remains a genuine standing engine win (the O(n²)
> scan is real and was 59% inclusive) — do it as an independent perf task, on the performance plan's
> own validation loop, not as a rollout prerequisite. 5c stays profile-gated and is now unlikely to
> be justified at all.

**The original budget, kept for the record:**

```
candidates after Phase 4 filter    ~8       determinizations   K = 1–2
rollouts per (candidate, world)     R = 3–4  actions/rollout   ~50 (post-auto-pass; ~200 before)
→ ~3,200 process() calls per decision ÷ 2.0 s  →  ~1,600 process()/sec/thread
```

With sequential halving (Phase 7) the effective candidate count drops to ~3, so
**1,500–2,000 `process()`/sec/thread was the target** — cleared today at ~3,400.

#### 5a. Finish `engine-performance.md` Step 4 — hoist the O(n²) battlefield scans

**This is the headline perf item, not a projection cache.** `ManaSolver.findAvailableManaSources`
was **59% inclusive** in the profile, and it is still quadratic:
`findAvailableManaSources` loops over candidate entities (`ManaSolver.kt:945`) and calls
`getStaticGrantedManaAbilities(entityId, state)` (`:1823`), which itself loops over
`state.getBattlefield()` — an O(n²) scan per enumerate, and enumerate runs at every priority step.

Hoist: compute the granted-mana-ability map **once** per `findAvailableManaSources` call and pass it
down. Same treatment for `TriggerAbilityResolver.isWardSuppressed` (`:662`), which still does
`state.getBattlefield().any { … }` from inside a per-entity path (`:496`) — precompute the
suppressor set once per detection pass.

`getBattlefield()`'s memoization (already landed) removed the *allocation* cost of these scans but
not the *iteration*. This is the remaining half.

#### 5b. Do **not** build a projection cache — *confirmed by Phase 0*

An earlier draft of this plan proposed caching `StateProjector.project` across rollout states, on the
theory that `by lazy` per-instance never hits during a rollout. **The profile contradicts it:**
`StateProjector.project` is **7.4% inclusive** and explicitly flagged "already cached — *not* a
target." And critically, the profile was taken on `RandomActionBenchmark`, which visits each state
exactly once — so it *already* reflects the zero-cache-hit case. A cross-state projection cache would
buy ≤7%, not the 2–5× a rollout budget needs, and it would carry real silent-wrongness risk from a
fingerprint that misses a layer input. **Dropped.**

Phase 0 measured this independently: projection timed cold on a fresh `state.copy()` is **11.2–11.5%
of one `process()` call**. Same verdict, from a different measurement — the ceiling on a perfect
cache is ~12%.

#### 5c. Persistent collections for `entities` / `zones` — profile-gated

Matches `engine-performance.md`'s own Step 5 ("only if `Arena::grow` is still hot"). `withEntity`
(`GameState.kt:312`) is `copy(entities = entities + (id to container))` — an **O(entities) map copy
per single component write**, at 125–250 entities.

Containment is better than it looks:
- All mutation funnels through `GameState.kt:296-440`. Only ~20 direct `copy(entities = …)` /
  `copy(zones = …)` sites exist elsewhere (`GameInitializer`, `StackResolver`, `MulliganHandler`,
  `ZoneTransitionService`, `LibraryAndZoneContinuationResumer`, `MoveCollectionExecutor`,
  `ShuffleLibraryExecutor`, `ScenarioBuilderService`), all of `x + (k to v)` form, which type-checks
  unchanged against `PersistentMap.plus`. Two (`ShuffleLibraryExecutor.kt:37`,
  `MoveCollectionExecutor.kt:551`) build a local map and need `.toPersistentMap()`.
- All ~127 read sites compile unchanged — `PersistentMap : Map`, `PersistentList : List`.

**What actually breaks is serialization.** `GameState` is `@Serializable` and round-trips through
`RedisGameRepository.kt:60`, `ScenarioController.kt:110`, `PublicReplayController.kt:77`. Fix with
`rules-engine/.../state/PersistentCollectionSerializers.kt` whose serializers **delegate to the
standard `MapSerializer` / `ListSerializer`** and call `.toPersistentMap()` / `.toPersistentList()`
on decode → **byte-identical wire format**, no migration for persisted Redis sessions or committed
replays. `ComponentContainerSerializer` is untouched (it serializes values, not the map). Add a
golden-JSON `GameStateSerializationFormatStabilityTest`.

Do `entities` first. `zones` values are 40–60-element lists and `removeFromZone` is `current - id` on
every draw; `PersistentList.removeAt` is still O(n), so the win is smaller — treat as a follow-on.

Expect 1.5–3×. Add `kotlinx.collections.immutable` to `gradle/libs.versions.toml`.

#### Explicitly out of scope

**Incrementalizing `StateProjector`.** 2+ weeks, and the failure mode is silent rules bugs across the
whole engine. The profile says it's 7.4%. Not worth it.

**Exit:** `SimulationThroughputBenchmark` ≥1,500 `process()`/sec/thread — **already true at ~3,400
before any Phase 5 work**, so the meaningful exit for 5a is the performance plan's own loop: the
targeted leaf shrank under the profiler and `just benchmark-random 200 BLB` improved against a
freshly measured baseline. `just test-rules` and `:game-server:test` green.

---

### Phase 6 — `CardIntent`: card knowledge that generalizes · *5–7 d*

Replaces 42 hand-written advisor entries with a structural analyzer covering every card in the
engine. **Highest strength-per-effort in the plan**, and it raises the leaf evaluator Phase 7 averages
over.

Files: `ai/.../engine/knowledge/{CardIntent,CardIntentAnalyzer,EffectWalker,IntentCatalog}.kt`.

**Reuse the existing precedent.** `LimitedCardRater.effectBonus` (`LimitedCardRater.kt:205`) +
`scoreEffect` (`:229`) **already walk `CardDefinition.script: CardScript`** — spellEffect,
triggeredAbilities (×0.8), activatedAbilities (×0.6), with `asConditional()` / `asMayDecide()`
unwrapping and a `when (effect)` over the SDK effect types. **Extract that traversal into
`EffectWalker`** and have both `LimitedCardRater` and `CardIntentAnalyzer` consume it. One walk, two
scorers — the elegance the `review-changes` skill would ask for.

```kotlin
data class CardIntent(
    val tags: Set<IntentTag>,      // REMOVAL, EXILE_REMOVAL, SWEEPER, DRAW, TUTOR, RAMP, ANTHEM,
                                   // PUMP, COMBAT_TRICK, COUNTERSPELL, LIFEGAIN, DISCARD,
                                   // RECURSION, TOKEN_MAKER, PROTECTION, TAPPER, EVASION_GRANT,
                                   // SACRIFICE_OUTLET
    val speed: Speed,              // SORCERY | INSTANT | STATIC | ACTIVATED
    val removalReach: Int?,        // damage / toughness it can answer
    val cardsDrawn: Int?,
    val affectsOpponent: Boolean,
    val repeatable: Boolean,       // activated / triggered on a permanent
    val staticPriorValue: Double,  // feeds permanentValue
)
```

Pure function of `CardDefinition` — memoize by card name in a `ConcurrentHashMap` scoped to the
`CardRegistry`. Cost paid once.

**Five consumers:**

- **(a) `BoardFeatures.permanentValue` (`:81-98`)** — the flat `0.5` becomes `intent.staticPriorValue`:
  repeatable removal artifact/enchantment 2.5–4.0 · anthem/lord `1.0 + 0.5 × creatures pumped`
  (state-dependent, cheap) · token generator `1.5 + tokens/turn` · mana rock 0.7 (≈ a land) ·
  uninterpretable stays 0.5 (unchanged fallback). Planeswalkers: replace the flat `4.0` with
  `loyalty × 0.8 + Σ ability intent`.
- **(b) Targeting** — `Strategist.heuristicTargetRank:289`'s `else -> 0.0` becomes
  `intent.staticPriorValue + 10.0` on the opponent's side. This alone makes the AI capable of
  Disenchanting correctly, which it currently cannot do at all.
- **(c) Timing** — generalize the ad-hoc `passScore - 1.5` (`Strategist.kt:87-91`) into an
  intent-driven `HoldPolicy`: penalize casting an INSTANT COMBAT_TRICK / REMOVAL / COUNTERSPELL in
  our own main phase with no forcing reason; bonus on the opponent's end step or after blockers.
  Targets the "holding instants" puzzle category directly.
- **(d)** Playout-policy softmax weights (Phase 7 step 4).
- **(e)** Determinization prior (Phase 8, unknown-decklist tier).

**17Lands prior — expand it.** `LimitedCardRater` already loads real 17Lands GIH win-rate data from
`rules-engine/src/main/resources/ratings/{SET}.json` and maps it to a 0–5 rating, but
`LimitedCardRater.kt:45` hardcodes `setCodes = listOf("BLB")`. Derive the set list from a manifest,
add JSON for the sets we actually play, and consolidate with the duplicate store at
`ai/src/main/resources/draftai/ratings/BLB.json` — two rating stores for the same data is a trap.

> **Caveat:** GIH-WR is a *limited-format, context-free* prior. Use it for static card quality (deck
> building, cards-in-hand valuation, determinization priors). **Never** as a positional evaluation
> term — it says nothing about the board.

**Do not delete the 42 advisors in this phase.** Add `CardIntent` underneath, then run
`just arena-gauntlet` over `{intent-only, advisors-only, both}` and retire the advisors that
`CardIntent` reproduces, keeping the ones encoding genuinely card-specific tactics. A principled
retirement criterion beats a judgment call.

**Exit:** puzzle "non-creature valuation" **2/6 → ≥5/6** (Phase 2's measured baseline, not the ~0%
this plan guessed); "holding instants" up; arena lower CI bound above 50%.

> **Phase 2 sharpened the target.** All four non-creature failures are the AI declining to *cast*
> the Disenchant, not mis-targeting it: at flat `permanentValue = 0.5`, destroying an artifact is
> worth +0.75 weighted and costs −1.5 of card advantage, so passing wins. `staticPriorValue` has to
> clear the **cost of casting**, not merely outrank a sibling target. Consumer (b) — the
> `heuristicTargetRank` fix — is necessary but on its own changes nothing.

---

### Phase 7 — Rollout evaluator · *6–9 d* — the primary lever

**Plug point is one line:** `Strategist.kt:160`'s
`evaluator.evaluate(result.state, result.state.projectedState, playerId)`.

**Create** `ai/.../engine/rollout/CandidateEvaluator.kt`:

```kotlin
interface CandidateEvaluator {
    fun score(root: GameState, afterAction: GameState, playerId: EntityId, budget: DecisionBudget): Double
}
```

`StaticCandidateEvaluator` = today's behaviour, what `LEGACY_V0` uses. `RolloutCandidateEvaluator` =
the new one. **The `CardAdvisor` override path is untouched** — advisors receive the rollout score as
`CastContext.defaultScore`, so per-card overrides keep working over a much better base.

#### Scores must become win probabilities

`CompositeBoardEvaluator` returns `Double.MAX_VALUE / 2` for a win (`BoardEvaluator.kt:28`) —
averaging that with anything is meaningless. Add `rollout/WinProbability.kt`:
`squash(s) = 1 / (1 + exp(-s / SCALE))`, terminal = 1.0 / 0.5 / 0.0. Average **in probability
space**; squash the pass score the same way. This also retires the unscaled magic number at
`Strategist.kt:87`. `SCALE` falls out of Phase 9's logistic fit for free.

#### The playout engine

`PlayoutEngine.kt` owns its **own** `ActionProcessor(EngineServices(registry), computeUndo = false)`,
its own `LegalActionEnumerator`, its own state. **Do not share the Strategist's `GameSimulator`** —
`isResolving` (`GameSimulator.kt:35`) is mutable instance state and `decisionResolver` is a mutable
`var`; sharing one instance corrupts the recursion guard.

`PlayoutPolicy.kt` — **hard rule: it must never call `Strategist`, `CombatAdvisor`'s local search,
`DecisionResponder`'s simulation paths, or `chooseCommittedTargets`.** Anything that simulates inside
a playout makes the playout quadratic. In order:

1. `shouldAutoPass` → `PassPriority`, zero cost. Expect ~80% of windows.
2. `DeclareAttackers` / `DeclareBlockers` → **seed only**. Extract `CombatAdvisor`'s existing
   heuristic seed phase (it already builds `seedMap` *before* optionally running
   `improveAttackViaLocalSearch`) into `ai/.../CombatSeed.kt`; `CombatAdvisor` calls it too, so
   there's one implementation. The deadline local search is skipped in playouts.
3. Land drop → always, if legal.
4. Otherwise → **weighted random** over filtered candidates: softmax (τ≈1.0) over a zero-simulation
   priority score (`CardIntent` priority + mana value + creature bonus). **Stochastic is essential** —
   a deterministic policy makes all R rollouts within a determinization identical, collapsing K·R to K.
5. Targets → `heuristicTargetRank` (`Strategist.kt:289`) only.

**Decisions inside playouts:** `GameSimulator.trivialResponseFor` (`:163`) is private — extract it
verbatim to a top-level `TrivialDecisions.kt` and have `GameSimulator` delegate. Add
`rollout/FastDecisionResponder.kt` with an O(1) rule per `PendingDecision` type (trivial response if
any; else first legal / minimum / default assignment / random-from-legal). No simulation, no
prompt-string matching.

#### Horizon and depth schedule

Default horizon: **end of the opponent's next turn** — stop when the state next reaches
`activePlayerId == playerId && step == UNTAP` after ≥2 turn transitions, or `gameOver`, or a 150-action
safety cap. Depth schedule: after the first pass, if `|best − second| < ε` and budget remains, extend
to two opponent turns **for survivors only** — the one good idea inside the dead
`Searcher.recommendDepth`, which was deleted in Phase 0. Reimplement the idea here; don't go
looking for the file.

#### Variance reduction — all four; they are the difference between working and not

1. **Common random numbers — the big one.** `GameState.rng` is state-resident (`GameState.kt:260`),
   so at the start of rollout *(d, r)* set `state.copy(rng = GameRng(mix(rootSeed, d, r)))` and use
   the **same (d, r) seed grid for every candidate**. Candidate comparisons become paired and
   between-candidate noise mostly cancels. Without this you need ~4× the rollouts.
2. **Shared determinizations** — the same K worlds for all candidates. Same principle.
3. **Early cutoff** on `gameOver`; optionally on `|squashed − 0.5| > 0.47` (small bias, real speedup —
   put it behind a flag and A/B it).
4. **Sequential halving over candidates.** 1 rollout each → drop the bottom half → double the
   per-candidate budget → repeat until one remains or the budget expires. ~30 LOC, 2–4× effective
   rollouts on the contenders, and **inherently anytime** — this is how the `DecisionBudget` gets
   consumed: `while (!budget.expired() && survivors.size > 1) { … }`.

| Tier | Rollout behaviour |
|---|---|
| TRIVIAL | Not called |
| ROUTINE | Static evaluator only, no rollouts |
| NORMAL | Sequential halving to 2,000 ms |
| CRITICAL | Sequential halving to 5,000 ms, deeper horizon |

**Exit:** arena ≥53% with lower CI bound above 50%; `ArenaBudgetScalingTest` **monotone in budget**;
puzzle gains in sequencing / race math / board-wipe timing; p95 latency ≤5 s.

---

### Phase 8 — Determinization (fair play) · *5–7 d*

#### 8a. Extract the visibility oracle

**Create** `rules-engine/.../view/Visibility.kt` — a public object in the **same package** as
`ClientStateTransformer`, so no import churn. **Move** (don't copy) the private `isZoneVisibleTo`
(`ClientStateTransformer.kt:479`) and `isCardRevealedTo` (`:595`), plus the helpers they need
(`revealsOpponentHandsTo`, `hasActiveStaticAbility`, `hasLookAtFaceDownCreatures`).
`ClientStateTransformer` delegates.

That logic is non-trivial and correct — Mindslaver `actorFor`, 2HG `teammatesOf`, Seer's-Vision-style
reveal effects, sideboard privacy (CR 100.4 / 400.11a), conditional statics. Don't reimplement it.
`RevealedInHandTracker` (invoked at `ActionProcessor.kt:90`) maintains `RevealedToComponent` for the
"revealed into hand stays visible" rule.

**Regression net:** golden test asserting `ClientStateTransformer.transform` output is byte-identical
on a corpus of scenario states before/after.

#### 8b. The determinizer

`ai/.../engine/hidden/Determinizer.kt`:
`sample(state, viewerId, model: OpponentModel, rng: GameRng): GameState`

**Core design: permute identities, never entities.** This is what keeps everything else intact.

1. Collect hidden ids — opponent LIBRARY entities, plus HAND entities where
   `Visibility.isCardRevealedTo(state, id, viewerId)` is false.
2. **Pin** anything with `RevealedToComponent` for the viewer, referenced by `state.continuationStack`,
   targeted by something on the stack, or carrying unusual components (counters, face-down, attached).
3. Draw `|hidden|` `CardDefinition`s from the plausible pool.
4. **Rewrite each hidden entity's card identity in place** — same `EntityId`, same `OwnerComponent`,
   same zone slot, same zone ordering. Only `CardComponent` and card-derived components are replaced.
5. Shuffle library order using the passed `rng`.

Because entity ids and zone memberships are preserved, `continuationStack` references,
`RevealedToComponent`, `pendingDecision` and zone sizes are all structurally intact.
**`DeterminizerInvariantsTest`** enforces it: entity-id set, zone-key set, per-zone sizes, and the
full viewer-visible projection identical before/after.

**The riskiest bit** is rebuilding components from a different `CardDefinition`. Factor
`CardInstantiator.componentsFor(cardDef, ownerId, zone)` out of `GameInitializer`'s library-building
path so the determinizer uses the engine's own card-construction code rather than a hand-rolled copy.
Highest bug density in this phase.

#### `OpponentModel` — what pool do we draw from?

`setDeckList` is currently a no-op (`EngineAiPlayerController.kt:144-146`). Three tiers:

- **Known decklist** — wire `setDeckList` to store `Map<String, Int>`. Draw from `decklist − seen`,
  where `seen` = every card of that opponent currently visible (battlefield, graveyard, exile, stack,
  revealed-in-hand) plus a running ledger of cards played this game. **Available in the arena by
  construction**, and in any public-list format. Highest fidelity — tune here.
- **Unknown decklist (production default)** — pool from (i) cards the opponent has already revealed
  this game, upweighted 3–5× (people play 2–4 copies), and (ii) a format-legal pool
  (reuse `ConstructedDeckGenerator`'s `legalFormats` filter) restricted to the opponent's revealed
  colours and curve, weighted by the 17Lands / `LimitedCardRater` prior from Phase 6. A genuinely
  decent Bayesian-ish prior for very little code.
- **Identity permutation only (fallback)** — shuffle the *existing* hidden identities among the hidden
  entity ids. Costs nothing, needs no model; destroys knowledge of *which card is where* but not of
  *what is in the deck*. For unknown formats, Momir, Commander with an unknown list. **Document
  honestly as "cheating-lite."**

#### K and integration

**Start with K = 1.** In imperfect-information MTG most of the loss from cheating is "I know exactly
which card you're holding," not "I haven't averaged over enough worlds" — K=1 buys ~90% of the
fairness at 1/K the cost. Determinize **once per search**, at the root of
`RolloutCandidateEvaluator.score`, before the candidate loop (variance reduction #2). Raise K only if
the arena shows it beats spending the same wall clock on more rollouts. It probably won't — K
competes directly with R.

#### Expect a dip, and name it in advance

Turning determinization on **will make the AI weaker in the arena**, because today it cheats. Run
`just arena rollout-k1 rollout-k1-determinized 1000` and **record the dip as a deliverable** —
typically 2–6%. This is a fairness cost, not a bug.

**Exit — not "≥50% vs the cheating version":** the determinized agent still beats `LEGACY_V0` with
the lower CI bound above 50%; invariants test green; `ClientStateTransformer` golden test green; the
dip quantified in `docs/ai/`.

---

### Phase 9 — Texel-style evaluation tuning · *4–6 d*

Stop guessing the ~25 constants in `BoardFeatures.kt` and the 5 weights in
`AIPlayer.defaultEvaluator()` (`:179-187`).

**Weights as data.** `ai/src/main/resources/ai/eval-weights.json` (profileId → weight vector) +
`evaluation/EvalWeights.kt` with **today's values compiled in as the default**, so a missing or
malformed resource can never break the AI. `AiProfile.evalWeightsId` selects; the arena A/Bs two
weight sets as two agents, no recompile.

**Collection.** `-DarenaEmitFeatures=<path>` appends JSONL `{features, toMove, turn, gameId, result}`
at every 8th quiet state (decorrelation). Mirror `gym-trainer/.../defaults/JsonlSelfPlaySink.kt`'s
shape.

**Fit ~30 raw features, not the 5 composites.** The composites *are* the hand-tuned aggregation we're
replacing; weighting them can't fix `lifeValue`'s hand-drawn piecewise curve or `creatureValue`'s
`power × 1.0 + toughness × 0.4`. Proposed: life (mine / theirs / diff / `min(life,7)` burn-range) ·
creature count, total power, total toughness, evasive count, untapped count (each differential) ·
permanent counts by type · planeswalker loyalty sum · hand sizes · lands and untapped lands ·
graveyard sizes · summoning-sick count · turn number · is-my-turn · library size · a few
`CardIntent`-derived counts (removal in hand, threats in play). Keep the 5 composites as a fallback
profile.

**Fit in Python** — `scripts/tune_eval.py`, sklearn logistic regression with L2:
`P(win | position) = σ(w·x)`, label = the game's final result for the player to move. Gives
regularization paths, calibration curves and coefficient standard errors for free. Outputs
`eval-weights.json`, a calibration plot, **and the fitted `SCALE`** that Phase 7's
`WinProbability.squash` needs. Don't hand-roll gradient descent in Kotlin test code.

**Five overfitting guards:**
1. **Hold out by game, not by position** — positions within one game are massively correlated; a
   positional split gives a fantasy validation score.
2. **Hold out by set/format** — fit on {BLB, Portal, Onslaught}, validate on a set held out entirely.
   Report both.
3. **Validate by arena win rate, not log-loss.** A weight set with better log-loss that loses in the
   arena is **rejected**. Log-loss selects among candidates; the arena decides. This is the whole
   discipline.
4. **Diversify the data-generating agents** — collect from V0, the current best, and a
   high-temperature/ε-greedy agent. Pure self-play data collapses onto one policy's state
   distribution.
5. **Regularize and clamp** — L2 strength by held-out-by-game CV; cap coefficient magnitudes.

The puzzle suite is an independent third signal: if a tuned weight set improves log-loss *and* arena
but tanks a puzzle category, look hard before shipping.

---

### Phase 10 — Optional

- **Difficulty levels** *(1–2 d)* — nearly free once `AiProfile` + `DecisionBudget` exist. EASY = V0
  at 100 ms with an ε-greedy blunder rate; NORMAL = rollouts at 500 ms; HARD = full budget + K=2.
  Ship as `AiProfile` presets. There is currently **no** difficulty concept anywhere in the repo.
- **Replay-mined puzzles** — `ReplayReconstructor.reconstructStateAt(replay, frame)`
  (`game-server/.../replay/`) returns a full unmasked `GameState` at any frame of any recorded human
  game, with `ReplayFidelity.EXACT` verification and card pinning. Mine positions where a strong
  human and the AI diverge and promote them into the suite. Best puzzle source available — but only
  after the hand-authored core exists, and only from `EXACT`-fidelity replays.
- **Gym alignment** — fix `GameEnvironment.playGame`'s no-agent fallback (`:314`) to actually use
  `AIPlayer` as its KDoc claims; add `seed` to `EnvConfig` and stop dropping it in
  `MultiEnvService.toGameConfig` (~3 lines, and it's why `gym-self-play-testing.md` says games aren't
  seedable); add an `AIPlayer`-backed `ActionSelector`. Makes `AlphaZeroSearch` arena-benchmarkable as
  just another `ArenaAgent`. Off the critical path.
- **Full information-set MCTS** — with `PlayoutEngine`, `Determinizer`, `MeaningfulActionFilter` and
  `DecisionBudget` in place this becomes a contained change, and `gym-trainer/.../AlphaZeroSearch.kt`
  already has a working PUCT loop over O(1) `fork()`. Its gap is `StructuredDecisionResolver`, which
  collapses 12 of 18 decision types into one forced *random* edge (`:264-294`, `:408`) — targets,
  damage assignment and mana-source selection are effectively unsearched. Deliberately deferred:
  prove rollouts work first.

---

## Recommended order

```
0̶ → 1̶ → 2̶ → 3̶ → 4̶ → 6 → 7 → 8 → 9 → 10        (5a runs alongside, on its own schedule)
```

Phases 0–4 are done — all four scoreboards exist, the evaluator is no longer one-eyed at a pod
table, and the budget ladder is calibrated and monotone before any rollout depends on it.
**Phase 5 has left the critical path** — simulation is already ~2× the speed the rollout budget
needs, so 5a is now an independent engine-perf task and 5c is almost certainly not justified.
Next up is **Phase 6, CardIntent** — the highest strength-per-effort item left, and the first phase
since 3 that is meant to move a win rate rather than enable one.

Ranked by strength-per-effort, independent of ordering:

| Rank | Phase | Effort | Why |
|---|---|---|---|
| 1 | **6** CardIntent | 5–7 d | Removes flat-0.5 blindness to every artifact/enchantment/PW; feeds 4 other consumers. |
| — | **3̶** multiplayer eval | 1–2 d | **Done.** Was ranked 1: the evaluator scored a whole pod as a duel against one arbitrary neighbour, and read a stale life component in 2HG. |
| 2 | **9** Texel tuning | 4–6 d | Replaces ~25 guessed constants with fitted ones. Cheap once the arena exists. |
| 3 | **7** rollout evaluator | 6–9 d | Highest *ceiling*; the real lever. Costly, and worthless without the rest. |
| 4 | **1̶ / 2̶** arena + puzzles | 7–10 d | No direct strength — but nothing above is *knowable* without them. Both done. |
| — | **4̶a** auto-pass filter | 3–5 d | **Done.** Demoted by Phase 0 and rescoped to "skip the enumeration": 40% of priority windows now never call the enumerator. Also closed Phase 1's 889-of-945 illegal-action finding, which turned out to be a targeting bug. |
| — | **4̶b** DecisionBudget | 2–3 d | **Done.** Enabling infrastructure, and it measures like it — neutral in the arena, monotone in the scaling ladder. |
| 7 | **8** determinization | 5–7 d | **Costs** strength (fairness price). Do it because search over a cheated state is search over a lie. |
| 8 | **5a** hoist O(n²) scans | 3–5 d | Demoted by Phase 0: no longer needed for rollouts. Still a standing engine win — `findAvailableManaSources` was 59% inclusive. |
| — | **5c** persistent collections | 4–6 d | **Drop** unless a fresh profile demands it. Phase 0 measured throughput at ~2× the required rate. |
| — | projection incrementalization | 2+ wk | **Skip.** 7.4% in the profile, 11% measured cold, and already cached. |

Phases 0–2 are ~10 days of pure infrastructure before any strength lands. That is the correct trade:
without them every subsequent claim is unfalsifiable. Phase 0 already earned its keep by deleting
two phases' worth of assumed work.

---

## Risk register

| Risk | Detection | Mitigation |
|---|---|---|
| ~~**Search makes the AI slower AND weaker**~~ | `ArenaBudgetScalingTest` — strength must be monotone in budget | **Instrument built and calibrated in Phase 4, before rollouts exist, and it passes**: 55.7% / 54.0% / 55.3% up the ladder, every lower CI bound above parity. Still the standing gate for Phase 7 — non-monotone ⇒ rollouts are noise, and the fix is the leaf evaluator (6, 9), not more samples |
| **Determinization dip read as a regression** | Certain to happen | Named in advance; Phase 8's bar is "still beats V0", not "beats the cheating version" |
| **Overfitting to self-play** | Held-out set + gauntlet + puzzles | ≥3 collecting agents; hold out by game *and* by set; arena is the arbiter, not log-loss |
| **Non-transitive strength** | Full pairwise matrix, not just Elo | Must beat V0 *and* previous version; lose to no gauntlet member worse than 45% |
| ~~**Combat's 1 s cap fights the global budget**~~ | Blocking puzzle category; `DecisionBudgetTest` | **Closed in Phase 4b** — combat declaration is always CRITICAL and `MAX_BLOCK_SIMULATIONS = 10` is a floor on every tier, asserted directly, so combat can never search *less* than it did before the budget existed |
| **`GameSimulator.isResolving` / `decisionResolver` thread-safety** | Nondeterminism across arena reruns at the same seed | One `AIPlayer` per *seat* per game, never shared. `ArenaHarnessTest` asserts identical outcomes at 8 threads and at 1 — **green as of Phase 1**, so the AI is deterministic today. `PlayoutEngine` must own its own processor when Phase 7 lands |
| **A pod result is read against 50%** | Certain to happen — every other number in this plan is | The null is **1/teams**: 33% at `ffa3`, 25% at `ffa4`, 50% at `2hg`. `ArenaReport.podSummary` prints the null on the same line as the win share and states it in the verdict sentence |
| **A multiplayer harness trusts `GameState.turnNumber`** | Used to read every pod game as wedged after the first elimination | `turnNumber` was a round counter that only advanced for `turnOrder.first()`, who may be dead. **Closed**: it counts player turns now, so it is a sound clock at any table size (`backlog/multiplayer.md`) |
| **Persistent collections break persisted sessions / committed replays** | `GameStateSerializationFormatStabilityTest` golden JSON | Serializers delegate to standard `MapSerializer`/`ListSerializer` ⇒ byte-identical wire format |
| **`CardInstantiator` extraction produces malformed cards** | `DeterminizerInvariantsTest` + full engine suite | Reuse `GameInitializer`'s own construction path, don't hand-roll |
| ~~**Arena wall-clock makes the merge gate unaffordable**~~ | Measured in Phase 1, re-measured in Phase 4b | **Closed.** 1,000 games is still ~3.5 min *with* a budget, because `SearchAllowances` spends a tier as a count of simulations rather than as wall clock. The reduced-budget arena mode was never needed and was not built |
| ~~**`AiProfile.LEGACY_V0` silently drifts**~~ | `FrozenBaselineTest` golden action-stream hash | **Closed in Phase 1** — one fixed all-vanilla Portal game, SHA-256 over the action stream. All-vanilla so the hash tracks *AI* behaviour, not every card that ships |
| ~~**`respondBudgetModal` zero-cost infinite loop**~~ | Arena stuck-game detector | **Closed in Phase 0** — free modes are taken once; regression test committed |
| ~~**`Searcher.kt`'s `Double.MIN_VALUE/2` gets accidentally revived**~~ | — | **Closed in Phase 0** — file deleted rather than repaired |

---

## Verification

Per phase, in addition to the exit criteria above:

- **Build gates:** use the `verify` skill. Phases touching `rules-engine` (4a, 5a, 5c, 8a) need the
  full `:rules-engine:test` + `:game-server:test` run, not just `:ai:test`. Always via `just`, never
  raw `./gradlew` — parallel agents each spawn their own daemons and thrash the box.
- **Perf validation loop** (from `engine-performance.md`): after each perf step, `just test-rules` →
  `just benchmark-random 200 BLB` vs baseline → re-profile with async-profiler and confirm the
  targeted leaf shrank.
- **SDK docs:** no card-SDK surface changes are expected. If any phase adds an SDK primitive,
  `docs/card-sdk-language-reference.md` updates in the *same* change.
- **New docs:** `docs/ai/baseline-metrics.md` (Phase 0, appended per phase) ·
  `docs/ai/measurement.md` (Phase 1 — how to read an arena report; the promotion rule) ·
  `docs/ai/architecture.md` (profile / budget / evaluator seams, once Phase 7 lands).
- **End-to-end sanity:** after Phases 7 and 8, play a real game via `just server` and confirm decision
  latency feels right and the AI no longer plays around cards it shouldn't know about.
- **Standing regression set** after each merge: `just arena-puzzles` (seconds, from Phase 2) +
  `just arena <prev> <new> 1000` (the gate) + `just arena <new> v0 1000` (the compounding check).
  Both 1,000-game runs are ~3.5 min each today. Add `just arena-pod ffa3 <new> v0 300` (~10 min,
  from Phase 3) whenever the change touches evaluation — it is the only run that exercises more than
  one opponent.

---

## Cross-reference: `engine-performance.md` is partly stale

That doc's header still says *"Analysis complete, no fixes applied yet."* Verified against the code
on 2026-07-27:

| Step | Status |
|---|---|
| 1 — remove kotlin-reflect from component keys | **Done** |
| 2 — key components by `Class<*>` | **Done** — `ComponentContainer.kt:23` is `Map<Class<*>, Component>`, lookups at `:29/:45/:52` use `T::class.java` |
| 3 — memoize `getBattlefield()` | **Done** — `GameState.kt:808` returns a `by lazy cachedBattlefield` built in one pass |
| 4 — hoist battlefield scans in ward / trigger / mana detection | **NOT done** — this plan's Phase 5a |
| 5 — reduce component-map copy churn | **NOT done** — this plan's Phase 5c |

So the `~404 actions/sec/thread` baseline is **pre-Steps-1–3** and the current number is still
unknown — Phase 0 did *not* re-run `just benchmark-random 200 BLB`. What Phase 0 did measure is the
AI-driven workload (`just benchmark-throughput`), which puts `ActionProcessor.process` at ~3,400/sec
per thread. The two benchmarks use different action mixes and are not directly comparable, so the
random-action baseline still needs one clean run before Step 4 / Phase 5a starts.

Step 4 specifically, still live:
- `ManaSolver.findAvailableManaSources` (`:916`) loops candidate entities and calls
  `getStaticGrantedManaAbilities(entityId, state)` (`:945` → `:1823`), which itself loops
  `state.getBattlefield()` → **O(n²) per enumerate**, and `findAvailableManaSources` was **59%
  inclusive**.
- `TriggerAbilityResolver.isWardSuppressed` (`:662`) still does `state.getBattlefield().any { … }`
  from inside a per-entity path (`:496`).

`getBattlefield()`'s memoization removed the *allocation* cost of these scans but not the
*iteration*. Phase 5a is the remaining half — and it is a standing engine win independent of the AI.
