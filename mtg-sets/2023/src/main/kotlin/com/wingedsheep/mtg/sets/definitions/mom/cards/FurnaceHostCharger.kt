package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Furnace Host Charger
 * {5}{R}
 * Creature — Phyrexian Giant
 * 5/5
 * Haste
 * Mountaincycling {2}
 */
val FurnaceHostCharger = card("Furnace Host Charger") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phyrexian Giant"
    oracleText = "Haste\n" +
        "Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card, " +
        "reveal it, put it into your hand, then shuffle.)"
    power = 5
    toughness = 5

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.typecycling("Mountain", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Andreas Zafiratos"
        flavorText = "With barely a moment's warning, a one-giant avalanche crashed into " +
            "Kaldheim's defenders."
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2d142ded-a2df-41b0-9c02-056b5f34abab.jpg?1783916991"
    }
}
