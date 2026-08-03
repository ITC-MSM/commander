package com.wingedsheep.engine.handlers.continuations

import com.wingedsheep.engine.core.DecisionResponse
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.GameEvent
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.ResolvedEvent
import com.wingedsheep.engine.core.RiotEntryOnBattlefieldContinuation
import com.wingedsheep.engine.core.RiotEntrySpellContinuation
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.handlers.effects.RiotEntry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId

/**
 * Resumes the two **riot** (CR 702.136) walks — one decision per instance, applied as it is
 * answered. The branch application and the "is there another instance" chaining live in
 * [RiotEntry]; what differs between the two continuations is only the tail once the last instance
 * resolves:
 *
 *  - [RiotEntrySpellContinuation] — the entering object is still a resolving spell, so the tail
 *    finishes the permanent's entry (place it, emit the resolution + entry events).
 *  - [RiotEntryOnBattlefieldContinuation] — the permanent is already on the battlefield, so the
 *    tail emits its withheld entry [ZoneChangeEvent] (enters-the-battlefield triggers therefore see
 *    the riot counter already on it) and walks on to the next permanent in the entry batch.
 */
class RiotContinuationResumer(
    private val services: EngineServices
) : ContinuationResumerModule {

    override fun resumers(): List<ContinuationResumer<*>> = listOf(
        resumer(RiotEntrySpellContinuation::class, ::resumeSpellRiot),
        resumer(RiotEntryOnBattlefieldContinuation::class, ::resumeOnBattlefieldRiot),
    )

    private fun resumeSpellRiot(
        state: GameState,
        continuation: RiotEntrySpellContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OptionChosenResponse) {
            return ExecutionResult.error(state, "Expected option chosen response for riot")
        }

        val resolution = RiotEntry.applyAndContinue(
            state = state,
            enteringEntityId = continuation.spellId,
            controllerId = continuation.controllerId,
            choseCounter = response.optionIndex == RiotEntry.COUNTER_OPTION_INDEX,
            remainingInstances = continuation.remainingInstances,
        ) { decisionId, remaining ->
            continuation.copy(decisionId = decisionId, remainingInstances = remaining)
        }

        resolution.decision?.let {
            return ExecutionResult.paused(resolution.state, it, resolution.events)
        }

        // Last instance resolved — finish the permanent's entry, exactly as the as-enters choice
        // resumer does for a spell.
        var newState = resolution.state
        val spellContainer = newState.getEntity(continuation.spellId)
            ?: return ExecutionResult.error(state, "Spell entity not found: ${continuation.spellId}")
        val cardComponent = spellContainer.get<CardComponent>()
            ?: return ExecutionResult.error(state, "Spell has no CardComponent")
        val spellComponent = spellContainer.get<SpellOnStackComponent>()
            ?: return ExecutionResult.error(state, "Spell has no SpellOnStackComponent")
        val cardDef = services.cardRegistry.getCard(cardComponent.cardDefinitionId)

        val (enterState, enterEvents) = services.stackResolver.enterPermanentOnBattlefield(
            newState, continuation.spellId, spellComponent, cardComponent, cardDef
        )
        newState = enterState

        val events = resolution.events + enterEvents + listOf(
            ResolvedEvent(continuation.spellId, cardComponent.name),
            ZoneChangeEvent(
                continuation.spellId,
                cardComponent.name,
                null,
                Zone.BATTLEFIELD,
                continuation.ownerId
            )
        )
        return checkForMore(newState, events)
    }

    private fun resumeOnBattlefieldRiot(
        state: GameState,
        continuation: RiotEntryOnBattlefieldContinuation,
        response: DecisionResponse,
        checkForMore: CheckForMore
    ): ExecutionResult {
        if (response !is OptionChosenResponse) {
            return ExecutionResult.error(state, "Expected option chosen response for riot")
        }

        val resolution = RiotEntry.applyAndContinue(
            state = state,
            enteringEntityId = continuation.entityId,
            controllerId = continuation.controllerId,
            choseCounter = response.optionIndex == RiotEntry.COUNTER_OPTION_INDEX,
            remainingInstances = continuation.remainingInstances,
        ) { decisionId, remaining ->
            continuation.copy(decisionId = decisionId, remainingInstances = remaining)
        }

        resolution.decision?.let {
            return ExecutionResult.paused(resolution.state, it, resolution.events)
        }

        // This permanent is done: release its withheld entry event, then walk on to the rest of
        // the batch through the same helper the direct-entry executors use.
        val events = mutableListOf<GameEvent>()
        events += resolution.events
        events += RiotEntry.entryEvent(resolution.state, continuation.entityId, continuation.fromZone)

        val batch = RiotEntry.walkDirectEntries(
            resolution.state, continuation.remainingEntities, services.cardRegistry, continuation.fromZone
        )
        events += batch.events
        batch.decision?.let { return ExecutionResult.paused(batch.state, it, events) }

        return checkForMore(batch.state, events)
    }
}
