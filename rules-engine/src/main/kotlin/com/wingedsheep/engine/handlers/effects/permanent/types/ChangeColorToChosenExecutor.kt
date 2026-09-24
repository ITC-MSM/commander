package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.scripting.effects.ChangeColorToChosenEffect
import kotlin.reflect.KClass

/**
 * Executor for [ChangeColorToChosenEffect] (Blind Seer:
 * "{1}{U}: Target spell or permanent becomes the color of your choice until end of turn").
 *
 * Runs inside a [com.wingedsheep.sdk.scripting.effects.ChooseColorThenEffect], which has already
 * stamped the chosen color onto [EffectContext.chosenColor]. Creates a Layer-5 floating effect
 * that replaces the target's colors with that single color. The target may be a battlefield
 * permanent or a spell on the stack — the color projection reads the recolored entry in both
 * zones (gap #11).
 */
class ChangeColorToChosenExecutor : EffectExecutor<ChangeColorToChosenEffect> {

    override val effectType: KClass<ChangeColorToChosenEffect> = ChangeColorToChosenEffect::class

    override fun execute(
        state: GameState,
        effect: ChangeColorToChosenEffect,
        context: EffectContext
    ): EffectResult {
        // "The color or colors of your choice" (Quickchange) exposes the whole set; a single-color
        // choice (Blind Seer) exposes just `chosenColor`.
        val chosenColors = context.chosenColors.ifEmpty { setOfNotNull(context.chosenColor) }
        if (chosenColors.isEmpty()) return EffectResult.success(state)
        val targetId = context.resolveTarget(effect.target, state) ?: return EffectResult.success(state)
        if (!state.getBattlefield().contains(targetId) && !state.stack.contains(targetId)) {
            return EffectResult.success(state)
        }

        val newState = state.addFloatingEffect(
            layer = Layer.COLOR,
            modification = SerializableModification.ChangeColor(chosenColors.map { it.name }.toSet()),
            affectedEntities = setOf(targetId),
            duration = effect.duration,
            context = context
        )

        return EffectResult.success(newState)
    }
}
