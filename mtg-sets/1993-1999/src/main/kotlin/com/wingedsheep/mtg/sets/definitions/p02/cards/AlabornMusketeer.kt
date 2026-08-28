package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alaborn Musketeer
 * {1}{W}
 * Creature — Human Soldier
 * 2/1
 * Reach (This creature can block creatures with flying.)
 */
val AlabornMusketeer = card("Alaborn Musketeer") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "Reach (This creature can block creatures with flying.)"
    power = 2
    toughness = 1
    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Heather Hudson"
        flavorText = "Muskets gave the Alaborn army an advantage it had long lacked—air defense."
        imageUri = "https://cards.scryfall.io/normal/front/4/7/4776a9ed-dbc6-44d2-9761-b2d09ff34008.jpg"
    }
}
