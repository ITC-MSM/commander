package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scatter the Seeds
 * {3}{G}{G}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Create three 1/1 green Saproling creature tokens.
 */
val ScatterTheSeeds = card("Scatter the Seeds") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Create three 1/1 green Saproling creature tokens."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.CreateToken(
            count = 3,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "181"
        artist = "Rob Alexander"
        flavorText = "From the seeds of faith, great forests grow."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4415070-4304-482b-b8e5-2bf689af0843.jpg"
    }
}
