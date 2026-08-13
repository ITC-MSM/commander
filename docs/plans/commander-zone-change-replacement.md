# Commander zone-change replacement pipeline

## Boundary

`PendingGameEvent.ZoneChangePending` represents one not-yet-performed card zone
transition.  It is not Commander-specific: every replacement effect that can
match a `ZoneChangeEvent` competes in the same CR 614–616 pipeline.  The
mechanical move remains in `ZoneTransitionService`; the pauseable attempt and
its continuation own replacement selection and the final, exactly-once move.

## Rules constraints

* CR 903.9a is a state-based action only after a commander has entered its
  owner's graveyard or exile.  It must not cover hand or library.
* CR 903.9b is an optional replacement effect before a commander would enter
  its owner's hand or library.  Its owner makes the choice.
* CR 614.5 means a replacement effect cannot apply to the same event chain
  more than once.  CR 614.6 means only the final modified event occurs.
* CR 616 requires all applicable replacements to be considered in their rule
  priority groups; when two or more remain in the current group, the affected
  player chooses.  Optional effects cannot jump ahead of an earlier mandatory
  priority group.

## Implementation slices

1. Add serializable `ZoneChangePending`, a synthetic Commander 903.9b
   replacement identity, and a `ZoneChangePerformContinuation`.  Its modified
   event carries the original source key and entry options, so resumption is
   idempotent and uses the existing transition mechanics.
2. Make the generic replacement processor choose the first non-empty priority
   group across **all** gathered effects, then offer optional acceptance inside
   that group.  This removes the current optional-first CR 616 violation.
3. Add `ZoneTransitionService.attemptMoveToZone`: build the pending event,
   run the generic processor, and either return a normal transition, a paused
   state, or schedule exactly one mechanical transition continuation.  The
   underlying `moveToZone` remains the internal perform primitive.
4. Route generic `MoveToZoneEffect` through the attempt path and add a
   continuation that resumes the remaining effect pipeline after the move.
   Other specialised movement paths remain explicitly audited until migrated;
   none may reintroduce a Commander hand/library shortcut.
5. Retain the 903.9a SBA solely for graveyard/exile and remove all automation
   semantics from `alwaysDivertToCommand`.

## Proof matrix

| Scenario | Required assertion |
|---|---|
| Bounce commander, accept | pause before move; only command zone contains it; one ZoneChangeEvent to command |
| Bounce commander, decline | pause before move; only hand contains it; one ZoneChangeEvent to hand; no SBA prompt |
| Tuck commander, accept/decline | same outcomes for library and no post-move SBA |
| Competing redirect + Commander | affected owner is offered CR 616 choice; selected replacement determines sole destination |
| Re-check/re-entry | selected/declined replacement cannot be reapplied; no duplicate ZoneChangeEvent |
| Graveyard/exile | ordinary move then 903.9a SBA choice remains intact |

## Non-goals for this slice

This does not model every historic non-zone replacement as a zone transition,
nor does it alter Commander tax/damage.  Any migration of specialised movement
must use the same pending event rather than adding a Commander-specific bypass.

## 2026-08 migration inventory — 42 non-cost synchronous callers

### Counting boundary (do not silently change it)

`rg "ZoneTransitionService.moveToZone\\(" rules-engine/src/main/kotlin` finds
55 executable production call sites (plus one comment).  The **42** in this
inventory is exact after excluding the 13 cost-payment sites: ten in
`handlers/CostHandler.kt`, two in `handlers/costs/ForageCostResolver.kt`, and
one in `handlers/costs/CollectEvidenceResolver.kt`.  Those 13 are not safe
mechanical substitutions: a payment can be interrupted only if the entire
casting/activation payment continuation is preserved.  They form the final
cost-payment migration slice, not an omission from the audit.

This list does not claim that the 42 are the only zone moves in the codebase.
Direct `GameState.moveToZone`/`removeFromZone + addToZone` calls are a separate
negative-search gate.  In particular, a Commander hand/library move must not
remain on one of those bypasses merely because it is absent from this grep.

