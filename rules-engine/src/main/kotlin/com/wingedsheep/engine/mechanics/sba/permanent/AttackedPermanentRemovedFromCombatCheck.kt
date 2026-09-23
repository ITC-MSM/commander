package com.wingedsheep.engine.mechanics.sba.permanent

import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.mechanics.battle.Battles
import com.wingedsheep.engine.mechanics.sba.SbaOrder
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BeingAttackedComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * CR 506.4 for the *attacked* side of combat: a permanent is removed from combat "if its controller
 * or protector changes, … if it's a planeswalker that's being attacked and stops being a
 * planeswalker, [or] if it's a battle that's being attacked and stops being a battle". A
 * planeswalker or battle removed from combat stops being attacked.
 *
 * The creatures attacking it are not removed (CR 506.4c): they keep attacking, may still be
 * blocked, but are attacking nothing and deal no combat damage if unblocked. That is recorded as
 * [AttackingComponent.attackTargetRemoved], which the combat damage step reads.
 *
 * Compares the live controller and protector against the snapshot [BeingAttackedComponent] took
 * when the permanent was first attacked. The attacker-side half of 506.4 is
 * [com.wingedsheep.engine.mechanics.sba.creature.ControlChangedRemovesFromCombatCheck].
 */
class AttackedPermanentRemovedFromCombatCheck : StateBasedActionCheck {
    override val name = "506.4 Attacked Permanent Combat Removal"
    override val order = SbaOrder.ATTACKED_PERMANENT_COMBAT

    override fun check(state: GameState): ExecutionResult {
        val projected = state.projectedState
        val removed = mutableListOf<EntityId>()
        for (entityId in state.getBattlefield()) {
            val snapshot = state.getEntity(entityId)?.get<BeingAttackedComponent>() ?: continue
            val controllerChanged = projected.getController(entityId) != snapshot.controllerId
            val protectorChanged = Battles.protectorOf(state, entityId) != snapshot.protectorId
            // CR 506.4e: a permanent attacked as both stays in combat while it is still either one.
            val stillAttackable = projected.isPlaneswalker(entityId) || projected.isBattle(entityId)
            if (controllerChanged || protectorChanged || !stillAttackable) removed += entityId
        }
        if (removed.isEmpty()) return ExecutionResult.success(state)

        var newState = state
        for (entityId in removed) {
            newState = newState.updateEntity(entityId) { it.without<BeingAttackedComponent>() }
        }
        for ((attackerId, attacking) in newState.findEntitiesWith<AttackingComponent>()) {
            if (attacking.defenderId !in removed || attacking.attackTargetRemoved) continue
            newState = newState.updateEntity(attackerId) { it.with(attacking.copy(attackTargetRemoved = true)) }
        }
        return ExecutionResult.success(newState)
    }
}
