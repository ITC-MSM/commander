package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sunlance
 * {W}
 * Sorcery
 * Sunlance deals 3 damage to target nonwhite creature.
 */
val Sunlance = card("Sunlance") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Sunlance deals 3 damage to target nonwhite creature."

    spell {
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.notColor(Color.WHITE)))
        )
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "31"
        artist = "Volkan Baǵa"
        flavorText = "\"It's easy for the innocent to speak of justice. They seldom feel its terrible power.\"\n—Orim, Samite inquisitor"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46144ca5-aa81-4314-a1e5-1716f8565d70.jpg"
    }
}
