package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Havoc Devils
 * {2}{R}{R}
 * Creature — Devil
 * 4/3
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 */
val HavocDevils = card("Havoc Devils") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    power = 4
    toughness = 3
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Viktor Titov"
        flavorText = "For devils, burning things is the highest form of comedy, diversion, and artistic expression."
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f003678-0f17-4f1d-87d5-83613a82044b.jpg"
    }
}
