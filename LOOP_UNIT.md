# u22 — Baron Helmut Zemo (+ three SDK generalizations)

**Card.** Baron Helmut Zemo [MSH 87] — {B}{B}{B} Legendary Creature — Human Noble Villain 3/3.
"Whenever you cast a black spell from your hand, Baron Helmut Zemo connives. / Boast — Exile any
number of black cards from your graveyard with fifteen or more black mana symbols among their mana
costs: Copy those exiled cards. You may cast up to three of the copies without paying their mana
costs." Oracle text taken from Scryfall, not from the planner's paraphrase (which was accurate).

**I inherited ~520 lines of uncommitted WIP from a predecessor agent on this same unit.** I
re-triaged the card from the code on this branch and **kept the design** — see below for why each
piece is right — then added everything it was missing (the card's own scenario test, the primitive's
own scenario test, the DSL reference update) and fixed two real bugs in its one test file. Nothing
in it had ever been compiled or run.

## Re-triage against the eleven units under this branch

The planner's triage was nine days stale. What I checked before adding anything:

- **`Effects.CastUpToNFromCollectionWithoutPayingCost(from, maxCasts)`** already exists (Doom Reigns
  Supreme) and is exactly "cast up to three of the copies without paying their mana costs" — used
  as-is. `Effects.GrantNextSpellFreeCast` + `maxCasts` (u38/u40) is a *pending rider on the next
  spell you cast*, a different shape; not used.
- **`Effects.CopyCollectionIntoCollection`** already exists (The Tale of Tamiyo IV) and already
  creates the copies in exile per CR 707.12 — used as-is.
- **`ManaCost.coloredSymbolCount(colors)` (u36)** already exists and already backs
  `CardPredicate.ColoredManaSymbolsAtLeast` and `EntityNumericProperty.ColoredManaSymbolCount`. The
  new group-aggregate measure calls *that* function, so the pip count can never disagree with the
  per-card filter or amount. This was u36's own stated motivating example; `DevotionTo` was
  correctly left alone.
