package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crystacean
 * {3}{U}
 * Creature — Crab
 * 1/6
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 */
val Crystacean = card("Crystacean") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 1
    toughness = 6
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)"

    keywords(Keyword.FLASH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Mathias Kollros"
        flavorText = "It loves blending into its environment, ambushing prey, and long scuttles on the beach."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32afec8a-dbae-446e-919a-3efb556f5cb1.jpg"
    }
}
