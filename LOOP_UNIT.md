# loop-msh-u28 — Ronin, Shadow Stalker + equip-ability mana restriction

Branch `loop-msh-u28`, based on `loop-msh-u27` (local, **not** merged upstream). Since u27 sits on
u25 → u32 → u23, which was itself rebased onto `origin/main`, `main` *is* now an ancestor, but the
u23/u32/u25/u27 commits below this branch are not upstream yet, so this still waits for those to
land before it can be opened on its own. Reviewer: diff with `git diff loop-msh-u27...HEAD`.

## The primitive

- **`ManaRestriction.EquipAbilityActivationOnly`** — `mtg-sdk/.../scripting/effects/ManaRestriction.kt`.
  "Spend this mana only to activate equip abilities" (CR 702.6). A new `data object` atom, not a
  parameter on `AbilityActivationOnly`, because that one is a shipped `@SerialName` `data object`
  (turning it into a `data class` changes its serial shape) and because this file already models one
  spend context per atom (`TurnPermanentsFaceUpOnly`, `UnlockDoorOnly`, `FaceDownSpellsOnly`),
  composed with `AnyOf`.
- **`SpellPaymentContext.isEquipAbilityActivation`** — `rules-engine/.../mechanics/mana/ManaPool.kt`.
  Guarded by an `init { require(...) }` so it can't be set without `isAbilityActivation`.
- **`buildAbilityPaymentContext(..., ability)`** — `rules-engine/.../mechanics/mana/AbilityPaymentContextBuilder.kt`.
  The 4th parameter is required (nullable, no default) so every activation site had to be revisited;
  it is the single builder all ability-activation paths funnel through. All 8 call sites audited —
  see the PR body for the list.
- **Source-relative sacrifice-cost filters** — `CostEnumerationUtils.findAbilitySacrificeTargets`,
  `CostHandler.findMatchingPermanentsUnified` / `findMatchingCardsUnified` / `paySacrificeList`, and
  the sacrifice-choice pause in `ActivateAbilityHandler` now put the ability's source into the
  `PredicateContext`. Needed by Ronin's "Sacrifice an Equipment attached to Ronin"
  (`…attachedToSource()` = `StatePredicate.IsAttachedToSource`, unconditionally false without a
  source). This was *not* named in the unit's brief — I found it while implementing and it is the
  one piece of scope I added. Faunsbane Troll (WOE) ships the same untested shape and is fixed
  incidentally.

## The card

`mtg-sets/.../definitions/msh/cards/RoninShadowStalker.kt` — {2}{B} 3/3 Legendary Human Rogue Hero.

1. `Pay 2 life: Add two mana of any one color. … Activate only once each turn.` —
   `Effects.AddAnyColorMana(2, restriction = AnyOf(SubtypeSpellsOnly(setOf("Equipment")),
   EquipAbilityActivationOnly))`, `manaAbility = true`, `ActivationRestriction.OncePerTurn`.
2. `{T}, Sacrifice an Equipment attached to Ronin: Target creature gets -4/-4 until end of turn.` —
   `Costs.Composite(Costs.Tap, Costs.Sacrifice(Artifact.withSubtype("Equipment").attachedToSource()))`,
   `Effects.ModifyStats(-4, -4, target)`, `TimingRule.SorcerySpeed`.

## Tests

- `rules-engine/src/test/.../scenarios/EquipAbilityManaRestrictionTest.kt` — the primitive: the
  restriction's truth table over every spend context; proof that the old spelling
  `SubtypeSpellsOrAbilitiesOnly("Equipment")` really does admit an Equipment's non-equip ability;
  the `isAbilityActivation` invariant; end-to-end payment of Iron Man Armor's Equip {2} from Ronin's
  mana; **legal-action enumeration** showing Iron Man Armor's `{2}` animate ability greyed out while
  its equip is affordable (both cost {2}, so only the restriction separates them); and the mana
  emptying as the step ends.
- `rules-engine/src/test/.../scenarios/RoninShadowStalkerScenarioTest.kt` — the card: life cost,
  once-each-turn cap, both halves of the spend clause end-to-end, the sacrifice ability's
  source-relative scoping (enumeration *and* submission, positive *and* negative), the no-cost-
  payment auto-pick, and the sorcery-speed gate.
- `rules-engine/src/test/.../scenarios/FaunsbaneTrollScenarioTest.kt` — the WOE card the sacrifice
  fix switches on: its enters-Role pays the fight (enumerated affordable, sacrificed as a cost,
  loser exiled), and an Aura on another creature doesn't qualify.
- `manual-scenarios/sets/msh/loop-msh-u28-ronin-shadow-stalker.json` — playtest scenario; a
  Bonesplitter starts attached to Ronin so both halves of the card are reachable on turn one.

## Gate

`just test` — 10917 tests, 1 failure: `ConniveTargetingTest`, a 120 s `TimeoutCancellationException`
(contention, the known flake), which passes standalone on the same tree. Unrelated to this diff.
`just rebless-cards` moved only Ronin in
`mtg-sets/src/test/resources/snapshots/cards/MSH.json`. `just check-card-printing
"Ronin, Shadow Stalker"` clean. Backlog ticked and `just fix-backlog` run.

## Unsure / worth a reviewer's eye

- The added `PredicateContext.sourceId` threading in the sacrifice cost paths is scope I decided to
  take on; it changes behaviour only for cost filters carrying source-relative predicates, of which
  the whole corpus has exactly two (Ronin and Faunsbane Troll, both `attachedToSource()`, both
  previously matching nothing). Note the direction isn't universal: a *negated* source-relative
  predicate (`notAttachedToSource()`, `NotOfSourceChosenType`) matches everything without a source,
  so supplying one narrows rather than widens. It is a shared path either way.
- `LegalActionEnricher` resolves the ability by id from the printed script only, so a *granted*
  equip ability's restricted-mana hint is under-reported to the client. Presentation only — the
  server's own payment check always has the real ability. Documented at the call site.
- `SubtypeSpellsOnly` used to render "cast a Equipment spell" (a pre-existing article bug in that
  atom's `description`, player-visible on the client's restricted-mana orbs). Fixed in the atom;
  no snapshot moves, since every card using it spells its own oracle text out.
- I did not manually play the card in the web client, and there is no e2e coverage.
