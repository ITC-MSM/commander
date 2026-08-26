package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Shimmerscale Drake
 * {4}{U}
 * Creature — Drake
 * 3/4
 * Flying
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ShimmerscaleDrake = card("Shimmerscale Drake") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Tony Foti"
        flavorText = "They are drawn by the brilliant blue glint of the mineral lazotep from the mines below."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edb13075-0ea0-480a-84c0-8eec3119db45.jpg?1783936516"
    }
}
