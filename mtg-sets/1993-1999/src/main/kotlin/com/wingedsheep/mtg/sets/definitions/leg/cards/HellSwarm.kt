package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hell Swarm
 * {B}
 * Instant
 *
 * All creatures get -1/-0 until end of turn.
 */
val HellSwarm = card("Hell Swarm") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "All creatures get -1/-0 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreatures,
            Effects.ModifyStats(-1, 0, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Christopher Rush"
        flavorText = "The brightness of day turned in an instant to dusk as the swarm descended upon the " +
            "battlefield."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64164d1b-75f4-456e-a717-90ce554dc16c.jpg?1783948066"
    }
}
