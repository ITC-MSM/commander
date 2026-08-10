# loop-msh-u08 — Ability-source predicate on stack targets

## Cards

- **Echo, Perceptive Prodigy** (MSH 51) — vigilance + `{1}, {T}: Copy target activated or triggered
  ability you control from a creature source.` Composes `Targets.ActivatedOrTriggeredAbilityYouControlFrom(Creature)`
  (existing ability-on-stack target + the new `CardPredicate.AbilitySourceMatches`) with the existing
  `Effects.CopyTargetSpellOrAbility`, plus `holdPriority`.
- **Scientist Supreme of A.I.M.** (MSH 225) — `Pay 2 life: Copy target activated or triggered ability
  you control from an artifact source. Activate only during your turn and only once each turn.`
  Same pieces with `GameObjectFilter.Artifact`, `Costs.PayLife(2)` and
  `ActivationRestriction.OnlyDuringYourTurn` + `OncePerTurn`.

## SDK / engine

- `CardPredicate.AbilitySourceMatches(subfilter)` — new stack-branch predicate, sibling of
  `TargetsMatching`. Redirects the match onto the ability's `sourceId` (CR 113.7) and evaluates the
  subfilter there. Builders: `GameObjectFilter.abilitySourceMatches`, `TargetFilter.abilitySourceMatches`,
  `Targets.ActivatedOrTriggeredAbilityYouControlFrom`.
- `StackObjectTargeting.permitsAbilities` — the "may this stack target requirement offer abilities?"
  seam, extracted from `TargetFinder` so the enumerator can share it.
- **Bug fixed, not part of the brief:** `TargetEnumerationUtils.findValidSpellTargets` filtered every
  stack target down to spells unconditionally, so *no* ability-targeting card ever had an ability
  **offered** as a legal target — Gogo, Master of Mimicry and Peter Parker's Camera included. Since
  `ActivatedAbilityEnumerator` gates `holdPriority` on "top of stack ∈ validTargets", those cards
  also never stopped auto-pass. Both readers now go through `StackObjectTargeting`.

## Gate

`just test` — BUILD SUCCESSFUL in 15m 46s, 12200 PASSED / 0 FAILED (`build/pr/loop-msh-u08-gate.log`).
Plus `just rebless-cards` (only MSH.json moved, +150/−0), `just check-card-printing` ok for both
cards, `just check-backlog` in sync.

## Things I'm unsure about — please look

1. **Token sources are unmatchable once gone.** CR 704.5s deletes a token's entity, so a sacrificed
   Clue's "Sacrifice this artifact: draw a card" is *not* "from an artifact source" for Scientist
   Supreme. Non-token sources are fine (the entity survives in the graveyard with its printed types).
   Closing it needs a general last-known store for deleted tokens, which the engine doesn't have —
   `EntitySnapshot`/`LastKnownPermanentComponent` both die with the entity. Documented in the
   predicate KDoc and the DSL reference rather than silently approximated.
2. Related, smaller: a source whose *type* came from a continuous effect (animated land, crewed
   Vehicle) and has since left reads its printed types.
3. `ChosenTarget.Spell` gets no CR 608.2b filter re-check in `StackResolver.validateTargets` (only
   "still on the stack"). Pre-existing, not touched here; it means the source restriction is enforced
   at targeting time only. Arguably correct in practice (the ability can't stop being from a creature
   source), but worth a second opinion.
4. No client change was needed: `StackZone` already routes both the action-pipeline
   (`targetingState`) and decision (`decisionSelectionState`) click paths for stack objects, and
   `TargetingOverlay` only diverts to the pile picker for `Graveyard`/`Exile`. Verified by reading;
   not exercised in a browser.
