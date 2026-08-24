package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flight of Equenauts
 * {7}{W}
 * Creature — Human Knight
 * 4/5
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Flying
 */
val FlightOfEquenauts = card("Flight of Equenauts") {
    manaCost = "{7}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Flying"
    power = 4
    toughness = 5

    keywords(Keyword.CONVOKE, Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "11"
        artist = "Zezhou Chen"
        flavorText = "\"Yes, there's competition between our equenauts and the Boros skyjeks. At least they think it's a competition.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/c/accfc430-114e-4ce0-93ea-e8900955a71e.jpg?1783934201"
    }
}
