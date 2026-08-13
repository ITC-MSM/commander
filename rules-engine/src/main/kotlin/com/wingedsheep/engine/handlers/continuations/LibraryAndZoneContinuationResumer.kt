package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.*
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PipelineState
import com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler
import com.wingedsheep.engine.handlers.TargetFinder
import com.wingedsheep.engine.handlers.effects.library.CascadeExecutor
import com.wingedsheep.engine.handlers.effects.library.ChooseOnePerCategoryExecutor
import com.wingedsheep.engine.handlers.effects.library.CastFromCollectionWithoutPayingCostExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.state.components.identity.PlayWithoutPayingCostComponent
import com.wingedsheep.engine.state.permissions.MayPlayPermission
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.CastAnyNumberFromCollectionWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.CastFromCollectionWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

class LibraryAndZoneContinuationResumer(
    private val services: com.wingedsheep.engine.core.EngineServices
) : ContinuationResumerModule, AutoResumerModule {

    private val castSpellHandler: CastSpellHandler by lazy { CastSpellHandler.create(services) }
    private val targetFinder = TargetFinder()
    private val effectRunner: EffectContinuationRunner by lazy {
        EffectContinuationRunner(services.effectExecutorRegistry)
    }

    override fun resumers(): List<ContinuationResumer<*>> = listOf(
        resumer(ReturnFromGraveyardContinuation::class, ::resumeReturnFromGraveyard),
        resumer(MoveCollectionOrderContinuation::class, ::resumeMoveCollectionOrder),
        resumer(PutOnBottomOfLibraryContinuation::class, ::resumePutOnBottomOfLibrary),
        resumer(PutFromHandContinuation::class, ::resumePutFromHand),
        resumer(SelectFromCollectionContinuation::class, ::resumeSelectFromCollection),
        resumer(ChooseOnePerCategoryContinuation::class, ::resumeChooseOnePerCategory),
        resumer(ChoosePileContinuation::class, ::resumeChoosePile),
        resumer(SelectTargetPipelineContinuation::class, ::resumeSelectTargetPipeline),
        resumer(MoveCollectionAuraTargetContinuation::class, ::resumeMoveCollectionAuraTarget),
        resumer(PutOntoBattlefieldAttachedToChosenContinuation::class, ::resumePutOntoBattlefieldAttachedToChosen),
        resumer(PutOnTopOrBottomContinuation::class, ::resumePutOnTopOrBottom),
        resumer(CascadeMayCastContinuation::class, ::resumeCascadeMayCast),
        resumer(DiscoverMayCastContinuation::class, ::resumeDiscoverMayCast),
        resumer(CastFromCollectionTargetsContinuation::class, ::resumeCastFromCollectionTargets),
        resumer(CastAnyNumberFromCollectionContinuation::class, ::resumeCastAnyNumberFromCollection)
    )

    override fun autoResumers(): List<AutoResumer<*>> = listOf(
        autoResumer(ExileFromTopRepeatingContinuation::class) { state, continuation, events, checkForMore ->
            val executor = com.wingedsheep.engine.handlers.effects.library.ExileFromTopRepeatingExecutor()
            val result = executor.resumeAfterHandMove(state, continuation)
            if (result.isPaused) {
                ExecutionResult.paused(result.state, result.pendingDecision!!, events + result.events)
            } else {
                checkForMore(result.state, events + result.events)
            }
        },
        autoResumer(RevealLibraryMoveAfterReplacementContinuation::class) { state, continuation, events, checkForMore ->
            val finalState = if (continuation.cardId in state.getZone(ZoneKey(continuation.ownerId, Zone.LIBRARY))) {
                com.wingedsheep.engine.handlers.effects.library.LibraryRevealUtils
                    .markRevealed(state, listOf(continuation.cardId), state.turnOrder.toSet())
            } else {
                state
            }
            checkForMore(finalState, events)
        },
        autoResumer(DiscoverHandMoveAfterReplacementContinuation::class) { state, continuation, events, checkForMore ->
            runDiscoverThenEffect(
                state,
                continuation.discover,
                continuation.discoveredCollections,
                events,
                checkForMore
            )
        },
        autoResumer(MoveCollectionCommanderRemainderContinuation::class) { state, continuation, events, checkForMore ->
            val executor = com.wingedsheep.engine.handlers.effects.library.MoveCollectionExecutor(
                services.cardRegistry, targetFinder
            )
            val result = executor.resumeCommanderRemainder(state, continuation, events)
            if (result.isPaused) {
                ExecutionResult.paused(result.state, result.pendingDecision!!, result.events)
            } else if (result.error != null) {
                result.toExecutionResult()
            } else {
                checkForMore(result.state, result.events)
            }
        },
        autoResumer(MoveCollectionOrderedLibraryRemainderContinuation::class) { state, continuation, events, checkForMore ->
            resumeOrderedLibraryMoves(state, continuation, events, checkForMore)
        },
        autoResumer(BottomLibraryMoveRemainderContinuation::class) { state, continuation, events, checkForMore ->
            val result = CascadeExecutor.moveBottomRemainder(state, continuation, events)
            if (result.isPaused) {
                ExecutionResult.paused(result.state, result.pendingDecision!!, result.events)
            } else {
                checkForMore(result.state, result.events)
            }
        },
        autoResumer(CascadeMayCastAfterBottomContinuation::class) { state, continuation, events, checkForMore ->
            resumeCascadeAfterBottom(state, continuation.cascade, events, checkForMore)
        },
        autoResumer(DiscoverMayCastAfterBottomContinuation::class) { state, continuation, events, checkForMore ->
            resumeDiscoverAfterBottom(
                state, continuation.discover, continuation.castForFree, events, checkForMore
            )
        }
    )

    fun resumeReturnFromGraveyard(
        state: GameState,
        continuation: ReturnFromGraveyardContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is CardsSelectedResponse) {
            return ExecutionResult.error(state, "Expected card selection response for graveyard search")
        }

        val playerId = continuation.playerId
        val selectedCards = response.selectedCards

        // Empty selection — no card returned
        if (selectedCards.isEmpty()) {
            return checkForMore(state, emptyList())
        }

        val cardId = selectedCards.first()
        val graveyardZone = ZoneKey(playerId, Zone.GRAVEYARD)

        // Validate card is still in graveyard
        if (cardId !in state.getZone(graveyardZone)) {
            return checkForMore(state, emptyList())
        }

        val destZone = when (continuation.destination) {
            SearchDestination.HAND -> Zone.HAND
            SearchDestination.BATTLEFIELD -> Zone.BATTLEFIELD
            else -> return ExecutionResult.error(state, "Unsupported destination: ${continuation.destination}")
        }

        // Returning a commander from a graveyard to its owner's hand is a pre-move
        // Commander replacement opportunity.  This continuation has no executor-local
        // work after its one transition, so it can safely return the nested decision
        // and let ZoneChangePerformContinuation complete the move exactly once.
        val transitionResult = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
            state, cardId, destZone,
            com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(controllerId = playerId),
            ZoneKey(playerId, Zone.GRAVEYARD)
        )

        return if (transitionResult.isPaused) {
            ExecutionResult.paused(
                transitionResult.state,
                transitionResult.pendingDecision!!,
                transitionResult.events
            )
        } else {
            checkForMore(transitionResult.state, transitionResult.events)
        }
    }

    /**
     * Resume after player ordered cards for a MoveCollection with ControllerChooses order.
     *
     * The response contains the card IDs in the new order (first = new top of library).
     * We remove the cards from their current zones and place them on top in the chosen order.
     */
    fun resumeMoveCollectionOrder(
        state: GameState,
        continuation: MoveCollectionOrderContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OrderedResponse) {
            return ExecutionResult.error(state, "Expected ordered response for MoveCollection order")
        }

        val orderedCards = response.orderedObjects
        if (orderedCards.size != continuation.cards.size ||
            orderedCards.toSet().size != orderedCards.size ||
            orderedCards.toSet() != continuation.cards.toSet()
        ) {
            return ExecutionResult.error(state, "Ordered response must contain each offered card exactly once")
        }

        // A top insertion is performed in reverse so that the first card selected remains
        // the new top. Bottom insertion is performed in the selected order. Each physical
        // move goes through the generic replacement pipeline; an optional Commander
        // replacement can therefore pause before (rather than after) the zone change.
        val moveOrder = if (continuation.placement == ZonePlacement.Bottom) {
            orderedCards
        } else {
            orderedCards.asReversed()
        }
        val remainder = MoveCollectionOrderedLibraryRemainderContinuation(
            playerId = continuation.playerId,
            sourceName = continuation.sourceName,
            orderedCards = orderedCards,
            remainingMoveOrder = moveOrder,
            destinationPlayerId = continuation.destinationPlayerId,
            placement = continuation.placement,
            revealed = continuation.revealed,
            revealToSelf = continuation.revealToSelf
        )
        return resumeOrderedLibraryMoves(state, remainder, emptyList(), checkForMore)
    }

    /** Continue an ordered library insertion, retaining the remaining order below a nested replacement. */
    private fun resumeOrderedLibraryMoves(
        state: GameState,
        continuation: MoveCollectionOrderedLibraryRemainderContinuation,
        leadingEvents: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val nextCard = continuation.remainingMoveOrder.firstOrNull()
        if (nextCard == null) {
            val library = state.getZone(ZoneKey(continuation.destinationPlayerId, Zone.LIBRARY)).toSet()
            val cardsActuallyInLibrary = continuation.orderedCards.filter { it in library }
            val revealedState = if (cardsActuallyInLibrary.isNotEmpty()) {
                val audience = if (continuation.revealed) state.turnOrder.toSet() else setOf(continuation.playerId)
                com.wingedsheep.engine.handlers.effects.library.LibraryRevealUtils
                    .markRevealed(state, cardsActuallyInLibrary, audience)
            } else state
            return checkForMore(
                revealedState,
                leadingEvents + LibraryReorderedEvent(
                    playerId = continuation.playerId,
                    cardCount = continuation.orderedCards.size,
                    source = continuation.sourceName
                )
            )
        }

        val ownerId = state.getEntity(nextCard)?.get<CardComponent>()?.ownerId
            ?: return resumeOrderedLibraryMoves(
                state, continuation.copy(remainingMoveOrder = continuation.remainingMoveOrder.drop(1)), leadingEvents, checkForMore
            )
        val sourceZone = Zone.entries.firstNotNullOfOrNull { zone ->
            ZoneKey(ownerId, zone).takeIf { nextCard in state.getZone(it) }
        }
        if (sourceZone == null) {
            return resumeOrderedLibraryMoves(
                state, continuation.copy(remainingMoveOrder = continuation.remainingMoveOrder.drop(1)), leadingEvents, checkForMore
            )
        }

        val placement = if (continuation.placement == ZonePlacement.Bottom) {
            com.wingedsheep.engine.handlers.effects.LibraryPlacement.Bottom
        } else {
            com.wingedsheep.engine.handlers.effects.LibraryPlacement.Top
        }
        val attempt = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
            state = state,
            entityId = nextCard,
            destinationZone = Zone.LIBRARY,
            options = com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(
                controllerId = continuation.destinationPlayerId,
                libraryPlacement = placement
            ),
            fromZoneKey = sourceZone
        )
        val events = leadingEvents + attempt.events
        val remainder = continuation.copy(remainingMoveOrder = continuation.remainingMoveOrder.drop(1))
        if (!attempt.isPaused) {
            return resumeOrderedLibraryMoves(attempt.state, remainder, events, checkForMore)
        }

        // Replacement processing pushed its decision continuation. Keep our remaining work
        // immediately underneath it, so it resumes only after the chosen zone move completes.
        val stack = attempt.state.continuationStack
        val stateWithRemainder = attempt.state.copy(
            continuationStack = stack.dropLast(1) + remainder + stack.last()
        )
        return ExecutionResult.paused(stateWithRemainder, attempt.pendingDecision!!, events)
    }

    /**
     * Resume after player ordered cards to put on the bottom of their library.
     *
     * Same as resumeReorderLibrary but places cards on the BOTTOM of the library
     * instead of the top. Used for effects like Erratic Explosion.
     */
    fun resumePutOnBottomOfLibrary(
        state: GameState,
        continuation: PutOnBottomOfLibraryContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OrderedResponse) {
            return ExecutionResult.error(state, "Expected ordered response for library bottom reorder")
        }

        val playerId = continuation.playerId
        val orderedCards = response.orderedObjects
        val libraryZone = ZoneKey(playerId, Zone.LIBRARY)

        // Get current library
        val currentLibrary = state.getZone(libraryZone).toMutableList()

        // Remove the reordered cards from the library (they should already be removed by the executor,
        // but filter just in case)
        val cardsSet = orderedCards.toSet()
        val remainingLibrary = currentLibrary.filter { it !in cardsSet }

        // Place the cards on the BOTTOM in the player's chosen order
        val newLibrary = remainingLibrary + orderedCards

        // Update the library zone
        val newState = state.copy(
            zones = state.zones + (libraryZone to newLibrary)
        )

        val events = listOf(
            LibraryReorderedEvent(
                playerId = playerId,
                cardCount = orderedCards.size,
                source = continuation.sourceName
            )
        )

        return checkForMore(newState, events)
    }

    fun resumePutFromHand(
        state: GameState,
        continuation: PutFromHandContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is CardsSelectedResponse) {
            return ExecutionResult.error(state, "Expected card selection response for put-from-hand")
        }

        // Player selected 0 cards — declined
        if (response.selectedCards.isEmpty()) {
            return checkForMore(state, emptyList())
        }

        val cardId = response.selectedCards.first()
        val playerId = continuation.playerId
        val handZone = ZoneKey(playerId, Zone.HAND)

        // Verify card is still in hand
        if (cardId !in state.getZone(handZone)) {
            return checkForMore(state, emptyList())
        }

        // Delegate zone movement to ZoneTransitionService for full entry setup (including Saga entry)
        val transitionResult = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.moveToZone(
            state, cardId, Zone.BATTLEFIELD,
            com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(
                controllerId = playerId,
                tapped = continuation.entersTapped
            ),
            ZoneKey(playerId, Zone.HAND)
        )

        return checkForMore(transitionResult.state, transitionResult.events)
    }

    /**
     * Resume after a player chose a target for an Aura entering via MoveCollectionEffect.
     * Moves the aura from current zone to battlefield with AttachedToComponent.
     */
    fun resumeMoveCollectionAuraTarget(
        state: GameState,
        continuation: MoveCollectionAuraTargetContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is TargetsResponse) {
            return ExecutionResult.error(state, "Expected targets response for aura target selection")
        }

        val targetIds = response.selectedTargets[0] ?: emptyList()
        if (targetIds.isEmpty()) {
            return ExecutionResult.error(state, "No target selected for aura")
        }

        val targetId = targetIds.first()
        val auraId = continuation.auraId
        val destPlayerId = continuation.destPlayerId

        // Use MoveCollectionExecutor's helper to move aura to battlefield with attachment
        val executor = com.wingedsheep.engine.handlers.effects.library.MoveCollectionExecutor(
            cardRegistry = services.cardRegistry,
            targetFinder = services.targetFinder
        )
        val (newState, moveEvents) = executor.moveAuraToBattlefield(state, auraId, targetId, destPlayerId)

        // Continue with remaining auras
        val remainingAuras = continuation.remainingAuras
        if (remainingAuras.isNotEmpty()) {
            val nextAuraId = remainingAuras.first()
            val nextRemaining = remainingAuras.drop(1)

            // When underOwnersControl, use the next aura's owner as its controller
            val nextControllerId = if (continuation.underOwnersControl) {
                val e = newState.getEntity(nextAuraId)
                e?.get<OwnerComponent>()?.playerId
                    ?: e?.get<CardComponent>()?.ownerId
                    ?: continuation.controllerId
            } else continuation.controllerId

            val nextCardComponent = newState.getEntity(nextAuraId)?.get<CardComponent>()
            val nextCardDef = nextCardComponent?.let { services.cardRegistry.getCard(it.cardDefinitionId) }
            val nextAuraTarget = nextCardDef?.script?.auraTarget

            if (nextAuraTarget == null) {
                // Skip this aura, continue to next
                return resumeMoveCollectionAuraTarget(
                    newState,
                    continuation.copy(
                        auraId = nextAuraId,
                        controllerId = nextControllerId,
                        destPlayerId = nextControllerId,
                        remainingAuras = nextRemaining,
                        decisionId = "skip"
                    ),
                    response,
                    checkForMore
                )
            }

            val legalTargets = services.targetFinder.findLegalTargets(
                state = newState,
                requirement = nextAuraTarget,
                controllerId = nextControllerId,
                sourceId = nextAuraId,
                ignoreTargetingRestrictions = true
            )

            if (legalTargets.isEmpty()) {
                // No targets — Aura stays in current zone (Rule 303.4g), continue to next
                if (nextRemaining.isNotEmpty()) {
                    return resumeMoveCollectionAuraTarget(
                        newState,
                        continuation.copy(
                            auraId = nextRemaining.first(),
                            controllerId = nextControllerId,
                            destPlayerId = nextControllerId,
                            remainingAuras = nextRemaining.drop(1),
                            decisionId = "skip"
                        ),
                        response,
                        checkForMore
                    )
                }
                return checkForMore(newState, moveEvents)
            }

            // Pause for next aura target
            val decisionId = java.util.UUID.randomUUID().toString()
            val auraName = nextCardComponent.name
            val requirementInfo = TargetRequirementInfo(
                index = 0,
                description = nextAuraTarget.description,
                minTargets = 1,
                maxTargets = 1
            )
            val decision = ChooseTargetsDecision(
                id = decisionId,
                playerId = nextControllerId,
                prompt = "Choose what $auraName enchants",
                context = DecisionContext(
                    sourceId = nextAuraId,
                    sourceName = auraName,
                    phase = DecisionPhase.RESOLUTION
                ),
                targetRequirements = listOf(requirementInfo),
                legalTargets = mapOf(0 to legalTargets)
            )

            val nextContinuation = MoveCollectionAuraTargetContinuation(
                decisionId = decisionId,
                auraId = nextAuraId,
                controllerId = nextControllerId,
                destPlayerId = nextControllerId,
                remainingAuras = nextRemaining,
                sourceId = continuation.sourceId,
                sourceName = continuation.sourceName,
                underOwnersControl = continuation.underOwnersControl
            )

            val stateWithDecision = newState.withPendingDecision(decision)
            val stateWithContinuation = stateWithDecision.pushContinuation(nextContinuation)

            return ExecutionResult(
                state = stateWithContinuation,
                events = moveEvents,
                pendingDecision = decision
            )
        }

        return checkForMore(newState, moveEvents)
    }

    /**
     * Resume after the controller chooses a host for a card put onto the battlefield attached to
     * a chosen permanent (One Last Job mode 3). Moves the Aura/Equipment to the battlefield under
     * the controller's control and attaches it to the chosen host, reusing the permanent-agnostic
     * [com.wingedsheep.engine.handlers.effects.library.MoveCollectionExecutor.moveAuraToBattlefield].
     */
    fun resumePutOntoBattlefieldAttachedToChosen(
        state: GameState,
        continuation: PutOntoBattlefieldAttachedToChosenContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is TargetsResponse) {
            return ExecutionResult.error(state, "Expected targets response for attach-host selection")
        }

        val hostIds = response.selectedTargets[0] ?: emptyList()
        if (hostIds.isEmpty()) {
            // No host chosen — leave the card where it is (mode does nothing).
            return checkForMore(state, emptyList())
        }
        val hostId = hostIds.first()

        // Host must still be on the battlefield.
        if (!state.getBattlefield().contains(hostId)) {
            return checkForMore(state, emptyList())
        }

        val executor = com.wingedsheep.engine.handlers.effects.library.MoveCollectionExecutor(
            cardRegistry = services.cardRegistry,
            targetFinder = services.targetFinder
        )
        val (newState, events) = executor.moveAuraToBattlefield(
            state, continuation.cardId, hostId, continuation.controllerId
        )

        return checkForMore(newState, events)
    }

    fun resumeSelectFromCollection(
        state: GameState,
        continuation: SelectFromCollectionContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is CardsSelectedResponse) {
            return ExecutionResult.error(state, "Expected card selection response for SelectFromCollection")
        }

        // Apply any selection restrictions server-side. Iterate the player's
        // response order so earlier picks win when a restriction rejects a later
        // one; rejected cards fall through into the remainder collection.
        val acceptedSet: Set<EntityId> = if (continuation.restrictions.isEmpty()) {
            response.selectedCards.toSet()
        } else {
            val kept = mutableSetOf<EntityId>()
            val claimedTypes = mutableSetOf<com.wingedsheep.sdk.core.CardType>()
            val claimedColors = mutableSetOf<com.wingedsheep.sdk.core.Color>()
            val claimedNames = mutableSetOf<String>()
            val claimedLandTypes = mutableSetOf<com.wingedsheep.sdk.core.Subtype>()
            val claimedPowers = mutableSetOf<Int>()
            var runningManaValue = 0
            var runningPower = 0
            // A card's projected power (after continuous effects), or 0 if undefined. Used by
            // TotalPowerAtMost — battlefield P/T must read projection (CLAUDE.md).
            fun projectedPowerOf(cardId: EntityId): Int =
                state.projectedState.getPower(cardId) ?: 0
            // A card's fixed (printed) power, or null for cards with no fixed power.
            fun fixedPowerOf(cardId: EntityId): Int? =
                state.getEntity(cardId)
                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                    ?.baseStats?.basePower
            // Basic land subtypes a card has (Plains/Island/Swamp/Mountain/Forest), for OnePerBasicLandType.
            fun basicLandTypesOf(cardId: EntityId): Set<com.wingedsheep.sdk.core.Subtype> =
                state.getEntity(cardId)
                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                    ?.typeLine?.subtypes
                    ?.filter { it.value in com.wingedsheep.sdk.core.Subtype.ALL_BASIC_LAND_TYPES }
                    ?.toSet() ?: emptySet()
            for (cardId in response.selectedCards) {
                val acceptsAllRestrictions = continuation.restrictions.all { restriction ->
                    when (restriction) {
                        is SelectionRestriction.OnePerCardType -> {
                            val cardTypes = state.getEntity(cardId)
                                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                ?.typeLine?.cardTypes ?: emptySet()
                            cardTypes.isEmpty() || cardTypes.none { it in claimedTypes }
                        }
                        is SelectionRestriction.OnePerColor -> {
                            val cardColors = state.getEntity(cardId)
                                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                ?.colors ?: emptySet()
                            // Colourless cards are not constrained by this restriction.
                            cardColors.isEmpty() || cardColors.none { it in claimedColors }
                        }
                        is SelectionRestriction.OnePerCardName -> {
                            val cardName = state.getEntity(cardId)
                                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                ?.name
                            cardName == null || cardName !in claimedNames
                        }
                        is SelectionRestriction.TotalManaValueAtMost -> {
                            val mv = state.getEntity(cardId)
                                ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                ?.manaValue ?: 0
                            runningManaValue + mv <= restriction.max
                        }
                        is SelectionRestriction.TotalPowerAtMost -> {
                            runningPower + projectedPowerOf(cardId) <= restriction.max
                        }
                        is SelectionRestriction.OnePerBasicLandType -> {
                            val types = basicLandTypesOf(cardId)
                            // A typeless land can't be kept; a typed land needs all its types free.
                            types.isNotEmpty() && types.none { it in claimedLandTypes }
                        }
                        is SelectionRestriction.OnePerPower -> {
                            // A card with no fixed power can't be kept; otherwise its power must be free.
                            val power = fixedPowerOf(cardId)
                            power != null && power !in claimedPowers
                        }
                        is SelectionRestriction.ReducedMinimumIfMatches -> true
                        is SelectionRestriction.MaxAffordablePayment ->
                            // A pure count cap, already folded into the decision's maxSelections
                            // at decision-build time and enforced by response validation; game
                            // state can't change while the decision is pending, so there is
                            // nothing to re-check per card here.
                            true
                    }
                }
                if (acceptsAllRestrictions) {
                    kept += cardId
                    // Update restriction bookkeeping for subsequent picks.
                    for (restriction in continuation.restrictions) {
                        when (restriction) {
                            is SelectionRestriction.OnePerCardType -> {
                                claimedTypes += state.getEntity(cardId)
                                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                    ?.typeLine?.cardTypes ?: emptySet()
                            }
                            is SelectionRestriction.OnePerColor -> {
                                claimedColors += state.getEntity(cardId)
                                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                    ?.colors ?: emptySet()
                            }
                            is SelectionRestriction.OnePerCardName -> {
                                val cardName = state.getEntity(cardId)
                                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                    ?.name
                                if (cardName != null) claimedNames += cardName
                            }
                            is SelectionRestriction.TotalManaValueAtMost -> {
                                runningManaValue += state.getEntity(cardId)
                                    ?.get<com.wingedsheep.engine.state.components.identity.CardComponent>()
                                    ?.manaValue ?: 0
                            }
                            is SelectionRestriction.TotalPowerAtMost -> {
                                runningPower += projectedPowerOf(cardId)
                            }
                            is SelectionRestriction.OnePerBasicLandType -> {
                                claimedLandTypes += basicLandTypesOf(cardId)
                            }
                            is SelectionRestriction.OnePerPower -> {
                                fixedPowerOf(cardId)?.let { claimedPowers += it }
                            }
                            is SelectionRestriction.ReducedMinimumIfMatches -> {
                                // Response validation enforces the conditional minimum.
                            }
                            is SelectionRestriction.MaxAffordablePayment -> {
                                // Count cap — no per-card bookkeeping (see the accept check above).
                            }
                        }
                    }
                }
            }
            kept
        }

        val selected = continuation.allCards.filter { it in acceptedSet }
        val remainder = continuation.allCards.filter { it !in acceptedSet }

        // Build the updated collections
        val updatedCollections = continuation.storedCollections.toMutableMap()
        updatedCollections[continuation.storeSelected] = selected
        if (continuation.storeRemainder != null) {
            updatedCollections[continuation.storeRemainder] = remainder
        }

        // Inject updated collections into the consumer frame beneath (if any)
        val newState = exposeCollectionsToNextFrame(state, updatedCollections)

        return checkForMore(newState, emptyList())
    }

    /**
     * Resume after one chooser answered one category of a
     * [com.wingedsheep.sdk.scripting.effects.ChooseOnePerCategoryEffect] ("chooses a permanent they
     * control of each permanent type"): record the pick and re-enter the collect loop, which either
     * asks the next question or — once every chooser is done — publishes the picks so the
     * downstream "…the rest" steps can act on them.
     */
    fun resumeChooseOnePerCategory(
        state: GameState,
        continuation: ChooseOnePerCategoryContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is CardsSelectedResponse) {
            return ExecutionResult.error(state, "Expected card selection response for ChooseOnePerCategory")
        }

        val result = ChooseOnePerCategoryExecutor().collectPicks(
            state = state,
            effect = continuation.effect,
            storedCollections = continuation.storedCollections,
            pendingPlayers = continuation.pendingPlayers,
            startCategory = continuation.categoryIndex + 1,
            picks = continuation.picks + response.selectedCards,
            sourceId = continuation.sourceId
        )

        if (result.isPaused) {
            return ExecutionResult.paused(result.state, result.pendingDecision!!, result.events)
        }

        // Republish the pipeline's collections alongside the picks so the consumer frame sees both
        // the original pool and the kept set.
        val merged = continuation.storedCollections + result.updatedCollections
        return checkForMore(exposeCollectionsToNextFrame(result.state, merged), result.events)
    }

    /**
     * Resume after the chooser picked one of two pre-existing piles via
     * [com.wingedsheep.sdk.scripting.effects.ChoosePileEffect]. Routes pile A
     * or pile B (per [OptionChosenResponse.optionIndex]) to [storeChosenAs],
     * and the other to [storeOtherAs], on the next [EffectContinuation].
     */
    fun resumeChoosePile(
        state: GameState,
        continuation: ChoosePileContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OptionChosenResponse) {
            return ExecutionResult.error(state, "Expected option choice response for ChoosePile")
        }
        val (chosen, other) = when (response.optionIndex) {
            0 -> continuation.pileAIds to continuation.pileBIds
            1 -> continuation.pileBIds to continuation.pileAIds
            else -> return ExecutionResult.error(
                state,
                "Invalid pile index for ChoosePile: ${response.optionIndex}"
            )
        }

        val updatedCollections = continuation.storedCollections.toMutableMap()
        updatedCollections[continuation.storeChosenAs] = chosen
        updatedCollections[continuation.storeOtherAs] = other

        val newState = exposeCollectionsToNextFrame(state, updatedCollections)

        return checkForMore(newState, emptyList())
    }

    /**
     * Resume after a player selected a target during a pipeline effect (SelectTargetEffect).
     *
     * Extracts the selected target IDs from the [TargetsResponse], stores them under
     * [SelectTargetPipelineContinuation.storeAs], and injects the updated collections
     * into the next [EffectContinuation] on the stack.
     */
    fun resumeSelectTargetPipeline(
        state: GameState,
        continuation: SelectTargetPipelineContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is TargetsResponse) {
            return ExecutionResult.error(state, "Expected targets response for SelectTargetPipeline")
        }

        val selectedTargetIds = response.selectedTargets[0] ?: emptyList()

        // Build the updated collections
        val updatedCollections = continuation.storedCollections.toMutableMap()
        updatedCollections[continuation.storeAs] = selectedTargetIds

        // Inject updated collections into the consumer frame beneath (if any)
        val newState = exposeCollectionsToNextFrame(state, updatedCollections)

        return checkForMore(newState, emptyList())
    }

    /**
     * Resume after a card's owner chose top or bottom of their library.
     * Moves the card to the chosen position through ZoneTransitionService.  A spell on
     * the stack uses the same replacement-aware path as any other card: this matters
     * for CR 903.9b, which can replace a commander spell's stack-to-library move with
     * a move to the command zone before the spell leaves the stack.
     */
    fun resumePutOnTopOrBottom(
        state: GameState,
        continuation: PutOnTopOrBottomContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OptionChosenResponse) {
            return ExecutionResult.error(state, "Expected option choice response for top/bottom of library")
        }

        if (response.optionIndex !in continuation.options.indices) {
            return ExecutionResult.error(state, "Invalid option index: ${response.optionIndex}")
        }

        val chosenPosition = continuation.positions.getOrNull(response.optionIndex)
            ?: run {
                // Backwards-compatible fallback: continuations serialised before
                // `positions` was added carry only option strings.
                when (continuation.options[response.optionIndex]) {
                    "Top of library" -> com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.Top
                    "Second from top of library" -> com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.SecondFromTop
                    "Bottom of library" -> com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.Bottom
                    else -> return ExecutionResult.error(state, "Unknown library position option")
                }
            }

        val placement = when (chosenPosition) {
            com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.Top ->
                com.wingedsheep.engine.handlers.effects.LibraryPlacement.Top
            com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.SecondFromTop ->
                com.wingedsheep.engine.handlers.effects.LibraryPlacement.NthFromTop(1)
            com.wingedsheep.sdk.scripting.effects.LibraryChoicePosition.Bottom ->
                com.wingedsheep.engine.handlers.effects.LibraryPlacement.Bottom
        }

        val cardId = continuation.cardId

        // Case 1: target is a spell on the stack.  Do not hand-roll stack removal here:
        // ZoneTransitionService knows that STACK is stored separately, strips the stack
        // components, and (critically) exposes the pre-move replacement event.
        if (cardId in state.stack) {
            return movePutOnTopOrBottomThroughReplacement(
                state, cardId, continuation.ownerId, placement,
                ZoneKey(continuation.ownerId, Zone.STACK), checkForMore
            )
        }

        // Case 2: target is in a zone (battlefield or elsewhere) — use ZoneTransitionService.
        val currentZone = state.zones.entries.firstOrNull { (_, entities) -> cardId in entities }?.key
            ?: return checkForMore(state, emptyList()) // Card no longer exists in any zone

        return movePutOnTopOrBottomThroughReplacement(
            state, cardId, continuation.ownerId, placement, currentZone, checkForMore
        )
    }

    /**
     * Performs the chosen library move and parks its public-information tail below a
     * possible Commander/replacement decision.  This is shared by a permanent and a
     * spell on the stack so both paths get exactly one ZoneChangeEvent and exactly one
     * physical movement.
     */
    private fun movePutOnTopOrBottomThroughReplacement(
        state: GameState,
        cardId: EntityId,
        ownerId: EntityId,
        placement: com.wingedsheep.engine.handlers.effects.LibraryPlacement,
        currentZone: ZoneKey,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val transitionResult = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
            state, cardId, Zone.LIBRARY,
            com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(
                controllerId = ownerId,
                libraryPlacement = placement
            ),
            currentZone
        )
        if (!transitionResult.isPaused) {
            // The card was visible to everyone before the move (battlefield or stack) and the owner's
            // choice of position was public, so all players know where it ended up. Mark it revealed
            // to every player so each library viewer shows the card face-up at its new slot.
            val finalState = com.wingedsheep.engine.handlers.effects.library.LibraryRevealUtils
                .markRevealed(transitionResult.state, listOf(cardId), transitionResult.state.turnOrder.toSet())
            return checkForMore(finalState, transitionResult.events)
        }

        // The original position decision has already been popped.  Keep its post-move work below
        // the replacement choice so the replacement resolves and performs exactly one movement
        // before we reveal the card (only if it really entered the library).
        val (replacementFrame, stateWithoutReplacementFrame) = transitionResult.state.popContinuation()
        val stateWithRemainder = stateWithoutReplacementFrame
            .pushContinuation(RevealLibraryMoveAfterReplacementContinuation(cardId = cardId, ownerId = ownerId))
            .let { withRemainder -> replacementFrame?.let(withRemainder::pushContinuation) ?: withRemainder }
        return ExecutionResult.paused(stateWithRemainder, transitionResult.pendingDecision!!)
    }

    /**
     * Resume cascade resolution (CR 702.85a) after the controller answers
     * "cast this card without paying its mana cost?".
     *
     * On **No** every exiled card — including the would-be cascade card — is
     * shuffled onto the bottom of the controller's library.
     *
     * On **Yes** the other exiled cards (the lands and any other non-hit cards
     * skipped past during the walk) are bottomed first. The cascade card is
     * granted [MayPlayPermission] + [PlayWithoutPayingCostComponent] so the
     * synthesized cast resolves to a free cast, then [CastSpellHandler] is
     * invoked directly to put the spell on the stack. If the cast pauses for
     * targets / X / modes, that pause is bubbled up unchanged — the leftover
     * bottoming has already happened, so the cascade resolution is effectively
     * complete. If the cast errors (no legal targets, etc.) the cascade card
     * is bottomed too, since it ultimately wasn't cast.
     */
    fun resumeCascadeMayCast(
        state: GameState,
        continuation: CascadeMayCastContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is YesNoResponse) {
            return ExecutionResult.error(state, "Expected yes/no response for cascade may-cast")
        }

        if (!response.choice) {
            val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
                state, continuation.playerId, continuation.exiledCards
            )
            return if (bottom.isPaused) {
                ExecutionResult.paused(bottom.state, bottom.pendingDecision!!, bottom.events)
            } else {
                checkForMore(bottom.state, bottom.events)
            }
        }

        val others = continuation.exiledCards.filter { it != continuation.cascadeCardId }
        val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
            state,
            continuation.playerId,
            others,
            CascadeMayCastAfterBottomContinuation(cascade = continuation)
        )
        return if (bottom.isPaused) {
            ExecutionResult.paused(bottom.state, bottom.pendingDecision!!, bottom.events)
        } else {
            resumeCascadeAfterBottom(bottom.state, continuation, bottom.events, checkForMore)
        }
    }

    /** The accepted Cascade branch, entered only after every non-hit card is bottomed. */
    private fun resumeCascadeAfterBottom(
        afterBottom: GameState,
        continuation: CascadeMayCastContinuation,
        bottomEvents: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {

        // A non-modal targeted spell can't carry targets through the synthesized CastSpell —
        // surface the ChooseTargetsDecision first, exactly as
        // CastFromCollectionWithoutPayingCostExecutor does. If a required slot has no legal
        // targets the cast can't initiate (CR 601.2c) and the cascade card is bottomed. Checked
        // *before* granting so the bottomed card carries no lingering free-cast grant.
        val targetPrep = CastFromCollectionWithoutPayingCostExecutor.prepareTargetSelection(
            state = afterBottom,
            cardId = continuation.cascadeCardId,
            casterId = continuation.playerId,
            cardRegistry = services.cardRegistry,
            targetFinder = targetFinder,
        )
        if (targetPrep is CastFromCollectionWithoutPayingCostExecutor.TargetPrep.NoLegalTargets) {
            val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
                afterBottom, continuation.playerId, listOf(continuation.cascadeCardId)
            )
            return if (bottom.isPaused) {
                ExecutionResult.paused(bottom.state, bottom.pendingDecision!!, bottomEvents + bottom.events)
            } else {
                checkForMore(bottom.state, bottomEvents + bottom.events)
            }
        }

        // Grant free-cast permission so the synthesized cast pays nothing.
        val (permId, stateWithGrant) = CastFromCollectionWithoutPayingCostExecutor.grantFreeCast(
            state = afterBottom,
            cardId = continuation.cascadeCardId,
            controllerId = continuation.playerId,
            sourceId = continuation.sourceId,
        )

        if (targetPrep is CastFromCollectionWithoutPayingCostExecutor.TargetPrep.NeedsTargets) {
            val targetsContinuation = targetPrep.continuation.copy(
                grantedPermissionId = permId,
                onCastFailure = FreeCastFallback.BOTTOM_OF_LIBRARY,
            )
            val pausedState = stateWithGrant
                .pushContinuation(targetsContinuation)
                .withPendingDecision(targetPrep.decision)
                .withPriority(continuation.playerId)
            return ExecutionResult.paused(pausedState, targetPrep.decision, bottomEvents + targetPrep.event)
        }

        // Hand priority to the cascade controller for the synthesized cast. The cast
        // happens *during* cascade resolution (CR 702.85a) rather than on a normal
        // priority window, so we override the priorityPlayerId for this single call.
        val stateForCast = stateWithGrant.copy(priorityPlayerId = continuation.playerId)
        val castAction = CastSpell(continuation.playerId, continuation.cascadeCardId)
        val castResult = castSpellHandler.execute(stateForCast, castAction)

        if (castResult.error != null) {
            // Cast couldn't initiate (no legal targets, etc.) — revoke the unused free-cast
            // grant; the cascade card wasn't cast, so it joins the leftovers on the bottom
            // of the library.
            val revoked = CastFromCollectionWithoutPayingCostExecutor.revokeFreeCast(
                stateWithGrant, continuation.cascadeCardId, permId
            )
            val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
                revoked, continuation.playerId, listOf(continuation.cascadeCardId)
            )
            return if (bottom.isPaused) {
                ExecutionResult.paused(bottom.state, bottom.pendingDecision!!, bottomEvents + bottom.events)
            } else {
                checkForMore(bottom.state, bottomEvents + bottom.events)
            }
        }

        // CastSpellHandler already detected + stacked this cast's triggers; propagate the flag
        // so SubmitDecisionHandler doesn't re-scan the SpellCastEvent and double-fire them.
        if (castResult.pendingDecision != null) {
            // The cast paused (for target / X / mode selection). The leftover
            // bottoming is already done; let the cast's own continuations finish
            // the cast on resume.
            return ExecutionResult.paused(
                castResult.state,
                castResult.pendingDecision,
                bottomEvents + castResult.events
            ).copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
        }

        return checkForMore(castResult.state, bottomEvents + castResult.events)
            .copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
    }

    /**
     * Resume after the controller answers "cast the discovered card for free, or put it into
     * your hand?" during a [com.wingedsheep.sdk.scripting.effects.DiscoverEffect] (CR 701.57a).
     *
     * In both branches the *other* exiled cards are bottom-randomized first. Then:
     *  - **Cast** (yes): the discovered card is granted a free cast (like [CascadeExecutor]) and
     *    synthesized through the normal cast machinery, so target / X / mode prompts surface and the
     *    cast's "whenever you cast a spell (from exile)" triggers are stacked exactly once (the
     *    `triggersAlreadyProcessed` flag is propagated so they aren't re-scanned). If the cast can't
     *    initiate — no legal target, etc. — the card falls back to the controller's hand, per
     *    "If you don't cast it, put that card into your hand."
     *  - **Hand** (no): the discovered card is moved straight to the controller's hand.
     *
     * Any [DiscoverMayCastContinuation.thenEffect] then resolves last, with the discovered card
     * published to [DiscoverMayCastContinuation.storeDiscoveredAs] so it can be read (Hit the
     * Mother Lode's "…create Treasure tokens equal to the difference"). In the cast branch it is
     * pre-pushed as an [EffectContinuation] so it runs after the cast even if the cast pauses.
     */
    fun resumeDiscoverMayCast(
        state: GameState,
        continuation: DiscoverMayCastContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is YesNoResponse) {
            return ExecutionResult.error(state, "Expected yes/no response for discover may-cast")
        }

        val discovered = continuation.discoveredCardId
        val others = continuation.exiledCards.filter { it != discovered }

        // Bottom-randomize every other exiled card first (CR 701.57a).
        val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
            state,
            continuation.playerId,
            others,
            DiscoverMayCastAfterBottomContinuation(discover = continuation, castForFree = response.choice)
        )
        return if (bottom.isPaused) {
            ExecutionResult.paused(bottom.state, bottom.pendingDecision!!, bottom.events)
        } else {
            resumeDiscoverAfterBottom(bottom.state, continuation, response.choice, bottom.events, checkForMore)
        }
    }

    /** The chosen Discover branch, entered only after every non-hit card is bottomed. */
    private fun resumeDiscoverAfterBottom(
        afterBottom: GameState,
        continuation: DiscoverMayCastContinuation,
        castForFree: Boolean,
        bottomEvents: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val discovered = continuation.discoveredCardId

        val discoveredCollections = continuation.storeDiscoveredAs
            ?.let { mapOf(it to listOf(discovered)) }
            ?: emptyMap()

        if (!castForFree) {
            // The discovered card is currently in exile.  A commander moving
            // from there to hand offers its owner the pre-move replacement;
            // retain Discover's tail beneath that nested decision.
            return moveDiscoveredToHandAfterReplacement(
                afterBottom, continuation, discoveredCollections, bottomEvents, checkForMore
            )
        }

        // A non-modal targeted spell (Zombify) can't carry targets through the synthesized
        // CastSpell — surface the ChooseTargetsDecision first, exactly as
        // CastFromCollectionWithoutPayingCostExecutor does. If a required slot has no legal
        // targets the cast can't initiate (CR 601.2c) and the card goes to hand instead. Checked
        // *before* granting so the card reaches hand without a lingering free-cast grant.
        val targetPrep = CastFromCollectionWithoutPayingCostExecutor.prepareTargetSelection(
            state = afterBottom,
            cardId = discovered,
            casterId = continuation.playerId,
            cardRegistry = services.cardRegistry,
            targetFinder = targetFinder,
        )
        if (targetPrep is CastFromCollectionWithoutPayingCostExecutor.TargetPrep.NoLegalTargets) {
            return moveDiscoveredToHandAfterReplacement(
                afterBottom, continuation, discoveredCollections, bottomEvents, checkForMore
            )
        }

        // Cast branch: grant a free cast and synthesize it through the normal cast machinery —
        // mirroring CascadeExecutor's may-cast rather than the CastFromCollection effect, so the
        // cast's "whenever you cast a spell (from exile)" triggers are stacked exactly once
        // (Quintorius Kand).
        val (permId, granted) = CastFromCollectionWithoutPayingCostExecutor.grantFreeCast(
            state = afterBottom,
            cardId = discovered,
            controllerId = continuation.playerId,
            sourceId = continuation.sourceId,
        )

        // The follow-up [thenEffect] is pre-pushed as an EffectContinuation so it resolves after
        // the cast even if the cast pauses for targets / X.
        var stateForCast = granted
        if (continuation.thenEffect != null) {
            val thenCtx = EffectContext(
                sourceId = continuation.sourceId,
                controllerId = continuation.playerId,
                pipeline = PipelineState.EMPTY.copy(storedCollections = discoveredCollections)
            )
            stateForCast = stateForCast.pushContinuation(
                EffectContinuation(
                    decisionId = "pending",
                    remainingEffects = listOf(continuation.thenEffect),
                    effectContext = thenCtx
                )
            )
        }

        if (targetPrep is CastFromCollectionWithoutPayingCostExecutor.TargetPrep.NeedsTargets) {
            val targetsContinuation = targetPrep.continuation.copy(
                grantedPermissionId = permId,
                onCastFailure = FreeCastFallback.HAND,
            )
            val pausedState = stateForCast
                .pushContinuation(targetsContinuation)
                .withPendingDecision(targetPrep.decision)
                .withPriority(continuation.playerId)
            return ExecutionResult.paused(pausedState, targetPrep.decision, bottomEvents + targetPrep.event)
        }

        val stateReady = stateForCast.copy(priorityPlayerId = continuation.playerId)
        val castResult = castSpellHandler.execute(stateReady, CastSpell(continuation.playerId, discovered))

        if (castResult.error != null) {
            // The cast couldn't initiate — pop the pre-pushed follow-up, revoke the unused
            // free-cast grant, put the discovered card into hand ("If you don't cast it, put
            // that card into your hand"), then run the follow-up (Hit the Mother Lode still
            // makes its Treasures — a card was discovered).
            val withoutThen = CastFromCollectionWithoutPayingCostExecutor.revokeFreeCast(
                if (continuation.thenEffect != null) stateForCast.popContinuation().second else stateForCast,
                discovered,
                permId,
            )
            return moveDiscoveredToHandAfterReplacement(
                withoutThen,
                continuation,
                discoveredCollections,
                bottomEvents,
                checkForMore
            )
        }

        if (castResult.pendingDecision != null) {
            // The cast paused (targets / X); the pre-pushed follow-up runs when it resumes.
            return ExecutionResult.paused(castResult.state, castResult.pendingDecision, bottomEvents + castResult.events)
                .copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
        }

        // Cast succeeded synchronously; checkForMore drains the pre-pushed follow-up continuation
        // (the card's thenEffect plus the DiscoveredEvent emit tail). CastSpellHandler already
        // stacked this cast's triggers (e.g. Quintorius Kand's "whenever you cast a spell from
        // exile"); propagate the flag so SubmitDecisionHandler doesn't re-scan the SpellCastEvent and
        // double-fire them. But that flag also suppresses scanning of the DiscoveredEvent the tail
        // emits (CR 701.57b) — a genuinely new event CastSpellHandler never saw — so scan its
        // "whenever you discover" triggers here.
        return scanDiscoveredEventTriggers(
            checkForMore(castResult.state, bottomEvents + castResult.events)
                .copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
        )
    }

    /**
     * Detect and process "whenever you discover" triggers (CR 701.57 — Curator of Sun's Creation)
     * from any [DiscoveredEvent] in [result]'s events. Used only on the discover **cast-for-free**
     * branch, which returns `triggersAlreadyProcessed = true` to protect the discovered card's own
     * `SpellCastEvent` from a re-scan — a flag that would otherwise also suppress the DiscoveredEvent
     * emitted by the discover tail. Detecting it here keeps the SpellCastEvent protected while still
     * firing discover watchers. No-op when the result paused (the emit tail hasn't run yet) or has no
     * DiscoveredEvent.
     */
    private fun scanDiscoveredEventTriggers(result: ExecutionResult): ExecutionResult {
        if (!result.isSuccess || result.isPaused) return result
        val discoveredEvents = result.events.filterIsInstance<com.wingedsheep.engine.core.DiscoveredEvent>()
        if (discoveredEvents.isEmpty()) return result
        val triggers = services.triggerDetector.detectTriggers(result.state, discoveredEvents)
        if (triggers.isEmpty()) return result
        val processed = services.triggerProcessor.processTriggers(result.state, triggers)
        val events = result.events + processed.events
        return if (processed.isPaused) {
            ExecutionResult.paused(processed.state, processed.pendingDecision!!, events)
                .copy(triggersAlreadyProcessed = true)
        } else {
            ExecutionResult.success(processed.newState, events)
                .copy(triggersAlreadyProcessed = true)
        }
    }

    /** Run a discover [DiscoverMayCastContinuation.thenEffect] (if any) with the discovered card published. */
    private fun runDiscoverThenEffect(
        state: GameState,
        continuation: DiscoverMayCastContinuation,
        discoveredCollections: Map<String, List<EntityId>>,
        leadingEvents: List<com.wingedsheep.engine.core.GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val thenEffect = continuation.thenEffect
            ?: return checkForMore(state, leadingEvents)
        val ctx = EffectContext(
            sourceId = continuation.sourceId,
            controllerId = continuation.playerId,
            pipeline = PipelineState.EMPTY.copy(storedCollections = discoveredCollections)
        )
        val result = effectRunner.executeRemainingEffects(state, listOf(thenEffect), ctx)
        if (result.isPaused) {
            return ExecutionResult.paused(result.state, result.pendingDecision!!, leadingEvents + result.events)
        }
        return checkForMore(result.state, leadingEvents + result.events)
    }

    /**
     * Move Discover's declined (or uncastable) card to hand through the
     * generic zone-change replacement chain.  If the chain pauses, the
     * original Discover continuation has already been popped, so park its
     * follow-up beneath the replacement frame.  The perform frame then makes
     * exactly one physical move before this auto-resumer runs the tail.
     */
    private fun moveDiscoveredToHandAfterReplacement(
        state: GameState,
        continuation: DiscoverMayCastContinuation,
        discoveredCollections: Map<String, List<EntityId>>,
        leadingEvents: List<GameEvent>,
        checkForMore: CheckForMore
    ): ExecutionResult {
        val move = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
            state = state,
            entityId = continuation.discoveredCardId,
            destinationZone = Zone.HAND,
            com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(controllerId = continuation.playerId),
            ZoneKey(continuation.playerId, Zone.EXILE)
        )
        if (!move.isPaused) {
            return runDiscoverThenEffect(
                move.state, continuation, discoveredCollections, leadingEvents + move.events, checkForMore
            )
        }

        // `attemptMoveToZone` has pushed the replacement decision.  Insert
        // Discover's tail directly below it, preserving LIFO resumption.
        val (replacementFrame, withoutReplacementFrame) = move.state.popContinuation()
        val withRemainder = withoutReplacementFrame
            .pushContinuation(
                DiscoverHandMoveAfterReplacementContinuation(
                    discover = continuation,
                    discoveredCollections = discoveredCollections
                )
            )
            .let { belowReplacement -> replacementFrame?.let(belowReplacement::pushContinuation) ?: belowReplacement }
        return ExecutionResult.paused(
            withRemainder,
            move.pendingDecision!!,
            leadingEvents + move.events
        )
    }


    /**
     * Resume after the controller picks targets for a free synthesized cast triggered by
     * [com.wingedsheep.sdk.scripting.effects.CastFromCollectionWithoutPayingCostEffect].
     *
     * Flattens the per-requirement target picks into a `List<ChosenTarget>` (via
     * [entityIdToChosenTarget]), invokes the normal cast pipeline, and bubbles any further
     * pause (X selection, modal-target prompts on a card that turned out to be modal, etc.)
     * through unchanged.
     */
    fun resumeCastFromCollectionTargets(
        state: GameState,
        continuation: CastFromCollectionTargetsContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is TargetsResponse) {
            return ExecutionResult.error(
                state,
                "Expected targets response for free-cast target selection"
            )
        }

        val chosenTargets = response.selectedTargets.entries
            .sortedBy { it.key }
            .flatMap { (_, ids) -> ids.map { entityIdToChosenTarget(state, it) } }

        val stateForCast = state.copy(priorityPlayerId = continuation.casterId)
        val castResult = castSpellHandler.execute(
            stateForCast,
            CastSpell(continuation.casterId, continuation.cardId, chosenTargets),
        )

        if (castResult.error != null) {
            // Cast still couldn't initiate (e.g., targets became illegal between selection
            // and resolution). Revoke the unused free-cast grant and send the card to the
            // owning flow's fallback zone (discover → hand, cascade → bottom of library) so
            // it isn't stranded in exile; checkForMore keeps the rest of the trigger's
            // resolution (e.g. a discover follow-up frame) alive.
            var cleaned = CastFromCollectionWithoutPayingCostExecutor.revokeFreeCast(
                state, continuation.cardId, continuation.grantedPermissionId
            )
            val fallbackEvents = mutableListOf<GameEvent>()
            when (continuation.onCastFailure) {
                FreeCastFallback.LEAVE -> {}
                FreeCastFallback.HAND -> {
                    // A targeted free cast can still fail after its target decision (for
                    // example if validation rejects the submitted target).  Discover's
                    // "if you don't cast it" fallback is an exile-to-hand zone change, so a
                    // commander must get its CR 903.9b pre-move choice here too.  This
                    // continuation has already been popped and has no executor-local tail:
                    // leaving the generic replacement frames on top preserves every parent
                    // frame below them and lets checkForMore run exactly once after the move.
                    // The caster can be playing an opponent's exiled card.  Card zones are
                    // keyed by owner, not controller, so retain that distinction for both the
                    // source lookup and the Commander choice's affected player.
                    val ownerId = cleaned.getEntity(continuation.cardId)
                        ?.get<CardComponent>()
                        ?.ownerId
                        ?: continuation.casterId
                    val move = com.wingedsheep.engine.handlers.effects.ZoneTransitionService.attemptMoveToZone(
                        state = cleaned,
                        entityId = continuation.cardId,
                        destinationZone = Zone.HAND,
                        options = com.wingedsheep.engine.handlers.effects.ZoneEntryOptions(
                            controllerId = continuation.casterId
                        ),
                        fromZoneKey = ZoneKey(ownerId, Zone.EXILE)
                    )
                    if (move.isPaused) {
                        return ExecutionResult.paused(
                            move.state,
                            move.pendingDecision!!,
                            fallbackEvents + move.events
                        )
                    }
                    cleaned = move.state
                    fallbackEvents.addAll(move.events)
                }
                FreeCastFallback.BOTTOM_OF_LIBRARY -> {
                    val bottom = CascadeExecutor.bottomRandomizeReplacementAware(
                        cleaned, continuation.casterId, listOf(continuation.cardId)
                    )
                    if (bottom.isPaused) {
                        return ExecutionResult.paused(
                            bottom.state,
                            bottom.pendingDecision!!,
                            fallbackEvents + bottom.events
                        )
                    }
                    cleaned = bottom.state
                    fallbackEvents.addAll(bottom.events)
                }
            }
            return checkForMore(cleaned, fallbackEvents)
        }

        // The cast initiated. Publish the cast card so an enclosing IfYouDoEffect frame beneath
        // (Kaervek's "If you do, you lose 2 life") sees a non-empty collection.
        val castCollections = continuation.storeCastTo?.let { mapOf(it to listOf(continuation.cardId)) }
            ?: emptyMap()

        // CastSpellHandler already detected + stacked this cast's triggers (e.g. Quintorius Kand's
        // "whenever you cast a spell from exile"); propagate the flag so SubmitDecisionHandler
        // doesn't re-scan the SpellCastEvent and double-fire them.
        if (castResult.pendingDecision != null) {
            val exposed = exposeCollectionsToNextFrame(castResult.state, castCollections)
            return ExecutionResult.paused(
                exposed,
                castResult.pendingDecision,
                castResult.events,
            ).copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
        }

        val exposed = exposeCollectionsToNextFrame(castResult.state, castCollections)
        // Shared by every free-cast-with-targets flow (cascade, discover, suspend, …). When a
        // *discovered* targeted spell is cast for free, the discover tail's DiscoveredEvent rides
        // this batch under triggersAlreadyProcessed = true and would be suppressed — scan it (no-op
        // for the non-discover callers, which emit no DiscoveredEvent).
        return scanDiscoveredEventTriggers(
            checkForMore(exposed, castResult.events)
                .copy(triggersAlreadyProcessed = castResult.triggersAlreadyProcessed)
        )
    }

    /**
     * Resume a [CastAnyNumberFromCollectionContinuation] — one iteration of the
     * "cast any number of them for free" loop.
     *
     * The controller picked 0..1 cards from the still-castable set:
     *  - **0** → done; uncast cards stay in exile (no later-in-turn permission was granted).
     *  - **1** → cast it for free, then loop over the rest. Both steps run through
     *    [effectRunner]: it casts the single chosen card via
     *    [CastFromCollectionWithoutPayingCostEffect] (which handles target / X / mode pauses
     *    exactly as Cascade and Shiko do — and, going through `CastSpellHandler.execute`
     *    directly, ignores card-type timing) and then re-runs
     *    [CastAnyNumberFromCollectionWithoutPayingCostEffect] over the remaining cards. The
     *    runner's per-effect `EffectContinuation` makes a paused cast auto-resume into the
     *    next loop iteration.
     *
     * The chosen card is keyed under a private collection name and the loop collection is
     * trimmed to the remainder, so each iteration's bookkeeping is self-contained.
     */
    fun resumeCastAnyNumberFromCollection(
        state: GameState,
        continuation: CastAnyNumberFromCollectionContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is CardsSelectedResponse) {
            return ExecutionResult.error(state, "Expected card selection response for free-cast loop")
        }

        val ctx = continuation.effectContext
        val collection = ctx.pipeline.storedCollections[continuation.from].orEmpty()
        val chosenId = response.selectedCards.firstOrNull()

        // Declined, or a stale / no-longer-offered pick: end the loop. Uncast cards remain
        // wherever they are.
        if (chosenId == null || chosenId !in collection) {
            return checkForMore(state, emptyList())
        }

        val singleKey = "${continuation.from}\$next"
        val remaining = collection - chosenId
        val loopContext = ctx.copy(
            pipeline = ctx.pipeline.copy(
                storedCollections = ctx.pipeline.storedCollections +
                    (singleKey to listOf(chosenId)) +
                    (continuation.from to remaining)
            )
        )

        val effects = listOf(
            CastFromCollectionWithoutPayingCostEffect(from = singleKey, payManaCost = continuation.payManaCost),
            CastAnyNumberFromCollectionWithoutPayingCostEffect(
                from = continuation.from,
                payManaCost = continuation.payManaCost,
            ),
        )
        val result = effectRunner.executeRemainingEffects(state, effects, loopContext)
        if (result.isPaused) return result.toExecutionResult()
        return checkForMore(result.state, result.events.toList())
    }
}
