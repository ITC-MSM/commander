package com.wingedsheep.engine.handlers.effects.combat

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ForceBlockEffect
import kotlin.reflect.KClass

/**
 * Executor for ForceBlockEffect.
 * "Target creature blocks this creature this combat if able."
 *
 * Creates a floating effect forcing the target to block the named attacker — the source by
 * default (Avalanche Tusker), or whichever creature `effect.attacker` resolves to for an
 * ANY-bound trigger that pins the blocker to the *triggering* attacker (Tolsimir, Midnight's
 * Light: "blocks that Wolf this combat if able").
 *
 * Unlike ProvokeExecutor, does NOT untap the target creature.
 */
class ForceBlockExecutor : EffectExecutor<ForceBlockEffect> {

    override val effectType: KClass<ForceBlockEffect> = ForceBlockEffect::class

    override fun execute(
        state: GameState,
        effect: ForceBlockEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target)
            ?: return EffectResult.error(state, "No valid target for force block effect")

        val targetContainer = state.getEntity(targetId)
            ?: return EffectResult.error(state, "Target creature no longer exists")
        val cardComponent = targetContainer.get<CardComponent>()
            ?: return EffectResult.error(state, "Target is not a card")
        if (!cardComponent.typeLine.isCreature) {
            return EffectResult.error(state, "Target is not a creature")
        }

        // The creature that must be blocked. Defaults to the ability's source; an ANY-bound
        // trigger can name the triggering attacker instead.
        val attackerId = context.resolveTarget(effect.attacker)
            ?: return EffectResult.error(state, "No valid attacker for force block effect")

        // Verify the attacker is actually attacking — CR 509.1c can only be satisfied against a
        // declared attacker, and the block-declaration validator reads the requirement per combat.
        val attackerContainer = state.getEntity(attackerId)
            ?: return EffectResult.error(state, "Attacking creature no longer exists")
        if (!attackerContainer.has<AttackingComponent>()) {
            return EffectResult.error(state, "Named creature is not attacking")
        }

        // Create a floating effect forcing the target to block that attacker
        val newState = state.addFloatingEffect(
            layer = Layer.ABILITY,
            modification = SerializableModification.MustBlockSpecificAttacker(attackerId),
            affectedEntities = setOf(targetId),
            duration = Duration.EndOfTurn,
            context = context
        )

        return EffectResult.success(newState)
    }
}
