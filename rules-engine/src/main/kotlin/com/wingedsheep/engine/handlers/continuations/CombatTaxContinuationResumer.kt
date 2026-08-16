package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.AttackTaxManaSelectionContinuation
import com.wingedsheep.engine.core.BlockTaxManaSelectionContinuation
import com.wingedsheep.engine.core.BlockTaxPayerPlan
import com.wingedsheep.engine.core.BlockTaxPaymentIntent
import com.wingedsheep.engine.core.BlockDeclarationSbaBoundaryContinuation
import com.wingedsheep.engine.core.BlockDeclarationPostPlacementContinuation
import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.ManaSourceOption
import com.wingedsheep.engine.core.ManaSourcesSelectedResponse
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilitiesSelectedResponse
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilityRef
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilitySelection
import com.wingedsheep.engine.core.AtomicBlockTaxManaAbilityOption
import com.wingedsheep.engine.core.PostDecisionHandling
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.SelectAtomicBlockTaxManaAbilitiesDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.tap
import com.wingedsheep.engine.core.TappedEvent
import com.wingedsheep.engine.core.PermanentsSacrificedEvent
import com.wingedsheep.engine.mechanics.mana.ManaPool
import com.wingedsheep.engine.mechanics.mana.ManaSolver
import com.wingedsheep.engine.handlers.actions.combat.BlockDeclarationFinalizer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.EntityId
import java.util.UUID

/**
 * Resumes attack / block declarations that paused for the player to pick mana sources
 * for a generic mana tax (Propaganda, Ghostly Prison, Windborn Muse, Collective
 * Restraint, Whipgrass Entangler, etc.).
 *
 * The prompt is a [com.wingedsheep.engine.core.SelectManaSourcesDecision] with the
 * auto-pay suggestion pre-selected, so the default response taps the same lands the
 * old auto-tap path used to — the player can swap selections or cancel before any
 * mana is spent.
 *
 * Branches:
 *  - `autoPay = true` → run the solver and tap its suggested sources, commit declaration.
 *  - manual non-empty selection → tap the chosen sources, commit declaration.
 *  - empty manual selection (`autoPay = false`) → clean no-op, declaration cancelled.
 *
 * Sources requiring a sub-cost (e.g. Springleaf Drum's "tap another creature") aren't
 * supported as combat-tax payment yet; selecting one returns an error.
 */
