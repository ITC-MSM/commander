package com.wingedsheep.engine.handlers.actions.combat

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.event.TriggerDetector
import com.wingedsheep.engine.event.TriggerProcessor
import com.wingedsheep.engine.handlers.actions.ActionHandler
import com.wingedsheep.engine.mechanics.combat.CombatDefenders
import com.wingedsheep.engine.mechanics.combat.CombatManager
import com.wingedsheep.engine.mechanics.StateBasedActionChecker
import com.wingedsheep.engine.event.StateTriggerPoller
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.Step
import kotlin.reflect.KClass

/**
 * Handler for the DeclareBlockers action.
 *
 * Delegates to CombatManager for the actual block declaration. In multiplayer,
 * block triggers wait until every defender has completed their declaration.
 */
class DeclareBlockersHandler(
    private val combatManager: CombatManager,
    private val triggerDetector: TriggerDetector,
    private val triggerProcessor: TriggerProcessor,
    private val sbaChecker: StateBasedActionChecker,
    private val stateTriggerPoller: StateTriggerPoller,
) : ActionHandler<DeclareBlockers> {
    override val actionType: KClass<DeclareBlockers> = DeclareBlockers::class

    override fun validate(state: GameState, action: DeclareBlockers): String? {
        // CR 805.10a — the active team is the attacking team; no member of it blocks.
        if (state.isActiveTurnFor(action.playerId)) {
            return "You cannot declare blockers on your turn"
        }
        if (state.step != Step.DECLARE_BLOCKERS) {
            return "You can only declare blockers during the declare blockers step"
        }
        // Shared-turn teams submit one combined block declaration (CR 805.10d), rather than
        // stepping through their seats one at a time.
        val isSharedTurnTeam = state.sharedTurnTeam(action.playerId).size > 1
        if (isSharedTurnTeam && state.sharedTurnTeam(action.playerId).any { defenderId ->
                state.getEntity(defenderId)
                    ?.has<com.wingedsheep.engine.state.components.combat.BlockersDeclaredThisCombatComponent>() == true
            }
        ) {
            return "Your team has already declared blockers this combat"
        }
        // Declaring blockers is a turn-based action, not an ordinary priority action.
        // The priority marker still belongs to the active player when this step begins,
        // so it must not be used to reject the first defender in a normal duel.  In a
        // multi-defender combat, the APNAP cursor below is the authoritative gate.
        if (!isSharedTurnTeam && CombatDefenders.nextDefenderToDeclare(state) != action.playerId
        ) {
            return "It is not your turn to declare blockers"
        }
        // Additional validation is done by CombatManager
        return null
    }

    override fun execute(state: GameState, action: DeclareBlockers): ExecutionResult {
        val result = combatManager.declareBlockers(state, action.playerId, action.blockers)

        if (result.isPaused) {
            return result
        }

        if (!result.isSuccess) {
            return result
        }

        return BlockDeclarationFinalizer.finish(
            result.newState,
            result.events,
            triggerDetector,
            triggerProcessor,
            sbaChecker,
            stateTriggerPoller,
        )
    }

    companion object {
        fun create(services: EngineServices): DeclareBlockersHandler {
            return DeclareBlockersHandler(
                services.combatManager,
                services.triggerDetector,
                services.triggerProcessor,
                services.sbaChecker,
                services.stateTriggerPoller,
            )
        }
    }
}
