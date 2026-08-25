package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spinal Villain
 * {2}{R}
 * Creature — Beast
 * 1/2
 *
 * {T}: Destroy target blue creature.
 */
val SpinalVillain = card("Spinal Villain") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 1
    toughness = 2
    oracleText = "{T}: Destroy target blue creature."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target blue creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withColor(Color.BLUE))),
        )
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "164"
        artist = "Anson Maddocks"
        flavorText = "\"Striking silent as a dream,/ Cutting short the strangled scream . . .\" —Tobrian, " +
            "\"Watchdragon\""
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6d5e36f-0049-4be8-bf85-8dc0186339a4.jpg?1783948052"
    }
}
