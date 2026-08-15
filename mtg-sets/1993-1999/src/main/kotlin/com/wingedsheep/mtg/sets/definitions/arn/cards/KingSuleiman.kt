package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * King Suleiman
 * {1}{W}
 * Creature — Human Noble
 * 1/1
 * {T}: Destroy target Djinn or Efreet.
 */
val KingSuleiman = card("King Suleiman") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Noble"
    power = 1
    toughness = 1
    oracleText = "{T}: Destroy target Djinn or Efreet."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target Djinn or Efreet",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.withSubtype("Djinn") or
                        GameObjectFilter.Creature.withSubtype("Efreet")
                )
            )
        )
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Mark Poole"
        flavorText = "\"We made tempestuous winds obedient to Solomon . . . And many of the devils We also made obedient to him.\" —The Qur'an, 21:81"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d3dce0f-2168-4f63-b2f9-156a11beeea7.jpg?1592364322"
    }
}
