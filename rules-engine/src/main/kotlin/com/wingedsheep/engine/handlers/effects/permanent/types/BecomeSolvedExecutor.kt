package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.CaseSolvedEvent
import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.sdk.scripting.effects.BecomeSolvedEffect
import kotlin.reflect.KClass

/**
 * Executor for [BecomeSolvedEffect] — the resolving half of a Case's "To solve" trigger
 * (CR 719.3a). Stamps the [SolvedComponent] marker and emits a [CaseSolvedEvent].
 *
 * "Solved" is a designation (CR 719.3b), not an ability or a copiable value: nothing about the
 * permanent's characteristics changes here. The Case's "Solved —" abilities read the marker back
 * on their own through `Conditions.SourceIsSolved`.
 *
 * Solving an already-solved permanent is a no-op that emits no event — the designation is sticky
 * and one-way, so a second application has nothing to report. The trigger's intervening-if
 * ("and this Case is not solved") normally prevents that case; this guard covers an outside effect
 * solving a Case directly.
 */
class BecomeSolvedExecutor : EffectExecutor<BecomeSolvedEffect> {

    override val effectType: KClass<BecomeSolvedEffect> = BecomeSolvedEffect::class

    override fun execute(
        state: GameState,
        effect: BecomeSolvedEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target)
            ?: return EffectResult.success(state)

        // Only a permanent on the battlefield can hold the designation. A Case that left play
        // before its own end-step trigger resolved simply does nothing.
        if (targetId !in state.getBattlefield()) {
            return EffectResult.success(state)
        }

        val container = state.getEntity(targetId) ?: return EffectResult.success(state)
        if (container.has<SolvedComponent>()) {
            return EffectResult.success(state)
        }

        val name = container.get<CardComponent>()?.name ?: "Unknown"
        // The solving player is the Case's controller (CR 719.3a — its own "To solve" trigger), read
        // from the projection so a Case under someone else's control credits that player.
        val solver = state.projectedState.getController(targetId)
            ?: container.get<ControllerComponent>()?.playerId
            ?: context.controllerId
        val newState = state.updateEntity(targetId) { it.with(SolvedComponent) }

        return EffectResult.success(newState, listOf(CaseSolvedEvent(targetId, name, solver)))
    }
}
