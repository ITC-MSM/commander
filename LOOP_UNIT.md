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

1. ~~**Token sources are unmatchable once gone.**~~ **Closed in review round 3.** The claim that the
   engine had no last-known store for a deleted token was wrong:
   `ActivatedAbilityOnStackComponent.lastKnownSourceSnapshot` already freezes an `EntitySnapshot` of
   the source whenever the activation cost sacrifices/exiles it — exactly the Clue / Food / Blood /
   Treasure shape. It just wasn't carrying the type line. It now does (via the shared
   `projectedTypeLine` helper), and the `AbilitySourceMatches` branch falls back to it through
   `PredicateEvaluator.matchesSnapshot` when `state.getEntity(sourceId)` is null. A cracked Clue's
   draw ability is a legal, *offered* target for Scientist Supreme; scenario test added. Timing
   nuance found while testing: SBAs run on the engine's post-resolution pass, so the token is still
   readable the instant its ability hits the stack and only vanishes once something else resolves —
   the test reproduces that sequence and asserts the entity is gone before checking the offer. The
   rule is
   **CR 704.5d** ("If a token is in a zone other than the battlefield, it ceases to exist"), not
   704.5s (that one is the Saga final-chapter sacrifice).
2. Related, smaller, still open: a **nontoken** source whose *type* came from a continuous effect
   (animated land, crewed Vehicle) and has since left the battlefield some other way reads its
   printed types — only the self-sacrifice/self-exile cost path freezes the projected type line.
3. `ChosenTarget.Spell` gets no CR 608.2b filter re-check in `StackResolver.validateTargets` (only
   "still on the stack"). Pre-existing, not touched here; it means the source restriction is enforced
   at targeting time only. Exposure is small but not zero: an artifact animated into a creature can
   stop being a creature between targeting and resolution, at which point CR 608.2b says Echo's copy
   ability should fizzle and it won't. Left open deliberately — fixing it is an engine change to the
   shared resolution-time target validation, not this unit's scope.
4. No client change was needed: `StackZone` already routes both the action-pipeline
   (`targetingState`) and decision (`decisionSelectionState`) click paths for stack objects, and
   `TargetingOverlay` only diverts to the pile picker for `Graveyard`/`Exile`. Verified by reading;
   not exercised in a browser.
