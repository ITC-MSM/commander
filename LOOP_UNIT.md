# loop-msh-u07 — Ward with a non-listed cost

Feature unit: two new `WardCost` variants plus the two MSH cards they unblock.

## SDK

- `WardCost.PlayerCounters(counterType, amount)` — counters placed on the **paying player**
  (CR 122.1). Facade `KeywordAbility.wardPlayerCounters(Counters.POISON, 5)`. The only ward cost with
  no affordability gate.
- `WardCost.Choice(options)` — OR disjunction, sibling of `Composite`'s AND. Facades
  `KeywordAbility.wardChoice(...)` and the named `wardDiscardOrPay("{2}")`. Modelled on
  `AdditionalCost.Choice` / `PayCost.Choice`; the prompt follows `CostPaymentService.choicePrompt`
  (payable options only + trailing decline, reduced list stored on the continuation).
- `WardCost.clause` — new property: the self-contained verb phrase ("discard a card", "pay {2}") as
  opposed to `description`'s object phrase. Only `Choice` reads it, so no existing card's rendered
  text changes.

## Engine

- `WardCounterEffectExecutor`: two new branches; `canPayWardCost` is now the single source of truth
  for "unpayable → counter without a prompt" and the four pre-existing inline can-pay checks were
  folded into it.
- Two continuations (`CounterUnlessPlayerCountersContinuation`, `WardCostChoiceContinuation`) in
  `ManaContinuations.kt`, registered in `Serialization.kt`, resumed in
  `ManaPaymentContinuationResumer`. The choice resumer routes the chosen option through the existing
  `chargeNextWardPartOrNull`, so the spell-left-the-stack guard and composite chaining stay
  single-sourced.
- Counters are placed via the ordinary `AddCountersEffect` executor with the payer as controller —
  replacement effects, `CountersAddedEvent` and the ten-poison SBA all follow for free.

## Cards

- **The Serpent Society** [226] — deathtouch; `wardPlayerCounters(Counters.POISON, 5)`; OTHER-bound
  dies trigger filtered by `withKeyword(DEATHTOUCH)` (matched against LKI) → `Effects.Sacrifice`
  nontoken over `Player.EachOpponent`.
- **Titania, Rugged Rumbler** [235] — `Costs.additional.DiscardOrPay("{2}")` +
  `KeywordAbility.wardDiscardOrPay("{2}")`. Same printed shape on both rails, deliberately
  matching facade names; the types stay separate because the additional cost's mana leg folds into
  the spell's mana cost at cast time and the ward's does not.

## Also

- `docs/card-sdk-language-reference.md` — ward section extended for both variants and the two-rail
  note.
- `backlog/sets/marvel-super-heroes/cards.md` — both ticked, count 239 → 241.
- `backlog/sets/marvel-super-heroes/mechanics.md` — section rewritten as SHIPPED; blocked count
  33 → 31.
- `mtgish-tooling` `CardStructure.kt` — `wardKeywordLine` learned `_Cost: "Or"` via a new
  `wardCostExpr` leg renderer; existing single-cost branches unchanged, no corpus ward uses `Or`, so
  no golden moves. The IR has no player-counter cost tag, so `PlayerCounters` is not taught.

## Things worth a second opinion

`WardPlayerCountersTest`, `WardCostChoiceTest` (engine-level, one per mechanic),
`TheSerpentSocietyScenarioTest`, `TitaniaRuggedRumblerScenarioTest` (one per card).

## Unsure / worth a reviewer's eye

- `WardCost.clause` duplicates information with `description`. The alternative — making `description`
  the full clause everywhere and dropping the per-renderer verb prefixes — is cleaner but rewrites
  the rendered text of every existing ward card and moves a lot of snapshots; I chose not to.
- `canPayWardCost` for `Composite` is a snapshot check (all parts payable *now*). Paying an earlier
  part could in principle make a later one unpayable; the per-part handler still counters at that
  point, so behaviour is right, but a `Choice` over a `Composite` could offer a leg that later fails.
  No printed card has that shape.
- The ward `Choice` picker labels are generated from `clause` ("Discard a card" / "Pay {2}" /
  "Counter spell"). Not seen in a running client.
