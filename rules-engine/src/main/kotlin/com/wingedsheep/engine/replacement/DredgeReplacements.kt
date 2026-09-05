package com.wingedsheep.engine.replacement

import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DredgeComponent
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.EventPattern

/** Supplies graveyard dredge abilities to the ordinary replacement-choice pipeline. */
internal object DredgeReplacements {
    private val drawPattern = EventPattern.DrawEvent()

    fun gather(state: GameState, event: PendingGameEvent, context: EffectContext?): List<GatheredReplacement> {
        val player = event.affectedPlayerId
        // Match the individual draw, never the multi-card draw announcement. Other event
        // families return before walking a graveyard, keeping damage/token hot paths cheap.
        if (!event.matches(drawPattern, player, state, context)) return emptyList()
        val librarySize = state.getLibrary(player).size
        val result = mutableListOf<GatheredReplacement>()
        for (id in state.getGraveyard(player)) {
            val card = state.getEntity(id) ?: continue
            val dredge = card.get<DredgeComponent>() ?: continue
            for ((index, amount) in dredge.amounts.withIndex()) {
                if (librarySize < amount) continue
                val name = card.get<CardComponent>()?.name ?: "this card"
                val cardsToMill = if (amount == 1) "a card" else "$amount cards"
                result.add(GatheredReplacement(
                    identity = ReplacementEffectIdentity.CardZoneIdentity(id, Zone.GRAVEYARD, index),
                    effect = dredge.replacements[index],
                    sourceControllerId = player,
                    description = "$name: Dredge $amount",
                    optionalPrompt = "Dredge $amount — Mill $cardsToMill and return $name from your graveyard to your hand instead of drawing?"
                ))
            }
        }
        return result
    }
}
