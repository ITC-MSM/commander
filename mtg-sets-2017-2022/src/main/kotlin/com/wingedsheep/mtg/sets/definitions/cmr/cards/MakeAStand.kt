package com.wingedsheep.mtg.sets.definitions.cmr.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Make a Stand reprint in CMR.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * OGW's `cards/` package (the card's earliest real printing). This file contributes only
 * the CMR-specific presentation row — set, collector number, art — picked up automatically
 * by `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val MakeAStandReprint = Printing(
    oracleId = "531f78d5-5004-4b02-99c7-b390cb342fd9",
    name = "Make a Stand",
    setCode = "CMR",
    collectorNumber = "32",
    artist = "Magali Villeneuve",
    imageUri = "https://cards.scryfall.io/normal/front/3/a/3a948ef4-145f-4bea-b4af-5daa951b338c.jpg",
    releaseDate = "2020-11-20",
    rarity = Rarity.UNCOMMON,
)
