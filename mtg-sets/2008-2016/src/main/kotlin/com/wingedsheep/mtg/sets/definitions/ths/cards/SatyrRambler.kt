package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Satyr Rambler
 * {1}{R}
 * Creature — Satyr
 * 2 / 1
 *
 * Trample
 */
val SatyrRambler = card("Satyr Rambler") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Satyr"
    power = 2
    toughness = 1
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "John Stanko"
        flavorText = "A satyr is bound by nothing—not home, not family, not loyalty."
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fabccddd-c0ea-45a5-bebc-d8f858242a2a.jpg"
    }
}
