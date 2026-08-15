package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Abandon reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfAbandonReprint = Printing(
    oracleId = "3baa8e38-ef93-435d-b63e-f781d5bfcc68",
    name = "Temple of Abandon",
    setCode = "FDN",
    collectorNumber = "696",
    scryfallId = "8a990a92-8d0f-489f-ae97-68c90fc5ccf1",
    artist = "Adam Paquette",
    imageUri = "https://cards.scryfall.io/normal/front/8/a/8a990a92-8d0f-489f-ae97-68c90fc5ccf1.jpg?1730491235",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
