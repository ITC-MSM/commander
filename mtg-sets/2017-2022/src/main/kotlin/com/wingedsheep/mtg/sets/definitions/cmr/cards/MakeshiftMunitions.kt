package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Makeshift Munitions reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * XLN's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MakeshiftMunitionsReprint = Printing(
    oracleId = "2421bec6-7647-4684-be61-d1aa951c6b4f",
    name = "Makeshift Munitions",
    setCode = "CMR",
    collectorNumber = "191",
    artist = "Filip Burburan",
    imageUri = "https://cards.scryfall.io/normal/front/1/9/19d1ad9f-e217-49fb-8b27-025ca133b6c9.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
