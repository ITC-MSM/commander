# Unit u03 — the five modal teamwork cards (MSH)

Branch `loop-msh-u03`, an independent PR against `main` — `loop-msh-u02` merged upstream as
PR #1731, so this branch was rebased off the stack onto `origin/main` and carries only its own
commits. Batch unit, ordinary card work: every card is `teamwork(n)` plus the modal recipe.

> **Amended twice after review.** The branch also carries fixes to `CastSpellEnumerator.kt` /
> `CastSpellHandler.kt` and one new SDK field, because the defects are only reachable through these
> five cards. See *Review corrections* and *Round-2 corrections* at the bottom.

## Cards

- **Widow's Bite** [122] `{1}{B}` Instant, teamwork 3 — `Effects.GrantKeyword(DEATHTOUCH)` /
  `Effects.ModifyStats(-2, -2)`, one `Targets.Creature` per mode.
- **HULK SMASH!** [135] `{1}{R}` Instant, teamwork 4 — `Effects.Destroy` on
  `TargetFilter(GameObjectFilter.Artifact.notCreature())` / the Rabid Bite one-sided
  `Effects.DealDamage` with `amount = EntityProperty(Target(0), Power)` and
  `damageSource = <your creature>`. Mode 2 carries two targets of its own.
- **Go Nuts!** [168] `{G}` Sorcery, teamwork 3 — `Effects.AddCounters("+1/+1", 1)` /
  `Effects.Fight`, the Epic Fight shape.
- **Murdock's Crusade** [24] `{1}{W}` Sorcery, teamwork 4 — `Effects.Exile` behind
  `TargetFilter.Creature.toughnessAtLeast(4)` / `TargetFilter.Enchantment.manaValueAtLeast(4)`.
  The printed mode names ("Street Justice —", "Legal Justice —") are part of the mode
  descriptions, which become the client's button text.
- **Atlantis Attacks** [46] `{5}{U}{U}` Sorcery, teamwork 4 — `Effects.CreateToken(6, 5, BLUE,
  "Leviathan", HEXPROOF, controller = <target player>)` / a single
  `TargetObject(count = 2, minCount = 1, filter = NonlandPermanent)` bounced through
  `ForEachTargetEffect(Effects.ReturnToHand(ContextTarget(0)))`.

All five: `teamworkModal { }` — the recipe documented in
`docs/card-sdk-language-reference.md` → *Teamwork N*, which pins the mode count to exactly one on a
plain cast and exactly all of them on a declared one. Thresholds verified against Scryfall
(3, 4, 3, 4, 4 respectively).

**Nothing was dropped.** The two shapes flagged as possible drops both turned out to be covered by
existing target vocabulary: "one or two target nonland permanents" is `count = 2, minCount = 1`
(Succumb to the Cold / Amazing Acrobatics precedent), and "enchantment with mana value 4 or
greater" is `TargetFilter.Enchantment.manaValueAtLeast(4)`.

## Gate

`just build` — **passed** (`BUILD SUCCESSFUL`, all modules).

Getting there took several runs because the Gradle daemon was OOM-killed three times on this shared
box (`daemon disappeared`), never with a compile or test error. The runs that stuck used
`scripts/gradle-locked … -Pkotlin.compiler.execution.strategy=in-process --max-workers=1`, and
`:rules-engine:test` had to be run on its own once so the test compile and the test run did not
share a memory window. The final `just build` is green over everything.

- `just rebless-cards` — `mtg-sets/src/test/resources/snapshots/cards/MSH.json` only, and a **pure
  insertion** (571 added lines, 0 removed): Atlantis Attacks, Go Nuts!, HULK SMASH!, Murdock's
  Crusade, Widow's Bite. No unrelated card moved.
