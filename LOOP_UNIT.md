# loop-msh-u26 — Cloak and Dagger, Entwined (feature unit)

Base branch: `loop-msh-u31` (a local branch, **not** merged upstream — this is a stack). u31 → u30 →
u28 → `origin/main`, so `main` *is* an ancestor now, but none of those commits are upstream yet;
this waits for them to land before it can be opened on its own. Reviewer: diff with
`git diff loop-msh-u31...HEAD`.

The rebase onto the current u31 moved the card and its scenario test into the per-era modules that
`origin/main` now uses: `mtg-sets/2026/src/main/.../msh/cards/CloakAndDaggerEntwined.kt` and
`mtg-sets/2026/tests/src/test/.../CloakAndDaggerEntwinedScenarioTest.kt` (both byte-identical to
their pre-rebase contents). `ReturnLinkedExileToZoneExiledFromTest.kt` is pure engine — it builds
`GameState` directly and names no corpus card — so it stays in
`rules-engine/src/test/.../handlers/effects/library/` beside its siblings.

## The primitive

- **`CardDestination.ToZoneExiledFrom(fallback = Zone.BATTLEFIELD)`**
  (`mtg-sdk/.../scripting/effects/PipelineEffects.kt`) — a *per-card* `MoveCollection` destination:
  each card returns to the zone it was exiled from. CR 610.3 ("this second one-shot effect returns
  the object to its previous zone"); verified against `/workspace/MagicCompRules_20260619.txt`, as
  were CR 610.3c (returns under owner's control), CR 400.7 (new object), CR 406.7 (exiled from
  exile) and CR 704.5d (tokens in wrong zones).
- **`ExiledFromZoneComponent(zone)`** (`rules-engine/.../state/components/identity/`) — the fact it
  reads. Written by `ZoneTransitionService`'s `Zone.EXILE` entry branch (every effect-driven exile)
  and, explicitly, by the direct-`addToZone` exile sites that matter: exile as a cost
  (`CostPaymentService`, `CastSpellHandler`'s three additional-cost branches),
  `ExileOpponentsGraveyardsExecutor`, and the graveyard sweep in `SbaZoneMovementHelper`. It is a
  best-effort record, not a guarantee — anything unstamped takes `ToZoneExiledFrom`'s fallback, so
  the fallback is load-bearing. Cleared by the same service on the way out, plus by the two exits
  that reuse the entity id (`StackResolver`'s cast-from-exile, `ReturnOneFromLinkedExileExecutor`).
  Registered in `Serialization.kt`.
- **`MoveCollectionExecutor.moveToZonesExiledFrom`** groups the collection by recorded zone and runs
  each group through the existing `ToZone` path, so owner routing / aura targeting / events are
  unchanged. Battlefield group last (it's the one that can pause on an Aura's enchant target).
- **Consolidation:** `ExilePatterns.returnLinkedExile` now takes a `destination`, and
  `returnLinkedExileToHand` is a thin facade over it. Both existing facades build **byte-identical**
  effect trees to before (same `storeAs` keys, same `underOwnersControl`), and
  `ReturnLinkedExileToZoneExiledFromTest` has two tests pinning their old behaviour.
- Facade: `Effects.ReturnLinkedExileToZoneExiledFrom()`.

## The card

`Cloak and Dagger, Entwined` (MSH #211, {1}{W}{B} 2/2 deathtouch + lifelink). ETB targets an
opponent + up to one creature they control, reveals their hand, then `MayEffect` → `ChooseAction`
picks **either** a gather→select→move(EXILE, `linkToSource`) over their hand **or**
`ExileUntilLeaves` on the chosen creature. The LTB trigger is the new
`ReturnLinkedExileToZoneExiledFrom()`, which is what lets one trigger serve both branches.

## Gate

`just test` — **BUILD SUCCESSFUL** (10 975 tests). The first run had one failure,
`ConniveTargetingTest` with a 120 s `TimeoutCancellationException` (contention, not an assertion,
not in my diff); green standalone. The final run reports `1 executed, 52 up-to-date` because the
preceding run had already executed the other modules' test tasks against this same tree — see the
PR body, which spells this out.

Mutation-checked twice: forcing `originZoneOf` to always return `Zone.BATTLEFIELD` turned red
exactly the hand / graveyard / library / mixed-pile / source-left primitive tests **and** the card's
hand-branch scenario test, leaving the battlefield and fallback cases green. Restored; no probe left
in the tree.

**Still owed — that green no longer covers this tree.** The branch has since been rebased onto the
rewritten `loop-msh-u31` (new base, and the card/scenario test changed modules), so the 10 975-test
result above is stale. This diff reaches `mtg-sdk` (`Effects`, `ExilePatterns`, `LibraryPatterns`,
`PipelineEffects`) as well as `rules-engine` and `mtg-sets`, so the re-run is the **full** `test`
suite, not the engine-only gate — and it must be re-run again after the eventual rebase onto
`origin/main`.

## Things I'm unsure about / a reviewer should look at

- **The card's second target is a two-player rendering, not the printed text.** Printed: "up to one
  target creature **they** control". Shipped: "up to one target creature an opponent controls".
  `TargetFinder.findLegalTargets` enumerates one requirement at a time with no knowledge of the
  other requirements' choices, so a filter bound to the chosen opponent yields an *empty* legal
  list (I hit exactly that: `legalTargets={0=[player-2], 1=[]}`) and the creature could never be
  picked. Same approximation Demonic Junker / Kitesail Larcenist / Unstable Glyphbridge document.
  Identical in two-player; laxer in a pod. Fixing it properly = cross-requirement target
  enumeration, an engine feature of its own.
- Neither `ChooseAction` branch has a `FeasibilityCheck`: both available checks
  (`ControlsPermanentMatching`, `HasCardsInZone`) evaluate against the **choosing** player, and both
  of this card's conditions are about the *targeted opponent*. So an empty-handed opponent still
  shows the "exile a nonland card from their hand" option, and it no-ops. Fixing that means a new
  player-scoped `FeasibilityCheck`, which I judged out of scope for this unit.
- The "You may" is a separate yes/no prompt before the two-option prompt (two clicks). A third
  "exile nothing" choice would collapse them, but there is no no-op `Effect` in the SDK to hang it on.
- `ToZoneExiledFrom` groups can pause; I order library and battlefield last and document that a
  caller combining `CardOrder.ControllerChooses` with a library+battlefield split could strand the
  battlefield group. The shipped facade uses `CardOrder.Preserve`, so it can't happen there.
- mtgish step 8b skipped: the corpus's exile-return IR tags are all fixed-destination
  (`PutExiledCardIntoOwnersHand`, `PutExiledCardOntoBattlefield`, …); I found no tag for
  "return it to the zone it was exiled from", so there is nothing to register.
- No manual playthrough in the web client, no e2e. A playtest scenario is staged at
  `manual-scenarios/sets/msh/loop-msh-u26-cloak-and-dagger-entwined.json` but was not run.
