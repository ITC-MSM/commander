package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brimstone Dragon
 * {6}{R}{R}
 * Creature — Dragon
 * 6/6
 * Flying, haste
 */
val BrimstoneDragon = card("Brimstone Dragon") {
    manaCost = "{6}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Flying, haste"
    power = 6
    toughness = 6
    keywords(Keyword.FLYING, Keyword.HASTE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "92"
        artist = "David A. Cherry"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54c72f51-2e8c-4a2f-9b61-80f36f8af521.jpg"
    }
}
