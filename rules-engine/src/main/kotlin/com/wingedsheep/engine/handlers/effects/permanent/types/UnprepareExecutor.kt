package com.wingedsheep.engine.handlers.effects.permanent.types

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.permissions.removeMayPlayPermissionsForCard
import com.wingedsheep.sdk.scripting.effects.UnprepareEffect
import kotlin.reflect.KClass

/**
 * Executor for [UnprepareEffect] (Secrets of Strixhaven) — the inverse of
 * [BecomePreparedExecutor]. Strips the target's prepared status: removes its [PreparedComponent]
 * and the cast-from-exile permission for its exile prepare-spell copy. The now-orphaned copy is
 * swept by the phantom-copy state-based action ([com.wingedsheep.engine.mechanics.sba.zone.PhantomCardCopiesCheck]),
 * which removes prepare-spell copies whose source is no longer prepared.
 *
 * This mirrors the unprepare half of casting the prepare-spell copy in
 * [com.wingedsheep.engine.mechanics.stack.StackResolver]. A creature that isn't prepared is
 * unaffected (no-op).
 */
class UnprepareExecutor : EffectExecutor<UnprepareEffect> {

    override val effectType: KClass<UnprepareEffect> = UnprepareEffect::class

    override fun execute(
        state: GameState,
        effect: UnprepareEffect,
        context: EffectContext
    ): EffectResult {
        val targetId = context.resolveTarget(effect.target, state)
            ?: return EffectResult.success(state)

        val prepared = state.getEntity(targetId)?.get<PreparedComponent>()
            ?: return EffectResult.success(state)

        var newState = state.updateEntity(targetId) { c ->
            c.without<PreparedComponent>()
        }
        // Consume the cast-from-exile permission for the linked copy. The copy itself is then
        // swept by PhantomCardCopiesCheck since its source is no longer prepared.
        newState = newState.removeMayPlayPermissionsForCard(prepared.exileCopyId)
        return EffectResult.success(newState)
    }
}