class CombatTaxContinuationResumer(
    private val services: com.wingedsheep.engine.core.EngineServices
) : ContinuationResumerModule, AutoResumerModule {

    override fun resumers(): List<ContinuationResumer<*>> = listOf(
        resumer(AttackTaxManaSelectionContinuation::class) { state, continuation, response, _ ->
            resumeAttackTaxSelection(state, continuation, response)
        },
        resumer(BlockTaxManaSelectionContinuation::class) { state, continuation, response, _ ->
            resumeBlockTaxSelection(state, continuation, response)
        },
    )

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(BlockDeclarationSbaBoundaryContinuation::class) { state, continuation, events, _ ->
            BlockDeclarationFinalizer.resumeAfterSbaDecision(
                state = state,
                continuation = continuation,
                precedingSbaEvents = events,
                triggerDetector = services.triggerDetector,
                triggerProcessor = services.triggerProcessor,
                sbaChecker = services.sbaChecker,
                stateTriggerPoller = services.stateTriggerPoller,
            )
        },
        autoResumer(BlockDeclarationPostPlacementContinuation::class) { state, continuation, events, _ ->
            BlockDeclarationFinalizer.resumePostPlacementBoundary(
                state = state,
                continuation = continuation,
                precedingEvents = events,
                triggerDetector = services.triggerDetector,
                triggerProcessor = services.triggerProcessor,
                sbaChecker = services.sbaChecker,
                stateTriggerPoller = services.stateTriggerPoller,
            )
        },
    )

    private fun resumeAttackTaxSelection(
        state: GameState,
        continuation: AttackTaxManaSelectionContinuation,
        response: DecisionResponse,
    ): ExecutionResult {
        if (response !is ManaSourcesSelectedResponse) {
            return ExecutionResult.error(state, "Expected mana sources selected response for attack tax")
        }
        if (response.isDecline(floatingCovers(state, continuation.attackingPlayer, continuation.manaCost))) {
            // Decline: no mana tapped, no AttackingComponent applied. Drop back into
            // DECLARE_ATTACKERS as a clean no-op (no error banner).
            return ExecutionResult.success(state)
        }

        val paid = payTax(state, continuation.attackingPlayer, continuation.manaCost, continuation.availableSources, response)
            ?: return ExecutionResult.error(state, "Cannot pay attack tax of ${continuation.manaCost}")

        return services.combatManager.attackPhase.commitAttackDeclaration(
            state = paid.state,
            attackingPlayer = continuation.attackingPlayer,
            attackers = continuation.attackers,
            projected = paid.state.projectedState,
            taxEvents = paid.events,
            bands = continuation.bands,
        )
    }

    private fun resumeBlockTaxSelection(
        state: GameState,
        continuation: BlockTaxManaSelectionContinuation,
        response: DecisionResponse,
    ): ExecutionResult {
        val legacyPlan = continuation.payerPlans.isEmpty()
        val payerPlans = if (legacyPlan) {
            val legacyManaCost = continuation.manaCost
                ?: return ExecutionResult.error(state, "Missing payer plan for block tax")
            listOf(
                BlockTaxPayerPlan(
                    payerId = continuation.blockingPlayer,
                    manaCost = legacyManaCost,
                    availableSources = continuation.availableSources,
                    autoPaySuggestion = continuation.autoPaySuggestion,
                )
            )
        } else continuation.payerPlans
        val plan = payerPlans.getOrNull(continuation.payerIndex)
            ?: return ExecutionResult.error(state, "Missing payer plan for block tax")
        val declined = when (response) {
            is ManaSourcesSelectedResponse -> response.isDecline(floatingCovers(state, plan.payerId, plan.manaCost))
            is AtomicBlockTaxManaAbilitiesSelectedResponse -> response.declined ||
                (!response.autoPay && atomicSelections(response).isEmpty() && !floatingCovers(state, plan.payerId, plan.manaCost))
            else -> return ExecutionResult.error(state, "Expected block-tax mana payment response")
        }
        if (declined) {
            // No prior payer has changed game state: declining any prompt rolls the whole proposed
            // team declaration back and keeps the defending team at its turn-based action.
            return ExecutionResult.success(state).copy(
                postDecisionHandling = PostDecisionHandling.RETURN_AS_IS,
            )
        }

        // Preserve the established single-player payment flow.  It may use complex mana
        // abilities and the solver's normal auto-pay semantics; only a shared-team proposal
        // needs the deferred, side-effect-free intent transaction below.
        if (legacyPlan || state.sharedTurnTeam(continuation.blockingPlayer).size <= 1) {
            if (response !is ManaSourcesSelectedResponse) {
                return ExecutionResult.error(state, "Expected mana sources selected response for block tax")
            }
            val paid = payTax(
                state,
                plan.payerId,
                plan.manaCost,
                plan.availableSources,
                response,
            ) ?: return ExecutionResult.error(state, "Cannot pay block tax of ${plan.manaCost}")
            val committed = services.combatManager.blockPhase.commitBlockDeclaration(
                state = paid.state,
                blockingPlayer = continuation.blockingPlayer,
                blockers = continuation.blockers,
                taxEvents = paid.events,
            )
            if (!committed.isSuccess) return committed
            return BlockDeclarationFinalizer.finish(
                committed.newState,
                committed.events,
                services.triggerDetector,
                services.triggerProcessor,
                services.sbaChecker,
                services.stateTriggerPoller,
            )
        }

        val intent = when (response) {
            is ManaSourcesSelectedResponse -> BlockTaxPaymentIntent(
                payerId = plan.payerId, selectedSources = response.selectedSources, autoPay = response.autoPay,
            )
            is AtomicBlockTaxManaAbilitiesSelectedResponse -> BlockTaxPaymentIntent(
                payerId = plan.payerId, autoPay = response.autoPay,
                selectedManaAbilitySelections = atomicSelections(response),
                selectedManaAbilityRefs = response.selectedManaAbilityRefs,
            )
            else -> return ExecutionResult.error(state, "Expected block-tax mana payment response")
        }
        // Validate this intent against the immutable plan on a throw-away candidate. The returned
        // state/events are deliberately discarded until every teammate has accepted.
        if (payTax(state, plan, intent, continuation.isAtomicTeamPayment) == null) {
            return ExecutionResult.error(state, "Cannot pay block tax of ${plan.manaCost}")
        }

        val accepted = continuation.acceptedIntents + intent
        val nextIndex = continuation.payerIndex + 1
        if (nextIndex < payerPlans.size) {
            val nextPlan = payerPlans[nextIndex]
            val decisionId = UUID.randomUUID().toString()
            val decision = if (continuation.isAtomicTeamPayment) atomicBlockTaxDecision(decisionId, nextPlan)
            else blockTaxDecision(decisionId, nextPlan)
            val nextContinuation = continuation.copy(
                decisionId = decisionId,
                payerIndex = nextIndex,
                acceptedIntents = accepted,
            )
            return ExecutionResult.paused(
                state.withPendingDecision(decision).pushContinuation(nextContinuation),
                decision,
            ).copy(postDecisionHandling = PostDecisionHandling.RETURN_AS_IS)
        }

        // Every intent is now known. Apply them to one candidate state; if any application fails,
        // return the untouched pre-payment state and emit no mana/block events.
        var paidState = state
        val taxEvents = mutableListOf<GameEvent>()
        if (accepted.size != payerPlans.size) {
            return ExecutionResult.error(state, "Incomplete block-tax payment intents")
        }
        for ((payerPlan, acceptedIntent) in payerPlans.zip(accepted)) {
            val paid = payTax(paidState, payerPlan, acceptedIntent, continuation.isAtomicTeamPayment)
                ?: return ExecutionResult.error(state, "Cannot pay block tax of ${payerPlan.manaCost}")
            paidState = paid.state
            taxEvents += paid.events
        }

        // A chosen `{T}, sacrifice this` mana source can be one of the creatures proposed as a
        // blocker. It was still declared, so its attacker remains blocked; but the source left
        // before 509.1g's blocking-status assignment and must not retain BlockingComponent.
        // Commit the original declaration first (which preserves the attacker's 509.1h status),
        // then clear only the departed blocker's own combat markers.
        val sacrificedBlockers = accepted.flatMap { intent ->
            atomicSelections(intent).filter { selection ->
                payerPlans.firstOrNull { it.payerId == intent.payerId }
                    ?.atomicManaAbilityOptions?.firstOrNull { it.ref == selection.ref }?.requiresSacrificeSelf == true
            }.map { it.ref.sourceId }
        }.toSet()
        val committed = services.combatManager.blockPhase.commitBlockDeclaration(
            state = paidState,
            blockingPlayer = continuation.blockingPlayer,
            blockers = continuation.blockers,
            taxEvents = taxEvents,
        )
        if (!committed.isSuccess) return ExecutionResult.error(state, committed.error ?: "Cannot commit block declaration")
        val committedState = sacrificedBlockers.fold(committed.newState) { current, blockerId ->
            current.updateEntity(blockerId) { container ->
                container.without<com.wingedsheep.engine.state.components.combat.BlockingComponent>()
                    .without<com.wingedsheep.engine.state.components.combat.BlockedThisCombatComponent>()
            }
        }
        return BlockDeclarationFinalizer.finish(
            committedState,
            committed.events,
            services.triggerDetector,
            services.triggerProcessor,
            services.sbaChecker,
            services.stateTriggerPoller,
        )
    }

    private data class TaxPayment(val state: GameState, val events: List<GameEvent>)

    private fun blockTaxDecision(decisionId: String, plan: BlockTaxPayerPlan): SelectManaSourcesDecision =
        SelectManaSourcesDecision(
            id = decisionId,
            playerId = plan.payerId,
            prompt = "Pay {${plan.manaCost.cmc}} to block with your declared creatures",
            context = DecisionContext(
                sourceId = null,
                sourceName = "Block tax",
                phase = DecisionPhase.COMBAT,
            ),
            availableSources = plan.availableSources,
            requiredCost = plan.manaCost.toString(),
            autoPaySuggestion = plan.autoPaySuggestion,
            canDecline = true,
        )

    private fun atomicBlockTaxDecision(
        decisionId: String,
        plan: BlockTaxPayerPlan,
    ) = SelectAtomicBlockTaxManaAbilitiesDecision(
        id = decisionId,
        playerId = plan.payerId,
        prompt = "Pay {${plan.manaCost.cmc}} to block with your declared creatures",
        context = DecisionContext(sourceId = null, sourceName = "Block tax", phase = DecisionPhase.COMBAT),
        availableOptions = plan.atomicManaAbilityOptions,
        requiredCost = plan.manaCost.toString(),
        autoPaySuggestion = plan.atomicAutoPaySuggestion,
        autoPaySelections = plan.atomicAutoPaySelections,
    )

    /** Existing single-payer attack-tax path, including solver-driven auto-pay. */
    private fun payTax(
        state: GameState,
        playerId: EntityId,
        manaCost: ManaCost,
        availableSources: List<ManaSourceOption>,
        response: ManaSourcesSelectedResponse,
    ): TaxPayment? {
        if (!response.autoPay) {
            if (response.selectedSources.size != response.selectedSources.toSet().size) return null
            return payTax(state, playerId, manaCost, availableSources, response.selectedSources)
        }

        val playerEntity = state.getEntity(playerId) ?: return null
        val poolComponent = playerEntity.get<ManaPoolComponent>() ?: return null
        var pool = ManaPool(
            poolComponent.white, poolComponent.blue, poolComponent.black,
            poolComponent.red, poolComponent.green, poolComponent.colorless,
        )
        val partial = pool.payPartial(manaCost)
        var currentState = state
        val events = mutableListOf<GameEvent>()
        if (!partial.remainingCost.isEmpty()) {
            val solution = ManaSolver(services.cardRegistry).solve(currentState, playerId, partial.remainingCost)
                ?: return null
            for (source in solution.sources) {
                val (tappedState, tapEvent) = tap(currentState, source.entityId)
                currentState = tappedState
                tapEvent?.let(events::add)
            }
            for ((_, production) in solution.manaProduced) {
                pool = if (production.color != null) {
                    pool.add(production.color, production.amount)
                } else {
                    pool.addColorless(production.colorless)
                }
            }
        }
        val newPool = pool.pay(manaCost) ?: return null
        currentState = currentState.updateEntity(playerId) { container ->
            container.with(
                ManaPoolComponent(
                    white = newPool.white, blue = newPool.blue, black = newPool.black,
                    red = newPool.red, green = newPool.green, colorless = newPool.colorless,
                )
            )
        }
        return TaxPayment(currentState, events)
    }

    private fun payTax(
        state: GameState,
        plan: BlockTaxPayerPlan,
        intent: BlockTaxPaymentIntent,
        atomicTeamPayment: Boolean,
    ): TaxPayment? {
        if (intent.payerId != plan.payerId) return null
        if (atomicTeamPayment) return payAtomicTax(state, plan, intent)
        val selectedSources = if (intent.autoPay) plan.autoPaySuggestion else intent.selectedSources
        if (selectedSources.size != selectedSources.toSet().size) return null
        val availableById = plan.availableSources.associateBy { it.entityId }
        if (selectedSources.any { it !in availableById }) return null

        // Recheck the fixed source identities before candidate mutation. This rejects forged,
        // off-controller, missing, and already-tapped sources even if a stale client submits them.
        for (sourceId in selectedSources) {
            if (sourceId !in state.getBattlefield()) return null
            val source = state.getEntity(sourceId) ?: return null
            if (state.projectedState.getController(sourceId) != plan.payerId) return null
            if (source.has<TappedComponent>()) return null
        }

        return payTax(
            state = state,
            playerId = plan.payerId,
            manaCost = plan.manaCost,
            availableSources = plan.availableSources,
            selectedSources = selectedSources,
        )
    }

    /** Applies only the explicit, fixed-output branches captured in the atomic payer plan. */
    private fun payAtomicTax(
        state: GameState,
        plan: BlockTaxPayerPlan,
        intent: BlockTaxPaymentIntent,
    ): TaxPayment? {
        val selections = if (intent.autoPay) {
            plan.atomicAutoPaySelections.ifEmpty { plan.atomicAutoPaySuggestion.map(::AtomicBlockTaxManaAbilitySelection) }
        } else atomicSelections(intent)
        if (selections.map { it.ref.sourceId }.size != selections.map { it.ref.sourceId }.toSet().size) return null
        val options = plan.atomicManaAbilityOptions.associateBy { it.ref }
        if (selections.any { it.ref !in options }) return null
        val playerEntity = state.getEntity(plan.payerId) ?: return null
        val oldPool = playerEntity.get<ManaPoolComponent>() ?: return null
        var pool = ManaPool(oldPool.white, oldPool.blue, oldPool.black, oldPool.red, oldPool.green, oldPool.colorless)
        var current = state
        val events = mutableListOf<GameEvent>()
        var explicitTaxManaPaid = 0
        for (selection in selections) {
            val ref = selection.ref
            val option = options.getValue(ref)
            // Colour choice is part of the accepted branch, not a resumption-time default.
            // Fixed-output branches reject a forged colour; any-one-colour branches require one
            // of their offered colours.
            val outputColor = when {
                option.activationManaCost != null -> {
                    if (selection.chosenColor != null || selection.taxPaymentColor !in option.taxPaymentColorChoices) return null
                    null
                }
                option.colorChoices.isEmpty() -> {
                    if (selection.chosenColor != null || selection.taxPaymentColor != null) return null
                    option.producesColors.singleOrNull()
                }
                else -> {
                    if (selection.taxPaymentColor != null) return null
                    selection.chosenColor?.takeIf { it in option.colorChoices } ?: return null
                }
            }
            val container = current.getEntity(ref.sourceId) ?: return null
            if (ref.sourceId !in current.getBattlefield() || current.projectedState.getController(ref.sourceId) != plan.payerId || container.has<TappedComponent>()) return null
            val secondaryTapTargetId = selection.secondaryTapTargetId
            if (option.secondaryTapTargets.isEmpty()) {
                if (secondaryTapTargetId != null) return null
            } else {
                val secondaryId = secondaryTapTargetId ?: return null
                if (secondaryId !in option.secondaryTapTargets.map { it.entityId }) return null
                if (secondaryId == ref.sourceId || secondaryId !in current.getBattlefield() ||
                    current.projectedState.getController(secondaryId) != plan.payerId ||
                    !current.projectedState.isCreature(secondaryId) ||
                    current.getEntity(secondaryId)?.has<TappedComponent>() == true
                ) return null
            }
            // The Signet branch pays its own `{1}` from this payer's already-floating mana
            // before its tap cost and output. Its selected produced colour then pays exactly one
            // unit of this payer's generic block tax; it must not be paid again below.
            option.activationManaCost?.let { activationCost ->
                pool = pool.pay(activationCost) ?: return null
            }
            if (option.requiresSacrificeSelf) {
                events += TappedEvent(ref.sourceId, option.sourceName)
                val tracked = ZoneTransitionService.trackPermanentSacrifice(current, listOf(ref.sourceId), plan.payerId)
                val moved = ZoneTransitionService.moveToZone(tracked, ref.sourceId, Zone.GRAVEYARD)
                current = moved.state
                events += PermanentsSacrificedEvent(plan.payerId, listOf(ref.sourceId), listOf(option.sourceName))
                events += moved.events
            } else {
                val tapped = tap(current, ref.sourceId)
                current = tapped.first
                tapped.second?.let(events::add)
            }
            // Secondary taps are a cost of the selected exact mana-ability branch. They are
            // applied only on this candidate state, after every payer has supplied an intent.
            // A creature selected as a blocker remains controlled/on the battlefield and can
            // therefore still become blocking at the later declaration-commit step.
            if (secondaryTapTargetId != null) {
                val tapped = tap(current, secondaryTapTargetId)
                current = tapped.first
                tapped.second?.let(events::add)
            }
            pool = when {
                option.activationManaCost != null -> option.fixedProducedMana.entries.fold(pool) { accumulated, (color, amount) ->
                    accumulated.add(color, amount)
                }.spend(selection.taxPaymentColor!!) ?: return null
                outputColor != null -> pool.add(outputColor, option.manaAmount)
                option.producesColorless -> pool.addColorless(option.manaAmount)
                else -> return null
            }
            if (option.activationManaCost != null) explicitTaxManaPaid++
        }
        // This bounded branch always spends one coloured output for one generic tax mana. Do not
        // let a malformed future option silently pay more tax than the fixed generic tax permits.
        if (explicitTaxManaPaid > plan.manaCost.genericAmount || plan.manaCost.colors.isNotEmpty() || plan.manaCost.colorlessAmount > 0) return null
        val paid = pool.pay(plan.manaCost.reduceGeneric(explicitTaxManaPaid)) ?: return null
        current = current.updateEntity(plan.payerId) { it.with(ManaPoolComponent(
            white = paid.white, blue = paid.blue, black = paid.black, red = paid.red, green = paid.green, colorless = paid.colorless,
        )) }
        return TaxPayment(current, events)
    }

    private fun payTax(
        state: GameState,
        playerId: EntityId,
        manaCost: ManaCost,
        availableSources: List<ManaSourceOption>,
        selectedSources: List<EntityId>,
    ): TaxPayment? {
        val playerEntity = state.getEntity(playerId) ?: return null
        val poolComponent = playerEntity.get<ManaPoolComponent>() ?: return null
        var pool = ManaPool(
            poolComponent.white, poolComponent.blue, poolComponent.black,
            poolComponent.red, poolComponent.green, poolComponent.colorless,
        )

        val partial = pool.payPartial(manaCost)
        var remainingCost = partial.remainingCost
        var currentState = state
        val events = mutableListOf<GameEvent>()

        if (!remainingCost.isEmpty()) {
            val sourceMap = availableSources.associateBy { it.entityId }
            for (sourceId in selectedSources) {
                val source = sourceMap[sourceId] ?: return null
                if (source.requiresSacrifice || source.requiresTappingAnotherPermanent) return null
                val (tappedState, tapEvent) = tap(currentState, sourceId)
                currentState = tappedState
                tapEvent?.let(events::add)
                pool = when {
                    source.producesColors.isNotEmpty() -> pool.add(source.producesColors.first(), source.manaAmount)
                    source.producesColorless -> pool.addColorless(source.manaAmount)
                    else -> pool
                }
            }
        }

        val newPool = pool.pay(manaCost) ?: return null
        currentState = currentState.updateEntity(playerId) { container ->
            container.with(
                ManaPoolComponent(
                    white = newPool.white, blue = newPool.blue, black = newPool.black,
                    red = newPool.red, green = newPool.green, colorless = newPool.colorless,
                )
            )
        }
        return TaxPayment(currentState, events)
    }

    /**
     * Whether [playerId]'s floating mana already covers [cost] — see
     * [ManaSourcesSelectedResponse.isDecline]. A player who taps their own sources during the
     * payment window (CR 605.3a) confirms with an empty selection, which must not read as a refusal.
     */
    private fun floatingCovers(state: GameState, playerId: EntityId, cost: ManaCost): Boolean =
        com.wingedsheep.engine.mechanics.mana.ManaPaymentWindow.floatingManaCovers(state, playerId, cost)

    @Suppress("DEPRECATION")
    private fun atomicSelections(response: AtomicBlockTaxManaAbilitiesSelectedResponse): List<AtomicBlockTaxManaAbilitySelection> =
        response.selectedManaAbilitySelections.ifEmpty {
            response.selectedManaAbilityRefs.map(::AtomicBlockTaxManaAbilitySelection)
        }

    @Suppress("DEPRECATION")
    private fun atomicSelections(intent: BlockTaxPaymentIntent): List<AtomicBlockTaxManaAbilitySelection> =
        intent.selectedManaAbilitySelections.ifEmpty {
            intent.selectedManaAbilityRefs.map(::AtomicBlockTaxManaAbilitySelection)
        }
}
