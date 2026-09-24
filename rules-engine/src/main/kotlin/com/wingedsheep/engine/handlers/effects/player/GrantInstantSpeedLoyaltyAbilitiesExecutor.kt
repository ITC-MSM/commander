package com.wingedsheep.engine.handlers.effects.player

import com.wingedsheep.engine.core.EffectResult
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.effects.EffectExecutor
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.player.InstantSpeedLoyaltyGrantsComponent
import com.wingedsheep.engine.state.components.player.PlayerEffectRemoval
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.GrantInstantSpeedLoyaltyAbilitiesEffect
import kotlin.reflect.KClass

/**
 * Records a turn-scoped "activate loyalty abilities any time you could cast an instant" permission
 * on the target player (Jace's Machinations).
 *
 * Appends the effect's planeswalker filter to the player's [InstantSpeedLoyaltyGrantsComponent];
 * a second grant the same turn stacks additively and all of them expire together. Permissive
 * mirror of [CantActivateLoyaltyAbilitiesExecutor], shaped like [GrantFlashToSpellsExecutor].
 */
class GrantInstantSpeedLoyaltyAbilitiesExecutor : EffectExecutor<GrantInstantSpeedLoyaltyAbilitiesEffect> {

    override val effectType: KClass<GrantInstantSpeedLoyaltyAbilitiesEffect> =
        GrantInstantSpeedLoyaltyAbilitiesEffect::class

    override fun execute(
        state: GameState,
        effect: GrantInstantSpeedLoyaltyAbilitiesEffect,
        context: EffectContext
    ): EffectResult {
        val targetIds = context.resolvePlayerTargets(effect.target, state)
            .filter { state.turnOrder.contains(it) }
        if (targetIds.isEmpty()) {
            return EffectResult.error(state, "No valid target for instant-speed loyalty grant")
        }

        val incomingRemoveOn = when (effect.duration) {
            is Duration.Permanent -> PlayerEffectRemoval.Permanent
            else -> PlayerEffectRemoval.EndOfTurn
        }

        val newState = targetIds.fold(state) { acc, targetId ->
            acc.updateEntity(targetId) { container ->
                val existing = container.get<InstantSpeedLoyaltyGrantsComponent>()
                // Never demote a Permanent grant because a later end-of-turn grant landed — the
                // same bias as GrantFlashToSpellsExecutor.
                val removeOn = if (existing?.removeOn == PlayerEffectRemoval.Permanent ||
                    incomingRemoveOn == PlayerEffectRemoval.Permanent
                ) PlayerEffectRemoval.Permanent else PlayerEffectRemoval.EndOfTurn
                container.with(
                    InstantSpeedLoyaltyGrantsComponent(
                        filters = (existing?.filters ?: emptyList()) + effect.planeswalkerFilter,
                        removeOn = removeOn,
                    )
                )
            }
        }

        return EffectResult.success(newState)
    }
}
