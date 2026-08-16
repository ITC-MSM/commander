package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kithkin Billyrider
 * {2}{W}
 * Creature — Kithkin Knight
 * 1/3
 * Double strike
 */
val KithkinBillyrider = card("Kithkin Billyrider") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Knight"
    oracleText = "Double strike"
    power = 1
    toughness = 3

    keywords(Keyword.DOUBLE_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Zara Alfonso"
        flavorText = "\"Brother, guide my right hand. Sister, my left. Rest well, my kin, but " +
            "Lorwyn still needs me. I will not be joining you quite yet.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0535b69f-247d-49c9-97e1-d988700578ab.jpg?1783917058"
    }
}
