package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Shivan Reef reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val ShivanReefReprint = Printing(
    oracleId = "0fe16212-66c3-4e45-a641-7391e9b2e304",
    name = "Shivan Reef",
    setCode = "BLC",
    collectorNumber = "331",
    scryfallId = "59d5e76c-4961-4dbf-be38-a18a2c16d33f",
    artist = "Rob Alexander",
    imageUri = "https://cards.scryfall.io/normal/front/5/9/59d5e76c-4961-4dbf-be38-a18a2c16d33f.jpg?1721429839",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