- **`ActivationRestriction.OncePerTurn` + `OnlyIfCondition(Conditions.SourceAttackedThisTurn)`
  (u21's unified activation axis)** already express both of boast's rules clauses, so boast needed
  no new activation machinery — only a marker flag, the same arrangement `isExhaust`/`isPowerUp` use.
- **`Effects.PayRepeatedly` / `DynamicAmounts.timesPaid` (u17)** — not applicable, no repeated payment.
- **CR number:** the planner said 702.135 (that is *Afterlife*). Boast is **CR 702.142**, verified in
  `/workspace/MagicCompRules_20260619.txt`.

## The three additions, all generalizations of something that existed

1. **`ActivatedAbility.isBoast` + `activatedAbility { isBoast = true }`** (CR 702.142a). Marker flag
   only: renders the "Boast — " prefix and auto-adds `OncePerTurn` + an `OnlyIfCondition` over
   `Conditions.SourceAttackedThisTurn`, so marker and enforcement can't drift. Deliberately *not*
   `ActivationRestriction.Once` — boast is once each turn, not exhaust's once ever.
2. **`CostAtom.ExileFromGraveyardForTotal(filter, measure, minTotal)` + `CardMeasure`.** The unnamed,
   *filtered* generalization of collect evidence. Rather than a parallel implementation,
   `CollectEvidenceResolver` was **refactored to delegate** to a new shared
   `GraveyardTotalExileResolver`; collect evidence keeps only what is keyword-specific (its name, its
   `EvidenceCollectedEvent`, its unfiltered/mana-value choice). Two axes are new: the filter, and the
   `CardMeasure` (mana value vs. coloured pips). CR 701.59a/b, 202.3, 107.4e/f verified locally.
3. **`CardSource.ExiledAsCost`** — the exile counterpart of the existing `TappedAsCost`, recorded at
   payment time (CR 601.2h) on `EffectContext` / `ActivatedAbilityOnStackComponent`. Scoped to *this*
   activation's payment, unlike `FromLinkedExile`, which would hand a second boast the first one's
   cards.

Client side reuses the collect-evidence sum-gated picker with a server-supplied weight table
(`costType: "ExileForTotal"`, `exileMinTotalWeight` + `exileCardWeights`), because a printed-pip
total is a server-side reading the client cannot compute — server-is-authoritative applied to a
running total.

## Tests

Three files, 21 tests, all green.

- `BaronHelmutZemoScenarioTest` (6) — the card: the from-hand colour-filtered connive trigger and its
  two negatives, the boast gated on attacking, the fourteen-pips-isn't-fifteen fail-closed case, and
  the full path (cost exiles five, resolution copies exactly those five, three copies cast free, the
  other two swept, originals still exiled, and the free casts do **not** re-fire the from-hand trigger).
- `BoastKeywordScenarioTest` (6) — the keyword, card-independent.
- `GraveyardTotalExileCostScenarioTest` (9) — the cost primitive, card-independent: fails closed,
  count is never the question, the weight payload, overpay honoured, an illegal submission rejected
  rather than substituted, heaviest-first auto-select, hybrid/Phyrexian pips, and the filter axis.

`docs/card-sdk-language-reference.md` updated in the same change (cost facades + `CardMeasure`,
`CardSource.ExiledAsCost`, and a Boast section beside Exhaust/Power-up).

## Bugs I fixed in the inherited test file

- `BoastKeywordScenarioTest` filtered legal actions on `LegalActionInfo.sourceId`, **a field that
  does not exist**; it never compiled. Now reads `(action as? ActivateAbility)?.sourceId`.
- Its "comes back next turn" test called `passUntilPhase(ENDING, END)` twice in a row.
  `passUntilPhase` returns immediately when already in the step, so the second call was a no-op and
  the test silently ran on the **opponent's** combat. It also relied on empty scenario libraries,
  which deck a player on the next draw step and end the game. Both fixed, and the test now asserts
  the active player rather than assuming it.

## Gate

`just rebless-cards` (Baron Helmut Zemo added to `MSH.json`: **159 insertions, 0 deletions** — no
other card moved, so no shared SDK behaviour leaked), then `just test` — **passed**:
`BUILD SUCCESSFUL in 12m 46s`, 13 322 tests, 0 failures / 0 errors.
`just check-card-printing "Baron Helmut Zemo"` ok (msh is the earliest real printing).
Backlog ticked; MSH is now **276 / 276**.

Two gate failures on the way, both real and both fixed here:
`CostAtomSerializationTest`'s sealed-subtype coverage net needed a representative for the new atom,
and `SetCoverageServiceTest` caught the now-stale `incomplete = true` override on
`MarvelSuperHeroesSet` — Zemo was the set's last missing card, so the set is complete and the
override had to go.

## Things I'm not sure about — worth a reviewer's eyes

- `ExileFromGraveyardForTotal` is deliberately reported **unpayable** as a spell's additional cost and
  as a `PayCost` (`canPayAdditionalCost` → false, `CostPaymentService` → `Unaffordable`). That is a
  choice — no printed card wants either shape, and an offered-then-unpayable cost is worse than an
  absent one — but it means those `when` branches are dead code until something needs them.
- **The web-client changes are untypechecked.** `web-client/node_modules` is absent and the network is
  firewalled, so `npm run typecheck` cannot run here. The diff is small and additive (two optional
  fields, one new `costType` case, one weight lookup in the existing evidence picker), but nobody has
  compiled it.
- `CollectEvidenceResolver.Candidates.shared` rebuilds a wrapper object on every access. Harmless at
  these sizes, and it keeps every existing caller reading `manaValueById` under that name, but it is a
  seam a reviewer might want collapsed.
- The card's `oracleText` spells out reminder text for both connive and boast, matching how other MSH
  cards in this set are written; I did not re-derive that convention from a style guide.
