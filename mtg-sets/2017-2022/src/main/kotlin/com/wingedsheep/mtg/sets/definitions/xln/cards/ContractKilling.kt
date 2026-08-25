package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Contract Killing
 * {3}{B}{B}
 * Sorcery
 *
 * Destroy target creature. Create two Treasure tokens.
 */
val ContractKilling = card("Contract Killing") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. Create two Treasure tokens. (They're artifacts with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")"

    spell {
        val victim = target("target", Targets.Creature)
        effect = Effects.Destroy(victim) then Effects.CreateTreasure(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Winona Nelson"
        flavorText = "For a price, the floating city of High and Dry offers all the amenities a pirate could want: rest, recreation, and revenge."
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1f20feb-b1ed-4d80-bef9-f3cc44ffb7b0.jpg"
    }
}
