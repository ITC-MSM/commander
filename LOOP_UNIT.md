# u19 — Wolverine, Fierce Fighter (MSH)

**Card.** `Wolverine, Fierce Fighter` {2}{R}{G} 3/5 — Haste, an ETB `Effects.Fight` with an optional
`TargetFilter.OtherCreature` target ("up to one other target creature"), and the healing factor: "If
damage would be dealt to Wolverine, instead that damage is dealt, but all other damage already dealt
to him is healed."

**Primitive.** New `ReplacementEffect.HealOtherDamage(appliesTo)` — the damage is dealt in full and
all damage marked *before* it is removed (CR 701.69a, **Heal**, which is a real keyword action in the
2026-06-19 Comprehensive Rules). Engine side: `DamageUtils.healMarkedDamage` (the bare CR 701.69a
action) + `DamageUtils.applyHealOtherDamage` (the replacement), wired on both creature-damage paths.

**Planner triage was wrong.** The plan said "`RemoveDamage` exists in `RemovalEffects.kt`; wiring it
as a damage replacement is the novel part." There is no `RemoveDamage`. What is in that file is
`RemoveDamageShieldEffect`, an unrelated *destruction*-replacement shield (Pyramids). No SDK effect
removes marked damage at all today — regeneration and the Pyramids shield each inline
`.without<DamageComponent>()`. So the primitive had to be built, not wired.

**Gate.** `just test` — see the PR body for the recorded result. `just rebless-cards` (only Wolverine
moved: +57 lines, zero deletions), `just check-card-printing "Wolverine, Fierce Fighter"` (ok), and
the backlog tick + `just fix-backlog` (268/276) also ran.

## For the reviewer — things worth a second opinion

- **Why a new type rather than composition.** Every existing damage replacement changes the *amount*
  (`PreventDamage` subtracts, `CapDamage` clamps, `ModifyDamageAmount` adds) or swaps the damage for
  something else (`ReplaceDamageWithCounters`/`Mill`). This one leaves the amount alone and its whole
  job is a side effect on already-marked damage — there was nothing to compose. Say if you'd rather
  see a generic "damage replacement with a rider effect" instead.
- **Simultaneity is enforced by a threaded mutable set, not by a batch pre-pass.** I added a
  `healProcessedTargets: MutableSet<EntityId>` through `CombatDamageManager.applySingleAssignment` →
  `applyDamageToPlayer`/`applyDamageToCreature` → `dealFinalDamage`, rather than mirroring
  `applyShieldCountersToCombatDamage`'s batch pre-pass. Reason: the pre-pass runs *before*
  per-assignment prevention, so a Fog would have healed Wolverine for free. Threading costs 5 private
  signatures; the pre-pass would have been shorter but wrong on that edge. Push back if the pre-pass
  shape is preferred for consistency.
- **No `GameEvent` is emitted for the heal.** Existing precedent (regeneration,
  `applyRemoveDamageReplacement`) removes marked damage silently, and the client reads damage off
  `DamageComponent` in `ClientStateTransformer`, so the UI is correct — but this does sit against
  AGENTS.md's "events, not silent mutations". Deliberate consistency choice; flag it if a
  `DamageHealedEvent` should exist (it would want to cover regeneration too, which is out of scope
  here).
- **Excess damage (CR 120.4a) is computed after the heal.** `lethalNeeded` therefore reads the full
  toughness rather than `toughness − previouslyMarked`. That follows from the heal being part of the
  same replacement, but I could not find a ruling that pins it; it only matters for trample-over and
  "excess damage" payoff cards.
- **`u18`'s `ai/EffectWalker` lesson checked and does not apply.** `ai/` has no consumer of
  `ReplacementEffect` at all (verified by grep); the only structural consumer is
  `StaticAbilityHandler.isRuntimeReplacementEffect`, whose exhaustive `when` I classified into.
- **The ETB prompts even with no legal target.** With Wolverine as the only creature, the engine
  still raises a `ChooseTargetsDecision` for the optional target; choosing nothing resolves it
  harmlessly. Asserted as-is rather than "fixed" — it is pre-existing optional-target behaviour, not
  something this card introduced.
