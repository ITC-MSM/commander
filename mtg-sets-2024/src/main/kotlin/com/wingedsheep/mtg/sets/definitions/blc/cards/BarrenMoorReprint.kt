package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Barren Moor reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val BarrenMoorReprint = Printing(
    oracleId = "326ba371-124c-4949-a048-3a0c8962e567",
    name = "Barren Moor",
    setCode = "BLC",
    collectorNumber = "292",
    scryfallId = "1517f3b6-579d-472f-a44e-ad7545407509",
    artist = "Heather Hudson",
    imageUri = "https://cards.scryfall.io/normal/front/1/5/1517f3b6-579d-472f-a44e-ad7545407509.jpg?1721429670",
    releaseDate = "2024-08-02",
    rarity = Rarity.UNCOMMON,
)
