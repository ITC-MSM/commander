package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Moss Viper
 * {G}
 * Creature — Snake
 * 1/1
 *
 * Deathtouch
 *
 * A single printed keyword — [Keyword.DEATHTOUCH] on the keyword set, nothing else to model.
 */
val MossViper = card("Moss Viper") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 1
    toughness = 1
    oracleText = "Deathtouch"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "Mike Bierek"
        flavorText = "Nylea watches over all the creatures of the forest except for the snakes. " +
            "Blessed by Pharika, they can take care of themselves."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4d35ec4-0e0d-4611-8ad9-39d2c8a2ad6e.jpg"
    }
}
