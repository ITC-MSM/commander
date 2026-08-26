package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Two-Headed Cerberus
 * {1}{R}{R}
 * Creature — Dog
 * 1 / 2
 *
 * Double strike (This creature deals both first-strike and regular combat damage.)
 */
val TwoHeadedCerberus = card("Two-Headed Cerberus") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dog"
    power = 1
    toughness = 2
    oracleText = "Double strike (This creature deals both first-strike and regular combat damage.)"

    keywords(Keyword.DOUBLE_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Karl Kopinski"
        flavorText = "The left head keeps the right head starved as motivation to track new prey."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8d2f75c-ef2a-4d30-86d1-c47307fc47ac.jpg"
    }
}
