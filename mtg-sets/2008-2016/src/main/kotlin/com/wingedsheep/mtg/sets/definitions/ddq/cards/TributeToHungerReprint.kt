package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tribute to Hunger reprint in Duel Decks: Blessed vs. Cursed.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in ISD's `cards/` package
 * (the card's earliest real printing). This file contributes only the DDQ presentation row.
 */
val TributeToHungerReprint = Printing(
    oracleId = "dc5a501c-e16c-4d4b-a510-70a14272473e",
    name = "Tribute to Hunger",
    setCode = "DDQ",
    collectorNumber = "65",
    scryfallId = "fcfc9bd3-4378-4305-b427-4e3dd4e1fba1",
    artist = "Dave Kendall",
    imageUri = "https://cards.scryfall.io/normal/front/f/c/fcfc9bd3-4378-4305-b427-4e3dd4e1fba1.jpg?1782750052",
    releaseDate = "2016-02-26",
    rarity = Rarity.UNCOMMON,
)
