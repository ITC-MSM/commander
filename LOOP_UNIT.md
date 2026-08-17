# u40 — World War Hulk (+ one new SDK primitive)

**Card.** World War Hulk [MSH 197] — {3}{G}{G} Enchantment — Saga. I: the next red or green
creature spell you cast this turn can be cast without paying its mana cost. II: three +1/+1
counters on target creature you control. III: choose target creature you control, double its power
and toughness and give it trample until end of turn.

**Triage (nobody triaged this card before me — I did it from Scryfall + the code).** Chapters II and
III compose from existing vocabulary: `Effects.AddCounters`, and Epic Fight's doubling shape
(`Effects.ModifyStats` with both halves read off the target at resolution — the standard layer-7c
+N/+N modification) composed with `Effects.GrantKeyword(TRAMPLE)`. Chapter I had no existing
vocabulary: the only free-cast mechanism was the battlefield static `MayCastWithoutPayingManaCost`,
which is read off a permanent and gated on first-spell / once-per-turn / active-player, none of
which is what the printed text says.

**Primitive — `GrantNextSpellFreeCastEffect` (facade `Effects.GrantNextSpellFreeCast(spellFilter)`).**
A one-shot pending rider on `GameState.pendingFreeCastSpells`, deliberately built as the third
member of an existing family (`PendingUncounterableSpell` = Mistrise Village,
`PendingNextSpellAffinity` = Don & Raph) rather than a new mechanism:

- `CostCalculator.hasFreeCastPermission` reads the rider **before** the battlefield scan, so the
  cast surfaces the *existing* `CastSpell.useWithoutPayingManaCost` action variant — no new legal
  action, no new decision, no new client component.
- `CastSpellHandler` consumes the rider on the next matching cast, next to where the two sibling
  riders are consumed. `TurnManager.startTurn` clears an unused one.
- **Consumed by the cast, not by the discount** — "the next red or green creature spell you cast
  this turn" names a spell, so a matching spell cast for full price is that spell and spends the
  grant. This is the one thing that could have looked right and played wrong, so it has its own
  test in both files.
- Living on the state (not on the permanent) is what the printed text requires: the ability has
  already resolved, so the permission survives the Saga leaving the battlefield and isn't restricted
  to a cast zone.
- CR cited and checked against `/workspace/MagicCompRules_20260619.txt`: 118.9 (without paying its
  mana cost *is* an alternative cost), 118.9a (only one alternative cost per cast), 107.3b (X = 0).
- A rider-funded free cast deliberately does **not** burn a `MayCastWithoutPayingManaCost(oncePerTurn
  = true)` source's use (fail-safe, mirroring the existing emblem guard).
- `docs/card-sdk-language-reference.md` updated in the same change — a new bullet in the stack-effect
  rider family, plus a cross-reference from the `MayCastWithoutPayingManaCost` static entry.
- Client: one `ClientPlayerEffect` badge ("Free Cast") so the player can see the pending grant, in
  the same block as the existing copy / uncounterable badges, plus its emoji in `shared.ts`.

**Gate.**
- `just rebless-cards` → exit 0. Only `mtg-sets/src/test/resources/snapshots/cards/MSH.json` moved:
  **104 insertions, 0 deletions**, the only added card `"name"` is `World War Hulk` (the three other
  added `name` keys are my own chapters' target requirements). No other set's golden moved.
- `just test` (full, run *after* the rebless) → BUILD SUCCESSFUL. From `*/build/test-results`:
  **13,297 tests, 0 failures, 0 errors, 27 pre-existing skips** — rules-engine 11,198 / game-server 483
  / mtg-sdk 450 / ai 458 / mtg-sets 345 / mtg-search 163 / mtgish-tooling 143 / gym 43 / gym-server 10
  / gym-trainer 4. `WorldWarHulkScenarioTest` 4/4, `GrantNextSpellFreeCastTest` 5/5.
  The **first** attempt died mid `:rules-engine:test` with "Gradle build daemon disappeared" — the
  documented OOM flake, with a sibling container's build at load 13 and ~350 MB RAM free. I left the
  sibling's daemons alone, waited for the box to go quiet, and re-ran the same gate to completion.
- `just check-card-printing "World War Hulk"` → ok (MSH is the earliest and only set; both MSH
  collector numbers map to the one canonical).
- `just fix-backlog` → 275 / 276.

**Mutation-proved (three, each reverted and the files verified byte-identical by md5 afterwards).**
- Neutering the consumption in `CastSpellHandler` → exactly the three rider-consumption assertions
  in `GrantNextSpellFreeCastTest` went red; "unused rider cleared at turn boundary" and the
  once-per-turn test stayed green.
- Removing the `hasFreeCastRider` guard in `oncePerTurnFreeCastSourceToConsume` → exactly "a
  rider-funded free cast doesn't burn a once-per-turn battlefield permission" went red, nothing else.
- Making the rider ignore its `spellFilter` → exactly "a red creature spell qualifies too, and a blue
  one does not" in `WorldWarHulkScenarioTest` went red, nothing else.

## Things I'm unsure about — please look

- **Consumption semantics.** I could find no Scryfall ruling for this card (it has none), so
  "a matching spell cast for full price spends the grant" is my reading of "the next … spell you
  cast this turn" plus the engine's existing precedent (the affinity rider is consumed by a cast
  whose cost it never changed). If you read it the other way, the change is one `if` in
  `CastSpellHandler` and two test expectations.
- **Graveyard casts.** The rider applies from any zone in `hasFreeCastPermission`, and the exile
  enumerator threads it (`freeCastPermissionFor(cardId, Zone.EXILE)`), but I did not find a
  free-cast variant on the *graveyard* (flashback-style) cast path — that looks like a pre-existing
  gap for the battlefield static too, and I did not widen it.
- **Filter evaluation is asymmetric between the two sites**, as it already is for the affinity
  rider: the permission site is `CostCalculator.matchesCardDefinition` (card definition + the
  rider's own sourceId), the consumption site is `predicateEvaluator.matches` on the stack entity.
  They agree for a printed red/green creature card; a colour-changing continuous effect is the case
  where they could disagree, and I did not test that.
- **Pause/resume casts.** The consumption sits in `CastSpellHandler.execute`, the same place the
  other two riders are consumed. I did not verify a cast that pauses mid-way (X, modes) and resumes
  through a different code path still consumes the rider — the sibling riders have the same
  exposure.
- **Multiplayer / opponents' spells.** The rider is controller-scoped by `controllerId`; only
  two-player games were tested.
- **Not done: no manual playthrough in the web client, no UX pass beyond adding the badge, no e2e
  test, no AI-heuristic review.** The free cast reuses the existing `useWithoutPayingManaCost`
  action variant that Weftwalking/Omniscience already surface, so no new UI was needed — but that is
  reasoning, not a check I ran.
