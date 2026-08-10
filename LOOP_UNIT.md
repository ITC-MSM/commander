# u09 — Improvise (CR 702.126)

Branch `loop-msh-u09`, off `origin/main` (`efe697ae9a`). Feature unit: a keyword + cross-layer
payment plumbing + the two cards it unblocks.

## Cards

- **Ironheart, Clever Champion** [msh 60] — {4}{U} Legendary Artifact Creature — Human Hero 3/4.
  `keywords(Keyword.IMPROVISE, Keyword.FLYING)` + `GrantKeywordToOwnSpells(Keyword.IMPROVISE,
  GameObjectFilter.Noncreature)`. No new primitive for the grant — `GrantedKeywordResolver` already
  handles cost keywords and `CardPredicate.IsNoncreature`.
- **Arc Reactor** [msh 243] — {5} Artifact. `keywords(Keyword.IMPROVISE)` + `EntersTapped()`
  replacement effect + `Effects.AddColorlessMana(3)` behind `Costs.Tap`.

## The design call the brief asked for

The backlog proposed a **fourth** parallel payment field (`improvisedArtifacts`) beside delve /
convoke / harmonize / waterbend. I **converged instead**: improvise and waterbend are the same
mechanism with different eligibility, so `AlternativePaymentChoice.waterbendPermanents` became
`tapForGenericPermanents` (one carrier), the eligibility became a value (`TapForGeneric.IMPROVISE` /
`.WATERBEND`), and one `applyTapForGeneric` / `findTapForGenericPermanents` /
`canAffordWithTapForGeneric` serves both. `LegalAction` / DTO / client renamed to match, plus a
`tapForGenericLabel` so one HUD names either mechanic. A third keyword of this shape is now one enum
entry.

**Convoke was not folded in, deliberately** — a convoked creature can pay a *colored* pip of its own
color (CR 702.51a), which is why it carries a per-creature color map and a colored-vs-generic
assignment affordability check. It is a different payment shape.

