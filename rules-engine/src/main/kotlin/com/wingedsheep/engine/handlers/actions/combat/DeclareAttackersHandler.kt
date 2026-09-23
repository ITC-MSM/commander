package com.wingedsheep.engine.handlers.actions.combat

import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.state.components.combat.AttackersDeclaredThisCombatComponent
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.handlers.actions.ActionHandler
import com.wingedsheep.engine.mechanics.combat.CombatManager
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Step
import kotlin.reflect.KClass

/**
 * Handler for the DeclareAttackers action.
 *
 * Delegates to CombatManager for the actual attack declaration. Attack triggers are put on the
 * stack by the settle boundary.
 */
class DeclareAttackersHandler(
    private val combatManager: CombatManager
) : ActionHandler<DeclareAttackers> {
    override val actionType: KClass<DeclareAttackers> = DeclareAttackers::class

    override fun validate(state: GameState, action: DeclareAttackers): String? {
        // CR 805.10a/b — the whole active team is the attacking team, so either teammate may make
        // the team's one combined attack declaration.
        if (!state.isActiveTurnFor(action.playerId)) {
            return "You can only declare attackers on your turn"
        }
        if (state.step != Step.DECLARE_ATTACKERS) {
            return "You can only declare attackers during the declare attackers step"
        }
        // One declaration per combat (CR 508.1; in a shared team turn the team's one combined
        // attack, CR 805.10b). The marker is stamped on every attacking player, so a teammate
        // can't append a second wave after the first head has declared.
        if (state.getEntity(action.playerId)?.has<AttackersDeclaredThisCombatComponent>() == true) {
            return "Attackers have already been declared this combat"
        }
        // Additional validation is done by CombatManager
        return null
    }

    override fun execute(state: GameState, action: DeclareAttackers): ExecutionResult =
        // Attack triggers, and the abilities that trigger on them (CR 603.3b), are the settle
        // boundary's job.
        combatManager.declareAttackers(state, action.playerId, action.attackers, action.bands)

    companion object {
        fun create(services: EngineServices): DeclareAttackersHandler {
            return DeclareAttackersHandler(services.combatManager)
        }
    }
}
