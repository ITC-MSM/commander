package com.wingedsheep.engine.handlers.effects.life

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.core.GameEvent as EngineGameEvent
import com.wingedsheep.engine.core.LifeChangeReason
import com.wingedsheep.engine.core.LifeChangedEvent
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.DamageUtils
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.drawing.DrawCardPrimitive
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.LifeTotalComponent
import com.wingedsheep.sdk.scripting.effects.ExchangeLifeTotalsEffect
import kotlin.reflect.KClass

/**
 * Executor for [ExchangeLifeTotalsEffect] — swaps the controller's life total with the target
 * player's (CR 701.12c, simultaneous: each becomes the other's former total). Emits gain/loss
 * [LifeChangedEvent]s for both players and marks life gained/lost this turn. When
 * [ExchangeLifeTotalsEffect.drawEqualToLifeLost], the controller then draws a card for each point of
 * life they lost (Mister Negative).
 */
class ExchangeLifeTotalsExecutor(
    private val cardRegistry: com.wingedsheep.engine.registry.CardRegistry
) : EffectExecutor<ExchangeLifeTotalsEffect> {

    private val drawPrimitive = DrawCardPrimitive(cardRegistry)

    override val effectType: KClass<ExchangeLifeTotalsEffect> = ExchangeLifeTotalsEffect::class

    override fun execute(
        state: GameState,
        effect: ExchangeLifeTotalsEffect,
        context: EffectContext
    ): EffectResult {
        val controllerId = context.controllerId
        val targetId = context.resolveTarget(effect.target, state) ?: return EffectResult.success(state)
        if (controllerId == targetId) return EffectResult.success(state)
        if (state.getEntity(controllerId)?.get<LifeTotalComponent>() == null) return EffectResult.success(state)
        if (state.getEntity(targetId)?.get<LifeTotalComponent>() == null) return EffectResult.success(state)

        // Read both totals before any change (simultaneous swap).
        val myLife = state.lifeTotal(controllerId)
        val theirLife = state.lifeTotal(targetId)
        if (myLife == theirLife) return EffectResult.success(state) // no-op swap

        val events = mutableListOf<EngineGameEvent>()
        var newState = state
        newState = newState.withLifeTotal(controllerId, theirLife)
        newState = newState.withLifeTotal(targetId, myLife)

        events.add(
            LifeChangedEvent(
                controllerId, myLife, theirLife,
                if (theirLife > myLife) LifeChangeReason.LIFE_GAIN else LifeChangeReason.LIFE_LOSS
            )
        )
        events.add(
            LifeChangedEvent(
                targetId, theirLife, myLife,
                if (myLife > theirLife) LifeChangeReason.LIFE_GAIN else LifeChangeReason.LIFE_LOSS
            )
        )
        if (theirLife > myLife) newState = DamageUtils.markLifeGainedThisTurn(newState, controllerId, theirLife - myLife)
        if (theirLife < myLife) newState = DamageUtils.markLifeLostThisTurn(newState, controllerId)
        if (myLife > theirLife) newState = DamageUtils.markLifeGainedThisTurn(newState, targetId, myLife - theirLife)
        if (myLife < theirLife) newState = DamageUtils.markLifeLostThisTurn(newState, targetId)

        // "If you lost life this way, draw that many cards."
        if (effect.drawEqualToLifeLost && myLife > theirLife) {
            repeat(myLife - theirLife) {
                val result = drawPrimitive.drawOne(newState, controllerId)
                newState = result.state
                events.addAll(result.events)
                if (result.failed) return@repeat
            }
        }

        return EffectResult.success(newState, events)
    }
}
