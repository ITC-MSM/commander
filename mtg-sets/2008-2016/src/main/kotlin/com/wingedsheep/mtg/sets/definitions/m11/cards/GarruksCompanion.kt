package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Garruk's Companion
 * {G}{G}
 * Creature — Beast
 * 3/2
 *
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val GarruksCompanion = card("Garruk's Companion") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 2
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Efrem Palacios"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/863c9a10-d83f-415b-adf2-2d0f870410b2.jpg?1783941798"
    }
}
