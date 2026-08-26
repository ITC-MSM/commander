package com.wingedsheep.engine.mechanics.sba.creature

import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.mechanics.combat.CombatRemovalHelper
import com.wingedsheep.engine.mechanics.sba.SbaOrder
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.combat.BlockingComponent
import com.wingedsheep.sdk.model.EntityId

/**
 * CR 506.4 — "A permanent is removed from combat if … its controller changes …".
 *
 * Walks every creature with [AttackingComponent] or [BlockingComponent] and compares its
 * projected controller (which already reflects every Layer 2 control-changing effect, plus
 * Old Man of the Sea's post-Layer-7 power gate) to the player it should be fighting for:
 *
 * - Attackers must remain controlled by the *active team* — every player who could have
 *   declared an attacker this turn. A projected controller off that team → removed from combat.
 * - Blockers must remain controlled by the defending team. A projected controller that has
 *   joined the active team → removed from combat.
 *
 * Asked through [GameState.isActiveTurnFor], which is the team-aware form of
 * `controller == activePlayerId`: in Two-Headed Giant the attacking team declares one combined
 * attack (CR 805.10a), so both teammates' creatures are legitimately attacking even though the
 * engine names only one of them [GameState.activePlayerId] (CR 805.9). Comparing against that
 * single id swept the non-active teammate's entire attack out of combat on the next SBA pass.
 * Every format without shared team turns reduces to the plain equality, unchanged.
 *
 * Removal uses [CombatRemovalHelper] so dependent `BlockedComponent` / `BlockingComponent`
 * references stay consistent.
 */
class ControlChangedRemovesFromCombatCheck : StateBasedActionCheck {
    override val name = "506.4 Controller-Changed Combat Removal"
    override val order = SbaOrder.CONTROL_CHANGED_COMBAT

    override fun check(state: GameState): ExecutionResult {
        val projected = state.projectedState

        val toRemove = mutableListOf<EntityId>()
        for (entityId in state.getBattlefield()) {
            val container = state.getEntity(entityId) ?: continue
            val isAttacking = container.has<AttackingComponent>()
            val isBlocking = container.has<BlockingComponent>()
            if (!isAttacking && !isBlocking) continue

            val controllerId = projected.getController(entityId) ?: continue
            val mismatched = when {
                isAttacking -> !state.isActiveTurnFor(controllerId)
                isBlocking -> state.isActiveTurnFor(controllerId)
                else -> false
            }
            if (mismatched) toRemove.add(entityId)
        }

        if (toRemove.isEmpty()) return ExecutionResult.success(state)

        var newState = state
        for (entityId in toRemove) {
            newState = CombatRemovalHelper.removeFromCombat(newState, entityId)
        }
        return ExecutionResult.success(newState)
    }
}
