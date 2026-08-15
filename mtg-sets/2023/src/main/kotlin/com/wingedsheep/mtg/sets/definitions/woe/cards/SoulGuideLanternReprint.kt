package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Soul-Guide Lantern reprint in Wilds of Eldraine.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] lives in THB's `cards/` package
 * (the card's earliest real printing). This file contributes only the WOE presentation row.
 */
val SoulGuideLanternReprint = Printing(
    oracleId = "1b5e6560-ff2e-4475-96cb-63f64c8a86db",
    name = "Soul-Guide Lantern",
    setCode = "WOE",
    collectorNumber = "251",
    scryfallId = "13571173-29e7-4915-af5f-05f13b463061",
    artist = "Iris Compiet",
    imageUri = "https://cards.scryfall.io/normal/front/1/3/13571173-29e7-4915-af5f-05f13b463061.jpg?1782696480",
    releaseDate = "2023-09-08",
    rarity = Rarity.UNCOMMON,
)
