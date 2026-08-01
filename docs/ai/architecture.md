# Engine AI — Architecture

The seams the engine AI is built from, and which phase of
[`backlog/engine-ai-improvement.md`](../../backlog/engine-ai-improvement.md) put each one there.

Read this before changing how the AI *decides*. For how to measure a change, see
[`measurement.md`](measurement.md); for the numbers, [`baseline-metrics.md`](baseline-metrics.md).

---

## The one-paragraph version

An `AIPlayer` is a `GameSimulator` ("what happens if I do X?"), a `BoardEvaluator` ("how good is
this?"), a `Strategist` ("which action?") and a `DecisionResponder` ("how do I answer this
prompt?"). Everything a phase adds hangs off an **`AiProfile`** — a named, reproducible
configuration that the arena treats as an agent. `AiProfile.LEGACY_V0` pins every feature off and
reproduces the original greedy 1-ply AI by construction; it is the permanent reference opponent and
must never be "improved".

---

## The four seams

| Seam | Question it answers | Added by |
|---|---|---|
| `AiProfile` | which version of the AI is this? | Phase 1 |
| `MeaningfulActionFilter` | is this window worth thinking about? | Phase 4a |
| `DecisionBudget` / `BudgetPolicy` | how hard should this decision be searched? | Phase 4b |
| `CandidateEvaluator` | how is a candidate's resulting position scored? | Phase 7 |

Plus `IntentCatalog` (Phase 6), which is card knowledge rather than a control-flow seam: it feeds
`BoardPresence.permanentValue`, `TargetSelection.rank`, `HoldPolicy` and the rollout policy's
priors.

Phase 9 adds a second leaf implementation behind the existing `BoardEvaluator` seam. Composite
profiles load from `eval-weights.json`; fitted raw-feature profiles load from
`raw-eval-weights.json`. `AiProfile.evalWeightsId` selects either kind, and malformed raw profiles
fall back to the compiled composite default. The raw evaluator extracts projected battlefield facts
once per evaluation and applies the fitted linear vector; it is internal AI behavior with no SDK,
server, or client contract.

---

## A decision, end to end

```
AIPlayer.chooseAction(state)
 ├─ MeaningfulActionFilter.canAutoPassWithoutEnumerating  → PassPriority, no enumeration  (Phase 4a)
 └─ Strategist.chooseAction
     ├─ combat declaration?  → CombatAdvisor (seed from CombatSeed, then local search)
     ├─ candidates           ← MeaningfulActionFilter.filterMeaningful                    (Phase 4a)
     ├─ budget               ← BudgetPolicy.budgetFor(state, player, candidates)          (Phase 4b)
     ├─ pass 1: simulate each candidate (and the pass) to its quiet state
     ├─ pass 2: CandidateEvaluator.scoreAll(root, leaves, player, budget)                 (Phase 7)
     ├─ pass 3: HoldPolicy timing delta + CardAdvisor override, in raw evaluator units    (Phase 6)
     └─ best > pass ? commit targets (refined by simulation) : pass
```

The three passes are the shape Phase 7 needed. The evaluator sees **every** candidate at once, which
is what lets it *allocate* effort rather than spend a fixed amount per candidate — sequential
halving is impossible with a one-candidate-at-a-time API. For `StaticCandidateEvaluator` the batch
is `map(::score)`, so `LEGACY_V0` is bit-identical; `FrozenBaselineTest` is what proves it.

---

## Hidden information (Phase 8)

Rollout profiles may enable determinization. Before any candidate is simulated, `Strategist` asks
`Determinizer` for one world consistent with the acting player's view and uses that same root for
every candidate. Sampling before the one-ply simulation matters because pending decisions reached
by that simulation can inspect hidden zones too. Sharing the world is essential: independently
sampling candidates would make hidden-information variance look like move quality.

`Visibility` in `rules-engine/view` is the common oracle for both client masking and AI
determinization. The determinizer rewrites identities, never entities: entity IDs, zone membership,
pending decisions, targets and continuations remain intact. Individually revealed cards and cards
carrying runtime state are pinned. With a known decklist it samples from `decklist − seen`;
otherwise it permutes the existing hidden multiset. That fallback removes exact hand and
library-order knowledge, but remains “cheating-lite” because it knows which cards exist unseen.

Phase 8 starts with one shared determinization per search. More worlds compete directly with rollout
count, so raising K requires an arena result showing it buys more strength at the same wall-clock
budget.

---

## Scores are in raw evaluator units, everywhere above the leaf

The pass comparison, the hold policy's `±1.5`, and a `CardAdvisor` returning `defaultScore + 2.0`
are all written in `BoardEvaluator` units. So `CandidateEvaluator.score` returns those units too,
whatever it did internally. `RolloutCandidateEvaluator` averages in probability space — it must, see
below — and converts back at its own boundary. New mechanism underneath the old vocabulary.

---

## The rollout evaluator (Phase 7)

```
RolloutCandidateEvaluator          allocates the budget, owns the seed grid
  └─ PlayoutEngine (Playouts)      plays one short game forward
      ├─ PlayoutPolicy             what a player does at a priority window — never simulates
      │   ├─ CombatSeed            the heuristic half of CombatAdvisor
      │   └─ TargetSelection       the heuristic half of the Strategist's target pick
      └─ FastDecisionResponder     O(1) answer per PendingDecision type
```

**`PlayoutEngine` owns its own `ActionProcessor` and `LegalActionEnumerator`.** Not tidiness:
`GameSimulator.isResolving` is a mutable recursion guard and `decisionResolver` is a mutable `var`,
so a playout sharing one would corrupt the guard of the simulation that spawned it.

**Nothing inside a playout may simulate.** A playout that simulates is quadratic, and cheapness is
the entire reason a rollout mean beats one static evaluation. That is why `CombatSeed` and
`TargetSelection` exist as extractions rather than as calls into `CombatAdvisor` and `Strategist`.

**The policy must be stochastic.** A deterministic policy makes all R playouts of a candidate
identical, collapsing R samples into one.

### Three things that are easy to get wrong

**1. Averaging must happen in probability space.** `CompositeBoardEvaluator` returns
`Double.MAX_VALUE / 2` for a won game, so the mean of "one win and three even boards" overflows to a
number that beats everything. In probability space those four samples are `0.625`, which is what
"one line in four wins outright" means. `WinProbability` is the conversion.

**2. Squash the *delta from the decision's root*, never the absolute score.** The evaluator has no
calibrated zero: `ThreatAssessment` prices "we can never kill them" with a 99-turn sentinel, so an
ordinary position where one side has no creatures scores around −176 while a close board is single
digits. Squashing absolutely pins every candidate to the same clamped extreme and the search
reports *certain loss for every line* — measured, on the puzzle suite, before the subtraction
existed. Because the baseline is the decision's root it is identical for every candidate, so the
offset cancels and only the differences the Strategist compares survive.

**3. A pure rollout is weaker than the greedy AI it replaces.** Passing in your own main phase does
not end the turn, it advances a step — and the playout policy then casts the very spell you just
declined. Two turns downstream the two lines have converged, the rollout mean cannot see the tempo
difference, and the tie goes to passing (48/66 against `v0`'s 55/66).
`RolloutSettings.staticWeight` mixes the static leaf back in; the two estimators are blind to
different things and the mixture recovers both.

**4. The horizon has a price, and it shows up as impatience.** `respond-02` — "do not spend the only
Counterspell on a 2/2 with seven lands still open" — is a puzzle `v0` solves and the rollout does
not. Countering shows a concrete gain inside the two-turn horizon; the cost of not having the card
later falls outside it. A longer horizon is not the fix. Knowing what a card is *for* is, which is
`CardIntent`/`HoldPolicy` territory — `production`, which has both, keeps it.

### Reproducibility

`ArenaHarnessTest` asserts identical outcomes at 8 threads and at 1, and `FrozenBaselineTest` hashes
`LEGACY_V0`'s action stream. Both survive Phase 7 because **nothing in the search reads a clock or a
counter**: every playout seed derives from the root state, and every allocation from
`SearchAllowances.rolloutPlayouts`. `DecisionBudget.expired()` is consulted only as the hard safety
stop it was designed to be. A rollout search that spent wall clock would produce a different move
under load than idle, and every arena rerun would be a different game.

---

## Where the cost is

A rollout decision costs *N* playouts × ~40 engine actions, so at the ~60 a 2 s tier affords it is
~2,400 `process()` calls and a rollout arena game is ~1,000× a `v0` game (~70 s against ~0.07 s).

**It does not need them.** The ladder (`v0-rollout-4/8/16/32`, via `RolloutBudgetPolicy`) measured
strength rising from 4 to 8 playouts and then flat: 8× more buys nothing (4-vs-32 is 50.7%, CI
[47.5%, 53.7%] over 400 games). The rollout term is bias-limited, not variance-limited — it carries
a quarter of each score, common random numbers already pair its comparisons, and no amount of
sampling reveals the tempo it cannot see. `SearchAllowances.NORMAL_PLAYOUTS` therefore ships at
**16** rather than 64.

That ladder is also Phase 7's safety net, the analogue of `ArenaBudgetScalingTest` one level down:
strength must never *fall* with more playouts, or the search is generating noise rather than signal.
Saturation is fine; inversion is the alarm.
