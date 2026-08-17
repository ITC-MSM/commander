package com.wingedsheep.engine.handlers.effects.stack

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.PendingFreeCastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.scripting.effects.GrantNextSpellFreeCastEffect
import kotlin.reflect.KClass

/**
 * Executor for [GrantNextSpellFreeCastEffect].
 *
 * Adds a [PendingFreeCastSpell] rider to the game state.
 * [com.wingedsheep.engine.mechanics.mana.CostCalculator.hasFreeCastPermission] reads it so the
 * controller's next matching spell is offered as a `CastSpell.useWithoutPayingManaCost` action, and
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler] consumes the rider on that cast
 * (whether or not the free cast was taken). Mirrors [GrantNextSpellAffinityExecutor].
 */
class GrantNextSpellFreeCastExecutor : EffectExecutor<GrantNextSpellFreeCastEffect> {

    override val effectType: KClass<GrantNextSpellFreeCastEffect> = GrantNextSpellFreeCastEffect::class

    override fun execute(
        state: GameState,
        effect: GrantNextSpellFreeCastEffect,
        context: EffectContext
    ): EffectResult {
        val (effectiveState, sourceId) = if (context.sourceId != null) {
            state to context.sourceId
        } else {
            val (id, s) = state.newEntity()
            s to id
        }
        val sourceName = effectiveState.getEntity(sourceId)?.get<CardComponent>()?.name ?: "Unknown"

        val pending = PendingFreeCastSpell(
            controllerId = context.controllerId,
            spellFilter = effect.spellFilter,
            sourceId = sourceId,
            sourceName = sourceName
        )
        val newState = effectiveState.copy(
            pendingFreeCastSpells = effectiveState.pendingFreeCastSpells + pending
        )
        return EffectResult.success(newState)
    }
}