| Group | Exact qualified call sites | Count | Execution risk | P0 relevance |
|---|---|---:|---|---|
| A. Effect/executor paths | `effects/DamageUtils.kt:2161,2245`; `effects/ZoneMovementUtils.kt:553,573,1049`; `effects/library/CascadeExecutor.kt:166`; `effects/library/MoveCollectionExecutor.kt:698`; `effects/linkedexile/ExileUntilLeavesExecutor.kt:68`; `.../ExileWithAurasNotingCountersExecutor.kt:72`; `.../ReturnNotedExileTappedWithAurasExecutor.kt:80,103`; `effects/permanent/ExploreEffectExecutor.kt:121`; `effects/permanent/types/ExileAndReturnTransformedExecutor.kt:70`; `.../TransformEffectExecutor.kt:333`; `effects/token/CreateRoleTokenExecutor.kt:64`; `effects/zones/ExileAndGrantOwnerPlayPermissionExecutor.kt:37`; `.../ForceExileMultiZoneExecutor.kt:139`; `.../ForceSacrificeExecutor.kt:224`; `.../MoveTrackedBattlefieldObjectExecutor.kt:58`; `.../PutOntoBattlefieldAttachedToChosenExecutor.kt:113`; `.../ReturnSameNamedFromGraveyardExecutor.kt:49`; `.../SacrificeExecutor.kt:184`; `.../SacrificeSelfExecutor.kt:44`; `.../SacrificeTargetExecutor.kt:99`; `.../MoveToZoneEffectExecutor.kt:117` | 25 | Effects may have post-move work, multi-card loops, attachments, shuffles, or links. A paused attempt must resume that work exactly once. | **First migration batch**, after the two blockers below. Contains generic bounce/tuck and every shared effect chokepoint. |
| B. Existing continuation resumptions | `continuations/LeylineContinuationResumer.kt:87`; `LibraryAndZoneContinuationResumer.kt:88,229,722`; `ManaPaymentContinuationResumer.kt:345,1430`; `ModalAndCloneContinuationResumer.kt:1164`; `ReplacementContinuationResumer.kt:53`; `SacrificeAndPayContinuationResumer.kt:82,318,658`; `StateBasedContinuationResumer.kt:106` | 12 | Already inside a continuation; nesting a replacement decision must restore the parent frame and its event accumulator in the correct order. | **Second batch**. `LibraryAndZone` is directly relevant to hand/library returns; `ReplacementContinuationResumer` is the perform primitive and stays mechanical. |
| C. Action/cost-adjacent paths | `mechanics/cost/CostPaymentService.kt:584`; `actions/spell/CastSpellHandler.kt:3223,3244` | 3 | Cast-time payment/alternative cost. Sneak and web-slinging return a permanent to hand while casting; a Commander choice can pause the cast. | **Third batch / P0-required** for Sneak and web-slinging. It cannot be implemented by returning a paused `EffectResult` from the current synchronous cast handler. |
| D. State-based movement | `mechanics/sba/SbaZoneMovementHelper.kt:103,171` | 2 | SBA loop must continue to stabilise, and no player receives priority in the middle. | Keep mechanical for ordinary SBA GY/exile moves. Verify they do not call a Commander hand/library replacement path. |
| **Total** | 25 + 12 + 3 + 2 | **42** |  |  |

### First safe batch: the 25 effect/executor paths

Do not change all 25 by mechanically replacing `moveToZone` with
`attemptMoveToZone`.  The smallest safe slices are:

1. **Finish the attempt contract first.** `ProcessorResult.Replaced` currently
   reaches `error("ZoneChangePending cannot be replaced by an effect yet")` in
   `ZoneTransitionService.attemptMoveToZone`.  Implement a domain-preserving
   replacement-with-effect continuation, including NextUse lifecycle handling,
   before any broad routing.  A redirect (`Modified`) is insufficient evidence.
