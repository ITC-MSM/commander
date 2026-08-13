package com.wingedsheep.engine.core

import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.PlayerComponent
import com.wingedsheep.engine.state.components.stack.AbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * Structural rules that must hold at every externally-observable action boundary.
 *
 * This is intentionally not a second rules engine. It catches corruption that is
 * independent of a card's text: dangling zone references, objects in two places,
 * malformed stack entries, and invalid player routing. Callers may opt in through
 * [InvariantCheckingActionObserver]; normal production action processing remains
 * allocation-free.
 */
class GameInvariantChecker {
    fun check(state: GameState): List<InvariantViolation> = buildList {
        val seen = mutableMapOf<EntityId, String>()

        state.zones.forEach { (zoneKey, entityIds) ->
            entityIds.forEach { entityId ->
                if (!state.hasEntity(entityId)) {
                    add(InvariantViolation.DanglingZoneEntity(zoneKey.toString(), entityId))
                }
                val previous = seen.putIfAbsent(entityId, zoneKey.toString())
                if (previous != null) {
                    add(InvariantViolation.EntityInMultipleLocations(entityId, previous, zoneKey.toString()))
                }
            }
        }

        state.stack.forEach { entityId ->
            if (!state.hasEntity(entityId)) {
                add(InvariantViolation.DanglingStackEntity(entityId))
            }
            val previous = seen.putIfAbsent(entityId, "stack")
            if (previous != null) {
                add(InvariantViolation.EntityInMultipleLocations(entityId, previous, "stack"))
            }
            val entity = state.getEntity(entityId)
            if (entity != null && !entity.hasStackObject()) {
                add(InvariantViolation.StackEntityWithoutStackComponent(entityId))
            }
        }

        if (state.turnOrder.distinct().size != state.turnOrder.size) {
            add(InvariantViolation.DuplicateTurnOrderPlayer)
        }
        state.turnOrder.forEach { playerId ->
            if (state.getEntity(playerId)?.has<PlayerComponent>() != true) {
                add(InvariantViolation.TurnOrderEntityIsNotPlayer(playerId))
            }
        }

        if (!state.gameOver) {
            state.activePlayerId?.let { playerId ->
                if (playerId !in state.activePlayers) add(InvariantViolation.UnknownActivePlayer(playerId))
            }
            state.priorityPlayerId?.let { playerId ->
                if (playerId !in state.activePlayers) add(InvariantViolation.UnknownPriorityPlayer(playerId))
            }
            state.priorityPassedBy.filterNot { it in state.activePlayers }.forEach { playerId ->
                add(InvariantViolation.UnknownPriorityPasser(playerId))
            }
            state.pendingDecision?.let { decision ->
                if (decision.playerId !in state.activePlayers) {
                    add(InvariantViolation.UnknownDecisionPlayer(decision.playerId))
                }
            }
        }
    }

    private fun com.wingedsheep.engine.state.ComponentContainer.hasStackObject(): Boolean =
        has<SpellOnStackComponent>() ||
            has<TriggeredAbilityOnStackComponent>() ||
            has<ActivatedAbilityOnStackComponent>() ||
            has<AbilityOnStackComponent>()
}

sealed interface InvariantViolation {
    data class DanglingZoneEntity(val zone: String, val entityId: EntityId) : InvariantViolation
    data class DanglingStackEntity(val entityId: EntityId) : InvariantViolation
    data class EntityInMultipleLocations(val entityId: EntityId, val first: String, val second: String) : InvariantViolation
    data class StackEntityWithoutStackComponent(val entityId: EntityId) : InvariantViolation
    data object DuplicateTurnOrderPlayer : InvariantViolation
    data class TurnOrderEntityIsNotPlayer(val entityId: EntityId) : InvariantViolation
    data class UnknownActivePlayer(val entityId: EntityId) : InvariantViolation
    data class UnknownPriorityPlayer(val entityId: EntityId) : InvariantViolation
    data class UnknownPriorityPasser(val entityId: EntityId) : InvariantViolation
    data class UnknownDecisionPlayer(val entityId: EntityId) : InvariantViolation
    data object RejectedActionChangedState : InvariantViolation
    data object RejectedActionEmittedEvents : InvariantViolation
}

/** Hook used by simulation, fuzz, and diagnostic callers without affecting normal games. */
fun interface ActionProcessorObserver {
    fun afterProcess(before: GameState, action: GameAction, result: ExecutionResult)
}

/** Fails fast with a useful trace when an action crosses the engine boundary in an invalid state. */
class InvariantCheckingActionObserver(
    private val checker: GameInvariantChecker = GameInvariantChecker()
) : ActionProcessorObserver {
    override fun afterProcess(before: GameState, action: GameAction, result: ExecutionResult) {
        val violations = buildList {
            addAll(checker.check(before))
            addAll(checker.check(result.state))
            if (result.error != null && result.state != before) add(InvariantViolation.RejectedActionChangedState)
            if (result.error != null && result.events.isNotEmpty()) add(InvariantViolation.RejectedActionEmittedEvents)
        }
        if (violations.isNotEmpty()) {
            throw InvariantViolationException(action::class.simpleName.orEmpty(), violations)
        }
    }
}

class InvariantViolationException(
    actionType: String,
    val violations: List<InvariantViolation>,
) : IllegalStateException("Engine invariant violation after $actionType: $violations")
