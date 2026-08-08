# Unit u04 — MSH teamwork, the four non-modal payoffs

Branch `loop-msh-u04`, stacked on `loop-msh-u03`. Ordinary card work: the teamwork rail
(`teamwork(n)`, `Conditions.TeamworkWasPaid`, `ChoiceSlot.TEAMWORK`) already shipped in u02/u03 and
is unchanged here. No engine or SDK code was touched.

## Cards

- **Cruel Alliance** [92] — {2}{B} Sorcery, teamwork 2. Plain: exile target creature with mana value
  3 or less. Teamwork: exile target creature (no restriction) and gain 3 life. Composes the shared
  optional-additional-cost rail's `kickerTarget` / `kickerEffect` slots (the Fight with Fire / Brave
  the Wilds shape) so the declared cast announces its *own* target requirement, plus
  `Effects.Exile` + `Effects.GainLife` + `Effects.Composite`.
- **Too Evil to Stay Dead** [118] — {2}{B} Sorcery, teamwork 4. Same `kickerTarget` / `kickerEffect`
  shape: plain targets a creature card in your graveyard with mana value 4 or less, teamwork targets
  any creature card in your graveyard. Both branches end in `Effects.PutOntoBattlefield`
  (`TargetFilter.CreatureInYourGraveyard`, `Targets.CreatureCardInYourGraveyard`).
- **We Say Thee Nay!** [82] — {1}{U} Instant — Arcane, teamwork 2. One
  `Effects.CounterUnlessDynamicPays` whose generic amount is a
  `DynamicAmount.Conditional(Conditions.TeamworkWasPaid, 4, 2)` — a single "counter unless they pay"
  event whose price changes, not two counter effects.
- **Earth's Mightiest Heroes** [165] — {4}{G}{G} Sorcery, teamwork 5. Gather → Select → Move
  pipeline (`GatherCardsEffect(TopOfLibrary(8), revealed)` → `SelectFromCollectionEffect` →
  two `MoveCollectionEffect`s), with a `ConditionalEffect(TeamworkWasPaid, …)` swapping only the
  selection mode: `SelectionMode.ChooseAnyNumber` vs `SelectionMode.ChooseUpTo(1)`. Structurally the
  same split KTK's See the Unwritten uses for its ferocious "two instead of one".

## Tests

One file per card, all under `rules-engine/src/test/kotlin/.../scenarios/`. 17 tests, every card
covering both branches.

Two of the four assert against `getLegalActions` directly, not just `execute(CastSpell(...))`,
because the teamwork bugs found in u02/u03 all lived in `CastSpellEnumerator`'s advertising path.
The other two (We Say Thee Nay!, Earth's Mightiest Heroes) drive `castSpell`/`CastSpell` only —
neither has a branch-dependent target or mode, so their enumeration path is the generic one
`TeamworkMechanicScenarioTest` already covers.

- `CruelAllianceScenarioTest` — with only a mana-value-6 creature on the board the plain `CastSpell`
  is **not** advertised while the `CastWithKicker` teamwork variant is, carrying that creature as a
  valid target; with a cheap creature present both are advertised and the plain one's
  `validTargets` is the narrow list; and with no creature to tap, the teamwork variant is
  advertised **unaffordable**.
- `TooEvilToStayDeadScenarioTest` — same shape over the graveyard, both directions.

## Gate

`just build` (see the verdict block for the result). The four scenario tests were also run on their
own first and are green. `just rebless-cards`, `just check-card-printing` ×4 (all ok, all canonical
in MSH), `just fix-backlog` (239/276).

## Dropped

None. The conditional target filter — the flagged drop candidate — turned out to be expressible on
the existing rail: `CardScript.kickerTargetRequirements` *replace* `targetRequirements` when a cast
declares any slot on the optional-additional-cost rail, which is exactly "the teamwork cast
announces a different target". No new primitive was needed.

## Things I'm unsure about, for review

- **`kickerTarget` forces the whole effect to be restated.** Too Evil to Stay Dead's printed last
  sentence ("Return the chosen card to the battlefield") is shared between the branches, but because
  `kickerSpellEffect` replaces `spellEffect` wholesale, it appears twice in the card file. That's
  faithful, just duplicated; if there's an appetite for a "branch replaces only the target" shape
  that's a separate feature, not this PR.
- **Cruel Alliance's "unaffordable but still advertised" teamwork variant** is asserted as
  `isAffordable == false` rather than absent. That matches the enumerator's explicit comment
  ("the greyed-out variant still tells the player what teamwork would ask for"), but the unit brief
  asked for "not advertised", so flagging the divergence rather than hiding it.
- **No manual playthrough.** `SelectionMode.ChooseAnyNumber` on Earth's Mightiest Heroes shows an
  8-card overlay with 3 selectable and 5 greyed out (`showAllCards = true`); I verified the decision
  payload in the test (`maxSelections` 1 vs 3) but never looked at it in the client.
- **`Instant — Arcane`** on We Say Thee Nay! is carried through as printed. Nothing in this set
  cares about the Arcane subtype (no splice), so it is inert here.
- I added one bullet to the Teamwork entry in `docs/card-sdk-language-reference.md` describing this
  "different target *restriction* per branch" shape. No SDK change accompanies it — it documents an
  authoring shape the rail already supported but the entry didn't name.
