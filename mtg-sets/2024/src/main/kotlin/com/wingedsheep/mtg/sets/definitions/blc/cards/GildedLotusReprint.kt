package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gilded Lotus reprint in BLC.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the BLC-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GildedLotusReprint = Printing(
    oracleId = "9a02a9a7-39d9-4763-85d3-747a0540b60b",
    name = "Gilded Lotus",
    setCode = "BLC",
    collectorNumber = "271",
    scryfallId = "725cb5db-d4b6-453b-9a10-4ab7d98e1023",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/7/2/725cb5db-d4b6-453b-9a10-4ab7d98e1023.jpg?1721429563",
    releaseDate = "2024-08-02",
    rarity = Rarity.RARE,
)
