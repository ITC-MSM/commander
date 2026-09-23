package com.wingedsheep.engine.mechanics.combat

import com.wingedsheep.engine.mechanics.battle.Battles
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BeingAttackedComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * The permanent side of an attack: a planeswalker or battle being attacked (CR 506.3).
 *
 * Every path that makes a creature attack a permanent — declaring attackers, ninjutsu, tokens and
 * cards put onto the battlefield attacking — stamps the attacked permanent through [markAttacked],
 * so CR 506.4 can later tell whether its controller or protector changed.
 */
object AttackedPermanents {

    /**
     * Records that [defenderId] is being attacked, if it is a permanent (a player needs nothing).
     * The first attack in a combat fixes the snapshot; later attackers leave it alone.
     */
    fun markAttacked(state: GameState, defenderId: EntityId): GameState {
        if (defenderId in state.turnOrder) return state
        val container = state.getEntity(defenderId) ?: return state
        if (container.has<BeingAttackedComponent>()) return state
        val controller = state.projectedState.getController(defenderId) ?: return state
        return state.updateEntity(defenderId) {
            it.with(BeingAttackedComponent(controller, Battles.protectorOf(state, defenderId)))
        }
    }

    /**
     * True if [attacking] still has something to deal combat damage to: a player, or a planeswalker
     * or battle that is on the battlefield and hasn't been removed from combat (CR 506.4c, 510.1b).
     */
    fun hasLiveTarget(state: GameState, attacking: AttackingComponent): Boolean {
        if (attacking.attackTargetRemoved) return false
        val defenderId = attacking.defenderId
        return defenderId in state.turnOrder || defenderId in state.getBattlefield()
    }
}
