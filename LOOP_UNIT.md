# Unit u03 — the five modal teamwork cards (MSH)

Branch `loop-msh-u03`, stacked on `loop-msh-u02`. Batch unit, ordinary card work: every card is
`teamwork(n)` plus the documented modal recipe. **No new SDK vocabulary, no engine change** — the
diff is five card files, five test files, the MSH snapshot, and two backlog files.

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

All five: `modal(chooseCount = 2, minChooseCount = 1, dynamicChooseCount =
DynamicAmount.Conditional(Conditions.TeamworkWasPaid, Fixed(2), Fixed(1)))` — the recipe documented
in `docs/card-sdk-language-reference.md` → *Teamwork N*. Thresholds verified against Scryfall
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

Five files, `<CardName>ScenarioTest.kt`, three cases each (15 total, all passing):

1. plain cast → exactly the one chosen mode resolves, the other mode's victim is untouched, nothing
   tapped;
2. teamwork cast → both modes resolve and the payer is tapped;
3. plain cast declaring both modes → rejected, card stays in hand.

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
  covers the advertised action generically; I did not add a per-card enumerator assertion.
- `Effects.Exile` is `MoveToZoneEffect(destination = EXILE)`; I did not check whether anything in
  MSH cares about the distinction between "exile" and "exile face down"/linked exile here. The
  printed text is a plain exile, so I believe this is right.
- Atlantis Attacks' token is named `"Leviathan Token"` by the executor's default
  (`creatureTypes.joinToString(" ") + " Token"`) since I passed no explicit `name`. That matches
  what Brigid's Command and the other token cards do, but the printed token is just "Leviathan".
- Each card's KDoc cites CR 702.194c for the "choose both instead" shape. 702.194c is strictly
  about *targets* being skipped when teamwork wasn't used; that is exactly what
  `dynamicChooseCount` produces here, and it matches the wording already in
  `backlog/.../mechanics.md`, but it is a slightly loose citation.
- Not done: no manual playthrough in the web client, no UX pass from either seat, no e2e run, no
  `/generate-scenario` JSON. The web client was not type-checked (no node_modules, no network).
