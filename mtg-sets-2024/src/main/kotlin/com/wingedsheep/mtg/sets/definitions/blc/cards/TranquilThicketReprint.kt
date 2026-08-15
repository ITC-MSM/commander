package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Tranquil Thicket reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TranquilThicketReprint = Printing(
    oracleId = "9f8fe514-77ed-41b4-a6f3-c6f095bb97be",
    name = "Tranquil Thicket",
    setCode = "BLC",
    collectorNumber = "350",
    scryfallId = "bdf8439b-a31e-49d4-981a-d9532f83c370",
    artist = "Heather Hudson",
    imageUri = "https://cards.scryfall.io/normal/front/b/d/bdf8439b-a31e-49d4-981a-d9532f83c370.jpg?1721429926",
    releaseDate = "2024-08-02",
    rarity = Rarity.COMMON,
)
