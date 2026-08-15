package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Gilded Lotus reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val GildedLotusReprint = Printing(
    oracleId = "9a02a9a7-39d9-4763-85d3-747a0540b60b",
    name = "Gilded Lotus",
    setCode = "FDN",
    collectorNumber = "725",
    scryfallId = "aa5e88f1-0ddf-45d7-bbd0-baa88d121867",
    artist = "Volkan Baǵa",
    imageUri = "https://cards.scryfall.io/normal/front/a/a/aa5e88f1-0ddf-45d7-bbd0-baa88d121867.jpg?1730572766",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
