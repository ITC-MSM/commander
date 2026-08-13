package com.wingedsheep.engine.handlers.effects.stack

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.effects.ReturnSpellToOwnersHandEffect
import kotlin.reflect.KClass

/**
 * Executor for [ReturnSpellToOwnersHandEffect].
 *
 * Removes the targeted spell from the stack and puts it into its owner's hand.
 * This is not a counter — "can't be countered" protections do not block it.
 */
class ReturnSpellToOwnersHandExecutor : EffectExecutor<ReturnSpellToOwnersHandEffect> {

    override val effectType: KClass<ReturnSpellToOwnersHandEffect> = ReturnSpellToOwnersHandEffect::class

    override fun execute(
        state: GameState,
        effect: ReturnSpellToOwnersHandEffect,
        context: EffectContext
    ): EffectResult {
        val target = context.targets.firstOrNull() as? ChosenTarget.Spell
            ?: return EffectResult.error(state, "No valid spell target to return")

        val spellId = target.spellEntityId
        if (spellId !in state.stack) {
            // Spell already left the stack — fizzle silently
            return EffectResult.success(state)
        }

        val container = state.getEntity(spellId)
            ?: return EffectResult.success(state)

        val cardComponent = container.get<CardComponent>()
        val spellComponent = container.get<SpellOnStackComponent>()
        val ownerId = cardComponent?.ownerId
            ?: spellComponent?.casterId
            ?: return EffectResult.error(state, "Cannot determine spell owner")

        // CR 903.9b is an optional *pre-move* replacement even when a commander is
        // returned from the stack.  The generic zone pipeline may therefore pause here.
        val attempt = ZoneTransitionService.attemptMoveToZone(
            state = state,
            entityId = spellId,
            destinationZone = Zone.HAND,
            fromZoneKey = ZoneKey(ownerId, Zone.STACK)
        )
        return if (attempt.isPaused) {
            EffectResult.paused(attempt.state, attempt.pendingDecision!!, attempt.events)
        } else {
            EffectResult.success(attempt.state, attempt.events)
        }
    }
}
