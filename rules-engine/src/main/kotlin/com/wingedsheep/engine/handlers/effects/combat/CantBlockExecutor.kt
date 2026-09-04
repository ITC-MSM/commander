package com.wingedsheep.engine.handlers.effects.combat

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.TargetResolutionUtils
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.CantBlockEffect
import kotlin.reflect.KClass

/**
 * Executor for CantBlockEffect.
 *
 * Creates a floating effect with SetCantBlock for the targeted creature, or — when the effect
 * names an [CantBlockEffect.attacker] — the pairwise CantBlockSpecificAttacker, which leaves the
 * blocker free to block everything else.
 * For multi-target spells, wrap in ForEachTargetEffect.
 */
class CantBlockExecutor : EffectExecutor<CantBlockEffect> {

    override val effectType: KClass<CantBlockEffect> = CantBlockEffect::class

    override fun execute(
        state: GameState,
        effect: CantBlockEffect,
        context: EffectContext
    ): EffectResult {
        // State-aware overload, matching SuspectExecutor and GrantKeywordExecutor — the
        // attachment-relative targets (EnchantedCreature) only resolve with state in hand.
        val entityId = TargetResolutionUtils.resolveTarget(effect.target, context, state)
            ?: return EffectResult.success(state)
        val container = state.getEntity(entityId)
            ?: return EffectResult.success(state)
        container.get<CardComponent>()
            ?: return EffectResult.success(state)

        val attacker = effect.attacker
        val modification = if (attacker == null) {
            SerializableModification.SetCantBlock
        } else {
            // "can't block this creature this turn" — if the named attacker is already gone the
            // restriction has nothing to bite on, so the whole effect does nothing.
            val attackerId = TargetResolutionUtils.resolveTarget(attacker, context, state)
                ?: return EffectResult.success(state)
            SerializableModification.CantBlockSpecificAttacker(attackerId)
        }

        val newState = state.addFloatingEffect(
            layer = Layer.ABILITY,
            modification = modification,
            affectedEntities = setOf(entityId),
            duration = effect.duration,
            context = context
        )

        return EffectResult.success(newState)
    }
}