2. **Single-target, no post-move pipeline:** migrate
   `ExileAndGrantOwnerPlayPermission`, `MoveTrackedBattlefieldObject`,
   `ReturnSameNamedFromGraveyard`, `SacrificeSelf`, and `SacrificeTarget`.
   Each needs a small “resume remaining executor work” continuation if it has
   any event/link bookkeeping after the movement.
3. **Generic public chokepoint:** make `MoveToZoneEffectExecutor` uniformly
   attempt-aware, not only for Commander-to-hand/library.  Its continuation
   must preserve counter placement, linking, auto-reveal and Aura-entry
   semantics.  This is the highest-leverage P0 path.
4. **Ordered or linked multi-object work:** migrate `MoveCollection`,
   `Cascade`, the three linked-exile executors,
   `PutOntoBattlefieldAttachedToChosen`, `ForceExileMultiZone`, and
   `ForceSacrifice`.  Their continuation must retain remaining IDs, current
   state, event order, library shuffle/deferred random state, attachment data,
   and “already moved” IDs.  Never rerun a completed iteration after a choice.
5. **Semantic helpers last:** `DamageUtils`, `ZoneMovementUtils`,
   `SacrificeExecutor`, `Explore`, transform/exile-return, and role-token
   cleanup.  Some intentionally use a physical move after a prior decision;
   migrate only when their outer caller can accept a pause.  `ZoneMovementUtils`
   is especially dangerous because it is a shared legacy redirect chokepoint:
   remove/disable any Commander hand/library shortcut only after all its public
   callers are on the generic chain.

### Remaining batches and order

6. Migrate Group B in one continuation-focused PR.  Preserve frame ordering:
   parent remainder below replacement choice/perform; emitted events appended
   once after the actual move.  `ReplacementContinuationResumer` itself must
   call the **mechanical** perform primitive to avoid re-entering its own chain.
7. Migrate Group C with a dedicated `CastPaymentZoneMoveContinuation` (or an
   equivalent frame carrying the complete unconsumed cast action).  Test both
   choice answers for Sneak and web-slinging, and prove mana/other costs are not
   paid twice.  Do not migrate the excluded 13 cost sites until this abstraction
   is in place.

#### CastSpell `AdditionalCost.ReturnToHand`: required extraction blueprint

