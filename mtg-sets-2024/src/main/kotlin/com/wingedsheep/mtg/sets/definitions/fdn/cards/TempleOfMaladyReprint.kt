package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple of Malady reprint in FDN.
 *
 * The canonical [com.wingedsheep.sdk.model.CardDefinition] (script, types, P/T) lives in
 * another set's `cards/` package. This file contributes only the FDN-specific
 * presentation row — set, collector number, art — picked up automatically by
 * `CardDiscovery.findPrintingsIn` and surfaced via the set's `printings`.
 */
val TempleOfMaladyReprint = Printing(
    oracleId = "dc55421f-dee8-4263-9df0-2365df5f14bb",
    name = "Temple of Malady",
    setCode = "FDN",
    collectorNumber = "700",
    scryfallId = "10e7d0d5-2329-42a6-b6ff-a5197ba5f1d0",
    artist = "Titus Lunter",
    imageUri = "https://cards.scryfall.io/normal/front/1/0/10e7d0d5-2329-42a6-b6ff-a5197ba5f1d0.jpg?1730491251",
    releaseDate = "2024-11-15",
    rarity = Rarity.RARE,
)