- `just check-card-printing` — ok for all five (canonical in MSH, the earliest real printing).
- Backlog ticked; `just fix-backlog` resynced MSH to 235 / 276. The teamwork section of
  `mechanics.md` now reads 8 of 13 implemented, with the remaining four (We Say Thee Nay!, Cruel
  Alliance, Too Evil to Stay Dead, Earth's Mightiest Heroes) plus the still-blocked Agent Maria Hill.

## Tests

Five files, `<CardName>ScenarioTest.kt`, four cases each plus extras (25 total, all passing):

1. plain cast → exactly the one chosen mode resolves, the other mode's victim is untouched, nothing
   tapped;
2. teamwork cast → both modes resolve and the payer is tapped;
3. plain cast declaring both modes → rejected, card stays in hand;
4. teamwork cast declaring one mode → rejected and rewound, card in hand *and* payer untapped
   (added in round 2 — "choose both instead" is mandatory in both directions).

Go Nuts!'s teamwork case points both modes at the same 2/2 so the +1/+1 counter lands before the
fight and it trades with a 3/3 — that pins mode ordering within the single resolution. HULK SMASH!'s
teamwork case pins that the bite amount reads *that mode's* first target (the 6/4 Craw Wurm) rather
than the spell's first target overall (the artifact) — the snapshot confirms
`targetRequirements[0]` is "target creature you control".

Neither of the two u02 regressions (missing `declaredCostSlot` in the evaluation context; no modal
enumeration for a declared cast) reappeared — the teamwork branch is reached and both modes resolve.

## Uncertain / worth a reviewer's eye

- The tests drive `game.execute(CastSpell(...))` directly with `chosenModes` +
  `modeTargetsOrdered`, because `ScenarioTestBase.castSpellWithTeamwork` takes only a single
  `targetId` and cannot express per-mode target lists. That bypasses the legal-action enumerator, so
  these tests prove the *handler* accepts the shape but not that the client is offered the
  `CastSpellModal` "(Teamwork N)" variant for these particular cards. `TeamworkMechanicScenarioTest`
  covers the advertised action generically. Murdock's Crusade and Widow's Bite now carry per-card
  `getLegalActions` assertions; the other three still rely on the generic coverage.
- `Effects.Exile` is `MoveToZoneEffect(destination = EXILE)`; I did not check whether anything in
  MSH cares about the distinction between "exile" and "exile face down"/linked exile here. The
  printed text is a plain exile, so I believe this is right.
- Atlantis Attacks' token is named `"Leviathan Token"` by the executor's default
  (`creatureTypes.joinToString(" ") + " Token"`) since I passed no explicit `name`. That matches
  what Brigid's Command and the other token cards do, but the printed token is just "Leviathan".
- ~~Each card's KDoc cites CR 702.194c for the "choose both instead" shape.~~ Corrected in round 2
  to CR 700.2 / 601.2b; the loose citation was in fact wrong, and contradicted this repo's own SDK
  reference.
- Not done: no manual playthrough in the web client, no UX pass from either seat, no e2e run, no
  `/generate-scenario` JSON. The web client was not type-checked (no node_modules, no network).

## Review corrections

Applied on top of the original commits, after the independent review (0 blocking, 4 important,
6 minor).

- **Engine, `CastSpellEnumerator.kt`** — new private `effectiveModalMaxChooseCount` mirroring the
  dynamic branch of `CastSpellHandler.effectiveModalChooseCounts` (declaration in the evaluation
  context, so `Conditions.TeamworkWasPaid` answers correctly from hand). Two call sites:
  - the plain cast now advertises the dynamic-evaluated maximum (1 for an undeclared teamwork cast)
    instead of the printed 2, so the client can no longer submit a two-mode plain cast the handler
    rejects. Flame of Anor / Molten Collapse / Wail of the Forgotten were mis-advertised identically.
  - the declared-cost branch now drops the variant unless at least that many modes are *available*
    (CR 700.2a), not merely one — Murdock's Crusade's teamwork cast was being offered with no legal
    enchantment on the board and could never be completed. `allowRepeat` is exempt (CR 700.2d).
- ~~**Still open:** the handler's effective *minimum* for a teamwork cast is the printed
  `minChooseCount` (1), not 2.~~ **Fixed in round 2** — see below. It was not separable: the client's
  confirm button unlocks at the advertised `minChooseCount`, so a player could tap their team and
  take one mode through the normal UI.
- **`AtlantisAttacks.kt` KDoc** — it claimed one target going illegal "does not strand the other".
  False: `processPreChosenModeQueue` skips the whole mode all-or-nothing. Now documented as the
  pre-existing engine-wide deviation from CR 608.2b that it is; behaviour untouched.
- **`GoNuts.kt`** — CR 608.2 → 608.2c (the rule that actually says instructions are followed in the
  order written).
- **Tests** — `MurdocksCrusadeScenarioTest` gains two `getLegalActions` cases (advertised mode count,
  per-mode target candidates, teamwork variant absent with no legal enchantment, and the negative
  side of both filters); `AtlantisAttacksScenarioTest` gains a single-target bounce case for
  `minCount = 1`; `HulkSmashScenarioTest`'s bite victim became a 0/8 Wall of Stone so the assertion
  pins `damage == 6` rather than "the 2/2 died".
- No action on the Leviathan token name (matches the repo-wide default and the test lookup) or the
  CR 702.194c citations (the reviewer verified them as accurate).

## Round-2 corrections

Applied after a second review (1 blocking, 3 important).

- **SDK, `ModalEffect.dynamicMinChooseCount`** — the mandatory sibling of `dynamicChooseCount`.
  The printed wording splits: "you *may* choose two instead" (Flame of Anor) leaves the floor at
  one, "choose both **instead**" (these five) does not. With only a ceiling, a teamwork cast could
  legally take a single mode.
- **SDK, `teamworkModal { }`** in `TeamworkDsl.kt` — sets both bounds from the same `Conditional`
  and derives "both" from the modes declared. The five cards each drop six lines of copy-pasted
  `DynamicAmount.Conditional`.
- **Engine, `ModalChooseCounts.forCast`** — one authority for the `min..max` range, called by both
  `CastSpellHandler` (validating) and `CastSpellEnumerator` (advertising). The two had separate
  copies that had already drifted: the enumerator's knew nothing about the blight path or the floor.
- **Engine, the enumerator's drop gate** now tests the effective *minimum*, not the maximum. This
  turned out to fix a bug beyond teamwork: `CastSpellEnumeratorTest` had a case asserting that
  Brigid's Command ("Choose two —") is still offered with only one mode available. It isn't
  castable — CR 700.2a plus CR 601.2, and Wizards' own Cryptic Command ruling ("You must choose two
  different modes"). That test's name already said "dropped entirely" while its body asserted the
  opposite; it now asserts the drop.
- **CR 702.194c → CR 700.2 / 601.2b** in five card KDocs and one test KDoc. 702.194c is about
  targets; `docs/card-sdk-language-reference.md` already said so.

**Gate (on `origin/main` b9d89050eb, after the rebase off the stack):** `:rules-engine:test` +
`:mtg-sets:test` — 10601 passed, 0 failed. `:mtg-sdk:test` + `:game-server:test` — 897 passed,
0 failed. Snapshot re-blessed: `MSH.json` +75 lines, 0 removed, all five `dynamicMinChooseCount`
blocks and nothing else.