The ordinary script cost (`CostAtom.ReturnToHand`, currently paid in
`CastSpellHandler`'s generic additional-cost loop) is a separate P0 member of
Group C.  It is not safe to make that loop call `attemptMoveToZone` and return
its paused result: preceding atoms in the same cast may already have paid life,
discarded/exiled cards, tapped permanents, or sacrificed permanents, while the
rest of the cast still has to pay mana, record cast history, and put the spell
on the stack exactly once.

**Required shape.** Extract the unconsumed part of `CastSpellHandler.execute`
into a private post-additional-cost tail.  On a paused return-to-hand attempt,
push a serializable `CastSpellPostAdditionalCostsContinuation` immediately
*below* the generic replacement continuation.  The replacement machinery then
performs the final physical move first; the auto-resumer invokes only that
post-cost tail.  It must never call `CastSpellHandler.execute` again and must
not re-run validation, mode/target selection, cost calculation, or any cost
atom.  For several selected permanents, a small remainder frame must sit below
each replacement continuation and only enter the post-cost tail after the last
physical move.

**State retained by the frame.** The frame (or a serializable nested payload)
must retain all state already frozen before the tail, rather than re-reading a
possibly changed battlefield after the Commander has left it:

* the complete already-authorized `CastSpell` action: caster, card, targets,
  X, payment strategy, alternative payment, all declared cost selections,
  modes and their ordered targets/damage, face choice, and cast-time choices;
* locked mana values: `effectiveCost` and `paymentXValue`, plus whether the
  cast is free/uses its selected alternative route.  Cost reductions or static
  abilities from the returned Commander must not change a cost fixed during
  casting;
* pre-cost identity/permission observations: transformed/modal face result,
  linked-exile granter entry, limited top-library cast source, and any
  graveyard/may-play/free-cast authorization that is consumed only once;
* completed-cost payload: `sacrificedSnapshots`, `exiledCardCount`,
  `beheldCards`, `discardedAsCostCards`, `chosenEntitySnapshots`, pipeline
  collections/numbers/choices, and the accumulated `GameEvent`s in original
  order;
* cast/stack payload calculated after payment: spell target requirements,
  per-mode requirements/ordered targets, creature-type choice if already made,
  mana-spend provenance and per-colour/X accounting, cast-time flags, all
  `was*` mechanic flags, Sneak/Web-slinging metadata (when those rails are
  migrated), splice names, pay-X-life amount, cast origin, and all entry-rider
  data needed by `StackResolver.castSpell` and its post-cast trigger scan.

The tail may re-fetch immutable card definitions by definition ID, but it must
not infer any of the above from live state unless the value is explicitly
defined to be checked at the later stage.  A `SpellOnStackComponent`/cast
payload factory is preferable to a long, loosely related parameter list: it
makes the serializable frozen boundary auditable.

**Test matrix (one engine-level class, plus existing card scenario).**

| Case | Assertions |
|---|---|
| Fear of Isolation, ordinary permanent | cast succeeds; returned permanent moves once to hand; mana is spent once; spell is on stack once, then resolves normally |
| Fear of Isolation, Commander accepts | cast pauses before movement; accepting yields exactly one battlefield-to-command event; resumption spends mana once, puts Fear on stack once, and does not re-open a cost/mode/target prompt |
| Fear of Isolation, Commander declines | same pause/resume path; exactly one battlefield-to-hand event; spell and all selected targets/modes remain those originally validated |
| composite prior cost + return cost | prior life/discard/tap/sacrifice cost is emitted exactly once across the pause; no event/order duplication |
| multi-return cost | first and later Commander choices resume the remaining selected permanent in order; each card has one final zone event; tail runs only after all moves |
| Commander static cost modifier | moving the Commander does not change the already locked mana amount; no mana ability/source is paid twice |
| competing replacement | CR 616 selection/Commander accept or decline produces one final destination and then one tail execution |
| cancellation/error guard | no continuation can put a spell on stack after an invalid response; normal cost-validation failure still leaves all zones and mana unchanged |

The existing `FearOfIsolationScenarioTest` remains the card-level ordinary
regression.  Add a dedicated `CastSpellReturnToHandCommanderReplacementTest`
for the accept/decline, event-count, mana, and one-stack-entry assertions.
8. Retain Group D as mechanical post-event SBA movement and add assertions that
   only CR 903.9a GY/exile runs there.  A Commander entering hand/library from
   an SBA is not a valid reason to prompt after the move.
9. Migrate the excluded 13 payment sites only after step 7; then run the
   negative-search gate until no public Commander-capable zone movement bypass
   remains.

### Required proof before each batch can be accepted

* **Exactly once:** accept, decline, redirect, and replacement-with-effect each
  emit one final `ZoneChangeEvent`, leave the card in one zone, and do not rerun
  post-move effects or shuffle twice.
* **CR 614.5/616:** competing effects are chosen by the affected player in the
  correct priority group; an accepted or declined identity cannot recur after
  resumption.  Cover a Commander choice competing with `RedirectZoneChange`.
* **Parent semantics:** a paused single-target effect resumes its later effects;
  a paused batch resumes at the next card; a paused cast payment neither loses
  the spell nor double-pays it; a paused continuation retains prior events.
* **Commander matrix:** owner chooses hand and library replacement; accept and
  decline both work from every migrated P0 path; GY/exile remains the separate
  903.9a SBA choice and never leaks to hand/library.
* **Regression gates:** focussed class tests per slice, `just test-rules`, then
  a Commander browser/API scenario covering bounce, tuck, and a competing
  replacement.  Finish with ripgrep evidence for the 42/13 classification and
  an explicit reviewed list of non-service movement bypasses.
