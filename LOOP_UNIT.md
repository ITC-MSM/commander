# u38 — Doom Reigns Supreme (+ one capped-free-cast bound)

**Card.** Doom Reigns Supreme [MSH 96] — {1}{B} Enchantment — Plan. Accumulator: "Whenever a
Villain you control enters, each opponent loses 1 life and you gain 1 life. Put a plan counter on
this enchantment." Payoff: "When the fifth plan counter is put on this enchantment, sacrifice it.
When you do, target opponent exiles the top five cards of their library. You may cast up to two
spells from among the exiled cards without paying their mana costs."

**Triage (nobody did it before me).** Everything except the "up to two" bound composes from
existing vocabulary. The set's own gap doc had already reached the same conclusion
(`backlog/sets/marvel-super-heroes/mechanics.md` → "Capped free cast — Doom Reigns Supreme"); I
re-derived it from Scryfall + the code before reading that, and the two agree.

**Primitive — `maxCasts` on `CastAnyNumberFromCollectionWithoutPayingCostEffect`.** A
generalization of the existing free-cast loop, not a new effect type. `null` (the default) is the
old uncapped "any number" behaviour, so Villainous Wealth / Etali / Kotis / The Tale of Tamiyo are
byte-identical. Engine: the executor no-ops when the budget is `<= 0` (before offering a decision),
puts the budget on `CastAnyNumberFromCollectionContinuation`, and the resumer re-enters the loop
with `maxCasts - 1` when the pick will actually be cast. Facade
`Effects.CastUpToNFromCollectionWithoutPayingCost(from, maxCasts)` (which rejects a non-positive
cap). `docs/card-sdk-language-reference.md` updated in the same change.

**Composition for everything else.** Villain-enters filter is
`GameObjectFilter.Permanent.withSubtype(VILLAIN).youControl()` — "a Villain you control" selects by
subtype, so a noncreature permanent that gained it would count (every printed Villain is in fact a
creature, so the two filters agree on today's pool). The drain is `LoseLife(1, EachOpponent)` + flat `GainLife(1)`, *not* `DrainLife` (the
gain is 1 regardless of opponent count / prevention) — the Kang, Temporal Tyrant idiom. The
threshold is the Plan cycle's `Triggers.countersPlacedOn(SELF)` +
`Conditions.SourceCounterCountAtLeast(PLAN, 5)`; exact because the payoff sacrifices its own
source, so a sixth counter can never land. The payoff is the Villainous Wealth pipeline
(`Patterns.Library.exileTop(5, TargetOpponent)` → `FilterCollection(Nonland)`) plus the capped cast,
inside a mandatory `ReflexiveTriggerEffect` (CR 603.12, verified against the local comp rules).

**Gate.** `just test` → 13,280 tests, 1 failure, 0 errors (counted from `build/test-results/*.xml`).
The one failure is `AIPlayerTest > AI can evaluate board state`, a `TimeoutCancellationException` in
the `ai` module — not in this diff, and green on a `just test-ai` re-run on the quiet box (all 14
cases PASSED). All three `DoomReignsSupremeScenarioTest` cases passed inside the full run. The box
was shared with a sibling container's build throughout (load peaked at 25, free memory at 68 MiB);
an earlier attempt was OOM-killed outright. `just rebless-cards` → only `MSH.json` moved, 139
insertions and **zero** deletions, and the only added `"name"` is `Doom Reigns Supreme`; no other
set's golden moved. `just check-card-printing "Doom Reigns Supreme"` ok (MSH is the only real
printing). `just fix-backlog` → 273/276.

**Mutation-proved.** The resumer's decrement was neutered (`it - 1` → `it - 0`) and the suite
re-run: *exactly* "the loop stops after two casts even though a third card is still castable" went
red; the ETB/threshold test and the decline-early test stayed green. Restored and re-run green.

## Things I'm unsure about — please look

- **The cap is enforced in the loop, not by a separate counter.** The budget decrements only in the
  resumer. A declined pick ends the loop without spending anything, and (after review) a pick whose
  required target has no legal choice — the cast can't initiate, CR 601.2c — leaves the budget
  alone too, though that card is dropped from the pool. The one case that still spends a cast
  without a spell reaching the stack is a cast that initiates and then errors inside
  `CastSpellHandler`, which isn't knowable before the loop's tail effect is built. The number is
  never surfaced as engine state a test could read directly; the tests prove it by what is and
  isn't offered.
- **`maxCasts` is not exposed on the paid facade** (`Effects.CastAnyNumberFromCollection`). The
  field is on the shared effect, so a paid capped form is one keyword argument away, but no card
  needs it and I did not add a facade for it.
- **I edited `backlog/.../mechanics.md`**: deleted the now-closed "Capped free cast — Doom Reigns
  Supreme" section, struck gap (2) on Baron Helmut Zemo (same missing field, now shipped), and
  decremented the header's blocked count 22 → 21. I decremented by exactly one for the card I
  shipped; I did **not** re-audit the other sections against the current SDK, so that number is
  only as good as it was before.
- **The prompt text changed for capped loops only** — "Choose a spell to cast for free (N
  remaining), or select none to stop" (review reworded it from "up to N more", which reads as N *in
  addition to* the card being chosen). Uncapped loops keep their old string verbatim. Nobody has
  looked at how that reads in the real client.
- **Not done: manual playthrough in the web client, UX pass, e2e, AI-heuristic review.** No new
  decision type and no new `GameEvent`, so `SELECT_CARDS` routing and `ClientEvent` are untouched —
  but that is an argument, not an observation.
- **Multiplayer**: "each opponent loses 1 life" and "target opponent" are both modelled with the
  proper multi/target references. A three-player test now pins the drain half (both opponents lose
  1, the controller still gains exactly 1); the *targeted* payoff is still only exercised
  two-player.
