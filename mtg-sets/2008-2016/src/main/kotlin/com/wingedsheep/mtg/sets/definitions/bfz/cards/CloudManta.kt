package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloud Manta
 * {3}{U}
 * Creature — Fish
 * 3/2
 * Flying
 */
val CloudManta = card("Cloud Manta") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 3
    toughness = 2
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Mike Bierek"
        flavorText = "When Emeria's worshippers learned that she was no more than a twisted memory of Emrakul, " +
            "they abandoned their temples to the god. The deserted shrines now serve as breeding grounds " +
            "for the mantas."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/1854f819-d08e-4a23-bedb-4618b79623e9.jpg?1783938210"
    }
}
