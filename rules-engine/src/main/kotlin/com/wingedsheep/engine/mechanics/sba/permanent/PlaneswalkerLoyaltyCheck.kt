package com.wingedsheep.engine.mechanics.sba.permanent

import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.handlers.effects.ZoneTransitionService
import com.wingedsheep.engine.mechanics.sba.SbaOrder
import com.wingedsheep.engine.mechanics.sba.SbaZoneMovementHelper
import com.wingedsheep.engine.mechanics.sba.StateBasedActionCheck
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.CounterType

/**
 * 704.5i - A planeswalker with 0 loyalty is put into its owner's graveyard.
 *
 * A planeswalker with the projected [AbilityFlag.SURVIVES_ZERO_LOYALTY] flag (Sanctum Lurker) is
 * exempt from this one check.
 */
class PlaneswalkerLoyaltyCheck(private val zones: ZoneTransitionService) : StateBasedActionCheck {
    override val name = "704.5i Planeswalker Loyalty"
    override val order = SbaOrder.PLANESWALKER_LOYALTY

    override fun check(state: GameState): ExecutionResult {
        var newState = state
        val events = mutableListOf<com.wingedsheep.engine.core.GameEvent>()
        val projected = state.projectedState

        for (entityId in state.getBattlefield().toList()) {
            val container = state.getEntity(entityId) ?: continue
            val cardComponent = container.get<CardComponent>() ?: continue

            if (!projected.isPlaneswalker(entityId)) continue
            if (projected.hasKeyword(entityId, AbilityFlag.SURVIVES_ZERO_LOYALTY)) continue

            val counters = container.get<CountersComponent>()
            val loyalty = counters?.getCount(CounterType.LOYALTY) ?: 0

            if (loyalty <= 0) {
                val result = SbaZoneMovementHelper.putPermanentInGraveyard(
                    zones,
                    newState, entityId, cardComponent
                )
                newState = result.newState
                events.addAll(result.events)
            }
        }

        return ExecutionResult.success(newState, events)
    }
}
