package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Woodland Cemetery reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val WoodlandCemeteryReprint = Printing(
    oracleId = "c9fe1383-1331-4a58-a45a-3320250221a9",
    name = "Woodland Cemetery",
    setCode = "BLC",
    collectorNumber = "354",
    scryfallId = "7ab2f083-b674-4f8d-897b-9dc84ff85658",
    artist = "Christine Choi",
    imageUri = "https://cards.scryfall.io/normal/front/7/a/7ab2f083-b674-4f8d-897b-9dc84ff85658.jpg?1721429948",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
