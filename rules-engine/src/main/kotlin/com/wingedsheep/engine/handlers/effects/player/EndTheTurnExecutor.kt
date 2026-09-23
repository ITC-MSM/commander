package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.EndTheTurnRequestedComponent
import com.wingedsheep.sdk.scripting.effects.EndTheTurnEffect
import kotlin.reflect.KClass

/**
 * Executor for [EndTheTurnEffect] (CR 724.1, "end the turn").
 *
 * The end-the-turn sequence — exiling the rest of the stack, discarding the pending triggers,
 * skipping to the cleanup step and starting the next turn — cannot run here: this executor is
 * invoked mid-resolution, so other objects may still be on the stack, and it has no access to the
 * turn/stack machinery. Instead it records the request on the active player via
 * [EndTheTurnRequestedComponent], and the settle boundary ([com.wingedsheep.engine.core.Settler])
 * performs the sequence through [com.wingedsheep.engine.core.TurnManager.performEndTheTurn] once
 * the current resolution finishes.
 *
 * The marker is keyed to the active player because "end the turn" always ends the current turn,
 * regardless of who controls the effect. [context.sourceId] is stored so the source can be exiled
 * with the rest of the stack (CR 724.1b).
 */
class EndTheTurnExecutor : EffectExecutor<EndTheTurnEffect> {

    override val effectType: KClass<EndTheTurnEffect> = EndTheTurnEffect::class

    override fun execute(
        state: GameState,
        effect: EndTheTurnEffect,
        context: EffectContext
    ): EffectResult {
        val activePlayer = state.activePlayerId
            ?: return EffectResult.success(state)

        val newState = state.updateEntity(activePlayer) { container ->
            container.with(EndTheTurnRequestedComponent(sourceId = context.sourceId))
        }
        return EffectResult.success(newState)
    }
}
