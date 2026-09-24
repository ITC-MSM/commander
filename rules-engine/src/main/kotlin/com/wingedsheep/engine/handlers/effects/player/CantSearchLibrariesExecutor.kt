package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.CantSearchLibrariesComponent
import com.wingedsheep.engine.state.components.player.PlayerEffectRemoval
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CantSearchLibrariesEffect
import kotlin.reflect.KClass

/**
 * Executor for [CantSearchLibrariesEffect]: stamps [CantSearchLibrariesComponent] on each target
 * player. The restriction is enforced where searches happen (`GatherCardsExecutor` for a
 * `search = true` gather, `EmitLibrarySearchedEventExecutor` for the searched event).
 */
class CantSearchLibrariesExecutor : EffectExecutor<CantSearchLibrariesEffect> {

    override val effectType: KClass<CantSearchLibrariesEffect> = CantSearchLibrariesEffect::class

    override fun execute(
        state: GameState,
        effect: CantSearchLibrariesEffect,
        context: EffectContext
    ): EffectResult {
        val targetIds = context.resolvePlayerTargets(effect.target, state)
            .filter { state.turnOrder.contains(it) }
        if (targetIds.isEmpty()) {
            return EffectResult.error(state, "No valid target for can't search libraries effect")
        }

        val removeOn = when (effect.duration) {
            is Duration.Permanent -> PlayerEffectRemoval.Permanent
            else -> PlayerEffectRemoval.EndOfTurn
        }

        val newState = targetIds.fold(state) { acc, targetId ->
            acc.updateEntity(targetId) { container ->
                container.with(CantSearchLibrariesComponent(removeOn = removeOn))
            }
        }

        return EffectResult.success(newState)
    }
}
