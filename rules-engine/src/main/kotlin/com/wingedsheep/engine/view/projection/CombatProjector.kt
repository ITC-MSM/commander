package com.wingedsheep.engine.view.projection

import com.wingedsheep.engine.mechanics.combat.CombatDefenders
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BlockedComponent
import com.wingedsheep.engine.state.components.combat.BlockingComponent
import com.wingedsheep.engine.state.components.combat.DamageAssignmentComponent
import com.wingedsheep.engine.state.components.combat.DamageAssignmentOrderComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.view.ClientAttacker
import com.wingedsheep.engine.view.ClientBlocker
import com.wingedsheep.engine.view.ClientCombatState
import com.wingedsheep.engine.view.ClientCombatTarget
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.model.EntityId

/** Projects the combat in progress — attackers, blockers, damage assignment — for the client. */
internal class CombatProjector {

    /** The combat state, or null outside the combat phase or when nothing is attacking. */
    fun project(state: GameState): ClientCombatState? {
        // Check if we're in a combat step
        if (state.step.phase != Phase.COMBAT) {
            return null
        }

        // Find all creatures with "must be blocked by all" requirement
        val mustBeBlockedCreatures = findMustBeBlockedCreatures(state)

        val attackers = mutableListOf<ClientAttacker>()
        val blockers = mutableListOf<ClientBlocker>()
        var attackingPlayerId: EntityId? = null
        var defendingPlayerId: EntityId? = null

        // Find all attackers and blockers
        for (entityId in state.getBattlefield()) {
            val container = state.getEntity(entityId) ?: continue
            val cardComponent = container.get<CardComponent>() ?: continue

            val attackingComponent = container.get<AttackingComponent>()
            if (attackingComponent != null) {
                val blockedComponent = container.get<BlockedComponent>()

                // Track the attacking and defending players
                val controllerId = container.get<ControllerComponent>()?.playerId
                if (controllerId != null) {
                    attackingPlayerId = controllerId
                    // A player, not the attacked permanent: a planeswalker's controller or a
                    // battle's protector (CR 310.9d).
                    defendingPlayerId = CombatDefenders.defendingPlayerOf(state, attackingComponent.defenderId)
                }

                val damageOrderComponent = container.get<DamageAssignmentOrderComponent>()
                val damageAssignmentComponent = container.get<DamageAssignmentComponent>()

                attackers.add(
                    ClientAttacker(
                        creatureId = entityId,
                        creatureName = cardComponent.name,
                        attackingTarget = when {
                            state.turnOrder.contains(attackingComponent.defenderId) ->
                                ClientCombatTarget.Player(attackingComponent.defenderId)
                            state.projectedState.isBattle(attackingComponent.defenderId) ->
                                ClientCombatTarget.Battle(attackingComponent.defenderId)
                            else -> ClientCombatTarget.Planeswalker(attackingComponent.defenderId)
                        },
                        blockedBy = blockedComponent?.blockerIds ?: emptyList(),
                        mustBeBlockedByAll = entityId in mustBeBlockedCreatures,
                        bandId = attackingComponent.bandId,
                        damageAssignmentOrder = damageOrderComponent?.orderedBlockers,
                        damageAssignments = damageAssignmentComponent?.assignments
                    )
                )
            }

            val blockingComponent = container.get<BlockingComponent>()
            if (blockingComponent != null) {
                for (attackerId in blockingComponent.blockedAttackerIds) {
                    blockers.add(
                        ClientBlocker(
                            creatureId = entityId,
                            creatureName = cardComponent.name,
                            blockingAttacker = attackerId
                        )
                    )
                }
            }
        }

        // If no attackers, there's no combat state to show
        if (attackers.isEmpty()) {
            return null
        }

        return ClientCombatState(
            attackingPlayerId = attackingPlayerId ?: state.activePlayerId ?: return null,
            defendingPlayerId = defendingPlayerId ?: state.getOpponents(attackingPlayerId!!).firstOrNull() ?: return null,
            attackers = attackers,
            blockers = blockers
        )
    }

    /**
     * Find all creatures that have any "must be blocked" requirement from floating effects.
     * Includes both "must be blocked by all" (Lure) and "must be blocked if able" (Gaea's Protector).
     */
    private fun findMustBeBlockedCreatures(state: GameState): Set<EntityId> {
        return state.floatingEffects
            .filter {
                it.effect.modification is SerializableModification.MustBeBlockedByAll ||
                    it.effect.modification is SerializableModification.MustBeBlockedIfAble
            }
            .flatMap { it.effect.affectedEntities }
            .toSet()
    }
}
