package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.SkipUntapComponent
import com.wingedsheep.sdk.scripting.effects.SkipUntapEffect
import kotlin.reflect.KClass

/**
 * Executor for SkipUntapEffect.
 * "Creatures and lands [that player] controls don't untap during their next untap step."
 *
 * This adds a SkipUntapComponent to the affected player. When that player's
 * next untap step occurs, the specified permanent types (creatures and/or lands)
 * are skipped and the component is removed.
 *
 * The affected player comes from the effect's own [SkipUntapEffect.target], resolved through the
 * shared player resolver. That matters for the cards whose clause names a player it never
 * *targeted*: Pollen Lullaby freezes the clash opponent, read back from the source's chosen-opponent
 * slot via [com.wingedsheep.sdk.scripting.references.Player.ChosenOpponent], and there is no entry
 * in `context.targets` to find them in. The resolver still routes the default
 * `PlayerRef(TargetPlayer)` back to the first chosen target, so the targeted cards (Exhaustion,
 * Mana Vapors, Blinding Beam) are unchanged.
 */
class SkipUntapExecutor : EffectExecutor<SkipUntapEffect> {

    override val effectType: KClass<SkipUntapEffect> = SkipUntapEffect::class

    override fun execute(
        state: GameState,
        effect: SkipUntapEffect,
        context: EffectContext
    ): EffectResult {
        val targetPlayerId = TargetResolutionUtils.resolvePlayerTarget(effect.target, context, state)
            ?: return EffectResult.error(state, "No valid player target for SkipUntapEffect")

        // Add the SkipUntapComponent to the affected player
        val newState = state.updateEntity(targetPlayerId) { container ->
            container.with(
                SkipUntapComponent(
                    affectsCreatures = effect.affectsCreatures,
                    affectsLands = effect.affectsLands
                )
            )
        }

        return EffectResult.success(newState)
    }
}
