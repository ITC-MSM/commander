package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pouncing Cheetah
 * {2}{G}
 * Creature — Cat
 * 3/2
 *
 * Flash
 */
val PouncingCheetah = card("Pouncing Cheetah") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    oracleText = "Flash"
    power = 3
    toughness = 2

    keywords(Keyword.FLASH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "Matt Stewart"
        flavorText = "Rhonas's monument is home to a wider variety of creatures than anywhere else in the city of Naktamun—a feature most initiates fail to appreciate."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fca8a590-75c1-4e85-b8b7-8c0c0f18b96e.jpg?1783936471"
    }
}
