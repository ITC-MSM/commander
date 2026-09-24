package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.CombatDeclarationControlComponent
import com.wingedsheep.sdk.scripting.effects.ControlCombatDeclarationsThisTurnEffect
import kotlin.reflect.KClass

/**
 * Executor for [ControlCombatDeclarationsThisTurnEffect] (Master Warcraft): marks the controller as
 * the player who makes every attack and block declaration for the rest of this turn. The marker is
 * turn-stamped, so it lapses by itself when the turn ends; a later one (a second Warcraft) wins via
 * its timestamp. Routing the declarations is [com.wingedsheep.engine.mechanics.combat.CombatDeclarationControl].
 */
class ControlCombatDeclarationsExecutor : EffectExecutor<ControlCombatDeclarationsThisTurnEffect> {

    override val effectType: KClass<ControlCombatDeclarationsThisTurnEffect> =
        ControlCombatDeclarationsThisTurnEffect::class

    override fun execute(
        state: GameState,
        effect: ControlCombatDeclarationsThisTurnEffect,
        context: EffectContext
    ): EffectResult {
        val ticked = state.tick()
        val newState = ticked.updateEntity(context.controllerId) { container ->
            container.with(
                CombatDeclarationControlComponent(
                    turnNumber = ticked.turnNumber,
                    timestamp = ticked.timestamp
                )
            )
        }
        return EffectResult.success(newState)
    }
}
