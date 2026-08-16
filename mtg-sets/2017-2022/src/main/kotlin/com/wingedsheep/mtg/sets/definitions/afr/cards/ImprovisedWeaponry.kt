package com.wingedsheep.mtg.sets.definitions.afr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Improvised Weaponry
 * {2}{R}
 * Sorcery
 * Improvised Weaponry deals 2 damage to any target. Create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 */
val ImprovisedWeaponry = card("Improvised Weaponry") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Improvised Weaponry deals 2 damage to any target. Create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    spell {
        val t = target("any target", Targets.Any)
        effect = Effects.Composite(
            Effects.DealDamage(2, t),
            Effects.CreateTreasure(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "150"
        artist = "Alix Branwyn"
        flavorText = "Anything can be a weapon if you swing it hard enough."
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29d5fd00-c616-4079-a91e-4da0bcaf9120.jpg?1783926476"
    }
}
