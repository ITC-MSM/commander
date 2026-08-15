package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Razorverge Thicket reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val RazorvergeThicketReprint = Printing(
    oracleId = "94f6c407-e665-4032-be13-a01e40c1f306",
    name = "Razorverge Thicket",
    setCode = "BLC",
    collectorNumber = "325",
    scryfallId = "7c55f26b-4c18-4418-b30f-64c73e3f884d",
    artist = "Randy Gallegos",
    imageUri = "https://cards.scryfall.io/normal/front/7/c/7c55f26b-4c18-4418-b30f-64c73e3f884d.jpg?1721429814",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
