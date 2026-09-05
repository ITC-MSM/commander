package com.wingedsheep.engine.handlers.effects.combat

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.mechanics.layers.Layer
import com.wingedsheep.engine.mechanics.layers.SerializableModification
import com.wingedsheep.engine.mechanics.layers.addFloatingEffect
import com.wingedsheep.engine.state.GameState
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

        if (!state.projectedState.isCreature(targetId)) {
            return EffectResult.error(state, "Target is not a creature")
        }

        // The creature that must be blocked. Defaults to the ability's source; an ANY-bound
        // trigger can name the triggering attacker instead.
        val attackerId = context.resolveTarget(effect.attacker)
            ?: return EffectResult.error(state, "No valid attacker for force block effect")

        // The named creature need not be attacking *yet*: an activated "blocks this creature this
        // turn if able" (Sisters of Stone Death) can resolve in the precombat main phase, and the
        // requirement then applies once the creature attacks. The block-declaration validator
        // reads the requirement per combat and ignores it while the named creature isn't
        // attacking (CR 509.1c), so a requirement created early is simply dormant, not wrong.
        if (state.getEntity(attackerId) == null) {
            return EffectResult.error(state, "Named creature no longer exists")
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
