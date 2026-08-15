package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Maalfeld Twins reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * AVR's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MaalfeldTwinsReprint = Printing(
    oracleId = "dda4b515-49c0-43fe-9f6a-36defa326bb1",
    name = "Maalfeld Twins",
    setCode = "CMR",
    collectorNumber = "132",
    artist = "Mike Sass",
    imageUri = "https://cards.scryfall.io/normal/front/f/a/fa715fde-8339-447b-8ac8-3126830bece8.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.COMMON,
)
