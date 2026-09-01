package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skyline Predator
 * {4}{U}{U}
 * Creature — Drake
 * 3/4
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Flying
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val SkylinePredator = card("Skyline Predator") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Flying"
    power = 3
    toughness = 4

    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Wesley Burt"
        flavorText = "\"It will dodge your first arrow and flatten you before there's a second.\"\n" +
            "—Alcarus, Selesnya archer"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/5839556c-6635-44c4-96ed-666e4466b929.jpg?1783940366"
    }
}
