# loop-msh-u31 — Invisible Woman, Sue Storm (MSH)

**Kind:** feature · **Base branch:** `loop-msh-u30` (local, **not** merged upstream). u30 sits on u28
which sits on `origin/main`, so `main` *is* an ancestor now, but the u28/u30 commits below this
branch are not upstream yet — this waits for them to land before it can be opened on its own.
Reviewer: diff with `git diff loop-msh-u30...HEAD`.

The rebase onto the current u30 moved the card and its scenario test into the per-era modules that
`origin/main` now uses: `mtg-sets/2026/src/main/.../msh/cards/InvisibleWomanSueStorm.kt` and
`mtg-sets/2026/tests/src/test/.../InvisibleWomanSueStormScenarioTest.kt` (both byte-identical to
their pre-rebase contents). The mechanic-level `CountersPlacedBatchTriggerTest.kt` uses
`GameTestDriver`/`TestCards` and locally-defined cards, so it stays in
`rules-engine/src/test/.../triggers/`.

## The primitive

- **What:** `EventPattern.CountersPlacedEvent.batch: Boolean = false` — the "one or more counters on
  **one or more** permanents" batch multiplicity (CR 603.2c), alongside the existing per-permanent
  reading of the same pattern.
- **Where:**
  - SDK: `mtg-sdk/.../scripting/EventPattern.kt` (the `batch` field + description branch),
    `mtg-sdk/.../dsl/Triggers.kt` (`countersPlacedOn(..., batch = false)`).
  - Engine: `rules-engine/.../event/TriggerDetector.kt` →
    `detectCountersPlacedBatchTriggers` (new pass, called from `detectTriggers` next to the tap/untap
    batch passes); `rules-engine/.../event/TriggerMatcher.kt` → the per-event `CountersPlacedEvent`
    branch returns `false` for batch patterns, and both paths narrow through one shared
    `matchesCountersPlacedAxes` (the `DamageTriggerDetector` → `matchesDealsDamageTrigger` shape), so
    the two multiplicities cannot drift apart.
- **Pattern followed:** the existing `TapEvent.batch` / `UntapEvent.batch` / `DealsDamageEvent.batch`
  shape — same flag name, same "per-event matcher skips it, dedicated detector fires once" wiring,
  same `TriggerCategory` reused (`COUNTERS_ADDED`; no new category, exactly like tap/untap).
  **No new batching mechanism was invented.**
- **Semantics:** every other axis (counter type, `placedBy`, `firstTimeEachTurn`, recipient filter,
  `TriggerBinding.SELF/OTHER`) *narrows* the batch rather than discarding it. The first matching
  recipient is bound as the triggering entity, all matching recipients as `capturedEntityIds` (as
  `detectUntapBatchTriggers` does); `TriggerContext.counterCount` is the batch total.
  Separate resolutions are separate detection passes and so separate batches. A placement of zero
  counters is not a placement, for either multiplicity.

## The card

Invisible Woman, Sue Storm — {4}{W} Legendary Creature — Human Hero 2/5, lifelink, MSH #17.
"Whenever you put one or more +1/+1 counters on one or more other Heroes you control, you may create
a 0/4 colorless Wall creature token with defender." Uses `Triggers.countersPlacedOn(batch = true,
placedBy = Player.You, binding = OTHER, filter = Creature.youControl().withSubtype(HERO))` plus
`optional = true` and `Effects.CreateToken`. No new effect vocabulary.

## Tests

- `rules-engine/src/test/kotlin/.../triggers/CountersPlacedBatchTriggerTest.kt` — the primitive,
  driven directly through `TriggerDetector.detectTriggers` with synthesized `CountersAddedEvent`s.
  Every case is asserted against a **per-permanent twin observer** built from the same pattern with
  `batch = false`, so the flag itself is what the assertions discriminate.
- `rules-engine/src/test/kotlin/.../scenarios/InvisibleWomanSueStormScenarioTest.kt` — the card
  end-to-end, driven by an **inline** "put a +1/+1 counter on each creature you control" sorcery.
  (Cathars' Crusade has that exact printed text and was the first driver, but it re-triggers off the
  Wall entering — a genuine loop with this card — so the driver is inline.)
- `manual-scenarios/sets/msh/loop-msh-u31-invisible-woman-sue-storm.json` — playtest scenario. It
  *does* pair Invisible Woman with Cathars' Crusade, deliberately, so the loop is visible by hand.
- **Mutation-checked:** batching switched off in the detector → the three multi-recipient engine
  assertions and the "one Wall" scenario assertion went red, the other 14 stayed green; restored.

## Gate

`just test` after the review corrections — **10959 tests, 1 failed**, and that one is the known
`ConniveTargetingTest` `TimeoutCancellationException` contention flake, which passes alone
(`just test-class ConniveTargetingTest` → 2/0). Not cache-served: 21 tasks executed, every module's
`test` task ran except `:mtg-search:`/`:mtgish-tooling:` (inputs untouched).
`just rebless-cards` moved no snapshot (`MSH.json` was already blessed and the card is unchanged);
`just check-card-printing "Invisible Woman, Sue Storm"` ok; backlog ticked + `just fix-backlog`
(MSH 260/276).

**Still owed — that green no longer covers this tree.** The branch has since been rebased onto the
rewritten `loop-msh-u30` (new base, and the card/test changed modules), so the 10959-test result
above is stale. This diff reaches `mtg-sdk` (`EventPattern`, `Triggers`) as well as `rules-engine`
and `mtg-sets`, so the re-run is the **full** `test` suite, not the engine-only gate — and it must
be re-run again after the eventual rebase onto `origin/main`.

## Things I'm unsure about / worth a reviewer's eye

- `counterCount` on a batch trigger is the *sum* over the collapsed placements. No shipped card reads
  it from a batch trigger, so this is a judgement call, not a verified requirement.
- The batch is scoped **per detection pass**, not per placing effect. Two effects that somehow
  resolve into the same event batch would collapse into one firing. I did not find a path where that
  happens (each resolution triggers its own detection pass) but I did not exhaustively prove it.
- I audited the `CountersAddedEvent` emission sites by grep and read the multi-recipient ones
  (`AddCountersToCollectionExecutor`, `ProliferateExecutor`, `AddCountersExecutor` via
  `ForEachInGroup`). I did **not** individually verify every one of the ~25 emit sites.
- Pre-existing, **not** touched by this unit: `AddCountersToCollectionExecutor` and
  `DistributeCountersAmongTargetsExecutor` do not check `ProjectedState.canReceiveCounters`, unlike
  `AddCountersExecutor` / `AddCountersUpToExecutor` / `ProliferateExecutor` / `ExploreEffectExecutor`.
  The check is per-executor, not central, so a no-op placement from those two *would* enter a batch —
  the engine KDoc, the SDK reference and the scenario-test comment all now say so instead of claiming
  the batch excludes it by construction. My scenario test for the prohibition goes through the
  `ForEachInGroup` + `AddCounters` path, which does guard, and proves only that path. Fixing those two
  executors is its own unit: they also add the counters to the component.
- No mtgish bridge entry was added: the counters-placed IR tag
  (`WhenAnyNumberOfCountersOfTypeArePutOnAPermanent`) is already `supported`, and this is a
  multiplicity variation on it rather than a new capability or IR tag. The emitter still declines
  non-SELF subjects to SCAFFOLD, which is the correct behaviour.
- No web-client change: the card surfaces only the existing optional-trigger yes/no decision.
