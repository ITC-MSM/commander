package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Eyeblight Assassin reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * ORI's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val EyeblightAssassinReprint = Printing(
    oracleId = "09cacd93-5c20-4993-9ee2-a756204796f7",
    name = "Eyeblight Assassin",
    setCode = "CMR",
    collectorNumber = "123",
    artist = "Dan Murayama Scott",
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a480da7-1b5d-41a5-b603-e63af100724a.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
