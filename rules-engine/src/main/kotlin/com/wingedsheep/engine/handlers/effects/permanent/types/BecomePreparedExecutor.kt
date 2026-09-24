package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.mechanics.stack.PreparationLogic
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.scripting.effects.BecomePreparedEffect
import kotlin.reflect.KClass

/**
 * Executor for [BecomePreparedEffect] (Secrets of Strixhaven). Makes the target permanent become
 * prepared via the shared [PreparationLogic.makePrepared] — the same logic that runs when a
 * PREPARED-keyword creature enters prepared.
 *
 * Only a PREPARE-layout permanent on the battlefield can become prepared. If the target left play,
 * isn't a preparation card, or is already prepared, the effect does nothing (CR 614 / the prepare
 * rulings: a creature that's already prepared doesn't re-prepare).
 */
class BecomePreparedExecutor(
    private val cardRegistry: CardRegistry
) : EffectExecutor<BecomePreparedEffect> {

    override val effectType: KClass<BecomePreparedEffect> = BecomePreparedEffect::class

    override fun execute(
        state: GameState,
        effect: BecomePreparedEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state)

        if (targetId !in state.getBattlefield()) {
            return EffectResult.success(state)
        }

        val card = state.getEntity(targetId)?.get<CardComponent>()
            ?: return EffectResult.success(state)
        val cardDef = cardRegistry.getCard(card.cardDefinitionId)
            ?: return EffectResult.success(state)
        if (cardDef.layout != CardLayout.PREPARE) {
            return EffectResult.success(state)
        }

        // The controller of the now-prepared permanent owns the prepare-spell copy and the
        // cast-from-exile permission (falls back to the ability's controller).
        val controllerId = state.getEntity(targetId)?.get<ControllerComponent>()?.playerId
            ?: context.controllerId

        val newState = PreparationLogic.makePrepared(state, targetId, cardDef, controllerId)
        return EffectResult.success(newState)
    }
}