**Not converged, deliberately:** `ActivatedAbility.hasWaterbend` (the card-facing DSL flag on 15 TLA
cards — it's the waterbend *cost*, not the rail) and the Ward—Waterbend / in-resolution decision
path (`WaterbendPermanentChoice`, `ManaSourcesSelectedResponse.waterbendPermanents`, the
continuations' `waterbend` flags). Improvise functions only while the spell is on the stack
(CR 702.126a), so it can never be paid there.

## Player-facing verification

Tests assert against `game.getLegalActions(player)` — the enriched DTO the server actually sends —
so enumerability and the DTO payload are covered, not just executability: `isAffordable` is true only
because of the artifacts, `hasTapForGeneric` / `tapForGenericLabel == "improvise"` /
`tapForGenericAmount == null` are set, and `validTapForGenericPermanents` holds exactly the caster's
untapped artifacts. The client path was traced by hand end to end (phase → `startTapForGenericSelection`
→ `GameCard` toggle → `advancePipeline` → `alternativePayment.tapForGenericPermanents` on the wire,
field name matching Kotlin), and `tsc --noEmit` + vitest (516 tests) pass. **No manual playthrough,
no screenshots, no e2e.**

## Things I'm unsure about / worth a reviewer's eye

1. **The rename is large** (~250 sites across mtg-sdk, rules-engine, ai, web-client) and it is the
   bulk of the single commit `9b3323b3fa`. It is mechanical and behaviour-preserving, and the
   existing waterbend suites are the regression net. I did *not* split it into its own commit —
   the rename and the improvise wiring touch the same lines in `AlternativePaymentHandler`,
   `CastSpellEnumerator` and `LegalAction`, so a split would have been an artificial one. Reading
   the diff, everything named `tapForGeneric*` that was previously `waterbend*` is rename-only.
2. **Improvise on an {X} spell** pays only the *printed* generic, not the mana paid for X — a known
   **gap**, corrected in round 3 after the reviewer showed the original justification here was
   false. The rules: CR 601.2b announces X, CR 601.2f then determines the total cost, and
   CR 702.126a bounds the taps at the generic in *that* total — so improvise **does** pay the
   X-derived generic, and the official Whir of Invention ruling says so outright ("choose X to be 3
   … if you tap two artifacts, you'll have to pay `{1}{U}{U}{U}`"). Four printed cards reach it:
   Whir of Invention, Universal Surveillance, Saheeli's Directive, Battle at the Bridge. **None of
   the four is implemented in this repo, and no MSH card has improvise with {X}**, so nothing here
   is wrong today — but the earlier claim that no such card exists was simply untrue.
   `maxAffordableX` still ignores improvise, deliberately: the ceiling can't move alone, because
   the payment side stops crediting taps once the printed generic runs out, so a raised ceiling
   would offer an X the handler then refuses to pay (under-offering is the safe direction). Closing
   it is a four-layer change — fold X into the cost the way `waterbend {X}` does, charge the
   leftover against the X mana the way `CastSpellHandler.harmonizePaymentXValue` does, raise the
   ceiling, and lift the client cap in `pipelinePhases.ts` — with no card in the repo to exercise
   it. Deferred as an explicit TODO at all four sites, to be done with the first improvise-{X} card.
3. **`applyImproviseMetadata` is a post-process pass** over the enumerated actions rather than a
   field set at each `LegalAction(...)` site — same shape as the existing
   `applySpellWaterbendMetadata`. It means every cast shape gets it for free; it also means it runs
   over every action each enumeration (memoized per player and per card definition).
4. **AI heuristic taps artifacts only**, even for a waterbend cost that also accepts creatures.
   Rationale in the code: an artifact is rarely doing anything else, a creature gives up an
   attack/block. This slightly *improves* waterbend (the AI previously filled no tap payment at all
   and could pick a cast it couldn't pay) but it is a behaviour change outside the strict unit.
   Round 3 added the second half the reviewer found missing: it fills the payment **only when the
   taps are needed**. Improvise is optional, and filling an optional one can lose the cast — the AI
   would tap Arc Reactor (`{T}: Add {C}{C}{C}`, shipped in this unit) for {1} and make its own cast
   unpayable. `LegalAction.tapForGenericRequired` (new) carries "is the cost payable with mana
   alone?" and the AI skips filling when it is; when it isn't, the enumerator's
   `canAffordWithTapForGeneric` has already validated tapping *every* offered permanent, so filling
   to the cap is safe. `ImprovisePaymentAiTest` pins both directions.
5. **UX friction the mechanic implies:** while Ironheart is out, *every* noncreature cast picks up
   the improvise tap step whenever the player controls any untapped artifact — even when they have
   plenty of mana and don't want to tap. Confirming with nothing selected pays normally, so it is
   one extra click, and it matches how convoke behaves today. Worth a human's eye in play.
   Round 3 removed the *second* cost the note originally missed: the tap phase used to also force
   the `manaSource` phase (via `hasAlternativePaymentPhase`), which silently turned **auto-tap off**
   for every noncreature spell for the rest of the game. Because improvise is granted over a whole
   card type rather than printed per card, that is a much bigger imposition than it is for
   delve/convoke — and it buys nothing, since the server applies the taps and then auto-solves the
   remainder. `tapForGeneric` no longer forces it; delve and convoke still do.
6. **Cast-from-graveyard/exile improvise is not wired** (`CastFromZoneEnumerator` only got the
   waterbend rename). Neither card needs it and no MSH card grants improvise to a graveyard cast.

## Gate

Per-module (a full `just test` has OOM-killed on this box today):

- `scripts/gradle-locked :mtg-sdk:test :mtg-sets:test` — mtg-sdk green; mtg-sets failed only on the
  expected `CardDefinitionSnapshotTest` MSH golden, then `just rebless-cards`; re-diff shows **only
  the two new cards** added to `MSH.json` (78 insertions, 0 deletions).
- `just check-card-printing` for both cards — ok, canonical in earliest printing.
- `just test-rules` — see `build/pr/loop-msh-u09-gate-rules.log`. **Note:** a first attempt with the
  reduced-footprint override (`-Pkotlin.compiler.execution.strategy=in-process` +
  `-Dorg.gradle.jvmargs=-Xmx3g`) died with `OutOfMemoryError: Java heap space` inside
  `compileTestKotlin` after ~60 min. That is the override being too small for a full non-incremental
  recompile of the rules-engine **test** source set (the rename touches shared SDK types), not a
  code fault and not the OS OOM-killer — the sanctioned `just test-rules` recipe compiles in a
  separate Kotlin daemon with its own heap and was used for the recorded result.
- `web-client`: `npx tsc --noEmit` clean, `npx vitest run` 37 files / 516 tests pass.
- `just fix-backlog` — MSH header now 241 / 276.
